import java.io.Serializable;

public class TerminationMessage extends Message implements Serializable{

	public TerminationMessage(Node sourceNode, int clock) {
		super(sourceNode, clock);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
