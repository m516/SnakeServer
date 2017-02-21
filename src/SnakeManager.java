import java.util.ArrayList;

public class SnakeManager {
	public ArrayList<ClientBridge> clients = new ArrayList<ClientBridge>();
	public SnakeManager() {}
	
	public void addClientBridge(ClientBridge b){
		clients.add(b);
	}
	
	public Snake getSnakeAt(int i){
		return clients.get(i).getSnake();
	}
	
	
	public ClientBridge getBridgeAt(int i){
		return clients.get(i);
	}

	/**
	 * Sends a message to all of the applications connected to the server
	 * @param msg - the message to send to all of the applications 
	 * connected to the server
	 */
	public void spam(int msg){
		for(ClientBridge bridge:clients){
			if(bridge.isLive()){
				bridge.getOutStream().println(msg);
			}
		}
	}
	
	
	
	

	/**
	 * Closes the connections to all of the client applications
	 */
	public void close(){
		for(ClientBridge bridge:clients){
			bridge.closeConnection();
		}
	}

}
