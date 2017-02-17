import java.util.ArrayList;
/**
 * This class represents a snake participating in the game managed by
 * the current instance of ArenaHost
 * @author Micah Mundy
 *
 */
public class Snake {
	private ArrayList<LocI> segments;
	public Snake(){
		segments = new ArrayList<LocI>();
	}
	public Snake(LocI[] initialSegments) {
		segments = new ArrayList<LocI>();
		for(LocI segment: initialSegments){
			segments.add(segment);
		}
	}
}
