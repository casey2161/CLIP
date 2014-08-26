import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.UIManager;


public class Main {

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			
		}
		UIManager.put("swing.boldMetal", Boolean.FALSE); 		
		IDE ide = new IDE();
		ide.setFocusable(true);
		ide.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_F11)
				{
					
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}});
		ide.requestFocus();
		
		//TODO to be named
		//JFrame creation
		JFrame frame = new JFrame();
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setTitle("To be Named");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(ide);
		frame.pack();
		frame.setVisible(true);
	}
}
