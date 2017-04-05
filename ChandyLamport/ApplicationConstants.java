

/**
 * @author anirudh kuttiyil valsalan 
 * 		   praveen erode murugesan
 * 		   rahul aravind
 */
public class ApplicationConstants {

	 
	//total number of nodes in the config file
	public static Integer Tot_Num_Nodes;
	//minimum number of active messages
	public static Integer MIN_PER_ACTIVE;
	//maximum number of active messages
	public static Integer MAX_PER_Active;
	//minimum value for send delay
	public static Integer MIN_SEND_DELAY;
	//snap shot delay
	public static Integer SNAPSHOT_DELAY ;
	//maximum number of messages
	public static Integer MAX_MESSAGES ;
	//default node which is active
	public static Integer DEFAULT_ACTIVE_NODEID=0;
	//initial delay of MAP protocol
	public static Integer INITIAL_DELAY_MAP_PROTOCOL=3000;
	//config file name
	public static String configFileName;
	
	/**
	 * @return the totNumNodes
	 */
	public static Integer getTotNumNodes() {
		return Tot_Num_Nodes;
	}
	/**
	 * @param totNumNodes the totNumNodes to set
	 */
	public static void setTotNumNodes(Integer totNumNodes) {
		ApplicationConstants.Tot_Num_Nodes = totNumNodes;
	}
	/**
	 * @return the minPerActive
	 */
	public static Integer getMinPerActive() {
		return MIN_PER_ACTIVE;
	}
	/**
	 * @param minPerActive the minPerActive to set
	 */
	public static void setMinPerActive(Integer minPerActive) {
		ApplicationConstants.MIN_PER_ACTIVE = minPerActive;
	}
	/**
	 * @return the maxPerActive
	 */
	public static Integer getMaxPerActive() {
		return MAX_PER_Active;
	}
	/**
	 * @param maxPerActive the maxPerActive to set
	 */
	public static void setMaxPerActive(Integer maxPerActive) {
		ApplicationConstants.MAX_PER_Active = maxPerActive;
	}
	/**
	 * @return the minSendDelay
	 */
	public static Integer getMinSendDelay() {
		return MIN_SEND_DELAY;
	}
	/**
	 * @param minSendDelay the minSendDelay to set
	 */
	public static void setMinSendDelay(Integer minSendDelay) {
		ApplicationConstants.MIN_SEND_DELAY = minSendDelay;
	}
	/**
	 * @return the snapShotDelay
	 */
	public static Integer getSnapShotDelay() {
		return SNAPSHOT_DELAY;
	}
	/**
	 * @param snapShotDelay the snapShotDelay to set
	 */
	public static void setSnapShotDelay(Integer snapShotDelay) {
		ApplicationConstants.SNAPSHOT_DELAY = snapShotDelay;
	}
	/**
	 * @return the maxMessages
	 */
	public static Integer getMaxMessages() {
		return MAX_MESSAGES;
	}
	/**
	 * @param maxMessages the maxMessages to set
	 */
	public static void setMaxMessages(Integer maxMessages) {
		ApplicationConstants.MAX_MESSAGES = maxMessages;
	}
	
	/**
	 * @return the configFileName
	 */
	public static String getConfigFileName() {
		return configFileName;
	}
	
	/**
	 * @param configFileName
	 */
	public static void setConfigFileName(String configFileName) {
		ApplicationConstants.configFileName = configFileName;
	}
	
	

}
