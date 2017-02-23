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
		spam(ClientBridge.CLOSE);
		spam(ClientBridge.END);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		close();
		for(ClientBridge bridge:clients){
			bridge.closeConnection();
		}
	}
	
	/**
	 * Updates the arena after all of the applications submit their
	 * new snake directions
	 */
	public static void update(int[] directions){
		//Update snake positions
		for (int i = 0; i < directions.length; i++) {
			if(snakes[i].length>1){
				for (int j = snakes[i].length-1; j > 0; j--) {
					snakes[i][j].jumpTo(snakes[i][j-1]);
				}
			}
			

			//Destroy snakes that are on top of walls, out of bounds, or
			//attempting to eat other snakes
			if(!isInBounds(snakes[i][0])){
				killSnake(i);
				break;
			}
		}

}
