import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

public class LamportServiceImpl implements CriticalSectionService  {

	private HashSet<Integer> receivedResponsesSet;
	
	PriorityQueue<Request> requestsQueue;
	Node node;
	
	private RequestMessage requestMessage;
	private ReleaseMessage releaseMessage;
	private Request request;
	
	private Integer requestMadeToCSTime;
	
	public LamportServiceImpl(Node node) {
		this.node = node;
		receivedResponsesSet = new HashSet<>();
		requestsQueue =  new PriorityQueue<Request>(ApplicationConstants.TOT_NODES,new Comparator<Request>() {

			@Override
			public int compare(Request o1, Request o2) {
				if(o1.getClock() < o2.getClock()) {
					return -1;
				}
				else if(o1.getClock() == o2.getClock()){
					if(o1.getNodeID() < o2.getNodeID()) {
						return -1;
					} else {
						return 1;
					}
				}
				else {
					return 1;
				}
			}
		});
		
		
	}
	
	public synchronized void processRequestMessage(Message requestMessage) {
		System.out.println("Node Id: " + this.node.getNodeId() + " received REQUEST Message from " + requestMessage);
		
		Node sender = requestMessage.getSourceNode();
		Request receivedRequest = new Request(sender.getNodeId(), requestMessage.getClock());
		
		requestsQueue.add(receivedRequest);
		
		System.out.println("Node ID: " + node.getNodeId() + " priority queue "  + requestsQueue + " received response set " + receivedResponsesSet);
		if (node.getCsStatus() == CSStatus.CSREQUESTED && requestMessage.getClock() > this.requestMessage.getClock()) {
			receivedResponsesSet.add(requestMessage.getSourceNode().getNodeId());
		}
		
		this.node.incrementClock(requestMessage.getClock());
		ReplyMessage reply = new ReplyMessage(this.node, this.node.getClock());
		node.sendMessage(requestMessage.getSourceNode(), reply);
	}
	
	private synchronized boolean isResponseReceivedFromAllProcess(){
		return receivedResponsesSet.size() == node.getNeighbours().size();
	}
	
	public synchronized void processReplyMessage(Message replyMessage) {
		
		System.out.println("Node Id: " + node.getNodeId() + " received REPLY Message from " + replyMessage + " received response set " + receivedResponsesSet);
		Node sender = replyMessage.getSourceNode();
		if (node.getCsStatus() == CSStatus.CSREQUESTED && replyMessage.getClock() > this.requestMessage.getClock()) {
			receivedResponsesSet.add(sender.getNodeId());
		}
	}
	
	public synchronized void processReleaseMessage(Message releaseMessage) {
		
		
		if (requestsQueue.peek().getNodeID() == releaseMessage.getSourceNode().getNodeId()) {
			requestsQueue.poll();
		} else {
			Request reqServed = null;
			for (Request request : requestsQueue) {
				if (request.getNodeID() == releaseMessage.getSourceNode().getNodeId()) {
					reqServed=request;
					break;
				}
			}
			requestsQueue.remove(reqServed);
		}
		System.out.println("Node Id: " + node.getNodeId() + " received RELEASE Message from " + releaseMessage+ "priority queue "+requestsQueue + " received response set " + receivedResponsesSet);
		if (node.getCsStatus() == CSStatus.CSREQUESTED && releaseMessage.getClock() > this.requestMessage.getClock()) {
			receivedResponsesSet.add(releaseMessage.getSourceNode().getNodeId());
		}
        System.out.println(" cs status "+node.getCsStatus()+ " size of hashset >> "+receivedResponsesSet.size());
	}
	
	@Override
	public void csEnter() {

		synchronized (node.getCsStatus()) {
			node.setCsStatus(CSStatus.CSREQUESTED);
			receivedResponsesSet.clear();
			
			requestMadeToCSTime = node.getClock();

			requestMessage = new RequestMessage(node, requestMadeToCSTime);
			request = new Request(node.getNodeId(), requestMadeToCSTime);
			requestsQueue.add(request);
			node.broadcastRequest(requestMessage);
		}
		
		System.out.println("Node Id: " + node.getNodeId() + " making a REQUEST: " + (Message)requestMessage);
        System.out.println("response received "+receivedResponsesSet.size()+" peek elem "+requestsQueue.peek().getNodeID());
		
        while (!isResponseReceivedFromAllProcess() || !request.equals(requestsQueue.peek())) {
			try {
				//System.out.println("status "+isResponseReceivedFromAllProcess()+" for node "+node.getNodeId());
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
        System.out.println("about to cs "+isResponseReceivedFromAllProcess()+" peek Id "+requestsQueue.peek().getNodeID());
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
			
			releaseMessage =new ReleaseMessage(node,node.getClock());
			
			requestsQueue.poll();
			System.out.println("Node Id: " + node.getNodeId() + " broadcasting RELEASE messages: " + (Message)releaseMessage+" priority "+requestsQueue);
			node.broadcastReleaseMessages(releaseMessage);
			requestMessage = null;
		}}

}
