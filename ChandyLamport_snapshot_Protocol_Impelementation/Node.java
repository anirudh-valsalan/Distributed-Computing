import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author anirudh kuttiyil valsalan 
 * 		   praveen erode murugesan
 * 		   rahul aravind
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
	private transient Color nodeColor;
	private Node parentNode;
	// application clock
	private int[] applicationClock;

	private transient Boolean isActive;
	private transient ArrayList<ChannelState> channelStates;

	private transient Integer sentMessageCount;
	private transient HashMap<Integer, Boolean> markerReceivedMap;

	private transient GlobalState globalState;

	private transient LocalState localState;

	private transient Integer snapshotCount = 1;
	private transient Integer snapshotCountDEBUG = 1;
	

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

	BufferedWriter bw = null;
	FileWriter fw = null;

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
		return neighbours;
	}

	public void setNeighbours(List<Node> neighbours) {
		this.neighbours = neighbours;
	}

	/**
	 * Node constructor
	 * 
	 * @param nodeId
	 * @param host
	 * @param portNumber
	 */

	public Node(int nodeId, String host, int portNumber) {
		super();
		this.nodeId = nodeId;
		this.host = host;
		this.portNumber = portNumber;
		outStreamNodeIdMap = new HashMap<>();
		inputStreamNodeIdMap = new HashMap<>();
		localTerminationSet = new HashSet<>();
		isActive = (nodeId == ApplicationConstants.DEFAULT_ACTIVE_NODEID);
		channelStates = new ArrayList<>();
		this.applicationClock = new int[ApplicationConstants.Tot_Num_Nodes];
		Arrays.fill(this.applicationClock, 0);
		this.sentMessageCount = 0;
		this.nodeColor = Color.BLUE;
		this.globalState = new GlobalState();
	}

	public void resetMarkerReceivedMap() {
		// reset marker received map to false
		this.markerReceivedMap = new HashMap<>();
		for (Node neighbourNode : this.neighbours) {
			this.markerReceivedMap.put(neighbourNode.getNodeId(), false);
		}
	}
    
	public void initializeCommunication() throws IOException, ClassNotFoundException {

		new Thread(new Listen()).start();
		try {
			// main thread is sleeping for some time.
			Thread.sleep(2000);

			for (Node neighbour : Node.this.neighbours) {
				if (this.nodeId > neighbour.getNodeId()) {
					System.out.println("starting neighbour "+neighbour.getNodeId());
					startNeighbours(neighbour, this.nodeId);
				}
			}

			// System.out.println("here>>>>>>");
			while (getOutStreamNodeIdMap().size() != neighbours.size()) {

			}

			System.out.println("map is updated");
			for (Node neighbour : Node.this.neighbours) {
				new Thread(new MessageProcessingThread(neighbour)).start();
			}

			if (this.nodeId == ApplicationConstants.DEFAULT_ACTIVE_NODEID) {
				//Thread.sleep(ApplicationConstants.INITIAL_DELAY_MAP_PROTOCOL);
				new Thread(new SendMessagesThread(this)).start();
				System.out.println("SnapShot #" + snapshotCountDEBUG + " Inititating chandy lamport protocol............");
				new Thread(new ChandyLamportSnapShotThread()).start();
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

	// send application messages
	private synchronized void sendApplicationMessages() {

		Message applicationMessage;
		Integer messagePerActive;
		try {
			// when node is active retrieve random message count.
			messagePerActive = getRandomMessageCount();
			System.out.println("SnapShot #" + snapshotCountDEBUG + " Node Id: " + Node.this.nodeId + " messages per Active " + messagePerActive);
			for (int i = 0; i < messagePerActive; i++) {
				//synchronized (Node.this.applicationClock) {
					System.out.println("SnapShot #" + snapshotCountDEBUG +  " Node Id: " + Node.this.nodeId + " Sent App Msg Count: " + Node.this.sentMessageCount);
					if (Node.this.isActive && (Node.this.sentMessageCount < ApplicationConstants.MAX_MESSAGES)) {
						//System.out.println("Error " + Node.this.nodeId + " " + Arrays.toString(Node.this.applicationClock) + " " + ApplicationConstants.totNumNodes);
						Node.this.applicationClock[Node.this.nodeId]++;
						applicationMessage = new ApplicationMessage(this, Node.this.applicationClock, MessageType.APPLICATION);
						sendMessage(getRandomNeighbour(), applicationMessage);
						Node.this.sentMessageCount++;
					} else {
						System.out.println("Caught");
						break;
					}
				//}
				Thread.sleep(ApplicationConstants.MIN_SEND_DELAY);
			}
			// node will be passive after it sends all messages
			synchronized (Node.this.isActive) {
				Node.this.isActive = false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
   //Method which will be used for sending message to destination
	private void sendMessage(Node randomNeighbour, Message message) {
		ObjectOutputStream output = null;
		try {
			System.out.println("SnapShot #" + snapshotCountDEBUG +  " Node Id: " + Node.this.nodeId + " sending " 
						+ message.getMessageType() + " message to Node Id: " + randomNeighbour.getNodeId());
			output = getOutStreamNodeIdMap().get(randomNeighbour.getNodeId());
			output.writeObject(message);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			// System.out.println("writing "+nodeId);
			updateNodeIdMap(neighbour.getNodeId(), objectOutputStream, objectInputStream);
			// System.out.println("map in neighbour >>>
			// "+getOutStreamNodeIdMap().toString());

			// System.out.println("connected to neighbour" +
			// neighbour.getHost());

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

	// generate random message count
	private synchronized Integer getRandomMessageCount() {
		Random randomGenerator = new Random();
		int difference = ApplicationConstants.MAX_PER_Active - ApplicationConstants.MIN_PER_ACTIVE + 1;
		int randomNumber = randomGenerator.nextInt(difference);
		int randomMessageCount = ApplicationConstants.MIN_PER_ACTIVE + randomNumber;
		return randomMessageCount;
	}

	// generate random neighbour
	private synchronized Node getRandomNeighbour() {
		Integer randomNeighbour = new Random().nextInt(neighbours.size());
		Node neighbour = neighbours.get(randomNeighbour);
		return neighbour;
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
    /*
     * Chandy Lamport protcol starts here
     */
	private void startChandyLamportProtocol() {
		analyzeMarkerMessage(null);
	}
    /**
     * Method which will analyze the marker message
     * @param markerMessage
     */
	private void analyzeMarkerMessage(MarkerMessage markerMessage) {
		synchronized (this) {
			if(markerMessage != null) {
				System.out.println("SnapShot #" + snapshotCountDEBUG + " Node Id: " + Node.this.nodeId + " Received marker message from Node Id: " + markerMessage.getSource().getNodeId());
			}
			
			if (Node.this.nodeColor == Color.BLUE) {//if node color is blue then change color to red on receiving marker message
				Node.this.nodeColor = Color.RED;
				

				if (Node.this.localState == null) {
					Node.this.localState = new LocalState();
				}
				
				System.out.println("SnapShot #" + snapshotCountDEBUG +  " Node Id: " + Node.this.nodeId + " capturing the local state " + Arrays.toString(Node.this.applicationClock));
				
				Node.this.localState.setApplicationVectorClockState(Node.this.applicationClock);
				Node.this.localState.setActiveInd(Node.this.isActive);
				Node.this.localState.setNodeID(Node.this.nodeId);
				
				Message sendMarkerMessage = null;
				
				for (Node neighbour : Node.this.neighbours) {//send marker message to neighbors
					sendMarkerMessage = new MarkerMessage("MARKER", this, MessageType.MARKER);
					sendMessage(neighbour, sendMarkerMessage);
				}
				
				if (markerMessage != null) {
					markerReceivedMap.put(markerMessage.getSource().getNodeId(), true);//update the marker received map
					Message snapShotMessage = null;
					if (isAllMarkerReceivedFromNeighbours()) {//if only need to receive marker from one neighbor
						this.nodeColor = Color.BLUE;
						if(this.channelStates==null){
							this.channelStates=new ArrayList<>();
						}
						snapShotMessage = new SnapshotMessage(this.localState,this.channelStates, this, MessageType.SNAPSHOT);
						sendMessage(this.parentNode, snapShotMessage);//converge cast for snapshot message
						resetNode();
					}
				}
			} else {
				System.out.println("SnapShot #" + snapshotCountDEBUG + " Node Id: " + Node.this.nodeId + " color " + Node.this.nodeColor);
				markerReceivedMap.put(markerMessage.getSource().getNodeId(), true);
				if (isAllMarkerReceivedFromNeighbours() && this.nodeColor != Color.BLUE) {
					this.nodeColor = Color.BLUE;
					System.out.println("SnapShot #" + snapshotCountDEBUG + " Node Id: " + Node.this.nodeId + " received Marker from all the neighbors and turned " + Node.this.nodeColor);
					SnapshotMessage snapshotMessage = null;
					if (Node.this.nodeId != ApplicationConstants.DEFAULT_ACTIVE_NODEID) {
						snapshotMessage = new SnapshotMessage(this.localState, channelStates, this, MessageType.SNAPSHOT);
						sendMessage(this.parentNode, snapshotMessage);//converge cast for snapshot message
						snapshotCountDEBUG++;
						resetNode();
					}

					else {//if the initiator received marker from all neighbors then
						  //store the local state and channel state to global state
						Node.this.globalState.addLocalState(localState);
						for (ChannelState channelState : this.channelStates) {
							Node.this.globalState.addChannelState(channelState);
						}
						if (Node.this.globalState.getLocalStates().size() == ApplicationConstants.Tot_Num_Nodes) {//if the global state contain all the local state info
							ChandyLamportUtil.addGlobalStates(globalState);
							processRecordedSnapshot();  //process the recorded snapshot
						}
					}
				}

			}
		}
	}

	private void analyzeApplicationMessage(ApplicationMessage applicationMessage) {
		synchronized (this) {
			System.out.println("SnapShot #" + snapshotCountDEBUG +  " Node Id: " + Node.this.nodeId + " received application message from Node Id: " + applicationMessage.getSource().getNodeId());

			// update the application clock
			for (int pIdx = 0; pIdx < ApplicationConstants.Tot_Num_Nodes; pIdx++) {
				Node.this.applicationClock[pIdx] = Math.max(Node.this.applicationClock[pIdx],
						applicationMessage.getMessageVectorClock()[pIdx]);
			}
			
			System.out.println("SnapShot #" + snapshotCountDEBUG + " Node Id: " + Node.this.nodeId + " Updated Application clock " + Arrays.toString(Node.this.applicationClock));

			// record channel state for in transit messages
			if (Node.this.nodeColor == Color.RED
					&& !Node.this.markerReceivedMap.get(applicationMessage.getSource().getNodeId())) {
				if (Node.this.channelStates == null) {
					Node.this.channelStates = new ArrayList<>();
				}
				ChannelState channelState = new ChannelState(applicationMessage.getSource(), Node.this,
						Node.this.applicationClock);
				Node.this.channelStates.add(channelState);
			}

		
			
			System.out.println("SnapShot #" + snapshotCountDEBUG + " Node Id: " + Node.this.nodeId + " is Active: " + Node.this.isActive);
           
			if (!Node.this.isActive) {//node will turn passive if send message count less than max messages
				Node.this.isActive = (Node.this.sentMessageCount < ApplicationConstants.MAX_MESSAGES);
				if(Node.this.isActive){
					new Thread(new SendMessagesThread(this)).start();
				}
			}
		}
	}

	/**
	 * analyze the snapshot message
	 * @param snapshotMessage
	 */
	private void analyzeSnapshotMessage(SnapshotMessage snapshotMessage) {
		synchronized (this) {
			
			System.out.println("SnapShot #" + snapshotCountDEBUG + " Node Id: " + Node.this.nodeId + " received snapshot message from Node Id: " + snapshotMessage.getSource().getNodeId());
			if (Node.this.nodeId != ApplicationConstants.DEFAULT_ACTIVE_NODEID) {
				sendMessage(Node.this.parentNode, snapshotMessage);//converge cast operation
			} else {
				Node.this.globalState.addLocalState(snapshotMessage.getLocalState());
				for (ChannelState channelState : snapshotMessage.getChannelStates()) {
					Node.this.globalState.addChannelState(channelState);
				}

				if (Node.this.globalState.getLocalStates().size() == ApplicationConstants.Tot_Num_Nodes) {
					ChandyLamportUtil.addGlobalStates(globalState);
					processRecordedSnapshot();
				}
			}
		}
	}
	/**
	 * Method to check whether all nodes are passive
	 * @return true if all nodes are passive
	 */

	private boolean isAllNodesPassive() {
		
		for (LocalState localState : Node.this.globalState.getLocalStates()) {
			if (localState.isActiveInd()) {
				return false;
			}
		}

		return true;
	}
	/**
	 * Method which will check whether all channels are empty.
	 * @return true if all channels are empty
	 */

	private boolean isAllChannelsEmpty() {
		return Node.this.globalState.getChannelStates().size() == 0;
	}
	
	/**
	 * process the recorded snapshot to determine whether it can initiate termination or re-snapshot
	 */
	private void processRecordedSnapshot() {
		boolean applnPassiveCheck = isAllNodesPassive();
		boolean channelEmptyCheck = isAllChannelsEmpty();
		
		System.out.println("SnapShot #" + snapshotCountDEBUG + " Node Id: " + Node.this.nodeId + " passive check: " + applnPassiveCheck + " channel empty check: " + channelEmptyCheck);

		if (!applnPassiveCheck || !channelEmptyCheck) {//need to start chandy lamport again
			resetSnapshotParameters();
			new Thread(new ChandyLamportSnapShotThread()).start(); // initiate chandy lamport  protocol
		} else {
			initiateTermination(); //can initiate termination
		}
	}

	/**
	 * Method to check whether the recorded snapshot is consistent or not.
	 */
	private void checkSnapshotConsistency() {
		System.out.println("SnapShot #" + snapshotCountDEBUG +" Node Id: " + Node.this.nodeId + " Checking the consitency of the recorded snapshot");
		int snapShotNumber = 1;
		for (GlobalState globalState : ChandyLamportUtil.getGlobalStates()) {
			boolean isGlobalStateConsistent = true;
			for (int nodeIdx = 0; nodeIdx < ApplicationConstants.Tot_Num_Nodes; nodeIdx++) {
				
				int p_i_value = globalState.getLocalStateByNodeId(nodeIdx).getApplicationVectorClockState()[nodeIdx];

				for (int nodeJdx = 0; nodeJdx < ApplicationConstants.Tot_Num_Nodes; nodeJdx++) {
					if (nodeIdx != nodeJdx) {
						int p_j_value = globalState.getLocalStateByNodeId(nodeJdx)
								.getApplicationVectorClockState()[nodeIdx];
						if (p_j_value > p_i_value) {
							isGlobalStateConsistent = false;
							System.out.println("SnapShot #" + snapshotCountDEBUG + " Global state: " + snapShotNumber + " is not consistent");
							break;
						}
					}
				}
			}

			if (isGlobalStateConsistent) {
				System.out.println("SnapShot #" + snapshotCountDEBUG + " Global state: " + snapShotNumber + " is consistent");
			}
			snapShotNumber += 1;
		}
	}
	/**
	 * write the output of each snapshot to a file.
	 */
	private synchronized void recordSnapShotOutput() {
		try {
			for (GlobalState globalState : ChandyLamportUtil.globalStates) {
				for (LocalState localState : globalState.getLocalStates()) {
					System.out.println("SnapShot #" + snapshotCountDEBUG +" Node Id: " + Node.this.nodeId + " " + localState);
					String fileName = ApplicationConstants.getConfigFileName() + "-" + localState.getNodeID() + ".out";
					File file = new File(fileName);
					FileWriter fileWriter;
					if (file.exists()) {
						fileWriter = new FileWriter(file, true);
					} else {
						fileWriter = new FileWriter(file);
					}

					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					if (file.exists()) {
						bufferedWriter.write("\n");
					}

					for (Integer iThClockValue : localState.getApplicationVectorClockState()) {
						bufferedWriter.write(iThClockValue + " ");
					}
					
					bufferedWriter.flush();
					bufferedWriter.close();
					fileWriter.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Initiate termination
	 */
	private void initiateTermination() {
		// send finish messages to all the neighbors if the given node id is not the default active node Id
		for (Node node : Node.this.neighbours) {
			if (node.getNodeId() != ApplicationConstants.DEFAULT_ACTIVE_NODEID) {
				FinishMessage finishMessage = new FinishMessage("FINISH", Node.this, MessageType.FINISH);
				sendMessage(node, finishMessage);
			}
		}
        // if the node id is default active node id then check consistency and record snapshot output
		if (this.nodeId == ApplicationConstants.DEFAULT_ACTIVE_NODEID) {
			System.out.println("Node Id: " + Node.this.nodeId + " recording snapshot output to files");
			recordSnapShotOutput();
			checkSnapshotConsistency();
		}

		System.out.println("SnapShot #" + snapshotCountDEBUG + " Node Id: " + Node.this.nodeId + " terminating.......");
		System.out.println("SnapShot #" + snapshotCountDEBUG + " Chandy lamport protocol have executed " + Node.this.snapshotCount + " times");
		System.exit(0);
	}
	
	/**
	 * Method which will reset the snapshot parameters
	 */
	private void resetSnapshotParameters() {
		Node.this.localState = new LocalState();
		Node.this.globalState = new GlobalState();
		Node.this.channelStates = new ArrayList<>();
		Node.this.nodeColor = Color.BLUE;
		resetMarkerReceivedMap();
		System.out.println("SnapShot #" + snapshotCount + " Re-Initiating the snap shot protocol: Snapshot Count: " + snapshotCount++);
		snapshotCountDEBUG++;
	}
    /**
     * Method to reset the node.
     */
	private void resetNode() {
		this.localState = new LocalState();
		Node.this.globalState = new GlobalState();
		this.channelStates = new ArrayList<>();
		Node.this.nodeColor = Color.BLUE;
		resetMarkerReceivedMap();
	}
	/**
	 * Check if marker is received from all the neighbors.
	 * @return true if marker is received from all neighbors.
	 */
	private boolean isAllMarkerReceivedFromNeighbours() {
		for (Integer nodeId : markerReceivedMap.keySet()) {
			if (!markerReceivedMap.get(nodeId)) {
				return false;
			}
		}
		return true;
	}
   
	private void analyzeFinishMessage() {
		synchronized (this) {
			initiateTermination();
		}
	}
 
	/**
	 * chandy Lamport Thread Class
	 *
	 */
	class ChandyLamportSnapShotThread implements Runnable {

		public void run() {

			try {
				Thread.sleep(ApplicationConstants.SNAPSHOT_DELAY);
				startChandyLamportProtocol();//invoke chandy lamport protocol
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	/**
	 * 
	 * Thread which will send the application messages
	 *
	 */
	class SendMessagesThread implements Runnable{

		Node node;
		public SendMessagesThread(Node mainObj){
			this.node = mainObj;
		}
		public void run(){
			try {
				node.sendApplicationMessages();//send application messages
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Message processing Thread which will process the received message from neighbor node socket
	 *
	 */
	
	class MessageProcessingThread implements Runnable {

		Node neighbor;

		public MessageProcessingThread(Node neighbor) {
			this.neighbor = neighbor;
		}

		public void run() {
			try {
				Thread.sleep(1000);
				while (true) {

					ObjectInputStream objIs = getInputStreamNodeIdMap().get(this.neighbor.getNodeId());
					Message message = (Message) objIs.readObject();
 
					if (message instanceof MarkerMessage) {//the received message is of type marker message
						MarkerMessage markerMessage = (MarkerMessage) message;
						analyzeMarkerMessage(markerMessage);
					} else if (message instanceof ApplicationMessage) {//the received message is of type application message
						ApplicationMessage applicationMessage = (ApplicationMessage) message;
						analyzeApplicationMessage(applicationMessage);
					} else if (message instanceof SnapshotMessage) {//the received message is of type snapshot message
						SnapshotMessage snapshotMessage = (SnapshotMessage) message;
						analyzeSnapshotMessage(snapshotMessage);
					} else if (message instanceof FinishMessage) {//the received message is of type finish message
						analyzeFinishMessage();
					}

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

}
