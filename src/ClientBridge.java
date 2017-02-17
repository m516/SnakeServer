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
	private ServerSocket connectionSocket;
	private Socket socket;
	private PrintWriter out;
	private InputStreamReader inReader;
	private BufferedReader in;
	private boolean isLive = true;
	private Snake snake;
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
	 * @param port
	 */
	public ClientBridge(int port){
		try {
			connectionSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Failed to create a socket at port "+port);
			isLive = false;
			return;
		}
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
	 * 
	 */
	public Snake getSnake(){
		return snake;
	}
	/**
	 * Syncs this socket with a snake
	 * <p>
	 * @param s
	 */
	public void syncWithSnake(Snake s){
		snake = s;
	}
}
