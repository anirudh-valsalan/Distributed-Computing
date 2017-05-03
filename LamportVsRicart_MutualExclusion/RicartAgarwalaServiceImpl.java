import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class RicartAgarwalaServiceImpl implements CriticalSectionService{

	private HashSet<Integer> receivedResponsesSet;
	
	Queue<Request> deferredRequestQueue;
	Node node;
	
	private RequestMessage requestMessage;
	private ReplyMessage replyMessage;
	private Integer requestMadeToCSTime;
	
	public RicartAgarwalaServiceImpl(Node node) {
		this.node = node;
		receivedResponsesSet = new HashSet<>();
		deferredRequestQueue =  new LinkedList<>();
	}
	
	public synchronized void processRequestMessage(Message requestMessage) {
		System.out.println("Node Id: " + this.node.getNodeId() + " received REQUEST Message from " + requestMessage);
		
		Node sender = requestMessage.getSourceNode();
		Request receivedRequest = new Request(sender.getNodeId(), requestMessage.getClock(), sender);
		
		node.incrementClock(requestMessage.getClock());
		
		//System.out.println("Node Id: " + node.getNodeId() + " status " + node.getCsStatus() + " condition " 
		//+ receivedRequest.getClock() +" == " + node.getClock().get() + "&&" + receivedRequest.getNodeID() + ">" + node.getNodeId());
		if(node.getCsStatus() == CSStatus.CSREQUESTED || node.getCsStatus() == CSStatus.CSIN) {
			if((receivedRequest.getClock() > requestMadeToCSTime) || (receivedRequest.getClock() == requestMadeToCSTime && receivedRequest.getNodeID() > node.getNodeId())) {
				deferredRequestQueue.add(receivedRequest);
			} else {
				ReplyMessage reply = new ReplyMessage(this.node, this.node.getClock());
				node.sendMessage(requestMessage.getSourceNode(), reply);
			}
		} else {
			ReplyMessage reply = new ReplyMessage(this.node, this.node.getClock());
			node.sendMessage(requestMessage.getSourceNode(), reply);
		}
		
		System.out.println("Node ID: " + node.getNodeId() + " deferred requests queue "  + deferredRequestQueue);
	}
	
	private synchronized boolean isResponseReceivedFromAllProcess(){
		return receivedResponsesSet.size() == node.getNeighbours().size();
	}
	
	public synchronized void processReplyMessage(Message replyMessage) {
		
		System.out.println("Node Id: " + node.getNodeId() + " received REPLY Message from " + replyMessage);
		Node sender = replyMessage.getSourceNode();
		receivedResponsesSet.add(sender.getNodeId());
	}
	
	public synchronized void processReleaseMessage(Message releaseMessage) {
		
		// Ricart & aggarwala protocol combines the reply & release messages.
		
		return;
	}
	
	@Override
	public void csEnter() {

		synchronized (node.getCsStatus()) {
			node.setCsStatus(CSStatus.CSREQUESTED);
			receivedResponsesSet.clear();
			
			requestMadeToCSTime = node.getClock();
			
			requestMessage = new RequestMessage(node, node.getClock());
			//new Request(node.getNodeId(), node.getClock());
			node.broadcastRequest(requestMessage);
		}
		
		System.out.println("Node Id: " + node.getNodeId() + " making a REQUEST: " + (Message)requestMessage);
        System.out.println("Node Id: " + node.getNodeId()+ " response set " + receivedResponsesSet.size());
		
        while (!isResponseReceivedFromAllProcess()) {
			try {
				//System.out.println("status "+isResponseReceivedFromAllProcess()+" for node "+node.getNodeId());
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
        
        System.out.println("Node Id: " + node.getNodeId() + " about to enter cs "+ isResponseReceivedFromAllProcess());
		synchronized (node.getCsStatus()) {
            
			System.out.println("Node " + node.getNodeId() + " entering the CS : " + node.getClock());
			node.setCsStatus(CSStatus.CSIN);
		}

	}

	@Override
	public void csLeave() {
		synchronized (node.getCsStatus()) {
			System.out.println("Node --> " + node.getNodeId() + " leaving the CS : " + node.getClock());
			node.setCsStatus(CSStatus.CSOUT);
			
			replyMessage = new ReplyMessage(node, node.getClock());
			
			System.out.println("Node Id: " + node.getNodeId() + " broadcasting REPLY messages to deferred Requests: " + (Message)replyMessage+" defered request queue "+deferredRequestQueue);
			node.sendReplytoDeferredRequest(deferredRequestQueue, replyMessage);
			deferredRequestQueue.clear();
		}}

}
