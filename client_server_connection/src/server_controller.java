// Patricia Vines
// 1000536317

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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class server_controller {
	// client controller class manages the client GUI and server connection

	private Socket socket = null; // socket that connects to clients
	private ServerSocket server = null; // server socket that listens for clients
	private BufferedReader in = null; // input stream for client sockets
	private PrintWriter out = null; // output stream for client sockets
	private ArrayList<String> clients = new ArrayList<String>(); // list of connected clients
	private server_view view = null; // Server GUI class object
	private String chosen_client = ""; // client chosen to send a number to
	private String time = ""; // number from 3-9 to send to the chosen client
	private Semaphore mutex = new Semaphore(1); // semaphore for setting and retrieving client and time
	private boolean quit = false; // boolean used when quit button is clicked

	public server_controller(int port, int x, int y) {
		// constructor for server controller
		// socket code modified from https://www.geeksforgeeks.org/socket-programming-in-java/
		// EventQueue code modified from http://zetcode.com/javaswing/firstprograms/
		// ActionListener code modified from https://javatutorial.net/jframe-buttons-listeners-text-fields
		// inputs:
		// port - port number of server process
		// x - int x position of window
		// y - int y position of window

		// create server GUI
		this.view = new server_view(x, y);

		// start the 10 second timer to pause connection threads
		timer();

		// Add the action to make the GUI visible to the event queue of the event
		// dispatching thread
		EventQueue.invokeLater(() -> {

			view.setVisible(true);
		});

		// listen for the quit button and exit the process from the initial GUI
		view.getQuitButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				quit = true; // set quit boolean to true if quit button pressed
			}
		});

		try {
			// create the server socket using the given port number
			server = new ServerSocket(port);

			// server keeps listening for clients until the process ends
			while (true) {
				// set up the socket when the server is contacted by a client
				socket = server.accept();
				System.out.println("new socket accept");
				// set up input and output streams to read and write messages
				// to/from the client socket
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				// user variable is the username of the client
				String user = "";
				// line variable is what is read in from the client
				String line = "";

				try {
					// wait for the client to send the username
					while (!in.ready()) {
					}

					// store the username in user
					user = in.readLine();
					System.out.println("user = " + user);
					System.out.println("clients = " + clients);
					// check if the given username is already connected to the server
					if (clients.contains(user)) {
						// if username found in clients, send code 1 and message to client
						out.println("1");
						out.println("Username already in use. Choose another username.");
					} else {
						// if username not found in clients, add the user
						clients.add(user);

						// if username is unique, create a new thread to manage the connection
						// in server_connection method
						server_connection(user);
						// send code 2 and message to client
						out.println("2");
						out.println("Client accepted.");

						// loop through clients list and create a string m with names of clients
						String m = "";
						if (clients.size() > 0)
							m = clients.get(0);
						for (int i = 1; i < clients.size(); i++) {
							m = m + ", " + clients.get(i);
						}
						// update the connected message with list of all connected users
						view.getConnected().setText(m + " connected to server");
						// set message to say that the latest user just connected
						view.getMessage().setText(user + " connected");
					}
				}
				// couldn't connect to client:
				catch (IOException i) {
					// remove client from list of clients if client is not connected
					if (clients.contains(user))
						clients.remove(user);
				}
			}

		}
		// couldn't connect server socket:
		catch (

		IOException i) {
		}
	}

	public void server_connection(String user) {
		// method creates a new thread to manage the connection with each client
		// socket code modified from https://www.geeksforgeeks.org/socket-programming-in-java/
		// mutex code from https://www.baeldung.com/java-mutex
		// inputs:
		// user - username of registered client

		// set user1 to user
		String user1 = user;
		Thread t = new Thread() {

			public void run() { // code that the connection thread should execute
				try {
					// set the socket to the socket just created for the client
					Socket socket1 = socket;

					// set the input and output to input/output just created for client
					BufferedReader in1 = in;
					PrintWriter out1 = out;

					// line variable stores what is read in from the client
					String line = "";

					// keep going until the client quits (by sending "OVER" or the
					// quit button is pressed and quit boolean is set to true
					while (!(line.equals("OVER") || quit)) {
						boolean itsme = false; // flag set to true if this client is chosen

						// if there is input from the client, read it
						if (in1.ready())
							line = in1.readLine();

						// if the chosen_client variable is set to this thread's
						// client username, send the client code 3 along with the
						// time which is stored in the global variable "time"
						try {
							mutex.acquire();
							if (chosen_client.equals(user1)) {
								chosen_client = "";
								itsme = true; // flag set to true if this client is chosen
							}
							// mutex error:
						} catch (InterruptedException e) {
						} finally {
							mutex.release(); // release mutex
						}
						if (itsme) {
							out1.println("3");
							out1.println(time);
							// wait for client to finish pausing and send return message
							line = in1.readLine();
							// if the client quits while pausing, break from the loop
							if (line.equals("OVER"))
								break;
							// set the thread message to the client's response
							view.getThread().setText(line);
						}
					}

					// if the quit button was pressed, send code 5 and quit message to client
					// the exit process
					if (quit) {
						out1.println("5");
						out1.println("Server quit.");
						System.exit(0);
					} else { // client quit by sending "OVER" message
						// remove client username from clients list
						if (clients.contains(user1))
							clients.remove(user1);
						// close connection
						socket1.close();

						// loop through clients list and create a string m with names of clients
						String m = "";
						if (clients.size() > 0)
							m = clients.get(0);
						for (int i = 1; i < clients.size(); i++) {
							m = m + ", " + clients.get(i);
						}
						// if there are no clients, left, set m to "No clients"
						if (m.equals("")) {
							m = "No clients";
							view.getThread().setText("");
						}

						// update the connected message with list of all connected users
						view.getConnected().setText(m + " connected to server");
						// set message to say that the user just disconnected
						view.getMessage().setText(user1 + " disconnected");
					}
				}
				// client socket connection error:
				catch (IOException i) {
					// remove client username from clients list
					if (clients.contains(user1))
						clients.remove(user1);

					// loop through clients list and create a string m with names of clients
					String m = "";
					if (clients.size() > 0)
						m = clients.get(0);
					for (int j = 1; j < clients.size(); j++) {
						m = m + ", " + clients.get(j);
					}
					// if there are no clients, left, set m to "No clients
					if (m.equals("")) {
						m = "No clients";
						view.getThread().setText("");
					}

					// update the connected message with list of all connected users
					view.getConnected().setText(m + " connected to server");
					// set message to say that the user just disconnected
					view.getMessage().setText(user1 + " disconnected");
				}
			}
		};
		t.start();

	}

	public void timer() {
		// timer method to randomly choose a connected client and choose a random number
		// from 3 to 9 every 10 seconds
		// modified code from
		// https://stackoverflow.com/questions/12908412/print-hello-world-every-x-seconds
		// mutex code from https://www.baeldung.com/java-mutex
		// no inputs
		// communicates with connection threads using global variables chosen_client and
		// time
		// uses Semaphore mutex to prevent other threads from modifying the variables
		// during this time
		// uses client list to choose a random connected client

		// create runnable
		Runnable clientRunnable = new Runnable() {
			public void run() {
				try {
					// set semaphore mutex to acquire
					mutex.acquire();
					// if there is at least one client connected...
					if (clients.size() > 0) {
						Random rand = new Random(); // create instance of class Random
						// choose a random client from the client list by generating a
						// random number in the range of the connected clients
						chosen_client = clients.get(rand.nextInt(clients.size()));
						// generate a random number between 3 and 9 by generating a random
						// number between 0 and 6 and adding 3
						time = Integer.toString(3 + rand.nextInt(7));
						// set the message for the GUI with the client username and random time
						view.getMessage().setText("Pausing " + chosen_client + " for " + time + " seconds.");
					}
					// mutex error:
				} catch (InterruptedException e) {
				} finally {
					mutex.release(); // release mutex
				}

			}
		};

		// create the threadpool that will execute the runnable repeatedly
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		// schedule the timing of the task - set to 10 repeat every 10 seconds
		executor.scheduleAtFixedRate(clientRunnable, 0, 10, TimeUnit.SECONDS);
	}

	public static void main(String args[]) { 
		// Starts server with arguments port: 16993
		// and x and y positions of window
		server_controller server = new server_controller(16993, 700, 200);
	}
}