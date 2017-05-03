import java.io.Serializable;

public class RequestMessage extends Message implements Serializable, Comparable<RequestMessage> {

	private static final long serialVersionUID = 1147742072340994819L;

	public RequestMessage(Node sourceNode, int clock) {
		super(sourceNode, clock);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		RequestMessage other = (RequestMessage) obj;
		if (this.compareTo(other) != 0)
			return false;
		return true;
	}

	@Override
	public int compareTo(RequestMessage o) {
		int d = clock - o.getClock();
		if (d != 0)
			return d;
		return getSourceNode().getNodeId() - o.getSourceNode().getNodeId();
	}
}
