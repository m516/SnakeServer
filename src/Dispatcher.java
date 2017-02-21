import java.io.BufferedReader;
import java.io.PrintWriter;

public class Dispatcher extends Thread{
	ClientBridge initialConnectionPoint;
	public static final int INITIAL_CONNECTION_PORT = 2060;
	public Dispatcher() {
		start();
	}

	@Override public void run(){
		while(true){
			try{
				initialConnectionPoint = new ClientBridge(INITIAL_CONNECTION_PORT);
				if(!initialConnectionPoint.init()) System.exit(0);
				ClientBridge b = new ClientBridge();
				initialConnectionPoint.getOutStream().println(b.getPort());
				String inputLine = initialConnectionPoint.getInStream().readLine();
				System.out.println("Client: " + inputLine);
				initialConnectionPoint.closeConnection();
				if(!b.init()) System.exit(0);
				MainServer.currentSnakeManagerInstance.addClientBridge(b);

				//Test the input and output streams of the new ClientBridge
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
			catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
	}
}
