import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Node sourceNode;

	protected int clock;

	public Message(Node sourceNode, int clock) {
		this.sourceNode = sourceNode;
		this.clock = clock;
	}

	public Node getSourceNode() {
		return sourceNode;
	}

	public int getClock() {
		return clock;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		String s = "Node Id: " + sourceNode.getNodeId() + " clock: " + clock;
		return s;
	}
}
