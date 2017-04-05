import java.io.Serializable;

/**
 * @author anirudh kuttiyil valsalan 
 * 		   praveen erode murugesan
 * 		   rahul aravind
 */
public class ApplicationMessage extends Message implements Serializable {
 
	
	private static final long serialVersionUID = 1L;
	private int[] messageVectorClock;
	private Node source;

	public ApplicationMessage() {
		super();
		this.messageVectorClock = new int[ApplicationConstants.Tot_Num_Nodes];
	}

	public ApplicationMessage(Node source, int[] messageVectorClock, MessageType messageType) {
		super(messageType);
		this.messageVectorClock = new int[ApplicationConstants.Tot_Num_Nodes];
		System.arraycopy(messageVectorClock, 0, this.messageVectorClock, 0, messageVectorClock.length);
		this.source = source;

	}

	/**
	 * @return the source
	 */
	public Node getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(Node source) {
		this.source = source;
	}

	/**
	 * 
	 * @return messageVectorClock
	 */
	public int[] getMessageVectorClock() {
		return messageVectorClock;
	}
}
