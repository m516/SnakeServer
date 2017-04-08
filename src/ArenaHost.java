public class ArenaHost{
	/*
	 * Arenas are composed of byte arrays which represent what occupies each cell in the array
	 * ERR   - Dark grey      - no data retrieved or bad data given for this cell
	 * EMPTY - Black          - this cell is unoccupied
	 * WALL  - White          - this cell is a wall block
	 * FRUIT - Magenta        - this cell is occupied by a fruit
	 * SNAKE - snake-specific - this cell is occupied by a snake segment
	 */
	public static final byte ERR = 0, EMPTY = 1, WALL = 2, FRUIT = 3;
	/*
	 * Commands from the server to the arena are sent with these keys
	 * ERR             - bad message to server, followed by a command number, which 
	 * 					requests the client application to repeat the command
	 * ARENA_CONFIG    - requests the client to resize the arena to the x_size and y_size
	 * ARENA_DISPLAY   - the updated arena, followed by all of the pixels
	 * END             - An end of a command
	 */
	public static final int END = -2, ARENA_CONFIG = -3, ARENA_DISPLAY = -4, SNAKE_CONFIG = -6;
	public static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3, DEAD = 4;
	private static volatile byte[][] arena;
	private static int xSize, ySize;
	public static ArenaHost instance = new ArenaHost();
	static int[] snakeOwner;
	private ArenaHost(){
	}



	/**
	 * @return the width of the arena
	 */
	public static int getXSize() {
		return xSize;
	}



	/**
	 * @return the height of the arena
	 */
	public static int getYSize() {
		return ySize;
	}



	/**
	 * Initializes the arena
	 * @param new_x_size - the new width of the arena
	 * @param new_y_size - the new height of the arena
	 * @param numSnakes - the number of snakes in the arena
	 */
	public static void init(int new_x_size, int new_y_size){
		//resize the arena
		arena = new byte[new_x_size][new_y_size];
		xSize = new_x_size;
		ySize = new_y_size;
		for (int i = 0; i < arena.length; i++) {
			for (int j = 0; j < arena[i].length; j++) {
				arena[i][j] = EMPTY;
			}
		}
		//generate the walls
		for(int i = 0; i < xSize; i ++){
			arena[i][0] = WALL;
			arena[i][ySize-1] = WALL;
		}
		for(int i = 0; i < ySize; i ++){
			arena[0][i] = WALL;
			arena[xSize-1][i] = WALL;
		}
		//sprinkle some fruit
		for(int i = 0; i < 5; i ++){
			int x = (int)(Math.random()*(xSize-2))+1, y = (int)(Math.random()*(ySize-2))+1;
			arena[x][y] = FRUIT;
		}
		//Sprinkle some walls as well
		/*for(int i = 0; i < 50; i ++){
			int x = (int)(Math.random()*(xSize-2))+1, y = (int)(Math.random()*(ySize-2))+1;
			arena[x][y] = WALL;
		}*/
	}



	/**
	 * Changes a block 
	 * @param x - the x-coordinate of the block
	 * @param y - the y-coordinate of the block 
	 * @param type - the new type of the block's contents
	 */
	public static void setBlock(int x, int y, byte type){
		arena[x][y] = type;
	}



	/**
	 * Changes a block 
	 * @param location - the location of the position in the arena
	 * to change 
	 * @param type - the new type of the block's contents
	 */
	public static void setBlock(LocI location, byte type){
		arena[location.getX()][location.getY()] = type;
	}


	/**
	 * Gets the status of a block 
	 * @param x - the x-coordinate of the block
	 * @param y - the y-coordinate of the block 
	 */
	public static byte getBlock(int x, int y){
		return arena[x][y];
	}



	/**
	 * Gets the status of a block 
	 * @param location - the location of the block to get
	 */
	public static byte getBlock(LocI location){
		if(isInBounds(location))
			return arena[location.getX()][location.getY()];
		return ERR;
	}


	/**
	 * @param x - the x-position of the location
	 * @param y - the y-position of the location
	 * @return true if the location is in bounds
	 */
	final static protected boolean isInBounds(int x, int y) {
		if(x<0) return false;
		if(y<0) return false;
		if(x>=getXSize()) return false;
		if(y>=getYSize()) return false;
		return true;
	}
	/**
	 * @param l - the location
	 * @return true if the location is in bounds
	 */
	final protected static boolean isInBounds(LocI l) {
		return isInBounds(l.getX(),l.getY());
	}




	/**
	 * Updates the main arena by removing all of the snake segments
	 * and adding them back to the arena.  For best results (as
	 * in actually doing something with this method), use this
	 * method after calling <code>updateAllSnakes()</code> in
	 * the <code>SnakeManager</code> instance used in the parameter
	 * @param snakeManager - the <code>SnakeManager</code> instance
	 * containing live snakes to be added into the arena
	 */
	public static void updateArena(SnakeManager snakeManager){
		//Remove the old snake segments
		for (int i = 0; i < arena.length; i++) {
			for (int j = 0; j < arena[i].length; j++) {
				if(arena[i][j]>FRUIT){
					arena[i][j] = EMPTY;
				}
			}
		}
		//Add all of the snakes back to the arena
		Snake[] snakes = snakeManager.getSnakes();
		for (int i = 0; i < snakes.length; i++) {
			//Add this snake back to the arena
			for (int j = 0; j < snakes[i].size(); j++) {
				if(isInBounds(snakes[i].segmentAt(j))){
					byte segmentValue = (byte)(snakes[i].getId()+FRUIT+1);
					setBlock(snakes[i].segmentAt(j),segmentValue);
				}
			}
		}
		if(Math.random()>.5)setBlock(getRandomEmptyLocation(),FRUIT);
	}
	
	/**
	 * @return the current state of the arena
	 */
	public static byte[][] getArena(){
		return arena;
	}
	
	/**
	 * 
	 * @return a random empty location in the arena
	 */
	public static LocI getRandomEmptyLocation(){
		LocI l = new LocI(-1, -1);
		do{
			l.jumpTo((int)(Math.random()*xSize), (int)(Math.random()*ySize));
		}while(!isEmpty(l));
		return l;
	}
	
	/**
	 * 
	 * @param l - a location in the arena
	 * @return true if the location <b>l</b> is a valid, empty location
	 */
	public static boolean isEmpty(LocI l){
		if(l == null) return false;
		return getBlock(l) == EMPTY;
	}
}

