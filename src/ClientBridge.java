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
		snake = new Snake();
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
		snake = new Snake();
		init();
	}

	/**
	 * Accepts a client requesting to connect to the server
	 * @return true if the instance is properly initialized
	 */
	public boolean init(){
		try {
			socket = connectionSocket.accept();
			out = new PrintWriter(socket.getOutputStream(),true);
			inReader = new InputStreamReader(socket.getInputStream());
			in = new BufferedReader(inReader);
		} catch (IOException e) {
			System.out.println("Failed to establish connection to client");
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
	}

	/**
	 * Kills a snake of a certain ID
	 * @param id the ID of the snake to kill
	 * TODO include functionality for killing snakes instead of removing client bridges from "bridges"
	 */
	public void sendKillMessage(){
		out.println(KILL_SNAKE);
		out.println(END);
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
			e.printStackTrace();
			isLive = false;
			closeConnection();
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
		out.println(SNAKE_CONFIG);
		int id = snakeManager.getClients().indexOf(this);
		out.println(id+ArenaHost.FRUIT);
		if(snake == null){
			snake = new Snake();
			snake.setId(id);
			snake.syncWithClientBridge(this);
		}
		for (int i = 0; i < size; i++) {
			snake.addSegmentAt(x, y);
			out.println(x);
			out.println(y);
		}
		out.println(END);
	}

	public void updateSnake(){
		if(snake.isLive()){
			out.println(REQUEST_SNAKE);
			out.println(END);
			snake.update(getInt());
		}
	}
	
	public void sendArenaSize(){
		out.println(ClientBridge.ARENA_CONFIG);
		out.println(ArenaHost.getXSize());
		out.println(ArenaHost.getYSize());
		out.println(ClientBridge.END);
	}
}
