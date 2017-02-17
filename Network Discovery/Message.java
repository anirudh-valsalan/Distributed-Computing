import java.io.Serializable;
import java.util.Set;

public class Message implements Serializable {
	/**
	 * @author anirudh Kuttiyil valsalan
	 * NETID:axk153230
	 */
	private static final long serialVersionUID = 1L;
	private String hostName;
	private Integer portName;
	private Integer nodeId;
	private Integer roundNumber;
	private Set<Integer> nodeSet;
	private String status;
	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}
	/**
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	/**
	 * @return the portName
	 */
	public Integer getPortName() {
		return portName;
	}
	/**
	 * @param portName the portName to set
	 */
	public void setPortName(Integer portName) {
		this.portName = portName;
	}
	/**
	 * @return the nodeId
	 */
	public Integer getNodeId() {
		return nodeId;
	}
	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}
	/**
	 * @return the roundNumber
	 */
	public Integer getRoundNumber() {
		return roundNumber;
	}
	/**
	 * @param roundNumber the roundNumber to set
	 */
	public void setRoundNumber(Integer roundNumber) {
		this.roundNumber = roundNumber;
	}
	/**
	 * @return the nodeSet
	 */
	public Set<Integer> getNodeSet() {
		return nodeSet;
	}
	/**
	 * @param nodeSet the nodeSet to set
	 */
	public void setNodeSet(Set<Integer> nodeSet) {
		this.nodeSet = nodeSet;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	

}
