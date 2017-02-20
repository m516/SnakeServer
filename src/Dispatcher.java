import java.io.BufferedReader;
import java.io.PrintWriter;

public class Dispatcher extends Thread{

	public Dispatcher() {
	}
	
	@Override public void run(){
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

}
