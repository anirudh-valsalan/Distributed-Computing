import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author anirudh kuttiyil valsalan 
 * 		   praveen erode murugesan
 * 		   rahul aravind
 * 
 * Class which will store Global State Information
 */
public class GlobalState implements Serializable {
 
	private static final long serialVersionUID = 1L;
	private ArrayList<LocalState> localStates;
    private ArrayList<ChannelState> channelStates;

    public GlobalState() {
        localStates = new ArrayList<>();
        channelStates = new ArrayList<>();
    }

	/**
	 * @return the localStates
	 */
	public ArrayList<LocalState> getLocalStates() {
		return localStates;
	}

	/**
	 * @param localStates the localStates to set
	 */
	public void setLocalStates(ArrayList<LocalState> localStates) {
		this.localStates = localStates;
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
	
	 public LocalState getLocalStateByNodeId(Integer nodeId) {
	        for (LocalState localState : this.localStates) {
	            if (localState.getNodeID() == nodeId)
	                return localState;
	        }
	        return null;
	    }
	 
	 
	 public void addLocalState(LocalState localState) {
	        if (localStates == null)
	            localStates = new ArrayList<>();

	        localStates.add(localState);
	    }

	 
	 public void addChannelState(ChannelState channelState) {
	        if (channelStates == null)
	            channelStates = new ArrayList<>();

	        channelStates.add(channelState);
	    }

}