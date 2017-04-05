import java.io.Serializable;

/**
 * @author anirudh kuttiyil valsalan 
 * 		   praveen erode murugesan
 * 		   rahul aravind
 * 
 * Class to store channel state for Chandy Lamport Protocol
 */
public class ChannelState implements Serializable {
  
	private static final long serialVersionUID = 1L;
	private Node source;
    private Node destination;
    private int[] channelClock;

    public ChannelState() {
       this.channelClock = new int[ApplicationConstants.Tot_Num_Nodes];
    }

    public ChannelState(Node sourceNode, Node destinationNode, int[] channelClock) {
        this.channelClock = new int[ApplicationConstants.Tot_Num_Nodes];
        this.source=sourceNode;
        this.destination=destinationNode;
        System.arraycopy(channelClock, 0, this.channelClock, 0, channelClock.length);
    }

	/**
	 * @return the source
	 */
	public Node getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Node source) {
		this.source = source;
	}

	/**
	 * @return the destination
	 */
	public Node getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(Node destination) {
		this.destination = destination;
	}

   
}
