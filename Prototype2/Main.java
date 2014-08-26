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
