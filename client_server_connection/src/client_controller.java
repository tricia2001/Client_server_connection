// Patricia Vines
// 1000536317

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

public class client_controller {
	// client controller class manages the client GUI and server connection

	private String address; // IP address of the server (127.0.0.1 - local host)
	private int port; // port of the server (16993)
	public client_view view = null; // Client GUI class object
	private String svr_message = ""; // Message from the Server - connection or thread time
	private int svr_num = 0; // Number sent from server used in a switch to determine action
	private String username = ""; // username of client
	public volatile boolean quit = false; // boolean used when quit button is clicked
	private int x = 0; // x position of client window
	private int y = 0; // y position of client window
	private Thread t = null; // thread used to manage connection to server
	private boolean flag = true; // flag for thread communication in synchronized method
	String title = ""; // title for client GUI (client #)
	private final AtomicBoolean running = new AtomicBoolean(false); // thread running boolean

	public client_controller(String address, int port, String title, int x, int y) {
		// constructor method for the client controller
		// EventQueue code modified from http://zetcode.com/javaswing/firstprograms/
		// ActionListener code modified from https://javatutorial.net/jframe-buttons-listeners-text-fields
		// inputs:
		// address - IP address of server to connect to
		// port - port number of server process
		// title - String used to identify the type of window needed
		// x - int x position of window
		// y - int y position of window

		// set class variables to inputs
		this.x = x;
		this.y = y;
		this.view = new client_view(title, x, y); // create initial GUI
		this.address = address;
		this.port = port;
		this.title = title;

		// Add the action to make the GUI visible to the event queue of the event
		// dispatching thread
		EventQueue.invokeLater(() -> {

			view.setVisible(true);
		});

		// listen for the connect button and send username to thread_connection method
		view.getConnectButton().addActionListener(new ActionListener() {
			@Override
			// Perform this action when the connect button is pressed:
			public void actionPerformed(ActionEvent arg0) {
				username = view.getUser().getText(); // get username from text field

				System.out.println("connect button pressed for "+title+" "+username);
				// call thread_connection method to create connection thread
				thread_connection(username);
			}
		});

		// listen for the quit button and exit the process from the initial GUI
		view.getQuitButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		// main thread goes into the waiting synchronized loop to await messages from
		// the connection thread
		Waiting();

	}

	public void thread_connection(String username) {
		// method creates a new thread to manage the server connection
		// socket code modified from https://www.geeksforgeeks.org/socket-programming-in-java/
		// time calc code from
		// https://www.tutorialspoint.com/compute-elapsed-time-in-seconds-in-java#:~:text=To%20compute%20the%20elapsed%20time,The%20java.
		// inputs:
		// username - client's username

		t = new Thread() {

			public void run() { // code that the connection thread should execute
				try {
					// create a socket and connect to the address and port of the server
					Socket socket = new Socket(address, port);

					// set up input and output streams to read and write messages
					// to/from the socket
					BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

					// send username to the server
					out.println(username);

					// keep waiting for quit boolean and socket messages
					while (true) {
						try {
							// while there are no incoming socket messages, check
							// the quit boolean
							while (!input.ready()) {
								if (quit) { // means quit button was pressed
									// send message to server indicating client is disconnecting
									out.println("OVER");
									// end client process
									System.exit(0);
								}
							}
							// assign the first incoming socket message to svr_num
							// this is a number identifying which action to take
							svr_num = Integer.parseInt(input.readLine());
							
							// assign the second incoming socket message to svr_message
							// this is a String message or the number of seconds the
							// thread should sleep
							svr_message = input.readLine();

							// notify the main thread that there is a new message
							Notify();

							// if the username was already in use...
							if (svr_num == 1) {
								socket.close(); // close the socket
								break;
							}

							// if the server sent a sleep time...
							else if (svr_num == 3) {
								long start = 0, end; // start and end times
								float sec; // total time of thread sleep
								try {
									// get the start time
									start = System.currentTimeMillis();
									// put the thread to sleep for the time sent from the server
									Thread.sleep(Integer.parseInt(svr_message) * 1000);
									// get the end time
									end = System.currentTimeMillis();
									// calculate the total elapsed time the thread was asleep
									sec = (end - start) / 1000F;
									// send message with sleep time to server
									out.println("Client " + username + " waited " + sec + " seconds for server.");
								}
								// if the thread was interrupted with the "Resume Thread" button...
								catch (InterruptedException ie) {
									// get the end time
									end = System.currentTimeMillis();
									// calculate the total elapsed time the thread was asleep
									sec = (end - start) / 1000F;
									// send message with sleep time to server
									out.println("Client " + username + " waited " + sec + " seconds for server.");
								}
							}
						}
						// this catch handles a server disconnection
						catch (IOException i) {
							// set svr_num and svr_message variables to let the GUI thread know what
							// happened
							svr_num = 5;
							svr_message = "Server disconnected.";

							// notify the GUI thread that messages are waiting
							Notify();

							socket.close(); // close the socket
							break; // break from the loop, ending the thread
							// disconnect code here because the server quit
						}
					} // end of while loop
				}
				// couldn't connect to server:
				catch (UnknownHostException u) {
					System.out.println(u);
				}
				// couldn't connect input/output streams:
				catch (IOException i) {
					System.out.println(i);
				}

			}

		}; // end of thread code
		t.start(); // starts the new thread
	}

	public synchronized void Waiting() {
		// the main thread controlling the GUI waits here for messages from the
		// thread managing the server connection
		// code modified from
		// https://www.tutorialspoint.com/java/java_thread_communication.htm
		// EventQueue code modified from http://zetcode.com/javaswing/firstprograms/
		// ActionListener code modified from https://javatutorial.net/jframe-buttons-listeners-text-fields
		// no inputs
		// communication occurs through global variables:
		// flag - false if there is a new message
		// svr_num - number from server or connection thread used in switch
		// svr_message - message from server or connection thread

		// run until the quit button is pressed
		while (true) {
			if (flag) { // if there is no message from the connection thread wait
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			switch (svr_num) {
			// initial value - default case
			case 0:
				break;

			// case 1 is duplicate username from server
			case 1:
				view.getMessage().setText(svr_message); // sets message on GUI
				break;

			// case 2 is client accepted from server
			case 2:
				view.getMessage().setText(svr_message); // sets message on GUI

				// turn off the current view which is the initial GUI
				EventQueue.invokeLater(() -> {

					view.setVisible(false);
				});

				// create a new GUI showing the server connection
				view = new client_view(username + " is now connected to the server.", x, y);

				// show the new GUI
				EventQueue.invokeLater(() -> {

					view.setVisible(true);
				});

				// listen for the quit button to be pressed
				view.getQuitButton().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						quit = true; // set quit = true if quit button pressed
					}
				});
				break;

			// case 3 is countdown received from server
			case 3: // turn off the current view
				EventQueue.invokeLater(() -> {

					view.setVisible(false);
				});
				// set message to display on GUI
				String mssg = username + " received countdown for " + svr_message + " seconds";

				// create new view showing time received from server and countdown
				view = new client_view(mssg, x, y);

				// show the new GUI
				EventQueue.invokeLater(() -> {

					view.setVisible(true);
				});

				// listen for the quit button to be pressed
				view.getQuitButton().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						quit = true; // set quit = true if quit button pressed
						t.interrupt(); // interrupt the connection thread if it is asleep
					}
				});

				// listen for the "Resume Thread" button to be pressed
				view.getStopTimerButton().addActionListener(new ActionListener() {
					@Override
					// if "Resume Thread" button is pressed...
					public void actionPerformed(ActionEvent arg0) {
						// Set message on GUI to say '"Resume Thread" button pressed'
						view.getMessage().setText("\"Resume Thread\" button pressed");

						t.interrupt(); // interrupt the connection thread
						view.getTimer().stop(); // stop timer

						// remove the countdown label (clock) and the "Resume Thread" button and repaint
						// GUI
						Container parent = view.getClock().getParent();
						parent.remove(view.getClock());
						parent.remove(view.getStopTimerButton());
						parent.revalidate();
						parent.repaint();

						// Set message to Thread Awake. This coincides with the connection thread
						// waking and sending a message to the server with actual time asleep
						view.getMessage().setText("Thread awake.");
					}
				});
				break;

			// case 5 is message from connection thread - server disconnected
			case 5: // turn off the current view
				EventQueue.invokeLater(() -> {

					view.setVisible(false);
				});
				// set message to display on GUI
				view.getMessage().setText(svr_message);

				// create new view like initial view where client can login
				view = new client_view(title, x, y);

				// show the new GUI
				EventQueue.invokeLater(() -> {

					view.setVisible(true);
				});

				// listen for the connect button and send username to thread_connection method
				view.getConnectButton().addActionListener(new ActionListener() {
					@Override
					// Perform this action when the connect button is pressed:
					public void actionPerformed(ActionEvent arg0) {
						username = view.getUser().getText(); // get username from text field

						// call thread_connection method to create connection thread
						thread_connection(username);
					}
				});

				// listen for the quit button and exit the process from the initial GUI
				view.getQuitButton().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						System.exit(0);
					}
				});
			}
			flag = true; // this means there is no message from the connection thread
		}
	}

	public synchronized void Notify() {
		// the thread managing the server connection notifies the main thread
		// controlling the GUI
		// that there is a message from the connection thread or the server
		// code modified from
		// https://www.tutorialspoint.com/java/java_thread_communication.htm
		// no inputs

		flag = false; // there is a message from the connection thread
		notify(); // notifies the main thread
	}

}
