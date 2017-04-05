import java.io.Serializable;

/**
 * @author anirudh kuttiyil valsalan 
 * 		   praveen erode murugesan
 * 		   rahul aravind
 * 
 * Class which will store the Local State Information
 */
public class LocalState implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int[] applicationVectorClockState;
	private boolean activeInd;
	private int nodeID;

	public LocalState() {
		applicationVectorClockState = new int[ApplicationConstants.Tot_Num_Nodes];
		activeInd = false;
	}

	/**
	 * @return the applicationVectorClockState
	 */
	public int[] getApplicationVectorClockState() {
		return applicationVectorClockState;
	}

	/**
	 * @param applicationVectorClockState the applicationVectorClockState to set
	 */
	public void setApplicationVectorClockState(int[] applicationVectorClockState) {
		 System.arraycopy(applicationVectorClockState, 0, this.applicationVectorClockState, 0, applicationVectorClockState.length);
		
	}

	/**
	 * @return the activeInd
	 */
	public boolean isActiveInd() {
		return activeInd;
	}

	/**
	 * @param activeInd the activeInd to set
	 */
	public void setActiveInd(boolean activeInd) {
		this.activeInd = activeInd;
	}

	/**
	 * @return the nodeID
	 */
	public int getNodeID() {
		return nodeID;
	}

	/**
	 * @param nodeID the nodeID to set
	 */
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String s = "Local state: " + activeInd + " Node Id: " + nodeID;
		return s;
	}
	

}
