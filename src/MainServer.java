
public class MainServer {
	public static SnakeManager currentSnakeManagerInstance = new SnakeManager();
	public static void main(String[] args){
		ArenaHost.init(50, 50);
		Dispatcher dispatcher = new Dispatcher(currentSnakeManagerInstance);
		for(int i = 0; i < 10000; i ++){
			currentSnakeManagerInstance.updateAllSnakes();
			ArenaHost.updateArena(currentSnakeManagerInstance);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}