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
	private int id;
	private ClientBridge bridge;
	/**
	 * Initializes a new snake with a length of 0
	 */
	public Snake(){
		segments = new ArrayList<LocI>();
	}
	/**
	 * Initializes a new snake with all of the segments
	 * @param initialSegments - the initial cluster of segments the snake begins with
	 */
	public Snake(int id, LocI[] initialSegments) {
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
	 * 
	 * @return the number of segments in the snake
	 */
	public int size(){
		return segments.size();
	}
	/**
	 * @param index - the 
	 * @return
	 */
	public LocI segmentAt(int index){
		return segments.get(index);
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
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
	
	/**
	 * Adds a segment at a location
	 * @param x - the x-coordinate of the location to put the segment
	 * @param y - the y-coordinate of the location to put the segment
	 */
	public void addSegmentAt(int x, int y){
		LocI newSegment = new LocI(x, y);
		segments.add(newSegment);
	}
	
	/**
	 * Updates the snake based on a direction retrieved from a client
	 * @param direction - the new direction of the snake's head <p>
	 * The current release supports five versions
	 * UP 
	 * RIGHT
	 * DOWN
	 * LEFT 
	 * DEAD
	 * 
	 */
	public void update(int direction){
		//Translate all of the old segments so that they are
		//"pushed" forward
		for(int i = segments.size(); i >= 1; i --){
			segments.get(i).jumpTo(segments.get(i-1));
		}
		LocI newHead = segments.get(0);
		switch(direction){
		case DOWN:
			newHead.translate(0, 1);
			break;
		case UP:
			newHead.translate(0, -1);
			break;
		case RIGHT:
			newHead.translate(1, 0);
			break;
		case LEFT:
			newHead.translate(-1, 0);
			break;
		case DEAD:
			newHead.translate(0, 0);
			isLive = false;
			break;
		}
		if(!ArenaHost.isInBounds(newHead)){
			bridge.sendKillMessage();
		}
	}
	
}
