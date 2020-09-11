// Patricia Vines
// 1000536317

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class client_view extends JFrame {
	// JFrame window used for each client GUI.
	
	// displays messages from the server on the client GUI
	private JLabel message = new JLabel("");  
	// button used to connect to the server after username is entered
	private JButton connectButton = new JButton("Connect");  
	// button that resumes thread while it is sleeping
	private JButton stopTimerButton = new JButton("Resume Thread");  
	// button that disconnects the client from the server and ends the client process
	private JButton quitButton = new JButton("Quit"); 
	// input field for username
	private JTextField user = new JTextField(5);   
	 // displays server connection status of client
	private JLabel connected = new JLabel("Client is not connected to the server");
	
	public JLabel clock;  // displays the thread countdown
	private int text = 20;  // text size for all labels and buttons
	private Timer timer;   // timer used for the thread countdown
	
    public client_view(String title, int x, int y) {
    	// constructor method for the client GUI
    	// inputs:
    	// title - String used to identify the type of window needed
    	// x - int x position of window
    	// y - int y position of window
    	
    	// Set text size for all labels and buttons to work on my 4K screen
    	message.setFont(new Font("Serif", Font.PLAIN, text));
    	connectButton.setFont(new Font("Serif", Font.PLAIN, text));
    	stopTimerButton.setFont(new Font("Serif", Font.PLAIN, text));
    	quitButton.setFont(new Font("Serif", Font.PLAIN, text));
    	user.setFont(new Font("Serif", Font.PLAIN, text));
    	connected.setFont(new Font("Serif", Font.PLAIN, text));
    	
    	// Set input box size for username
        user.setMaximumSize(new Dimension(150,10));
    	
        // If "countdown" is in the title, the GUI needs the timer and "Resume Thread" button
    	if (title.contains("countdown")) {
    		// parse title to get username and time to pause
    		String[] message = title.split(" ");   
    		// call countdown method to start timer and create GUI
    		countdown(message[4], message[0], x, y);  
    	}
    	// If "connected" is in the title, the GUI needs to  remove the username input field and "Connect" button
    	else if (title.contains("connected"))
    		connected(title, x, y); // call connected method to create new GUI 
    	
    	//Else either the process has just been started or the server has shut down
    	else
    		initUI(title, x, y);// call initUI method to create initial GUI 
    }
    
    public void countdown(String time, String user, int x, int y) {
    	// countdown method sets the timer and countdown label and displays them
		// code modified from https://coderanch.com/t/339424/java/setup-Countdown-Timer-Swing-App
    	// and http://zetcode.com/javaswing/firstprograms/
    	// and https://stackoverflow.com/questions/27587048/how-can-i-remove-jbutton-from-jframe
    	// inputs:
    	// time - number between 3 and 9 sent to client from server
    	// user - username to add to the title of the window
    	// x - x position of window
    	// y - y position of window
    	
    	// set the timer to count in increments of 1 second (1,000 ms)
    	timer = new Timer(1000, null);  
    	// set the countdown to the time passed from the server (*1000 to get seconds)
		long countdown = Integer.parseInt(time)*1000;  
		// set the label format to seconds in 1 digit
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("s"); 
        // set the label to format and countdown time
        clock = new JLabel(sdf.format(new Date(countdown)),JLabel.CENTER); 
        // set label text formatting to match other labels
    	clock.setFont(new Font("Serif", Font.PLAIN, text));  
    	
        int z = 0;
        timer.addActionListener(new ActionListener(){  // listen for the timer
          long z = countdown - 1000;  // set the value to decrement the label by 1 second
          public void actionPerformed(ActionEvent ae){  // when the timer ticks one second...
            clock.setText(sdf.format(new Date(z)));  // set the text on the label to one second less
            z -= 1000;  // set the value to decrement the label by 1 second next time
            if (z == -1000) {  // when the timer hits 0 (-1 since z has already been decremented)...
            	timer.stop();  // stop the timer
            	
            	// remove the countdown label (clock) and the "Resume Thread" button and repaint GUI
            	Container parent = clock.getParent();
            	parent.remove(clock);
            	parent.remove(stopTimerButton);
            	parent.revalidate();
            	parent.repaint();
            	
            	// Set message to Thread Awake. This coincides with the connection thread
            	// waking and sending a message to the server with actual time asleep
                message.setText("Thread awake.");  
                								   
            }
           }
        });
        timer.start();  // start the timer
        
        // set the top label to show that the client is connected to the server
        connected.setText(user+" is connected to the server");  
        // set the next label to show that the connection thread is sleeping
        message.setText("Connection thread sleeping:"); 
        
        // set the layout for labels and buttons
        timerLayout(connected, message, clock, stopTimerButton, quitButton); 
        
        setTitle(user);  // set GUI title to the username
        setSize(500, 300);  // set size of GUI
        setLocation(x,y);  // set location of GUI
        setDefaultCloseOperation(EXIT_ON_CLOSE);  // set default close operation to exit
	}
    
    public void connected(String msg, int x, int y) {
    	// sets message, title, and labels/buttons after client has connected to server
    	// modified from code found at
    	// http://zetcode.com/javaswing/firstprograms/
    	// inputs:
    	// msg - connect message from the server
    	// x - x position of window
    	// y - y position of window
    	
    	// set the top label to show that the client is connected (message from server)
		connected.setText(msg); 
		
		// set the layout for labels and buttons
		connectedLayout(connected, message, quitButton);  
		
		 // set GUI title to the username (first word in message from server)
		setTitle(msg.split(" ")[0]); 
		
        setSize(500, 300);  // set size of GUI
        setLocation(x,y);  // set location of GUI
        setDefaultCloseOperation(EXIT_ON_CLOSE);  // set default close operation to exit
	}

    private void initUI(String title, int x, int y) {
    	// sets username and labels/buttons for initial window or after server disconnect
    	// modified from code found at
    	// http://zetcode.com/javaswing/firstprograms/
    	// inputs:
    	// title - Client # for window title
    	// x - x position of window
    	// y - y position of window
    	
    	// create a label that prompts the user for a username
    	JLabel username = new JLabel("Enter Username: "); 
    	// set label text formatting to match other labels
    	username.setFont(new Font("Serif", Font.PLAIN, text));  
    	
    	// set the layout for labels and buttons
        createLayout(connected, username, user, message, connectButton, quitButton);  
        
        // set the "Connect" button to default
        this.getRootPane().setDefaultButton(connectButton);
        
        setTitle(title);  // set GUI title to the client #
        setSize(500, 300);  // set size of GUI
        setLocation(x,y);  // set location of GUI
        setDefaultCloseOperation(EXIT_ON_CLOSE);  // set default close operation to exit
    }
    
    
    private void createLayout(JComponent... arg) {
    	// sets JFrame layout for the initial window or window if server has disconnected
    	// modified from code found at
    	// http://zetcode.com/javaswing/firstprograms/
    	// inputs: 
    	// connected message JLabel - arg[0]
    	// username prompt JLabel - arg[1]
    	// username text field - arg[2]
    	// message from server JLabel - arg[3]
    	// connect JButton - arg[4]
    	// quit JButton - arg[5]
    	
    	// get the layer of the JFrame that holds the objects
        Container pane = getContentPane();  
        // create the layout for the objects
        GroupLayout gl = new GroupLayout(pane);
        // set the created layout to the container layer
        pane.setLayout(gl);

        // set gaps to auto
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        // set the buttons and labels in the horizontal group
        gl.setHorizontalGroup(gl.createParallelGroup()
        		// connection message on top
        		.addComponent(arg[0])
        		// add username prompt and entry field next to each other
                .addGroup(gl.createSequentialGroup()
                		// username prompt
                		.addComponent(arg[1])
                		// username entry field
                		.addComponent(arg[2]))
                // under username, add message from server
                .addComponent(arg[3])
                // add connect and quit buttons next to each other
                .addGroup(gl.createSequentialGroup()
                		// connect button
                		.addComponent(arg[4])
                		// quit button
                		.addComponent(arg[5]))
        );
        // set the buttons and labels in the vertical group
        gl.setVerticalGroup(gl.createSequentialGroup()
        		// connection message on top
        		.addComponent(arg[0])
        		// add username prompt and entry field next to each other
                .addGroup(gl.createParallelGroup()
                		// username prompt
                		.addComponent(arg[1])
                		// username entry field
                		.addComponent(arg[2]))
                // under username, add message from server
                .addComponent(arg[3])
                // add connect and quit buttons next to each other
                .addGroup(gl.createParallelGroup()
                		// connect button
                		.addComponent(arg[4])
                		// quit button
                		.addComponent(arg[5]))
        );
    }
    
    public void connectedLayout(JComponent... arg) {
    	// sets JFrame layout for the window after client has connected to the server
    	// removes username inputs and connect button from createLayout
    	// modified from code found at
    	// http://zetcode.com/javaswing/firstprograms/
    	// inputs: 
    	// connected message JLabel - arg[0]
    	// message from server JLabel - arg[1]
    	// quit JButton - arg[2]
    	
    	// get the layer of the JFrame that holds the objects
        Container pane = getContentPane();
        // create the layout for the objects
        GroupLayout gl = new GroupLayout(pane);
        // set the created layout to the container layer
        pane.setLayout(gl);

        // set gaps to auto
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        // set the buttons and labels in the horizontal group
        gl.setHorizontalGroup(gl.createParallelGroup()
        			// connection message
        			.addComponent(arg[0])
        			// other message in case it's needed
        			.addComponent(arg[1])
        			// quit button
                	.addComponent(arg[2])
        );

        // set the buttons and labels in the vertical group
        gl.setVerticalGroup(gl.createSequentialGroup()
        		// connection message
        		.addComponent(arg[0])
    			// other message in case it's needed
    			.addComponent(arg[1])
    			// quit button
            	.addComponent(arg[2])
        );
    }
    
    public void timerLayout(JComponent... arg) {
    	// sets JFrame layout for the window after server has sent countdown time
    	// adds countdown JLabel and "Resume Thread" Button to connectLayout
    	// modified from code found at
    	// http://zetcode.com/javaswing/firstprograms/
    	// inputs: 
    	// connected message JLabel - arg[0]
    	// message JLabel - arg[1]
    	// countdown JLabel - arg[2]
    	// "Resume Thread" JButton - arg[3]
    	// quit JButton - arg[4]
    	
    	// get the layer of the JFrame that holds the objects
        Container pane = getContentPane();
        // create the layout for the objects
        GroupLayout gl = new GroupLayout(pane);
        // set the created layout to the container layer
        pane.setLayout(gl);

        // set gaps to auto
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        // set the buttons and labels in the horizontal group
        gl.setHorizontalGroup(gl.createParallelGroup()
        		// connection message
        		.addComponent(arg[0])
        		// message about countdown
        		.addComponent(arg[1])
        		// countdown timer
                .addComponent(arg[2])
                // add "Resume Thread" and quit buttons next to each other
                .addGroup(gl.createSequentialGroup()
                		// "Resume Thread" button
                		.addComponent(arg[3])
                		// quit button
                		.addComponent(arg[4]))
        );

        // set the buttons and labels in the horizontal group
        gl.setVerticalGroup(gl.createSequentialGroup()
        		// connection message
        		.addComponent(arg[0])
        		// message about countdown
        		.addComponent(arg[1])
        		// countdown timer
                .addComponent(arg[2])
                // add "Resume Thread" and quit buttons next to each other
                .addGroup(gl.createParallelGroup()
                		// "Resume Thread" button
                		.addComponent(arg[3])
                		// quit button
                		.addComponent(arg[4]))
        );
    }
    
    //Getters for labels, buttons, timer, and username input field
	public JLabel getMessage() {
		return message;
	}

	public JButton getConnectButton() {
		return connectButton;
	}

	public JButton getQuitButton() {
		return quitButton;
	}

	public JTextField getUser() {
		return user;
	}

	public JLabel getConnected() {
		return connected;
	}

	public JLabel getClock() {
		return clock;
	}

	public JButton getStopTimerButton() {
		return stopTimerButton;
	}
	
	public Timer getTimer() {
		return timer;
	}

}