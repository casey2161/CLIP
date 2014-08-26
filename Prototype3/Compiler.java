/* These imports are no longer used as of Prototype 2
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
*/
import java.util.ArrayList;
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
 * Prototype 2: October 7, 2012                                                                  *
 * Prototype 3: October 10, 2012                                                                 *
 * Finished:                                                                                     *
 * Last Modified: October 10, 2012                                                               *
 * Next Prototype Milestone: Incorporate looping either while or for or both                     *
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
			////System.out.println(curCommand);
			parseLine(curCommand);
			if(error)
			{
				console.append("Syntax Error at line: " + i + "\n" + "Line: " + curCommand+"\n");
				error = false;
				return;
			}
		}
	}
	
	public double parseLine(String line)
	{
		//TODO this is for printing stuff out until the IDE is up it only prints to the console
		if(line.contains("say") && !line.contains("\"") && !(line.contains("add")||line.contains("subtract")||line.contains("multiply")||line.contains("divide") ||line.contains("exp")))
		{
			line.trim();
			String[] lines = line.split(" ");
			if(variables.get(lines[1])!=null || variables.get(lines[1])!="null" && lines.length<3)
			{
				console.append(variables.get(lines[1]) + "\n");
			}
			else
			{
				error = true;
				return -1;
			}
		}
		else if(line.contains("say") && (line.contains("add")||line.contains("exp")||line.contains("subtract")||line.contains("multiply")||line.contains("divide")))
		{
			String[] lines = line.split("say");
			lines[1] = removeLeading(lines[1]);
			String temp = "";
			for(int i=1;i<lines.length;i++)
			{
				temp+=lines[i];
			}
			String temp1 = "";
			temp1+=parseLine(temp);
			console.append(temp1+ "\n");
		}
		else if(line.contains("say") && line.indexOf("\"")!=-1 && line.indexOf("\"",line.indexOf("\"")+1) !=-1)
		{
			line.trim();
			int x1 = line.indexOf("\"");
			int x2 = line.indexOf("\"", x1+1);
			console.append(line.substring(x1+1,x2)+"\n");
		}
		//TODO this is for setting variables to other variables or numbers
		else if(!(line.contains("add")||line.contains("exp")||line.contains("subtract")||line.contains("multiply")||line.contains("divide") || line.contains("\"") || line.equals("")))
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
		else if(line.indexOf("\"")!=-1 && line.indexOf("\"",line.indexOf("\"")+1) !=-1 && !(line.contains("add")||line.contains("exp")||line.contains("subtract")||line.contains("multiply")||line.contains("divide")))
		{
			line.trim();
			String[] lines = line.split("is");
			int x =lines[1].indexOf("\"");
			//System.out.println(x);
			lines[1] = lines[1].substring(x+1, lines[1].indexOf("\"", x+1) );
			lines[0] = trim(lines[0]);
			variables.put(lines[0],lines[1]);
		}
		//TODO this is for numbers and it doesn't support variable numbers anymore
		else if((line.contains("add")||line.contains("subtract")||line.contains("multiply")||line.contains("divide") ||line.contains("exp")) && line.contains("is"))
		{
			line.trim();
			String[] lines = line.split("is");
			//System.out.println(lines[1]);
			lines[1] = calculate(lines[1]);
			lines[0] = trim(lines[0]);
			//System.out.println(lines[0]);
			if(lines[1]!=null)
				variables.put(lines[0], lines[1]);
			else
				return -1;
		}
		else if (line.contains("add")||line.contains("subtract")||line.contains("multiply")||line.contains("divide") && !line.contains("is"))
		{
			return Double.parseDouble(calculate(line));
			
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
	
	//TODO this is used to calculate math expressions
	private String calculate(String line)
	{
		if(line.charAt(0) == ' ')
			line = removeLeading(line);
		//System.out.println(line);
		if(line.contains("(") && line.contains(")"))
		{
			int p1 = line.indexOf("(");
			int p2 = line.lastIndexOf(")");
			String temp3 = line.substring(p1+1,p2);
			line = line.replace(line.substring(p1,p2+1), calculate(temp3));
			
		}
		ArrayList<String> lines = new ArrayList<String>();
		String[] t = line.split(" ");
		for(int i=0;i<t.length;i++)
		{
			lines.add(t[i]);
		}
		boolean exp =false;
		boolean md = false;
		while(lines.size()>1)
		{
			//System.out.println(lines);
			for(int i=0;i<lines.size()-1;i++)
			{
				if(!exp && lines.get(i).equals("exp"))
				{
					double a;
					double b;
					if(isNumeric(lines.get(i-1)))
					{
						a = Double.parseDouble(lines.get(i-1));
					}
					else
					{
						if(isNumeric(variables.get(lines.get(i-1))))
						{
							a = Double.parseDouble(variables.get(lines.get(i-1)));
						}
						else
						{
							error = true;
							return null;
						}
					}
					if(isNumeric(lines.get(i+1)))
					{
						b = Double.parseDouble(lines.get(i+1));
					}
					else
					{
						if(isNumeric(variables.get(lines.get(i+1))))
						{
							b = Double.parseDouble(variables.get(lines.get(i+1)));
						}
						else
						{
							error = true;
							return null;
						}
					}
					String t1 = "";
					t1 += Math.pow(a, b);
					System.out.println(t1+ " "+ a + " " + b );
					lines.remove(i+1);
					lines.set(i, t1);
					lines.remove(i-1);
					break;
				}
				else if(exp && lines.get(i).equals("multiply") && !md)
				{
					double a;
					double b;
					if(isNumeric(lines.get(i-1)))
					{
						a = Double.parseDouble(lines.get(i-1));
					}
					else
					{
						if(isNumeric(variables.get(lines.get(i-1))))
						{
							a = Double.parseDouble(variables.get(lines.get(i-1)));
						}
						else
						{
							error = true;
							return null;
						}
					}
					if(isNumeric(lines.get(i+1)))
					{
						b = Double.parseDouble(lines.get(i+1));
					}
					else
					{
						if(isNumeric(variables.get(lines.get(i+1))))
						{
							b = Double.parseDouble(variables.get(lines.get(i+1)));
						}
						else
						{
							error = true;
							return null;
						}
					}
					String t1 = "";
					t1 += a*b;
					lines.remove(i+1);
					lines.set(i, t1);
					lines.remove(i-1);
					break;
				}
				else if(lines.get(i).equals("divide") && !md)
				{
					double a;
					double b;
					if(isNumeric(lines.get(i-1)))
					{
						a = Double.parseDouble(lines.get(i-1));
					}
					else
					{
						if(isNumeric(variables.get(lines.get(i-1))))
						{
							a = Double.parseDouble(variables.get(lines.get(i-1)));
						}
						else
						{
							error = true;
							return null;
						}
					}
					if(isNumeric(lines.get(i+1)))
					{
						b = Double.parseDouble(lines.get(i+1));
					}
					else
					{
						if(isNumeric(variables.get(lines.get(i+1))))
						{
							b = Double.parseDouble(variables.get(lines.get(i+1)));
						}
						else
						{
							error = true;
							return null;
						}
					}
					if(b==0)
					{
						error = true;
						return "-1";
					}
					String t1 = "";
					t1 += a/b;
					lines.remove(i+1);
					lines.set(i, t1);
					lines.remove(i-1);
					break;
				}
				else if(md && lines.get(i).equals("add"))
				{
					double a;
					double b;
					if(isNumeric(lines.get(i-1)))
					{
						a = Double.parseDouble(lines.get(i-1));
					}
					else
					{
						if(isNumeric(variables.get(lines.get(i-1))))
						{
							a = Double.parseDouble(variables.get(lines.get(i-1)));
						}
						else
						{
							error = true;
							return null;
						}
					}
					if(isNumeric(lines.get(i+1)))
					{
						b = Double.parseDouble(lines.get(i+1));
					}
					else
					{
						if(isNumeric(variables.get(lines.get(i+1))))
						{
							b = Double.parseDouble(variables.get(lines.get(i+1)));
						}
						else
						{
							error = true;
							return null;
						}
					}
					String t1 = "";
					t1 += a+b;
					lines.remove(i+1);
					lines.set(i, t1);
					lines.remove(i-1);
					break;
				}
				else if(md && lines.get(i).equals("subtract"))
				{
					double a;
					double b;
					if(isNumeric(lines.get(i-1)))
					{
						a = Double.parseDouble(lines.get(i-1));
					}
					else
					{
						if(isNumeric(variables.get(lines.get(i-1))))
						{
							a = Double.parseDouble(variables.get(lines.get(i-1)));
						}
						else
						{
							error = true;
							return null;
						}
					}
					if(isNumeric(lines.get(i+1)))
					{
						b = Double.parseDouble(lines.get(i+1));
					}
					else
					{
						if(isNumeric(variables.get(lines.get(i+1))))
						{
							b = Double.parseDouble(variables.get(lines.get(i+1)));
						}
						else
						{
							error = true;
							return null;
						}
					}
					String t1 = "";
					t1 += a+b;
					lines.remove(i+1);
					lines.set(i, t1);
					lines.remove(i-1);
					break;
				}
				if(exp && i==lines.size()-2 && !md)
				{
					md = true;
				}
				if(i==lines.size()-2 && !exp)
				{
					exp = true;
				}				
			}
		}
		return lines.get(0);
	}

	private String removeLeading(String line) 
	{
		String temp = "";
		for(int i=1;i<line.length();i++)
		{
			temp+=line.charAt(i);
		}
		return temp;
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
