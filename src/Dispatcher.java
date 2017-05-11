import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Dispatcher extends Thread{
	ServerSocket initialConnectionPoint;
	public static final int INITIAL_CONNECTION_PORT = 6419;
	private volatile SnakeManager snakeManager;
	public Dispatcher(SnakeManager mySnakeManager) {
		snakeManager = mySnakeManager;
	}

	@Override public void run(){
		try{
			initialConnectionPoint = new ServerSocket(INITIAL_CONNECTION_PORT);
			Socket s;
			while(true){
				s = initialConnectionPoint.accept();
				//A client has attempted to connect to this port
				System.out.println("Attempted connection");
				PrintWriter out = new PrintWriter(s.getOutputStream(),true);
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				ClientBridge b = new ClientBridge();
				b.setSnakeManager(snakeManager);
				//Create a client in an open port
				System.out.println("Opening connection on port "+b.getPort());
				String inputLine = in.readLine();
				out.println(b.getPort());
				System.out.println("Client: " + inputLine);
				out.close();
				in.close();
				s.close();
				if(b.initializeConnection()){
					b.sendArenaSize();
					//Place the snake in an empty space in the arena
					LocB initialLocation = ArenaHost.getRandomEmptyLocation();
					b.initializeSnake(initialLocation.getX(), initialLocation.getY(), (byte) 3);
					//This ClientBridge instance has been totally initialized.
					//Add it to the list of clients in SnakeManager
					snakeManager.addClientBridge(b);
				}
				else{System.err.println("Failed to establish connection");};
				System.out.println("Connection finished: waiting for new connection.");
			}
		}
		catch(Exception e){
			e.printStackTrace();
			try {
				initialConnectionPoint.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}
	}
}
