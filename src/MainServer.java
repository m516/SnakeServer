
public class MainServer {
	public static SnakeManager currentSnakeManagerInstance = new SnakeManager();
	/**
	 * @param args
	 */
	public static void main(String[] args){
		ArenaHost.init(50, 50);
		for(int i = 0; i < 10000; i ++){
			currentSnakeManagerInstance.updateAllSnakes();
			ArenaHost.updateArena(currentSnakeManagerInstance);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}