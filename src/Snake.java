import java.util.ArrayList;
/**
 * This class represents a snake participating in the game managed by
 * the current instance of ArenaHost
 * @author Micah Mundy
 *
 */
public class Snake {
	public static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3, DEAD = 4;
	private ArrayList<LocI> segments;
	private boolean isLive = true;
	private ClientBridge bridge;
	/**
	 * Initializes a new snake with a length of 0
	 */
	public Snake(){
		segments = new ArrayList<LocI>();
	}
	/**
	 * Initializes a new snake with all of the segments
	 * @param initialSegments
	 */
	public Snake(LocI[] initialSegments) {
		segments = new ArrayList<LocI>();
		for(LocI segment: initialSegments){
			segments.add(segment);
		}
	}
	/**
	 * Adds a segment to the tail of the tail
	 */
	public void grow(){
		segments.add(getTail().clone());
	}
	/**
	 * @return a copy of the tail segment if one exists
	 */
	public LocI getTail(){
		if(segments.size()>0)
		return segments.get(segments.size()-1);
		return null;
	}
	
	/**
	 * 
	 * @return a copy of the head segment if one exists
	 */
	public LocI getHead(){
		if(segments.size()>0)
		return segments.get(0);
		return null;
	}
	
	/**
	 * 
	 * @return true if the snake hasn't died yet
	 */
	public boolean isLive(){
		return isLive;
	}
	
	/**
	 * 
	 * @return the ClientBridge this snake has bonded to
	 */
	public ClientBridge getClientBridge(){
		return bridge;
	}
	
	/**
	 * Assigns a ClientBridge instance to this Snake instance
	 * @param newClientBridge - the ClientBridge to assigns to this instance
	 */
	public void syncWithClientBridge(ClientBridge newClientBridge){
		bridge = newClientBridge;
	}
	
	public void addSegmentAt(int x, int y){
		LocI newSegment = new LocI(x, y);
		segments.add(newSegment);
	}
	
	public void update(int direction){
		switch(direction){
		case DOWN:
			snakes[i][0].translate(0, 1);
			break;
		case UP:
			snakes[i][0].translate(0, -1);
			break;
		case RIGHT:
			snakes[i][0].translate(1, 0);
			break;
		case LEFT:
			snakes[i][0].translate(-1, 0);
			break;
		case DEAD:
			snakes[i][0].translate(0, 0);
			break;
		}
	}
	
}
