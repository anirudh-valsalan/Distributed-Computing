
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author anirudh kuttiyil valsalan praveen erode murugesan rahul aravind
 */
public class Node implements Serializable {

	/**
	 * @author anirudh Kuttiyil valsalan NETID:axk153230
	 */

	private static final long serialVersionUID = 1L;

	// map to store nodeId Object output stream map
	private transient Map<Integer, ObjectOutputStream> outStreamNodeIdMap;

	// map to store nodeId object input stream map
	private transient Map<Integer, ObjectInputStream> inputStreamNodeIdMap;

	// current node port number
	private int portNumber;

	// current node node id
	private int nodeId;

	// current node host value
	private String host;

	// current node neighbours
	private List<Node> neighbours;

	private Node parentNode;

	private transient CSStatus csStatus;

	private transient Integer clock;

	private transient CriticalSectionService criticalSectionService;

	public Boolean initiateTermination;

	BufferedWriter bw = null;
	FileWriter fw = null;

	private transient HashSet<Integer> terminationReceivedSet;

	public static Integer csExecutionCount = 0;

	/**
	 * @return the parentNode
	 */
	public Node getParentNode() {
		return parentNode;
	}

	/**
	 * @param parentNode
	 *            the parentNode to set
	 */
	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	private Set<Integer> localTerminationSet;

	/**
	 * @return the localTerminationSet
	 */
	public synchronized Set<Integer> getLocalTerminationSet() {
		return localTerminationSet;
	}

	/**
	 * @param localTerminationSet
	 *            the localTerminationSet to set
	 */
	public void setLocalTerminationSet(Set<Integer> localTerminationSet) {
		this.localTerminationSet = localTerminationSet;
	}

	public Integer getClock() {
		return clock;
	}
	
	public void incrementClock(Integer clock) {
		this.clock = Math.max(this.clock, clock) + 1;
	}

	boolean isLocallyTerminated = false;

	/**
	 * 
	 * @return outStreamNodeIdMap
	 */
	public synchronized Map<Integer, ObjectOutputStream> getOutStreamNodeIdMap() {
		return outStreamNodeIdMap;
	}

	/**
	 * 
	 * @param outStreamNodeIdMap
	 *            set OutStreamNodeIdMap
	 */

	public void setOutStreamNodeIdMap(Map<Integer, ObjectOutputStream> outStreamNodeIdMap) {
		this.outStreamNodeIdMap = outStreamNodeIdMap;
	}

	/**
	 * 
	 * @return inputStreamNodeIdMap
	 */

	public synchronized Map<Integer, ObjectInputStream> getInputStreamNodeIdMap() {
		return inputStreamNodeIdMap;
	}

	/**
	 * 
	 * @param inputStreamNodeIdMap
	 *            set inputStreamNodeIdMap
	 */
	public void setInputStreamNodeIdMap(Map<Integer, ObjectInputStream> inputStreamNodeIdMap) {
		this.inputStreamNodeIdMap = inputStreamNodeIdMap;
	}

	/**
	 * 
	 * @return port number
	 */
	public int getPortNumber() {
		return portNumber;
	}

	/**
	 * 
	 * @param portNumber
	 *            set portnumber
	 */

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public List<Node> getNeighbours() {
		return this.neighbours;
	}

	public void setNeighbours(List<Node> neighbours) {
		this.neighbours = neighbours;
	}

	/**
	 * @return the csStatus
	 */
	public CSStatus getCsStatus() {
		return csStatus;
	}

	/**
	 * @param csStatus
	 *            the csStatus to set
	 */
	public void setCsStatus(CSStatus csStatus) {
		this.csStatus = csStatus;
	}

	/**
	 * Node constructor
	 * 
	 * @param nodeId
	 * @param host
	 * @param portNumber
	 */

	public Node(int nodeId, String host, int portNumber, int protocol) {
		super();
		this.nodeId = nodeId;
		this.host = host;
		this.portNumber = portNumber;
		outStreamNodeIdMap = new HashMap<>();
		inputStreamNodeIdMap = new HashMap<>();
		localTerminationSet = new HashSet<>();
		csStatus = CSStatus.CSOUT;
		clock = new Integer(0);
		terminationReceivedSet = new HashSet<>();

		if (protocol == ApplicationConstants.LAMPORT_IND) {
			criticalSectionService = new LamportServiceImpl(this);
		} else if (protocol == ApplicationConstants.RA_IND) {
			criticalSectionService = new RicartAgarwalaServiceImpl(this);
		}
		initiateTermination = false;
	}

	public void sendMessage(Node randomNeighbour, Message message) {
		ObjectOutputStream output = null;
		try {
			output = getOutStreamNodeIdMap().get(randomNeighbour.getNodeId());
			output.writeObject(message);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initializeCommunication() throws IOException, ClassNotFoundException {

		new Thread(new Listen()).start();
		try {
			// main thread is sleeping for some time.
			Thread.sleep(2000);

			for (Node neighbour : Node.this.neighbours) {
				if (this.nodeId > neighbour.getNodeId()) {
					System.out.println("starting neighbour " + neighbour.getNodeId());
					startNeighbours(neighbour, this.nodeId);
				}
			}

			// System.out.println("here>>>>>>");
			while (getOutStreamNodeIdMap().size() != neighbours.size()) {
				// wait till all the node's neighbors are up and running.
			}

			System.out.println("map is updated");

			new Thread(new DistributedMutexThread(this)).start();
			for (Node neighbour : Node.this.neighbours) {
				new Thread(new MessageProcessingThread(neighbour, this)).start();
			}

		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void csExecute() {

		System.out.println("Node Id: " + this.nodeId + " is in CS Execute method");
		PrintWriter fw;
		try {
			// increment mutual exclusion clock
			/*
			 * synchronized (Node.this.getMutualExclusionClock()) {
			 * currentNode.getMutualExclusionClock()[currentNode.getNodeID()]++;
			 * }
			 */

			String extendedFileName = "";

			if (criticalSectionService instanceof LamportServiceImpl) {
				extendedFileName = "lamport";
			} else {
				extendedFileName = "ricart";
			}

			String outputFile = ApplicationConstants.executionFile + "_" + extendedFileName;

			fw = new PrintWriter(new FileWriter(outputFile, true));
			fw.println("CS Enter by Node " + this.nodeId);
			fw.flush();
			fw.close();

			/* CS Execution time */
			Long delay = generateExponentialRandomDelay(ApplicationConstants.CSEXECUTION_TIME);
			System.out.println(
					"Node Id: " + this.nodeId + " executing critical section for " + delay + " milliseconds...");
			Thread.sleep(delay);

			fw = new PrintWriter(new FileWriter(outputFile, true));
			fw.println("CS Exit by Node " + this.nodeId);
			fw.flush();
			fw.close();
			// increment mutual exclusion clock
			/*
			 * synchronized (currentNode.getMutualExclusionClock()) {
			 * currentNode.getMutualExclusionClock()[currentNode.getNodeID()]++;
			 * }
			 */

			System.out.println("Node Id: " + this.nodeId + " is going to leave CS Execute method");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Long generateExponentialRandomDelay(int maxDelayLimit) {
		return Math.round(-maxDelayLimit * Math.log(1 - Math.random()));
	}

	/**
	 * @param neighbour
	 * @param nodeId
	 * 
	 *            Method which will retrieve the object outputstream and
	 *            inputstream and update the corresponding maps.
	 */

	public void startNeighbours(Node neighbour, Integer nodeId) {

		// System.out.println("inside neighbours");

		try {

			Socket clientSocket = new Socket(neighbour.getHost(), neighbour.portNumber);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			objectOutputStream.write(nodeId);
			objectOutputStream.flush();
			updateNodeIdMap(neighbour.getNodeId(), objectOutputStream, objectInputStream);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 
	 * @param nodeId
	 *            : node id input
	 * @param objectOutputStream
	 *            : object stream output
	 * @param objectInputStream
	 *            : object stream input
	 * 
	 *            Method to update the output stream map and input stream map
	 *            for each input node id.
	 * 
	 */
	public synchronized void updateNodeIdMap(Integer nodeId, ObjectOutputStream objectOutputStream,
			ObjectInputStream objectInputStream) {

		getOutStreamNodeIdMap().put(nodeId, objectOutputStream);
		getInputStreamNodeIdMap().put(nodeId, objectInputStream);

	}

	// The main Listening Thread

	class Listen implements Runnable {

		private ServerSocket serverSocket;

		public Listen() {

		}

		@Override
		public void run() {
			try {
				System.out.print("The server " + getHost() + " started " + "at port number " + getPortNumber());
				serverSocket = new ServerSocket(getPortNumber());
				ObjectInputStream objectInputStream = null;
				ObjectOutputStream objectOutputStream = null;
				while (true) {
					Socket socket = serverSocket.accept();
					objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					objectInputStream = new ObjectInputStream(socket.getInputStream());
					int remNodeId = objectInputStream.read();
					updateNodeIdMap(remNodeId, objectOutputStream, objectInputStream);

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}

	}

	/**
	 * Distributed Mutex Thread Class
	 *
	 */
	class DistributedMutexThread implements Runnable {
		Node node;

		public DistributedMutexThread(Node mainObj) {
			this.node = mainObj;
		}

		public void run() {

			
			try {
                               int numberOfRequests = 0;

				synchronized (this) {
					
					while (numberOfRequests < ApplicationConstants.NUMBER_OF_REQUEST) {
						criticalSectionService.csEnter();
						System.out.println("The count is >>" + numberOfRequests+" max" +ApplicationConstants.NUMBER_OF_REQUEST);
						csExecute();
						criticalSectionService.csLeave();
						if (numberOfRequests < ApplicationConstants.NUMBER_OF_REQUEST - 1) {
							Long delay = generateExponentialRandomDelay(ApplicationConstants.INTER_REQUEST_DELAY);
							System.out.println("Sleeping before the next request for " + delay + " milliseconds...");
							Thread.sleep(delay);
						}
						numberOfRequests++;

					}
					broadcastTerminationMessage();
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			

				}

	}

	public void broadcast(Message message) {

		for (Node neighborNode : this.getNeighbours()) {
			sendMessage(neighborNode, message);
		}

	}

	public synchronized void processTerminationMessage(Message message) {

		System.out.println("Node Id: " + this.nodeId + " received termination message from " + message);
		terminationReceivedSet.add(message.getSourceNode().getNodeId());
		if (initiateTermination == true && terminationReceivedSet.size() == this.neighbours.size()) {
			System.exit(0);
		}

	}

	public void sendReplytoDeferredRequest(Queue<Request> queue, Message message) {
		for (Request request : queue) {
			Node node = request.getNode();
			sendMessage(node, message);
		}
	}

	public void broadcastReleaseMessages(Message message) {
		broadcast(message);
	}

	public void broadcastTerminationMessage() {

		System.out.println("Node Id: " + this.nodeId + " broadcasting termination messages ");
		
		TerminationMessage terminationMessage = new TerminationMessage(this, clock);
		for (Node neighborNode : this.getNeighbours()) {
			sendMessage(neighborNode, terminationMessage);
		}

		initiateTermination = true;
		if (terminationReceivedSet.size() == this.neighbours.size()) {
			System.exit(0);
		}

	}

	/**
	 * Message processing Thread which will process the received message from
	 * neighbor node socket
	 *
	 */

	class MessageProcessingThread implements Runnable {

		Node neighbor;
		Node receiver;

		public MessageProcessingThread(Node neighbor, Node receiver) {
			this.neighbor = neighbor;
			this.receiver = receiver;
		}

		public void run() {
			try {
				Thread.sleep(1000);

				while (true) {

					ObjectInputStream objIs = getInputStreamNodeIdMap().get(this.neighbor.getNodeId());
					Message message = (Message) objIs.readObject();
					
					synchronized (this.receiver.clock) {
						
					
						if (message instanceof RequestMessage) {
							criticalSectionService.processRequestMessage(message);
							//this.receiver.incrementClock(message.getClock());
							//ReplyMessage reply = new ReplyMessage(this.receiver, this.receiver.clock);
							//sendMessage(message.getSourceNode(), reply);
						}
						if (message instanceof ReplyMessage) {
							criticalSectionService.processReplyMessage(message);
						}
						if (message instanceof ReleaseMessage) {
							criticalSectionService.processReleaseMessage(message);
						}
						if (message instanceof TerminationMessage) {
							processTerminationMessage(message);
						}
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	public void broadcastRequest(RequestMessage request) {
		broadcast(request);

	}

}
