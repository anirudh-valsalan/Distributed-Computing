import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Node implements Serializable {
	
	/**
	 * @author anirudh Kuttiyil valsalan
	 * NETID:axk153230
	 */

	private static final long serialVersionUID = 1L;
	//map to store nodeId Object output stream map
	private Map<Integer, ObjectOutputStream> outStreamNodeIdMap;
	//map to store nodeId object input stream map
	private Map<Integer, ObjectInputStream> inputStreamNodeIdMap;
    //variable which determine whether node is sending information for the first time.
	private boolean beginning = true;
	//current node port number
	private int portNumber;
	//current node node id
	private int nodeId;
	//current node host value
	private String host;
	//current node neighbours
	private List<Node> neighbours;
	//current round value of node
	private Integer currentRound;
	//neighbour node set
	private Set<Integer> neighboursNodeIdSet;
	//round number message list map
	private Map<Integer, List<Message>> roundNumberMessageListMap;
    //node id hop count map
	private Map<Integer, Integer> nodeIdHopCountMap;
	//nodeId local termination status map
	
	private Set<Integer> localTerminationSet;
	/**
	 * @return the localTerminationSet
	 */
	public synchronized Set<Integer> getLocalTerminationSet() {
		return localTerminationSet;
	}
	/**
	 * @param localTerminationSet the localTerminationSet to set
	 */
	public void setLocalTerminationSet(Set<Integer> localTerminationSet) {
		this.localTerminationSet = localTerminationSet;
	}

	BufferedWriter bw = null;
	FileWriter fw = null;
	
	boolean isLocallyTerminated=false;
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
	 * set OutStreamNodeIdMap
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
	 * set inputStreamNodeIdMap
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
	 * set portnumber
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

	
	public Integer getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(Integer currentRound) {
		this.currentRound = currentRound;
	}

	public Set<Integer> getNeighboursNodeIdSet() {
		return neighboursNodeIdSet;
	}

	public void setNeighboursNodeIdSet(Set<Integer> neighboursNodeIdSet) {
		this.neighboursNodeIdSet = neighboursNodeIdSet;
	}

	public Map<Integer, List<Message>> getMap() {
		return roundNumberMessageListMap;
	}

	public void setMap(Map<Integer, List<Message>> map) {
		this.roundNumberMessageListMap = map;
	}

	/**
	 * Node constructor
	 * @param nodeId
	 * @param host
	 * @param portNumber
	 */

	public Node(int nodeId, String host, int portNumber) {
		super();
		this.nodeId = nodeId;
		this.host = host;
		this.portNumber = portNumber;
		neighboursNodeIdSet = new HashSet<>();
		this.currentRound = 1;
		roundNumberMessageListMap = new ConcurrentHashMap<>();
		outStreamNodeIdMap = new HashMap<>();
		inputStreamNodeIdMap = new HashMap<>();
		nodeIdHopCountMap = new HashMap<>();
		localTerminationSet=new HashSet<>();

	}

	/**
	 * Method which will prepopulate the node id hop value map with known hop number values.
	 */
	private void prepopulatingMap() {
		nodeIdHopCountMap.put(this.nodeId, 0);
		for (Node neighbour : this.neighbours) {
			nodeIdHopCountMap.put(neighbour.getNodeId(), 1);
		}

	}

	public void initializeCommunication() throws IOException, ClassNotFoundException {
		prepopulatingMap();

		new Thread(new Listen()).start();
		try {
			// main thread is sleeping for some time.
			Thread.sleep(2000);
			

			for (Node neighbour : Node.this.neighbours) {
			//	System.out.println("outside if"+neighbour.getNodeId());
				if (this.nodeId > neighbour.getNodeId()) {
					//System.out.println("starting neighbour "+neighbour.getNodeId());
					startNeighbours(neighbour, this.nodeId);
				}
			}

			//System.out.println("here>>>>>>");
			while (getOutStreamNodeIdMap().size() != neighbours.size()) {
				
				

			}

			System.out.println("map is updated");

			for (Node neighbour : Node.this.neighbours) {
				new Thread(new RoundingThread(neighbour)).start();
			}

			
			while (true) {
				// writing round1 information
				if (beginning == true) {
					// populating neighboursNodeIdSet
					neighboursNodeIdSet = new HashSet<>();
					neighboursNodeIdSet.add(this.nodeId);
					for (Node neighbour : Node.this.neighbours) {
						neighboursNodeIdSet.add(neighbour.getNodeId());
					}
					// adding round1 information for each of my neighbour.
					for (Node neighbour : Node.this.neighbours) {

						ObjectOutputStream out = outStreamNodeIdMap.get(neighbour.getNodeId());
						Message message = new Message();
						message.setHostName(this.host);
						message.setNodeId(this.nodeId);
						message.setRoundNumber(1);
						message.setNodeSet(neighboursNodeIdSet);
						message.setNodeSet(this.neighboursNodeIdSet);
						this.currentRound = 1;
						out.writeObject(message);
					}
					beginning = false;
				} else  if(!isLocallyTerminated){
					if (roundNumberMessageListMap.get(this.currentRound) != null) {
						if (roundNumberMessageListMap.get(this.currentRound).size() == this.neighbours.size()) {
						/*	System.out.println("recived round " + this.currentRound + "message from all neighbours");*/

							List<Message> messagesList = roundNumberMessageListMap.get(this.currentRound);
							Set<Integer> newNodeSet = new HashSet<>();
							for (Message message : messagesList) {

								Set<Integer> nodeSet = message.getNodeSet();

								for (Integer nodeId : nodeSet) {
									if (nodeIdHopCountMap.get(nodeId) == null) {
										newNodeSet.add(nodeId);
										nodeIdHopCountMap.put(nodeId, this.currentRound + 1);
									}
								}

							}
							// no new information is gatherd from neighbour
							this.currentRound = this.currentRound + 1;
							if (newNodeSet.isEmpty()) {

								System.out.println("No changes found after " + (this.currentRound - 1));
								System.out.println("can locally initiate termination");
								fw = new FileWriter("Node" + this.nodeId + "log");
								bw = new BufferedWriter(fw);
								bw.write("The final nodeid hop count pair for node id " + this.nodeId + ">>> "
										+ nodeIdHopCountMap.toString());
								bw.flush();
								System.out.println(nodeIdHopCountMap.toString());
								for (Node neighbour : this.neighbours) {
									ObjectOutputStream out = outStreamNodeIdMap.get(neighbour.getNodeId());
									Message message = new Message();
									message.setHostName(this.host);
									message.setNodeId(this.nodeId);
									message.setRoundNumber(this.currentRound);
									message.setNodeSet(newNodeSet);
									message.setStatus("FINISHED");
									out.writeObject(message);

								}
								isLocallyTerminated=true;
							
							} else {

								for (Node neighbour : this.neighbours) {
									ObjectOutputStream out = outStreamNodeIdMap.get(neighbour.getNodeId());
									Message message = new Message();
									message.setHostName(this.host);
									message.setNodeId(this.nodeId);
									message.setRoundNumber(this.currentRound);
									message.setNodeSet(newNodeSet);
									out.writeObject(message);

								}
							}

						} else {
							//System.out.println("still in round " + this.currentRound);
						}

					}

				}
				else if(localTerminationSet.size()==this.neighbours.size()){
					System.out.println("can globally terminate the connection");
					System.exit(0);
					
				}

			}
			

		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		finally{
			try{
			if(bw!=null){
				bw.close();
			}
			if(fw!=null){
				fw.close();
			}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * @param nodeId
	 * Method to update local termination set
	 */
	
	public synchronized void updateLocalTerminationSet(Integer nodeId){
		Node.this.localTerminationSet.add(nodeId);
		
	}
	/**
	 * 
	 * @param message
	 * method to update the round number message list map.
	 */

	public synchronized void updateMap(Message message) {

		if (Node.this.roundNumberMessageListMap.get(message.getRoundNumber()) != null) {
			List<Message> messageList = Node.this.roundNumberMessageListMap.get(message.getRoundNumber());
			messageList.add(message);
			Node.this.roundNumberMessageListMap.put(message.getRoundNumber(), messageList);
		} else {
			List<Message> messageList = new ArrayList<>();
			messageList.add(message);
			Node.this.roundNumberMessageListMap.put(message.getRoundNumber(), messageList);
		}
	}
	/**
	 * @param neighbour
	 * @param nodeId
	 * 
	 * Method which will retrieve the object outputstream and inputstream and update the corresponding maps.
	 */
	public  void startNeighbours(Node neighbour, Integer nodeId) {

		//System.out.println("inside neighbours");

		try {

			Socket clientSocket = new Socket(neighbour.getHost(), neighbour.portNumber);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			objectOutputStream.write(nodeId);
			objectOutputStream.flush();
            //System.out.println("writing "+nodeId);
			updateNodeIdMap(neighbour.getNodeId(), objectOutputStream, objectInputStream);
			//System.out.println("map in neighbour >>> "+getOutStreamNodeIdMap().toString());
			
			//System.out.println("connected to neighbour" + neighbour.getHost());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

  /**
   * 
   * @param nodeId : node id input
   * @param objectOutputStream : object stream output
   * @param objectInputStream : object stream input
   * 
   * Method to update the output stream map and input stream map for each input node id.
   * 
   */
	public synchronized void updateNodeIdMap(Integer nodeId, ObjectOutputStream objectOutputStream,
			ObjectInputStream objectInputStream) {

		getOutStreamNodeIdMap().put(nodeId, objectOutputStream);
		getInputStreamNodeIdMap().put(nodeId, objectInputStream);

	}
	
	//The main Listening Thread

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
					/*System.out.println("waiting for connection");*/
					Socket socket = serverSocket.accept();
					objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					objectInputStream = new ObjectInputStream(socket.getInputStream());
					int remNodeId = objectInputStream.read();
					//System.out.println("getting "+remNodeId);
					updateNodeIdMap(remNodeId, objectOutputStream, objectInputStream);
				//	System.out.println("map in listen>>> "+getOutStreamNodeIdMap().toString());

				}
			} catch (Exception ex) {
				/*System.out.println("inside catch");*/
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
 
	//The main application thread which is running for each neighbour
	class RoundingThread implements Runnable {
       
		Node neighbor;

		public RoundingThread(Node neighbor) {
			this.neighbor = neighbor;
		}

		public void run() {
			try {
				Thread.sleep(1000);
				while (true) {

					ObjectInputStream objIs = getInputStreamNodeIdMap().get(this.neighbor.getNodeId());
					Message message = (Message) objIs.readObject();
					updateMap(message);
					
					if(message.getStatus()!=null&&message.getStatus().equals("FINISHED")){
						updateLocalTerminationSet(message.getNodeId());
						
					}

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

}
