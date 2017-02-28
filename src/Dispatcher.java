import java.io.BufferedReader;
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
		start();
	}

	@Override public void run(){
		while(true){
			try{
				initialConnectionPoint = new ServerSocket(INITIAL_CONNECTION_PORT);
				Socket s = initialConnectionPoint.accept();
				PrintWriter out = new PrintWriter(s.getOutputStream(),true);
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				ClientBridge b = new ClientBridge();
				b.setSnakeManager(snakeManager);
				out.println(b.getPort());
				String inputLine = in.readLine();
				System.out.println("Client: " + inputLine);
				out.close();
				in.close();
				s.close();
				initialConnectionPoint.close();
				if(b.init()){
					snakeManager.addClientBridge(b);
					//Test the input and output streams of the new ClientBridge
					out = b.getOutStream();
					in = b.getInStream();
					while((inputLine = in.readLine()) != null){
						System.out.println(inputLine);
						if(inputLine.equals("Requesting test response")){
							out.println("Success");
							System.out.println("Success!");
							break;
						}
					}
					b.getSnake().setId(snakeManager.getClients().size()-1);
					LocI initialLocation = ArenaHost.getRandomEmptyLocation();
					b.initializeSnake(initialLocation.getX(), initialLocation.getY(), 3);
					b.sendArenaSize();
					System.out.println("Clients total: " + snakeManager.getClients().size());
				}
			}
			catch(Exception e){
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
}
