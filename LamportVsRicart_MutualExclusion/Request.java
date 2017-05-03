
public class Request {
	private int nodeID;
	private int clock;
	private Node node;
	
	Request(int nodeID, int clock) {
		this.nodeID = nodeID;
		this.clock = clock;
	}
	
	Request(int nodeID, int clock, Node node) {
		this.nodeID = nodeID;
		this.clock = clock;
		this.node = node;
	}

	/**
	 * @return the nodeID
	 */
	public int getNodeID() {
		return nodeID;
	}

	/**
	 * @return the clock
	 */
	public int getClock() {
		return clock;
	}
	
	public Node getNode() {
		return node;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String s = " Clock: " + clock + " Node ID : " + nodeID; 
		return s;
	}
}
