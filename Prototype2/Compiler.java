/* These imports are no longer used as of Prototype 2
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
*/
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextArea;

/*************************************************************************************************
 * This is the compiler in completion for partial graduation at the Alabama School of Fine Arts  *
 * Casey Michael Barnette                                                                        *
 * Senior Research Project                                                                       *
 *                                                                                               *
 * This is the compiler/interpretor portion of the program                                       *
 * For the purposes of bookmarking a TODO is used so that I may jump between portions of the code*
 * in eclipse                                                                                    *
 *                                                                                               *
 * Periods can't be used in variable names                                                       *
 *                                                                                               *
 * Started: August 29 2012                                                                       *
 * Prototype 1: September 17, 2012                                                               *
 * Finished:                                                                                     *
 * Last Modified: October 7 2012, 2012                                                             *
 *************************************************************************************************/

public class Compiler {
	 //The first string refers to the name while the second string refers to the value
	private Map<String, String> variables;
	/* No longer in use after prototype 2
	private ArrayList<String> commands = new ArrayList<String>();*/
	private boolean error;
	
	//this is the console
	public static JTextArea console;
	public static JTextArea program;
	public Compiler(JTextArea c, JTextArea p)
	{
		console = c;
		program = p;
		variables = new HashMap<String,String>();
		error = false;
	}
	
	public boolean isError()
	{
		return error;
	}
	
	public boolean canRun()
	{
		return program.getText() != "" || program.getText()!=null;
	}
	
	public void run()
	{
		//This was changed in prototype 2
		String consoleProgram = program.getText();
		String[] consoleCommands = consoleProgram.split("\n");

		for(int i=0;i<consoleCommands.length;i++)
		{
			String curCommand = consoleCommands[i];
			//System.out.println(curCommand);
			parseLine(curCommand);
			if(error)
			{
				console.append("Syntax Error at line: " + i + "\n" + "Line: " + curCommand+"\n");
				error = false;
				return;
			}
		}
	}
	
	public int parseLine(String line)
	{
		//TODO this is for printing stuff out until the IDE is up it only prints to the console
		if(line.contains("say") && !line.contains("\""))
		{
			line.trim();
			String[] lines = line.split(" ");
			if(!isNumeric(lines[1]))
			{
				if(variables.get(lines[1])!=null || variables.get(lines[1])!="null" && lines.length<3)
				{
					console.append(variables.get(lines[1]));
				}
				else
				{
					error = true;
					return -1;
				}
			}
			//this is to allow things like 2 add 2 minus 3 in a say command
			else
			{
				String temp = "";
				for(int i=1;i<lines.length;i++)
				{
					temp+=lines[1];
				}
				String temp1 = "";
				temp1+=parseLine(temp);
				console.append(temp1);
			}
		}
		else if(line.contains("say") && line.indexOf("\"")!=-1 && line.indexOf("\"",line.indexOf("\"")+1) !=-1)
		{
			line.trim();
			int x1 = line.indexOf("\"");
			int x2 = line.indexOf("\"", x1+1);
			console.append(line.substring(x1+1,x2)+"\n");
		}
		//TODO this is for setting variables to other variables or numbers
		else if(!(line.contains("add")||line.contains("subtract")||line.contains("multiply")||line.contains("divide") || line.contains("\"") || line.equals("")))
		{
			line.trim();
			String[] lines = line.split(" ");
			if(!isNumeric(lines[2]))
			{
				if(variables.get(lines[2]) != null ||  variables.get(lines[2])!="null")
				{
					variables.put(lines[0], variables.get(lines[2]));
				}
				else
				{
					error = true;
					return -1;
				}
			}
			else
			{
				variables.put(lines[0], lines[2]);
			}
			
		}
		//TODO this is for settting string variables
		else if(line.indexOf("\"")!=-1 && line.indexOf("\"",line.indexOf("\"")+1) !=-1 && !(line.contains("add")||line.contains("subtract")||line.contains("multiply")||line.contains("divide")))
		{
			line.trim();
			String[] lines = line.split("is");
			int x =lines[1].indexOf("\"");
			//System.out.println(x);
			lines[1] = lines[1].substring(x+1, lines[1].indexOf("\"", x+1) );
			lines[0] = trim(lines[0]);
			variables.put(lines[0],lines[1]);
		}
		//TODO this is for numbers
		else if(line.contains("add")||line.contains("subtract")||line.contains("multiply")||line.contains("divide"))
		{
			line.trim();
			String[] lines = line.split(" ");
			float temp = 0;
			//TODO start of the case for when it is just hello is 3 add 3
			if(lines.length ==5)
			{
				if(isNumeric(lines[2]) && isNumeric(lines[4]))
				{
					if(lines[3].equals("add"))
					{
						float a = Float.parseFloat(lines[2]);
						float b = Float.parseFloat(lines[4]);
						temp+=(a+b);
					}
					else if(lines[3].equals("subtract"))
					{
						float a = Float.parseFloat(lines[2]);
						float b = Float.parseFloat(lines[4]);
						temp+=(a-b);
					}
					else if(lines[3].equals("multiply"))
					{
						float a = Float.parseFloat(lines[2]);
						float b = Float.parseFloat(lines[4]);
						temp+=(a*b);
					}
					else if(lines[3].equals("divide"))
					{
						float a = Float.parseFloat(lines[2]);
						float b = Float.parseFloat(lines[4]);
						temp+=(a/b);
					}
				}
				else if(isNumeric(lines[2]) && ! isNumeric(lines[4]))
				{
					float a = Float.parseFloat(lines[2]);
					if(!isNumeric(variables.get(lines[4])))
					{
						error = true;
						return -1;
					}
					else 
					{
						float b = Float.parseFloat(variables.get(lines[4]));
						if(lines[3].equals("add"))
						{
							temp+=(a+b);
						}
						else if(lines[3].equals("subtract"))
						{
							temp+=(a-b);
						}
						else if(lines[3].equals("multiply"))
						{
							temp+=(a*b);
						}
						else if(lines[3].equals("divide"))
						{
							temp+=(a/b);
						}
					}
				}//This is if the second one is a variable and not a number
				else if(!isNumeric(lines[2]) && isNumeric(lines[4]))
				{
					float a = Float.parseFloat(lines[4]);
					System.out.println(variables.get(lines[2]));
					if(!isNumeric(variables.get(lines[2])))
					{
						error = true;
						return -1;
					}
					else 
					{
						float b = Float.parseFloat(variables.get(lines[2]));
						if(lines[3].equals("add"))
						{
							temp+=(a+b);
						}
						else if(lines[3].equals("subtract"))
						{
							temp+=(a-b);
						}
						else if(lines[3].equals("multiply"))
						{
							temp+=(a*b);
						}
						else if(lines[3].equals("divide"))
						{
							temp+=(a/b);
						}
					}
				}
				// TODO if both Numbers aren't numeric
				else if(!isNumeric(lines[2]) && !isNumeric(lines[4]))
				{
					if(isNumeric(variables.get(lines[2])) && isNumeric(variables.get(lines[4])))
					{
						float a = Float.parseFloat(variables.get(lines[2]));
						float b = Float.parseFloat(variables.get(lines[4]));
						if(lines[3].equals("add"))
						{
							temp+=(a+b);
						}
						else if(lines[3].equals("subtract"))
						{
							temp+=(a-b);
						}
						else if(lines[3].equals("multiply"))
						{
							temp+=(a*b);
						}
						else if(lines[3].equals("divide"))
						{
							temp+=(a/b);
						}
					}
				}
			}
				//End the if else chain for the chain where there is only something such as hello is 3 add 3
			else if(lines.length>5)
				{
					if(isNumeric(lines[2]) && isNumeric(lines[4]))
					{
						if(lines[3].equals("add"))
						{
							float a = Float.parseFloat(lines[2]);
							float b = Float.parseFloat(lines[4]);
							temp+=(a+b);
						}
						else if(lines[3].equals("subtract"))
						{
							float a = Float.parseFloat(lines[2]);
							float b = Float.parseFloat(lines[4]);
							temp+=(a-b);
						}
						else if(lines[3].equals("multiply"))
						{
							float a = Float.parseFloat(lines[2]);
							float b = Float.parseFloat(lines[4]);
							temp+=(a*b);
						}
						else if(lines[3].equals("divide"))
						{
							float a = Float.parseFloat(lines[2]);
							float b = Float.parseFloat(lines[4]);
							temp+=(a/b);
						}
					}
					else if(isNumeric(lines[2]) && ! isNumeric(lines[4]))
					{
						float a = Float.parseFloat(lines[2]);
						if(!isNumeric(variables.get(lines[4])))
						{
							error = true;
							return -1;
						}
						else 
						{
							float b = Float.parseFloat(variables.get(lines[4]));
							if(lines[3].equals("add"))
							{
								temp+=(a+b);
							}
							else if(lines[3].equals("subtract"))
							{
								temp+=(a-b);
							}
							else if(lines[3].equals("multiply"))
							{
								temp+=(a*b);
							}
							else if(lines[3].equals("divide"))
							{
								temp+=(a/b);
							}
						}
					}//This is if the second one is a variable and not a number
					else if(!isNumeric(lines[2]) && isNumeric(lines[4]))
					{
						float a = Float.parseFloat(lines[4]);
						if(!isNumeric(variables.get(lines[2])))
						{
							error = true;
							return -1;
						}
						else 
						{
							float b = Float.parseFloat(variables.get(lines[2]));
							if(lines[3].equals("add"))
							{
								temp+=(a+b);
							}
							else if(lines[3].equals("subtract"))
							{
								temp+=(a-b);
							}
							else if(lines[3].equals("multiply"))
							{
								temp+=(a*b);
							}
							else if(lines[3].equals("divide"))
							{
								temp+=(a/b);
							}
						}
					}
					// TODO if both Numbers aren't numeric
					else if(!isNumeric(lines[2]) && !isNumeric(lines[4]))
					{
						if(isNumeric(variables.get(lines[2])) && isNumeric(variables.get(lines[4])))
						{
							float a = Float.parseFloat(variables.get(lines[2]));
							float b = Float.parseFloat(variables.get(lines[4]));
							if(lines[3].equals("add"))
							{
								temp+=(a+b);
							}
							else if(lines[3].equals("subtract"))
							{
								temp+=(a-b);
							}
							else if(lines[3].equals("multiply"))
							{
								temp+=(a*b);
							}
							else if(lines[3].equals("divide"))
							{
								temp+=(a/b);
							}
						}
					}
					for(int i=6;i<lines.length;i+=2)
					{
						if(isNumeric(lines[i]))
						{
							if(lines[i-1].equals("add"))
							{
								temp+=Float.parseFloat(lines[i]);
							}
							else if(lines[i-1].equals("subtract"))
							{
								temp-=Float.parseFloat(lines[i]);
							}
							else if(lines[i-1].equals("multiply"))
							{
								temp*=Float.parseFloat(lines[i]);	
							}
							else if(lines[i-1].equals("divide"))
							{
								temp/=Float.parseFloat(lines[i]);
							}
						}
						else
						{
							if(lines[i-1].equals("add"))
							{
								temp+=Float.parseFloat(variables.get(lines[i]));
							}
							else if(lines[i-1].equals("subtract"))
							{
								temp-=Float.parseFloat(variables.get(lines[i]));
							}
							else if(lines[i-1].equals("multiply"))
							{
								temp*=Float.parseFloat(variables.get(lines[i]));
							}
							else if(lines[i-1].equals("divide"))
							{
								temp/=Float.parseFloat(variables.get(lines[i]));
							}
						}
					}
				}
			//this is so that I can add the float back to the variables
			String tempString  = "";
			tempString+=temp;
			variables.put(lines[0], tempString);
		}
		else if(line.equals(""))
		{
			
		}
		else
		{
			error = true;
			return -1;
		}
		return 0;
	}
	
	//TODO this is the trimming stuff for the strings
	private String trim(String string) {
		String ret = "";
		for(int i=0;i<string.length();i++)
		{
			if(string.charAt(i) != ' ' )
			{
				ret+=string.charAt(i);
			}
		}
		//System.out.println(ret);
		return ret;
		
	}

	//TODO this is to check if a string is a number
	public boolean isNumeric(String s)
	{
		for(int i=0;i<s.length();i++)
		{
			if(!Character.isDigit(s.charAt(i))&& s.charAt(i)!='.' && s.charAt(i)!='-')
			{
				return false;
			}
		}
		return true;
	}
	/* TODO This setFile method is no longer necesarry as of Prototype 2
	public void setFile(File file) {
		try {
			FileReader reader = new FileReader(file);
			Scanner in = new Scanner(reader);
			while(in.hasNextLine())
			{
				commands.add(in.nextLine());
			}
			reader.close();
			console.append("File Opened Successfully \n");
		} catch (FileNotFoundException e) {
			console.append("File does not exist \n");
		} catch (IOException e) {
			console.append("Can't close file \n");
		}
		
	}*/

}
