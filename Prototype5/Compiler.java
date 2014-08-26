package com;
/* These imports are no longer used as of Prototype 2
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
*/
import java.io.PrintStream;
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
 * Periods can't be used in variable names because I said so                                     *
 *                                                                                               *
 * Started: August 29 2012                                                                       *
 * Prototype 1: September 17, 2012                                                               *
 * Prototype 2: October 7, 2012                                                                  *
 * Prototype 3: October 10, 2012                                                                 *
 * Prototype 4: October 23, 2012                                                                 *
 * Finished: December 22, 2012                                                                   *
 * Last Modified: November 13, 2012                                                              *
 * Next Prototype Milestone: N/A                                                                 *
 *************************************************************************************************/

//TODO I need to reformat the way looping works

public class Compiler extends Thread{
	 //The first string refers to the name while the second string refers to the value
	//Variables can't have spaces, true, false, or any other keyword
	private Map<String, String> variables;
	// Put back into use after  prototype 3 to allow for looping
	private ArrayList<String> loopCommands = new ArrayList<String>();
	private boolean error;
	private boolean elif;
	private boolean loop;
	private String errMessage;
	private String[] keywords;
	
	//this is the console
	public static JTextArea console;
	public static JTextArea program;
	public Compiler(JTextArea c, JTextArea p)
	{
		console = c;
		program = p;
		variables = new HashMap<String,String>();
		error = false;
		loop = false;
		elif = false;
		errMessage = "";
		createKeywords();
		//This is setting System.out to print at the text area
		TextAreaOutputStream textOut = new TextAreaOutputStream(console);
		PrintStream outStream = new PrintStream(textOut, true);
		System.setOut(outStream);
	}
	
	private void createKeywords()
	{
		keywords = new String[4];
		keywords[0] = "is";
		keywords[1] = "if";
		keywords[2] = "say";
		keywords[3] = "while";
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
		
		//Check Error returns true if there is an error
		if(checkError(consoleProgram))
		{
			System.out.println(errMessage);
			return;
		}
		for(int i=0;i<consoleCommands.length;i++)
		{
			String curCommand = consoleCommands[i];
			////System.out.println(curCommand);
			parseLine(curCommand);
			if(error)
			{
				System.out.println("Syntax Error at line: " + i + "\n" + "Line: " + curCommand);
				error = false;
				return;
			}
		}
	}
	
	private boolean checkEven(String program, String token)
	{
		if(program.contains(token))
		{
			int counter = 0;
			for(int i=0;i<program.length();i++)
			{
				if(program.substring(i).contains(token))
				{
					i = program.indexOf(token,i);
					counter++;
				}
				else
				{
					i=program.length();
				}
			}
			if(counter%2 == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
		
	}
	private boolean checkParenth(String program)
	{		
		if(!program.contains("(") && !program.contains(")"))
		{
			return true;
		}
		int lIndex = 0;
		for(int i=0;i<program.length();i++)
		{
			if(program.substring(i).contains("(") && program.substring(lIndex).contains(")"))
			{
				lIndex = program.indexOf(")",lIndex);
				i = program.indexOf("(",i);
			}
			else
			{
				return false;
			}
		}
		return true;
	}
	private boolean checkIfWhile(String program)
	{
		int ifwhile =0;
		int ends = 0;
		for(int i=0;i<program.length();i++)
		{
			if(program.substring(i).contains("if"))
			{
				ifwhile++;
				i = program.indexOf("if",i) +1;
			}
		}
		for(int i=0;i<program.length();i++)
		{
			if(program.substring(i).contains("else"))
			{
				ifwhile++;
				i = program.indexOf("else",i) +1;
			}
		}
		for(int i=0;i<program.length();i++)
		{
			if(program.substring(i).contains("while"))
			{
				ifwhile++;
				i = program.indexOf("while",i)+1;
			}
		}
		for(int i=0;i<program.length();i++)
		{
			if(program.substring(i).contains("end"))
			{
				ends++;
				i = program.indexOf("end",i)+1;
			}
		}
		return ends == ifwhile;
	}
	private boolean checkError(String program) {
		if(program.contains(keywords[0]) || program.contains(keywords[1]) || program.contains(keywords[2]) || program.contains(keywords[3]))
		{
			if(checkEven(program,"\""))
			{
				if(checkParenth(program))
				{
					if(checkIfWhile(program))
					{
						return false;
					}
					else
					{
						errMessage = "Number of End statements does not match number of If and Whiles";
						return true;
					}
				}
				else
				{
					errMessage = "Parenthesis mis-match";
					return true;
				}
			}
			errMessage = "Incorrect number of \" either add one or remove one";
			return true;
		}
		errMessage = "No required keywords(" + keywords[0] + ", " + keywords[1] + ", " + keywords[2] + ", " + keywords[3] +  ") were found";
		return true;
	}

	private void runLoop()
	{
		String condition = loopCommands.get(0);
		condition = condition.replace("while ","");
		loopCommands.remove(0);
		//System.out.println(loopCommands);
		while(checkCondition(condition))
		{
			for(int i=0;i<loopCommands.size();i++)
			{
				parseLine(loopCommands.get(i));
			}
		}
		loopCommands.clear();
	}
	
	private void runIf()
	{
		String condition = loopCommands.get(0);
		condition = condition.replace("if ","");
		loopCommands.remove(0);
		if(checkCondition(condition))
		{
			for(int i=0;i<loopCommands.size();i++)
			{
				parseLine(loopCommands.get(i));
			}
		}
		loopCommands.clear();
	}
	
	//TODO checkConditon
	private boolean checkCondition(String line)
	{
		//For my language false is a 0 and true is any other number
		// Valid operators include or, and, less, equals, greater, le, ge
		//System.out.println("debug");
		
		//this is for parenthesis support
		if(line.charAt(0) == ' ')
			line = removeLeading(line);
		//System.out.println(line);
		if(line.contains("(") && line.contains(")"))
		{
			int p1 = line.indexOf("(");
			int p2 = line.lastIndexOf(")");
			String temp3 = line.substring(p1+1,p2);
			line = line.replace(line.substring(p1,p2+1), checkCondition(temp3)? "0":"1");			
		}
		//
		ArrayList<String> lines = new ArrayList<String>();
		String[] t = line.split(" ");
		for(int i=0;i<t.length;i++)
		{
			lines.add(t[i]);
		}
		while(lines.size()>1)
		{
			for(int i=0;i<lines.size()-1;i++)
			{
				if(lines.get(i).equals("greater"))
				{
					if(isNumeric(lines.get(i-1)))
					{
						float a = Float.parseFloat(lines.get(i-1));
						float b;
						if(isNumeric(lines.get(i+1)))
						{
							b = Float.parseFloat(lines.get(i+1));
						}
						else
						{
							if(isNumeric(variables.get(lines.get(i+1))))
							{
								b = Float.parseFloat(variables.get(lines.get(i+1)));
							}
							else
							{
								error = true;
								return false;
							}
						}
						if(a>b)
						{
							lines.remove(i+1);
							lines.set(i, "1");
							lines.remove(i-1);
						}
						else
						{
							lines.remove(i+1);
							lines.set(i,"0");
							lines.remove(i-1);
						}
					}
					else if(isNumeric(variables.get(lines.get(i-1))))
					{
						float a = Float.parseFloat(variables.get(lines.get(i-1)));
						float b;
						if(isNumeric(lines.get(i+1)))
						{
							b = Float.parseFloat(lines.get(i+1));
						}
						else
						{
							if(isNumeric(variables.get(lines.get(i+1))))
							{
								b = Float.parseFloat(variables.get(lines.get(i+1)));
							}
							else
							{
								error = true;
								return false;
							}
						}
						if(a>b)
						{
							lines.remove(i+1);
							lines.set(i, "1");
							lines.remove(i-1);
						}
						else
						{
							lines.remove(i+1);
							lines.set(i,"0");
							lines.remove(i-1);
						}
					}
				}
				else if(lines.get(i).equals("less"))
				{
					if(isNumeric(lines.get(i-1)))
					{
						float a = Float.parseFloat(lines.get(i-1));
						float b;
						if(isNumeric(lines.get(i+1)))
						{
							b = Float.parseFloat(lines.get(i+1));
						}
						else
						{
							if(isNumeric(variables.get(lines.get(i+1))))
							{
								b = Float.parseFloat(variables.get(lines.get(i+1)));
							}
							else
							{
								error = true;
								return false;
							}
						}
						if(a<b)
						{
							lines.remove(i+1);
							lines.set(i, "1");
							lines.remove(i-1);
						}
						else
						{
							lines.remove(i+1);
							lines.set(i,"0");
							lines.remove(i-1);
						}
					}
					else if(isNumeric(variables.get(lines.get(i-1))))
					{
						float a = Float.parseFloat(variables.get(lines.get(i-1)));
						float b;
						if(isNumeric(lines.get(i+1)))
						{
							b = Float.parseFloat(lines.get(i+1));
						}
						else
						{
							if(isNumeric(variables.get(lines.get(i+1))))
							{
								b = Float.parseFloat(variables.get(lines.get(i+1)));
							}
							else
							{
								error = true;
								return false;
							}
						}
						if(a<b)
						{
							lines.remove(i+1);
							lines.set(i, "1");
							lines.remove(i-1);
						}
						else
						{
							lines.remove(i+1);
							lines.set(i,"0");
							lines.remove(i-1);
						}
					}
				}
				else if(lines.get(i).equals("ge"))
				{
					if(isNumeric(lines.get(i-1)))
					{
						float a = Float.parseFloat(lines.get(i-1));
						float b;
						if(isNumeric(lines.get(i+1)))
						{
							b = Float.parseFloat(lines.get(i+1));
						}
						else
						{
							if(isNumeric(variables.get(lines.get(i+1))))
							{
								b = Float.parseFloat(variables.get(lines.get(i+1)));
							}
							else
							{
								error = true;
								return false;
							}
						}
						if(a>=b)
						{
							lines.remove(i+1);
							lines.set(i, "1");
							lines.remove(i-1);
						}
						else
						{
							lines.remove(i+1);
							lines.set(i,"0");
							lines.remove(i-1);
						}
					}
					else if(isNumeric(variables.get(lines.get(i-1))))
					{
						float a = Float.parseFloat(variables.get(lines.get(i-1)));
						float b;
						if(isNumeric(lines.get(i+1)))
						{
							b = Float.parseFloat(lines.get(i+1));
						}
						else
						{
							if(isNumeric(variables.get(lines.get(i+1))))
							{
								b = Float.parseFloat(variables.get(lines.get(i+1)));
							}
							else
							{
								error = true;
								return false;
							}
						}
						if(a>=b)
						{
							lines.remove(i+1);
							lines.set(i, "1");
							lines.remove(i-1);
						}
						else
						{
							lines.remove(i+1);
							lines.set(i,"0");
							lines.remove(i-1);
						}
					}
				}
				else if(lines.get(i).equals("le"))
				{
					if(isNumeric(lines.get(i-1)))
					{
						float a = Float.parseFloat(lines.get(i-1));
						float b;
						if(isNumeric(lines.get(i+1)))
						{
							b = Float.parseFloat(lines.get(i+1));
						}
						else
						{
							if(isNumeric(variables.get(lines.get(i+1))))
							{
								b = Float.parseFloat(variables.get(lines.get(i+1)));
							}
							else
							{
								error = true;
								return false;
							}
						}
						if(a<=b)
						{
							lines.remove(i+1);
							lines.set(i, "1");
							lines.remove(i-1);
						}
						else
						{
							lines.remove(i+1);
							lines.set(i,"0");
							lines.remove(i-1);
						}
					}
					else if(isNumeric(variables.get(lines.get(i-1))))
					{
						float a = Float.parseFloat(variables.get(lines.get(i-1)));
						float b;
						if(isNumeric(lines.get(i+1)))
						{
							b = Float.parseFloat(lines.get(i+1));
						}
						else
						{
							if(isNumeric(variables.get(lines.get(i+1))))
							{
								b = Float.parseFloat(variables.get(lines.get(i+1)));
							}
							else
							{
								error = true;
								return false;
							}
						}
						if(a<=b)
						{
							lines.remove(i+1);
							lines.set(i, "1");
							lines.remove(i-1);
						}
						else
						{
							lines.remove(i+1);
							lines.set(i,"0");
							lines.remove(i-1);
						}
					}
				}
				else if(lines.get(i).equals("equals"))
				{
					if(!isNumeric(variables.get(lines.get(i-1))) && !isNumeric(variables.get(lines.get(i+1))))
					{
						return variables.get(lines.get(i-1)).equals(variables.get(lines.get(i+1)));
					}
					else if(isNumeric(lines.get(i-1)))
					{
						float a = Float.parseFloat(lines.get(i-1));
						float b;
						if(isNumeric(lines.get(i+1)))
						{
							b = Float.parseFloat(lines.get(i+1));
						}
						else
						{
							if(isNumeric(variables.get(lines.get(i+1))))
							{
								b = Float.parseFloat(variables.get(lines.get(i+1)));
							}
							else
							{
								error = true;
								return false;
							}
						}
						if(a==b)
						{
							lines.remove(i+1);
							lines.set(i, "1");
							lines.remove(i-1);
						}
						else
						{
							lines.remove(i+1);
							lines.set(i,"0");
							lines.remove(i-1);
						}
					}
					else if(isNumeric(variables.get(lines.get(i-1))))
					{
						float a = Float.parseFloat(variables.get(lines.get(i-1)));
						float b;
						if(isNumeric(lines.get(i+1)))
						{
							b = Float.parseFloat(lines.get(i+1));
						}
						else
						{
							if(isNumeric(variables.get(lines.get(i+1))))
							{
								b = Float.parseFloat(variables.get(lines.get(i+1)));
							}
							else
							{
								error = true;
								return false;
							}
						}
						if(a==b)
						{
							lines.remove(i+1);
							lines.set(i, "1");
							lines.remove(i-1);
						}
						else
						{
							lines.remove(i+1);
							lines.set(i,"0");
							lines.remove(i-1);
						}
					}
				}
				else if(lines.get(i).equals("and"))
				{
					//System.out.println(lines.get(i+1));
					if(!(lines.get(i-1).equals("0") || lines.get(i+1).equals("0")))
					{
						lines.remove(i+1);
						lines.set(i,"1");
						lines.remove(i-1);
					}
					else
					{
						lines.remove(i+1);
						lines.set(i, "0");
						lines.remove(i-1);
					}
				}
				else if(lines.get(i).equals("or"))
				{
					if(!(lines.get(i-1).equals("0") && lines.get(i+1).equals("0")))
					{
						lines.remove(i+1);
						lines.set(i,"1");
						lines.remove(i-1);
					}
					else
					{
						lines.remove(i+1);
						lines.set(i, "0");
						lines.remove(i-1);
					}
				}
			}
		}
		return lines.get(0).equals("1");
	}

	//TODO start of parseLine
	private double parseLine(String line)
	{
		if(loop && !line.equals("end"))
		{
			//System.out.println("I have been added");
			loopCommands.add(line);
		}
		else if(line.equals("end") && loop)
		{
			loop = false;
			if(!elif)
				runLoop();
			else
				runIf();
			elif = false;
		}
		else if(line.contains("while") || line.contains("if"))
		{
			loop = true;
			loopCommands.clear();
			line = line.trim();
			loopCommands.add(line);
			elif = line.contains("if") ? true : false;
		}
		//TODO this is for printing stuff out until the IDE is up it only prints to the console
		else if(line.contains("say") && !line.contains("\"") && !(line.contains("add")||line.contains("subtract")||line.contains("multiply")||line.contains("divide") ||line.contains("exp")))
		{
			line.trim();
			final String[] lines = line.split(" ");
			if(variables.get(lines[1])!=null || variables.get(lines[1])!="null" && lines.length<3)
			{
				System.out.println(variables.get(lines[1]));
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
			System.out.println(temp);             
		}
		else if(line.contains("say") && line.indexOf("\"")!=-1 && line.lastIndexOf("\"")!=line.indexOf("\""))
		{
			line.trim();
			int x1 = line.indexOf("\"");
			int x2 = line.indexOf("\"", x1+1);
			String temp = line.substring(x1+1,x2);
			System.out.println(temp);
			
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
		//TODO this is for setting string variables
		else if(line.indexOf("\"")!=-1 && line.lastIndexOf("\"")!=line.indexOf("\"") && !(line.contains("add")||line.contains("exp")||line.contains("subtract")||line.contains("multiply")||line.contains("divide")))
		{
			line.trim();
			int x1 = line.indexOf("\"");
			int x2 = line.lastIndexOf("\"");
			variables.put(line.substring(0,line.indexOf(" ")),line.substring(x1+1,x2));
		}
		//TODO this is for numbers
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
					//System.out.println(t1+ " "+ a + " " + b );
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
	/* TODO This setFile method is no longer necessary as of Prototype 2
	public void setFile(File file) {
		try {
			FileReader reader = new FileReader(file);
			Scanner in = new Scanner(reader);
			while(in.hasNextLine())
			{
				commands.add(in.nextLine());
			}
			reader.close();
			System.out.print("File Opened Successfully \n");
		} catch (FileNotFoundException e) {
			System.out.print("File does not exist \n");
		} catch (IOException e) {
			System.out.print("Can't close file \n");
		}
		
	}*/

}
