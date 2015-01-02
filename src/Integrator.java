import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
	Has main function
*/
public class Integrator{
	
	/*
	 * List of global variables: 
	 * - variable: character representing the variable in given expression
	 * - patterns: list of regex patterns to help tokenize expression
	 * - tokens: list of tokens from expression
	 * - god: the queue of string tokens in RPN
	 * 
	 */
	
	public static char variable;
	public static ArrayList<Pattern> patterns;
	public static ArrayList<String> tokens;
	public static ArrayDeque<String> god;

	public static void main(String[] args){
		boolean again = true;
		Scanner scan = new Scanner(System.in);
		while(again){
			//Finds the String representation of the function, and the variable with respect to which it is integrated.
			//Creates functions, initializes local variables
			System.out.println("What expression would you like to integrate?");
			String expression = scan.nextLine();
			System.out.println("And with respect to what variable?");
			patterns = new ArrayList<Pattern>();
			variable = scan.nextLine().charAt(0);
			tokens = new ArrayList<String>();
			System.out.print("Starting bound: ");
			double start = scan.nextDouble();
			
			System.out.print("Ending bound: ");
			double end = scan.nextDouble();
			
			System.out.print("Leniency: ");
			double leniency = scan.nextDouble();
			//A token is either a number (string of digits)
			Pattern number = Pattern.compile("^(\\d+)");
			//or a token is one character (parentheses or binary operator)
			Pattern onechar = Pattern.compile("^(\\*|\\/|\\+|\\-|\\^|\\(|\\)|e|"+variable+ ")");
			//or a token is a trig/log function etc.
			Pattern complex = Pattern.compile("^sinh|cosh|tanh|log|ln|sin|cos|tan");
	
			//Add patterns to global variable
			patterns.add(number);
			patterns.add(onechar);
			patterns.add(complex);
	
			//Fill tokens
			tokenize(expression);
			//print(tokens);
			//preprocess tokens
			preprocess();
			//print(tokens);
			//Initialize and make god
			god = new ArrayDeque<String>();
			makeGod();
			//print(god);
			//Testing
			double test = romberg(start,end,leniency);
			
			//If an answer is within .001 of an integer, round to that integer.
			double round;
			if(test > 0){
				round = Math.floor(test+.5);
			}
			else{
				round = Math.floor(test-.5);
			}
			
			if(Math.abs(test-round) < .001){
				System.out.println(round);
			}
			else System.out.println(test);
			System.out.println("Would you like to do it again?");
			scan.nextLine();
			String response = scan.nextLine();
			if(response.charAt(0) == 'n') again=false;
		}
		System.out.println("Thank you, come again");
		scan.close();
	}
	
	//Recurssivly takes in a string and tokenizes it based on patterns
	public static void tokenize(String str){
		//Base case break.
		if(str.equals(""))
			return;
		
		String interest="";
		//Search for pattern match
		for(int i = 0; i < patterns.size(); i++){
			Matcher matcher = patterns.get(i).matcher(str);
			if(matcher.find()){
				//Make interest the token in question
				interest = matcher.group();
				//Add it to tokens, thne strip string of that token
				tokens.add(interest);
				str=str.substring(interest.length());
				break;
			}
		}
		//Pass new string through tokenize again. 
		tokenize(str);
	}
	
	/* Preprocess takes care of implicit multiplication
	 * e.g. 4sin(x) = 4*sin(x) and 4x = 4*x
	 * The rule here is if there is a number token
	 * followed by anything but a binary operator token,
	 * then add "*" token between them.
	 * Could also be variable followed by not a binary operator
	 * like xsin(x) in which case it would need a * as well.
	 * HOWEVER, closing paren do not need a *
	 * 
	 * Also deals with unary negative sign
	 */
	public static void preprocess(){
		String star = "*";
		for(int i = 0; i < tokens.size()-1; i++){
			
			//If it's a number
			if(tokens.get(i).matches("\\d*")){
				if(!tokens.get(i+1).matches("^(\\*|\\/|\\+|\\-|\\^|\\))")){
					tokens.add(i+1, star);
				}
			}
			
			//If it's the variable
			if(tokens.get(i).matches(variable+"")){
				if(!tokens.get(i+1).matches("^(\\*|\\/|\\+|\\-|\\^|\\))")){
					tokens.add(i+1, star);
				}
			}
			
			//If it's a negative sign (-x) -> (0-x)
			if(tokens.get(i).equals("-")){
				try{
					if(tokens.get(i-1).equals("(")){
						tokens.add(i,"0");
					}
				}catch(IndexOutOfBoundsException e){	//If it's at the beginning of the tokens, it's a preceding negative
					tokens.add(i,"0");
				}
			}
		}
	}
	
	/*
	 * Makes god by iterating through the tokens, and if it is a number or variable,
	 * then push it onto the god queue. If it's a binary or uniary operation, push it onto 
	 * stack of operations opStack. However, if an operation that is about to be pushed 
	 * to opStack but has an equal or higher precedence (as PEMDAS has taught us), then
	 * keep poping operators from opStack and push them onto the god queue. 
	 * At the end, push all remaining operators on opStack onto god queue. 
	 */
	public static void makeGod(){
		
		//Initialize opStack
		Stack<String> opStack = new Stack<String>();

		//Iterate through tokens
		for(int i = 0; i < tokens.size(); i++){
			//s is tokens, c is just an easy way to see what s is
			String s = tokens.get(i);
			char c = s.charAt(0);
			
			//If c is part of a number, add it to the queue
			if(isNumeric(s)||s.equals(variable+"")||s.equals("e")){
				god.add(s);
			}
			
			//If c is addition or subtraction
			//It will always have lowest precedent
			else if(s.equals("+")|| s.equals("-")){
				while(!opStack.isEmpty()){
					//Does not pop off open paren
					char t = opStack.peek().charAt(0);
					if(t=='(') break;
					
					//push operators onto god
					god.add(opStack.pop());
				}
				
				//push it onto the stack
				opStack.push(s);
			}
			
			//If multiplication or division
			else if(s.equals("*") || s.equals("/")){
				if(opStack.empty()) opStack.push(s);
				else{
					char t = opStack.peek().charAt(0);
					//If the other things had a higher or equal precedent, then pop operators onto queue
					
					while(t!='+' && t!='-' && t!='('){
						god.add(opStack.pop());
						if(opStack.isEmpty()) break;
						t = opStack.peek().charAt(0);
					}
					//If same/lower priority, push it onto opStack
					
					opStack.push(s);
					
				}
			}
			
			//If it's an exponent, only another exponent and uniary operator will have higher precedence 
			else if(s.equals("^")){
				if(opStack.empty()) opStack.push(s);
				else{
					char t = opStack.peek().charAt(0);
					//If the other things had a higher or equal precedent, then pop operators onto queue
					while(t=='s' || t=='c' || t=='t' || t=='l' || t=='^'){
						god.add(opStack.pop());
						if(opStack.isEmpty()) break;
						t = opStack.peek().charAt(0);
					}
					//push ^ on opStack at the end
					opStack.push(s);
				}
			}
			
			//If it's a pre-defined function like sin and log, then
			//it has a higher priority to everything
			else if(c=='s' || c=='c' || c=='t' || c=='l'){
				opStack.push(s);
			}
			
			//If it is an opening parentheses, then push it onto opstack
			else if(s.equals("(")){
				opStack.push(s);
			}
			
			//If it is a closing parentheses, then pop all operators onto 
			//god stack, until reaching opening parentheses
			else if(s.equals(")")){
				while(!opStack.peek().equals("(")){
					god.add(opStack.pop());
				}
				//Remove the opening paren from opStack
				//WE DON'T NEED ANY PAREN
				opStack.pop();
			}
		}
		//Pop all leftover operators onto god
		while(!opStack.empty()){
			god.add(opStack.pop());
		}
	}

	/*
	 * The evaluation of god requires another stack to hold things as we evaluate them.
	 * Evaluation is, if the next token in god is a number or variable, then push it to stack,
	 * if it's a binary operator (+,-,*,/,^) then god.pop() is the second argument,
	 * and god.pop() again is the first argument. Evaluate that binary operator on those two 
	 * arguments, then push the new value back onto the stack. If it's a unary operator, then
	 * evaluate the last thing put onto the stack with that operator. Repeat until done. 
	 */
	public static double evaluate(double x){
		//Since we can't modify god itself, we make a clone called temp.
		Queue<String> temp = god.clone();
		//Evaluation stack
		Stack<String> eval = new Stack<String>();
		//First and second arguments if necessary.
		double z;
		double y;
		
		
		while(!temp.isEmpty()){
			//See what's next in temp queue
			String toke = temp.poll();
			//char t = toke.charAt(0);
			
			//If it's a number, then push it to eval
			if(isNumeric(toke)){
				eval.push(toke);
			}
			
			//If it's the variable, push x (funciton argument) to eval
			else if(toke.equals(variable+"")){
				eval.push(Double.toString(x));
			}
			
			
			//If sin, cos, tan, or log then evaluate sin cos tan or log on last thing in eval. 
			else if(toke.equals("sin")){
				z = Double.parseDouble(eval.pop());
				eval.push(String.valueOf(Math.sin(z)));
			}
			
			else if(toke.equals("cos")){
				z = Double.parseDouble(eval.pop());
				eval.push(String.valueOf(Math.cos(z)));
			}
			
			else if(toke.equals("tan")){
				z = Double.parseDouble(eval.pop());
				eval.push(String.valueOf(Math.tan(z)));
			}
			
			else if(toke.equals("log") || toke.equals("ln")){
				z = Double.parseDouble(eval.pop());
				eval.push(String.valueOf(Math.log(z)));
			}
			
			
			//Binary operators pop two things off of eval and perform op
			else if(toke.equals("+")){
				z = Double.parseDouble(eval.pop());
				y = Double.parseDouble(eval.pop());
				eval.add(String.valueOf(z+y));
			}
			
			else if(toke.equals("-")){
				z = Double.parseDouble(eval.pop());
				y = Double.parseDouble(eval.pop());
				eval.add(String.valueOf(y-z));
			}
			
			else if(toke.equals("*")){
				z = Double.parseDouble(eval.pop());
				y = Double.parseDouble(eval.pop());
				eval.add(String.valueOf(z*y));
			}
			
			else if(toke.equals("/")){
				z = Double.parseDouble(eval.pop());
				y = Double.parseDouble(eval.pop());
				eval.add(String.valueOf(y/z));
			}
			
			else if(toke.equals("^")){
				z = Double.parseDouble(eval.pop());
				y = Double.parseDouble(eval.pop());
				eval.add(String.valueOf(Math.pow(y, z)));
			}
			
			else if(toke.equals("e")){
				eval.push(String.valueOf(Math.E));
			}
			
			else if(toke.equals("sinh")){
				z = Double.parseDouble(eval.pop());
				eval.push(String.valueOf(Math.sinh(z)));
			}
			
			else if(toke.equals("cosh")){
				z = Double.parseDouble(eval.pop());
				eval.push(String.valueOf(Math.cosh(z)));
			}
			
			else if(toke.equals("tanh")){
				z = Double.parseDouble(eval.pop());
				eval.push(String.valueOf(Math.tanh(z)));
			}
		}
		//Answer is the last thing left on eval
		double ans = Double.parseDouble(eval.pop());
		return ans;
	}
	
	
	/*
	 * Uses Romberg method of numerical integration. This is a way to make
	 * Riemann sum as accurate as we want, while (hopefully) not wasting too much
	 * time. The idea is double the number of steps we take in integration 
	 * until the difference between the last two times we integrated is less
	 * than the maxErr
	 */
	public static double romberg(double start, double end, double maxErr){
		//Initialize first 2 trials
		int steps = 2;
		double t1 = integrate(start,end,steps);
		steps *= 2;
		double t2 = integrate(start,end,steps);
		//Go through until difference is less than maxErr
		while(Math.abs(t2-t1) > maxErr){
			steps *= 2;
			t1 = t2;
			t2 = integrate(start,end,steps);
		}
		//Return last integrated
		return t2;
	}
	
	/*
	 * Takes in a beginning and an end and number of steps and evaluates a midpoint
	 * Riemann sum with given number of steps.
	 */
	public static double integrate(double start, double end, int numOfStep){
		
		//Actual width of steps is given by following formula:
		double step = (end-start)/numOfStep;
		double answer = 0;
		
		//Note: this is a midpoint Riemann Sum
		for(double i = start; i < end; i+=step){
			answer += evaluate(i+step/2);
		}
		
		//Scale back by step-width
		answer *= step;
		return answer;
	}
	
	/*
	 * isNumeric takes a string and returns if it's a number. 
	 * Taken from StackOverflow.com
	 */
	public static boolean isNumeric(String str)  
	{  
	  try{  
		  double d = Double.parseDouble(str);  
	  }catch(NumberFormatException nfe)  {  
		  return false;  
	  }  
	  return true;  
	}
	
	/*
	 * Helps with debugging :):
	 */
	public static void print(Collection<String> c){
		System.out.println(c);
	}
}