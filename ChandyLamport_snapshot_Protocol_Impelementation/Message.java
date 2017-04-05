import java.io.Serializable;

/**
 * @author anirudh kuttiyil valsalan 
 * 		   praveen erode murugesan
 * 		   rahul aravind
 */
public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String message;
	private Node source;
	private MessageType messageType;

	public Message() {

	}

	public Message(MessageType messageType) {
		this.messageType = messageType;
	}

	public Message(String message, Node source, MessageType messageType) {
		super();
		this.message = message;
		this.source = source;
		this.messageType = messageType;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
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

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

}
