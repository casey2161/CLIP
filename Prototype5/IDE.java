package com;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class IDE extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JButton open;
	private JButton run;
	private JButton save;
	private JButton clear;
	private JButton help;
	private JTextArea console;
	private JTextArea program;
	private JLabel consoleLabel;
	private String message;
	private final JFileChooser fileChooser;
	private Compiler compiler;
	
	//TODO this creates the layout and components for the IDE
	public IDE()
	{
		super(new BorderLayout());
		
		//this loads the string for the help box
		message = "To use the say command:\n   say <variable or quoted string>\n\nAssignment:\n   <variable> is <number expression, variable, or string>\n\nWhile Loop:\n   while <condition>\n      <statements>\n   end\n\nIf Statements:\n    if <condition> \n        <statements> \n    end";
		
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
		
		//Save Button
		save = new JButton("Save");
		save.addActionListener(this);
		
		//Clear Button
		clear = new JButton("Clear");
		clear.addActionListener(this);
		
		//Help Button
		help = new JButton("Help");
		help.addActionListener(this);
		
		//JTextArea and JScrollBar are created
		console = new JTextArea(10, 50);
		//console.setBorder(border);
		console.setMargin(new Insets(5,5,5,5));
		//This is setting System.out to print at the text area
		TextAreaOutputStream textOut = new TextAreaOutputStream(console);
		PrintStream outStream = new PrintStream(textOut, true);
		System.setOut(outStream);
		JScrollPane scroll = new JScrollPane(console);		
		consoleLabel = new JLabel("Console:");
		
		//panel for the scroll pane and label
		JPanel consolePanel = new JPanel(new BorderLayout());
		consolePanel.add(consoleLabel,BorderLayout.WEST);
		consolePanel.add(scroll, BorderLayout.SOUTH);
		
		//The Program Editor
		program = new JTextArea(20,100);
		JScrollPane scroll2 = new JScrollPane(program);
		JLabel programLabel = new JLabel("Program Editor:");
		
		JPanel editorPanel = new JPanel(new BorderLayout());
		editorPanel.add(programLabel,BorderLayout.WEST);
		editorPanel.add(scroll2,BorderLayout.SOUTH);
		
		//Separate Panels for the buttons then putting
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(open);
		buttonPanel.add(save);
		buttonPanel.add(run);
		buttonPanel.add(clear);
		buttonPanel.add(help);
		
		//Panel that combines the button Panel and the editor panel
		JPanel userPanel = new JPanel(new BorderLayout());
		userPanel.add(buttonPanel, BorderLayout.WEST);
		userPanel.add(editorPanel,BorderLayout.SOUTH);
		
		//Adds everything to the main panel a.k.a this
		this.add(userPanel, BorderLayout.CENTER);
		this.add(consolePanel, BorderLayout.SOUTH);		
		
		//This is creating the compiler
		compiler = new Compiler(console,program);
	}

	public JTextArea getConsole()
	{
		return console;
	}
	
	public void run()
	{
		
	}
	
	//TODO This handles what happens when buttons are pressed
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == open)
		{
			int returnVal = fileChooser.showOpenDialog(IDE.this);
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fileChooser.getSelectedFile();
	            if(file.getName().contains(".txt"))
	            {
	            	program.setText("");
	            	//This loads the file into the program editor
	            	try
	            	{
	            		Scanner in = new Scanner(file);
	            		while(in.hasNext())
	            		{
	            			program.append(in.nextLine()+"\n");
	            		}
	            	}
	            	catch(Exception exception)
	            	{
	            		System.out.print("This shouldn't happen ever \n");
	            	}
	            }
	            else
	            {
	            	System.out.print("Error: Can't open that file type \n");
	            }
	        }
		}
		else if(e.getSource() == save)
		{
			int returnVal = fileChooser.showSaveDialog(IDE.this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				PrintWriter out;
				try {
					out = new PrintWriter(new BufferedWriter(new FileWriter(fileChooser.getSelectedFile())));
					program.write(out);
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		else if(e.getSource() == run)
		{
			compiler = new Compiler(console,program);
			if(compiler.canRun())
			{
				compiler.run();
			}
			else
			{
				System.out.print("No program loaded \n");
			}
		}
		else if(e.getSource() == clear)
		{
			console.setText("");
		}
		else if(e.getSource() == help)
		{
			JOptionPane.showMessageDialog(this,message,"Help",JOptionPane.QUESTION_MESSAGE);
		}

	}
	
}
