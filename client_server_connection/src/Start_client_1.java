// Patricia Vines
// 1000536317

import java.awt.EventQueue;

public class Start_client_1 {
	public static void main(String[] args) {
		// Starts client 1 with arguments address: 127.0.0.1, port: 16993, title for initial window: Client 1, 
		// and x and y positions of window(s)
		client_controller client = new client_controller("127.0.0.1", 16993, "Client 1",100,600);
    }
}
