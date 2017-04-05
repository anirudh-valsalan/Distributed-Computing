import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author anirudh kuttiyil valsalan
 * 		   praveen erode murugesan
 * 		   rahul aravind
 * 
 * Snapshot message class
 */

public class SnapshotMessage extends Message implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
		private LocalState localState;
	    private ArrayList<ChannelState> channelStates;
	    private  Node source;

	    public SnapshotMessage() {
	        super();
	        this.localState = new LocalState();
	        this.channelStates = new ArrayList<>();
	    }

	    public SnapshotMessage(LocalState applicationState, ArrayList<ChannelState> channelStates, Node sourceNode, MessageType messageType) {
	        super(messageType);
	    	this.localState = applicationState;
	        this.channelStates = channelStates;
	        this.source=sourceNode;
	    }

	    /**
		 * @return the localState
		 */
		public LocalState getLocalState() {
			return localState;
		}

		/**
		 * @param localState the localState to set
		 */
		public void setLocalState(LocalState localState) {
			this.localState = localState;
		}

		/**
		 * @return the channelStates
		 */
		public ArrayList<ChannelState> getChannelStates() {
			return channelStates;
		}

		/**
		 * @param channelStates the channelStates to set
		 */
		public void setChannelStates(ArrayList<ChannelState> channelStates) {
			this.channelStates = channelStates;
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

		
}
