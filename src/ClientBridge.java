import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A connection to a client
 * @author Micah Mundy
 */
public class ClientBridge{
	public static final int END = -2, ARENA_CONFIG = -3, ARENA_DISPLAY = -4, CLOSE = -5, 
			SNAKE_CONFIG = -6, REQUEST_SNAKE = -7, KILL_SNAKE = -8;

	
	private ServerSocket connectionSocket;
	private SnakeManager snakeManager;
	private Socket socket;
	private PrintWriter out;
	private InputStreamReader inReader;
	private BufferedReader in;
	private boolean isLive = true;
	private Snake snake;
	/**
	 * Creates a socket on an unused port
	 */
	public ClientBridge(){
		try {
			connectionSocket = new ServerSocket(0);
		} catch (IOException e) {
			e.printStackTrace();
			isLive = false;
		}
	}

	/**
	 * Creates a socket
	 * @param port - the port number to connect to
	 */
	public ClientBridge(SnakeManager s, int port){
		try {
			connectionSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Failed to create a socket at port "+port);
			isLive = false;
			return;
		}
		initializeConnection();
	}

	/**
	 * Accepts a client requesting to connect to the server
	 * @return true if the instance is properly initialized
	 */
	public boolean initializeConnection(){
		try {
			socket = connectionSocket.accept();
			System.out.println("ClientBridge on port "+getPort() +" connected to client.");
			out = new PrintWriter(socket.getOutputStream(),true);
			inReader = new InputStreamReader(socket.getInputStream());
			in = new BufferedReader(inReader);
		} catch (IOException e) {
			System.out.println("Failed to establish connection to client");
			e.printStackTrace();
			isLive = false;
			return false;
		}
		return true;
	}

	/**
	 * Closes the connection to the client
	 */
	public void closeConnection(){
		try {
			connectionSocket.close();
			isLive = false;
		} catch (IOException e) {
			System.out.println("Couldn't close the connection to the client");
			isLive = false;
		}
	}

	/**
	 * @return the port this instance is connected to
	 */
	public int getPort(){
		return connectionSocket.getLocalPort();
	}

	/**
	 * @return the snakeManager
	 */
	public SnakeManager getSnakeManager() {
		return snakeManager;
	}

	/**
	 * @param snakeManager the snakeManager to set
	 */
	public void setSnakeManager(SnakeManager snakeManager) {
		this.snakeManager = snakeManager;
	}

	/**
	 * @return the output stream in the form of a PrintWriter
	 */
	public PrintWriter getOutStream() {
		return out;
	}

	/**
	 * @return the input stream in the form of a PrintWriter
	 */
	public BufferedReader getInStream() {
		return in;
	}

	/**
	 * @return true if this connection is still live
	 */
	public boolean isLive(){
		return isLive;
	}

	/**
	 * @return the snake binded with this ClientBridge
	 */
	public Snake getSnake(){
		return snake;
	}

	/**
	 * Syncs this socket with a snake
	 * @param s - the snake to bind to the client
	 */
	public void syncWithSnake(Snake s){
		snake = s;
		s.syncWithClientBridge(this);
	}

	/**
	 * Kills a snake of a certain ID
	 * @param id the ID of the snake to kill
	 */
	public void sendKillMessage(){
		out.println(KILL_SNAKE);
		out.println(END);
		snake.setDead(true);
	}

	/**
	 * Retrieves an integer value from an application
	 * @return the next number that the client application sent to the server
	 */
	public int getInt(){
		try {
			int r = Integer.parseInt(in.readLine());
			return r;
		} catch (NumberFormatException | IOException e) {
			isLive = false;
			closeConnection();
			snakeManager.getClients().remove(this);
			System.out.println("No response from client with Snake ID of " + snake.getId());
			System.out.println(MainServer.currentSnakeManagerInstance.getClients().size() + " snakes remaining");
		}
		return -1;
	}

	/**
	 * Initializes the snake and relays the initial positions of each segment to the client
	 * @param x - the x-coordinate of the snake
	 * @param y - the y-coordinate of the snake
	 * @param size - the initial size of the snake
	 */
	public void initializeSnake(int x, int y, int size){
		//Is there a snake for this client?
		if(snake == null){
			snake = new Snake();
			snake.setId(snakeManager.getUniqueSnakeID());
			snake.syncWithClientBridge(this);
		}
		//Send the command to the client first
		out.println(SNAKE_CONFIG);
		//Print the snake ID
		out.println(snake.getId());
		//Print all of the segments that it represent the snake
		for (int i = 0; i < size; i++) {
			snake.addSegmentAt(x, y);
			out.println(x);
			out.println(y);
		}
		//This command is finished
		out.println(END);
	}

	public void updateSnake(){
		if(snake.isLive()){
			out.println(REQUEST_SNAKE);
			out.println(END);
			snake.update(getInt());
		}
	}
	
	/**
	 * Configures the arena on the client's side by sending
	 * information regarding the size of the arena and
	 * how many snakes are in the arena
	 */
	public void sendArenaSize(){
		out.println(ARENA_CONFIG);
		out.println(ArenaHost.getXSize());
		out.println(ArenaHost.getYSize());
		out.println(ClientBridge.END);
	}
	
	public void sendArena(){
		byte[][] cells = ArenaHost.getArena();
		out.println(ARENA_DISPLAY);
		for(byte[] column: cells){
			for(byte row:column){
				out.println(row);
			}
		}
		out.println(END);
	}
}
