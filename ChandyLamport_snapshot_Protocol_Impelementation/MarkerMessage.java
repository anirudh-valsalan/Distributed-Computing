import java.io.Serializable;

/**
 * @author anirudh kuttiyil valsalan 
 * 		   praveen erode murugesan
 * 		   rahul aravind
 * 
 * Marker message class
 */
public class MarkerMessage extends Message implements Serializable {
 
	private static final long serialVersionUID = 1L;

	public MarkerMessage() {
        super();
    }

    public MarkerMessage(String message,Node sourceNode, MessageType messageType) {
        super(message, sourceNode, messageType);
    }
}