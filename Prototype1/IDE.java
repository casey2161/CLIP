import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class IDE extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JButton open;
	private JButton run;
	private JTextArea console;
	private JLabel consoleLabel;
	private final JFileChooser fileChooser;
	private Compiler compiler;
	
	//TODO this creates the layout and components for the IDE
	public IDE()
	{
		super(new BorderLayout());
		
		//This is the file chooser instantiation
		fileChooser = new JFileChooser();
		
		//Open Button
		open =  new JButton("Open");
		open.addActionListener(this);
		//open.setBorder(border);
		
		//Run Button
		run = new JButton("Run");
		run.addActionListener(this);
		//run.setBorder(border);
		
		//JTextArea and JScrollBar are created
		console = new JTextArea(10, 50);
		//console.setBorder(border);
		console.setMargin(new Insets(5,5,5,5));
		JScrollPane scroll = new JScrollPane(console);		
		consoleLabel = new JLabel("Console:");
		
		//panel for the scroll pane and label
		JPanel consolePanel = new JPanel(new BorderLayout());
		consolePanel.add(consoleLabel,BorderLayout.WEST);
		consolePanel.add(scroll, BorderLayout.SOUTH);
		
		//Seperate Panels for the buttons then putting
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(open);
		buttonPanel.add(run);
		
		
		
		//Adds everything to the main panel a.k.a this
		this.add(buttonPanel, BorderLayout.WEST);
		this.add(consolePanel, BorderLayout.SOUTH);		
		
		//This is creating the compiler
		compiler = new Compiler(console);
	}
	
	public JTextArea getConsole()
	{
		return console;
	}

	//TODO This handles what happens when buttons are pressed
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == open)
		{
			int returnVal = fileChooser.showOpenDialog(IDE.this);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fileChooser.getSelectedFile();
	            if(file.getName().contains(".txt"))
	            	compiler.setFile(file);
	            else
	            {
	            	console.append("Error: Can't open that file type \n");
	            }
	        }
		}
		else if(e.getSource() == run)
		{
			if(compiler.canRun())
			{
				compiler.run();
			}
			else
			{
				console.append("No program loaded \n");
			}
		}
	}
	
}
