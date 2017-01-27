import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class MainServer {
	public static final int END = -2, ARENA_CONFIG = -3, ARENA_DISPLAY = -4, CLOSE = -5, 
			SNAKE_CONFIG = -6, REQUEST_SNAKE = -7, KILL_SNAKE = -8;
	private static final int numApps = 1;
	private static ArrayList<ClientBridge> bridges = new ArrayList<ClientBridge>();
	private static ClientBridge initialConnectionPoint = new ClientBridge();
	public static void main(String[] args) throws IOException {
		int portNumber =2060;
		try {
			for(int i = 0; i < numApps; i ++){
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
			}
			Thread.sleep(3000);
			initializeArena(32, 32, numApps);
			initializeSnakes();
			for(int i = 0; i < 2048; i ++){
				updateSnakesAndArena();
			}
			spam(CLOSE);
			spam(END);
			Thread.sleep(5000);
			close();
			System.out.println("Done!");
		} catch (Exception e) {
			System.out.println("Exception caught when trying to listen on port "
					+ portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}



	/**
	 * Sends a message to all of the applications connected to the server
	 * @param msg - the message to send to all of the applications 
	 * connected to the server
	 */
	private static void spam(int msg){
		for(ClientBridge bridge:bridges){
			if(bridge.isLive()){
				bridge.getOutStream().println(msg);
			}
		}
	}



	/**
	 * Retrieves an integer value from an application
	 * @param clientNum - the client number <p> <b>NOTE:</b> clients are ordered 
	 * by the time at which they connected to the server, so the number <b>0</b>
	 * would reference the client application that signed onto the server
	 * first.
	 * @return the next number that the client application sent to the server
	 */
	private static int getInt(int clientNum){
		try {
			int r = Integer.parseInt(bridges.get(clientNum).getInStream().readLine());
			return r;
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
			bridges.get(clientNum).closeConnection();
			bridges.remove(clientNum);
		}
		return -1;
	}


	private static void initializeArena(int new_x_size, int new_y_size, int new_numSnakes){
		ArenaHost.init(new_x_size, new_y_size, new_numSnakes);
		spam(ARENA_CONFIG);
		spam(new_x_size);
		spam(new_y_size);
		spam(new_numSnakes);
		spam(END);
	}


	private static void initializeSnakes(){
		ArenaHost.snakes = new LocI[1][];
		ArenaHost.isLive = new boolean[numApps];
		ArenaHost.snakeOwner = new int[numApps];
		createSnake(0, 0, 5, 5, 10);
		for(int i = 0; i < numApps - 1; i ++){
			int x = (int)(Math.random()*(ArenaHost.getXSize()-2))+1, 
					y = (int)(Math.random()*(ArenaHost.getYSize()-2))+1;
			createNewSnake(x,y,4);
		}
		for(int  i = 0; i < numApps; i ++){
			ArenaHost.isLive[i] = true;
			ArenaHost.snakeOwner[i] = i;
		}
	}
	private static void createNewSnake(int x, int y, int size){
		//Duplicate the snake array with a slot for another snake
		LocI[][] temp = new LocI[ArenaHost.snakes.length+1][];
		for (int i = 0; i < temp.length-1; i++) {
			temp[i] = new LocI[ArenaHost.snakes[i].length];
			for (int j = 0; j < temp[i].length; j++) {
				temp[i][j] = ArenaHost.snakes[i][j];
			}
		}
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

	private static void createSnake(int appID, int snakeNum, int x, int y, int size){
		ArenaHost.snakes[snakeNum] = new LocI[size];
		printTo(appID,SNAKE_CONFIG);
		printTo(appID,ArenaHost.snakes.length+ArenaHost.FRUIT);
		for (int i = 0; i < size; i++) {
			ArenaHost.snakes[snakeNum][i] = new LocI(x,y);
			printTo(appID,x);
			printTo(appID,y);
		}
		printTo(appID,END);
	}

	private static void updateSnakesAndArena(){
		for (int i = 0; i < bridges.size(); i++) {
			if(bridges.get(i).isLive()){
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
		spam(ARENA_DISPLAY);
		for(int j = 0; j < ArenaHost.getXSize(); j ++){
			for(int k = 0; k < ArenaHost.getYSize(); k ++){
				spam(ArenaHost.getBlock(j, k));
			}
		}
		spam(END);
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



	/**
	 * Closes the connections to all of the client applications
	 */
	private static void close(){
		for(ClientBridge bridge:bridges){
			bridge.closeConnection();
		}
	}







	/**
	 * A connection to a client
	 * @author NullPointerException
	 *<p><q>Oh yeah, he's totally awesome!</q>
	 */
	private static class ClientBridge{
		private ServerSocket connectionSocket;
		private Socket socket;
		private PrintWriter out;
		private InputStreamReader inReader;
		private BufferedReader in;
		private boolean isLive = true;
		public ClientBridge(){
			try {
				connectionSocket = new ServerSocket(0);
			} catch (IOException e) {
				e.printStackTrace();
				isLive = false;
			}
		}
		public ClientBridge(int port){
			try {
				connectionSocket = new ServerSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
				isLive = false;
			}
		}
		public boolean init(){
			try {
				socket = connectionSocket.accept();
				out = new PrintWriter(socket.getOutputStream(),true);
				inReader = new InputStreamReader(socket.getInputStream());
				in = new BufferedReader(inReader);
			} catch (IOException e) {
				e.printStackTrace();
				isLive = false;
				return false;
			}
			return true;
		}
		public void closeConnection(){
			try {
				connectionSocket.close();
				isLive = false;
			} catch (IOException e) {
				e.printStackTrace();
				isLive = false;
			}
		}
		public int getPort(){
			return connectionSocket.getLocalPort();
		}
		public PrintWriter getOutStream() {
			return out;
		}
		public BufferedReader getInStream() {
			return in;
		}
		public boolean isLive(){
			return isLive;
		}
	}
}