import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class MainServer {
	public static final int END = -2, ARENA_CONFIG = -3, ARENA_DISPLAY = -4, CLOSE = -5, 
			SNAKE_CONFIG = -6, REQUEST_SNAKE = -7, KILL_SNAKE = -8;
	public static SnakeManager currentSnakeManagerInstance = new SnakeManager();
	public static void main(String[] args){
		ArenaHost.init(32, 32);
		Dispatcher dispatcher = new Dispatcher(currentSnakeManagerInstance);
		for(int i = 0; i < 1000; i ++){
			currentSnakeManagerInstance.updateAllSnakes();
			ArenaHost.updateArena(currentSnakeManagerInstance);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}