import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class MainServer {
	public static final int END = -2, ARENA_CONFIG = -3, ARENA_DISPLAY = -4, CLOSE = -5, 
			SNAKE_CONFIG = -6, REQUEST_SNAKE = -7, KILL_SNAKE = -8;
	public static SnakeManager currentSnakeManagerInstance = new SnakeManager();
	public static void main(String[] args) throws IOException {
		int portNumber =2060;
		try {/*
			for(int i = 0; i < numSnakes; i ++){
				initialConnectionPoint = new ClientBridge(portNumber);
				if(!initialConnectionPoint.init()) System.exit(0);
				ClientBridge b = new ClientBridge();
				bridges.add(b);
				initialConnectionPoint.getOutStream().println(b.getPort());
				String inputLine = initialConnectionPoint.getInStream().readLine();
				System.out.println("Client: " + inputLine);
				initialConnectionPoint.closeConnection();
				if(!b.init()) System.exit(0);
				PrintWriter out = b.getOutStream();
				BufferedReader in = b.getInStream();
				while((inputLine = in.readLine()) != null){
					System.out.println(inputLine);
					if(inputLine.equals("Requesting test response")){
						out.println("Success");
						System.out.println("Success!");
						break;
					}
				}
			}*/
			Thread.sleep(3000);
			initializeArena(32, 32);
			initializeSnakes();
			for(int i = 0; i < 2048; i ++){
				updateSnakesAndArena();
			}
			System.out.println("Done!");
		} catch (Exception e) {
			System.out.println("Exception caught when trying to listen on port "
					+ portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}







	/**
	 * Initializes a new arena
	 * @param new_x_size
	 * @param new_y_size
	 * @param new_numSnakes
	 */
	private static void initializeArena(int new_x_size, int new_y_size){
		ArenaHost.init(new_x_size, new_y_size);
		currentSnakeManagerInstance.spam(ARENA_CONFIG);
		currentSnakeManagerInstance.spam(new_x_size);
		currentSnakeManagerInstance.spam(new_y_size);
		currentSnakeManagerInstance.spam(END);
	}

	/**
	 * Initializes the snakes in the arena
	 */
	private static void initializeSnakes(){
		createSnake(0, 0, 5, 5, 10);
		for(int i = 0; i < numSnakes - 1; i ++){
			int x = (int)(Math.random()*(ArenaHost.getXSize()-2))+1, 
					y = (int)(Math.random()*(ArenaHost.getYSize()-2))+1;
			createNewSnake(x,y,4);
		}
		for(int  i = 0; i < numSnakes; i ++){
			ArenaHost.isLive[i] = true;
			ArenaHost.snakeOwner[i] = i;
		}
	}
	
	/**
	 * Initializes a new snake
	 * @param x - the initial x-position of the snake
	 * @param y - the initial y-position of the snake
	 * @param size - the initial length of the snake
	 */
	private static void createNewSnake(int x, int y, int size){
		
		ArenaHost.snakes = temp;
		//Initialize the new snake
		ArenaHost.snakes[ArenaHost.snakes.length-1] = new LocI[size];
		int appID = ArenaHost.snakes.length-1;
		printTo(appID,SNAKE_CONFIG);
		printTo(appID,ArenaHost.snakes.length+ArenaHost.FRUIT);
		for (int i = 0; i < ArenaHost.snakes[ArenaHost.snakes.length-1].length; i++) {
			ArenaHost.snakes[ArenaHost.snakes.length-1][i] = new LocI(x,y);
			printTo(appID,x);
			printTo(appID,y);
		}
		printTo(appID,END);
	}

	
	/**
	 * Updates all of the snakes and the arenaHost
	 */
	private static void updateSnakesAndArena(){
		for (int i = 0; i < bridges.size(); i++) {
			if(ArenaHost.isLive(i)){
				printTo(i,REQUEST_SNAKE);
				printTo(i,END);
			}
		}
		int[] directions = new int[bridges.size()];
		for (int i = 0; i < bridges.size(); i++) {
			if(bridges.get(i).isLive()){
				directions[i] = getInt(i);
			}
		}
		ArenaHost.update(directions);
		currentSnakeManagerInstance.spam(ARENA_DISPLAY);
		for(int j = 0; j < ArenaHost.getXSize(); j ++){
			for(int k = 0; k < ArenaHost.getYSize(); k ++){
				currentSnakeManagerInstance.spam(ArenaHost.getBlock(j, k));
			}
		}
		currentSnakeManagerInstance.spam(END);
	}



	/**
	 * Writes to a client application
	 * @param clientNum - the client number <p> <b>NOTE:</b> clients are ordered 
	 * by the time at which they connected to the server, so the number <b>0</b>
	 * would reference the client application that signed onto the server
	 * first.
	 * @param msg - the message to send to the client application specified by
	 * the previous parameter
	 */
	static void printTo(int clientNum, int msg){
		bridges.get(clientNum).getOutStream().println(msg);
	}



}