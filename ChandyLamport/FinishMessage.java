import java.io.Serializable;

/**
 * @author anirudh kuttiyil valsalan 
 * 		   praveen erode murugesan
 * 		   rahul aravind
 * 
 * Finish Message class 
 */
public class FinishMessage extends Message implements Serializable {
 
	private static final long serialVersionUID = 1L;

	public FinishMessage() {
        super();
    }

    public FinishMessage(String message, Node sourceNode, MessageType messageType) {
        super(message, sourceNode, messageType);
    }
}