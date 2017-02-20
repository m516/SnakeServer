import java.util.ArrayList;
/**
 * This class represents a snake participating in the game managed by
 * the current instance of ArenaHost
 * @author Micah Mundy
 *
 */
public class Snake {
	private ArrayList<LocI> segments;
	private boolean isLive = true;
	private ClientBridge bridge;
	public Snake(){
		segments = new ArrayList<LocI>();
	}
	public Snake(LocI[] initialSegments) {
		segments = new ArrayList<LocI>();
		for(LocI segment: initialSegments){
			segments.add(segment);
		}
	}
	public void grow(){
		segments.add(getTail().clone());
	}
	public LocI getTail(){
		if(segments.size()>0)
		return segments.get(segments.size()-1);
		return null;
	}
	public LocI getHead(){
		if(segments.size()>0)
		return segments.get(0);
		return null;
	}
	public boolean isLive(){
		return isLive;
	}
	public ClientBridge getClientBridge(){
		return bridge;
	}
	public void syncWithClientBridge(ClientBridge newClientBridge){
		bridge = newClientBridge;
	}
}
