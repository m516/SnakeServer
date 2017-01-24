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
	static LocI[][] snakes;
	static boolean[] isLive;
	static int[] snakeOwner;
	//TODO include functionality so that each snake has an owner identification
	//
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
	public static void init(int new_x_size, int new_y_size, int new_numSnakes){
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
	 * Adds a segment to the tail end of a snake
	 * @param snakeID the ID of the snake
	 */
	public static void growSnake(int snakeID){
		LocI[] temp = new LocI[snakes[snakeID].length+1];
		for(int i = 0; i < snakes[snakeID].length; i ++){
			temp[i] = snakes[snakeID][i];
		}
		temp[temp.length-1] = temp[temp.length-2].clone();
		snakes[snakeID] = temp;
	}
	
	/**
	 * Kills a snake of a certain ID
	 * @param id the ID of the snake to kill
	 * TODO include functionality for killing snakes instead of removing client bridges from "bridges"
	 */
	public static void killSnake(int id){
		MainServer.printTo(snakeOwner[id], MainServer.KILL_SNAKE);
		MainServer.printTo(snakeOwner[id], END);
		isLive[id] = false;
	}

	/**
	 * Updates the arena after all of the applications submit their
	 * new snake directions
	 */
	public static void update(int[] directions){
		//Update snake positions
		for (int i = 0; i < directions.length; i++) {
			if(snakes[i].length>1){
				for (int j = snakes[i].length-1; j > 0; j--) {
					snakes[i][j].jumpTo(snakes[i][j-1]);
				}
			}
			switch(directions[i]){
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
		//Remove the snake segments
		for (int i = 0; i < arena.length; i++) {
			for (int j = 0; j < arena[i].length; j++) {
				if(arena[i][j]>FRUIT){
					arena[i][j] = EMPTY;
				}
			}
		}
		//Add all of the snakes back to the arena with a few final checks
		for (int i = 0; i < snakes.length; i++) {
			//Is this snake still alive?
			if(isLive[i]){
				//Grow snakes that eat fruit
				switch(getBlock(snakes[i][0])){
				case FRUIT:
					int x = (int)(Math.random()*(xSize-2))+1, y = (int)(Math.random()*(ySize-2))+1;
					arena[x][y] = FRUIT;
					growSnake(i);
					break;
				case EMPTY:
					break;
				default: 
					killSnake(i);
					break;
				case ERR:
					
				}
				//Destroy snakes that are on top of walls, out of bounds, or
				//attempting to eat other snakes
				//TODO
				//Add this snake back to the arena
				for (int j = 0; j < snakes[i].length; j++) {
					if(isInBounds(snakes[i][0]))
					setBlock(snakes[i][j],(byte)(FRUIT+1+i));
				}
			}
		}
	}
}

