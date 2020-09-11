// Patricia Vines
// 1000536317

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class server_view extends JFrame {
	    // JFrame window used for the server GUI.
		
		// displays message when client connects or pause message is sent
		private JLabel message = new JLabel("");
		// displays which clients are connected
    	private JLabel connected = new JLabel("No clients connected to the server");
    	// button that ends the server process
    	private JButton quitButton = new JButton("Quit");
    	// displays message from client when thread resumes
    	private JLabel thread = new JLabel("");
    	// text size for all labels and buttons
    	private int text = 20;
		
	    public server_view(int x, int y) {
	    	// constructor method for server GUI
	    	// modified from code found at
	    	// http://zetcode.com/javaswing/firstprograms/
	    	// inputs:
	    	// x - x position of server window
	    	// y - y position of server window
	    	
	    	// call initUI to start the server GUI
	        initUI(x, y);
	    }

	    private void initUI(int x, int y) {
	    	// sets labels/buttons for server window
	    	// modified from code found at
	    	// http://zetcode.com/javaswing/firstprograms/
	    	// inputs:
	    	// x - x position of server window
	    	// y - y position of server window
	    	
	    	// Set text size for all labels and buttons to work on my 4K screen
	    	message.setFont(new Font("Serif", Font.PLAIN, text));
	    	connected.setFont(new Font("Serif", Font.PLAIN, text));
	    	quitButton.setFont(new Font("Serif", Font.PLAIN, text));
	    	thread.setFont(new Font("Serif", Font.PLAIN, text));

	    	// set the layout for labels and buttons
	        createLayout(connected, message, thread, quitButton);
	        
	        setTitle("Server");  // set GUI title to Server
	        setSize(500, 300);  // set size of GUI
	        setLocation(x, y);  // set location of GUI
	        setDefaultCloseOperation(EXIT_ON_CLOSE);  // set default close operation to exit
	    }
    
	    private void createLayout(JComponent... arg) {
	    	// sets JFrame layout for the server window
	    	// modified from code found at
	    	// http://zetcode.com/javaswing/firstprograms/
	    	// inputs: 
	    	// connected message JLabel - arg[0]
	    	// Server message JLabel - arg[1]
	    	// Thread message JLabel - arg[2]
	    	// quit JButton - arg[3]
	    	
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
	        		// connected message on top
	                .addComponent(arg[0])
	                // add Server message
	                .addComponent(arg[1])
	                // add Thread message
	                .addComponent(arg[2])
	                // add quit button
	                .addComponent(arg[3])
	        );

	        // set the buttons and labels in the vertical group
	        gl.setVerticalGroup(gl.createSequentialGroup()
	        		// connected message on top
	        		.addComponent(arg[0])
	        		// add Server message
	                .addComponent(arg[1])
	                // add Thread message
	                .addComponent(arg[2])
	                // add quit button
	                .addComponent(arg[3])
	        );
	    }


	// Getters for labels and quit button
	public JLabel getMessage() {
		return message;
	}

	public JButton getQuitButton() {
		return quitButton;
	}


	public JLabel getConnected() {
		return connected;
	}
	
	public JLabel getThread() {
		return thread;
	}


}