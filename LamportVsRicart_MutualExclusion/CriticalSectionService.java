

public interface CriticalSectionService{

	public void csEnter();
	public void csLeave();
	
	public void processRequestMessage(Message message);
	public void processReplyMessage(Message message);
	public void processReleaseMessage(Message message);
	
}
