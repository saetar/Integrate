import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
	Has main function
*/
public class Integrator{

	public static char variable;
	public static ArrayList<Pattern> patterns;
	public static ArrayList<String> tokens;
	public static Stack<String> opStack;
	public static Stack<String> god;

	public static void main(String[] args){
		//Finds the String representation of the function, and the variable with respect to which it is integrated.
		//Creates functions, initializes local variables
		Scanner scan = new Scanner(System.in);
		System.out.println("What expression would you like to integrate?");
		String expression = scan.nextLine();
		System.out.println("And with respect to what variable?");
		patterns = new ArrayList<Pattern>();
		variable = scan.nextLine().charAt(0);
		tokens = new ArrayList<String>();
		
		//A token is either a number (string of digits)
		Pattern number = Pattern.compile("^(\\d+)");
		//or a token is one character (parentheses or binary operator)
		Pattern onechar = Pattern.compile("^(\\*|\\/|\\+|\\-|\\^|\\(|\\)|"+variable+ ")");
		//or a token is a trig/log function etc.
		Pattern complex = Pattern.compile("^sin|cos|tan|log|ln");

		
		patterns.add(number);
		patterns.add(onechar);
		patterns.add(complex);


		tokenize(expression);
		print(tokens);
		preprocess();
		print(tokens);
		
		opStack = new Stack<String>();
		god = new Stack<String>();
		makeGod();
		print(god);
		//System.out.println(integrate(0,2));
		scan.close();
	}
	
	//Takes in a string and tokenizes it based on patterns
	public static void tokenize(String str){
		if(str.equals(""))
			return;
		String interest="";
		//Search for pattern match
		for(int i = 0; i < patterns.size(); i++){
			Matcher matcher = patterns.get(i).matcher(str);
			if(matcher.find()){
				System.out.println("Found " + i + "th matcher ");
				interest = matcher.group();
				System.out.println("interest: " + interest);
				tokens.add(interest);
				str=str.replace(interest, "");
				break;
			}
		}
		System.out.println(str);
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
	 */
	public static void preprocess(){
		String star = "*";
		for(int i = 0; i < tokens.size()-1; i++){
			
			if(tokens.get(i).matches("\\d*")){
				if(!tokens.get(i+1).matches("^\\*|\\/|\\+|\\-|\\^|\\)")){
					tokens.add(i+1, star);
				}
			}
			
			if(tokens.get(i).matches(variable+"")){
				if(!tokens.get(i+1).matches("^\\*|\\/|\\+|\\-|\\^")){
					tokens.add(i+1, star);
				}
			}
			
		}
	}
	
	public static void makeGod(){
		for(int i = 0; i < tokens.size(); i++){
			String s = tokens.get(i);
			char c = s.charAt(0);
			
			//If c is part of a number, add it to the queue
			if(Character.isDigit(c)||c==variable){
				god.push(tokens.get(i));
			}
			
			//If c is addition or subtraction
			else if(c=='+'|| c=='-'){
				if(opStack.empty()) opStack.push(s);
				else{
					//If the other things had a higher precedent, then pop operators onto queue
					if(opStack.peek().charAt(0)!='+' && opStack.peek().charAt(0)!='-'){
						while(opStack.peek().charAt(0) != '(' && opStack.peek().charAt(0)!='+' && opStack.peek().charAt(0)!='-' && !opStack.empty()){
							god.push(opStack.pop());
						}
					}
					//push it onto the stack
						opStack.push(s);
				}
			}
			
			//If multiplication or division
			else if(c=='*' || c=='/'){
				if(opStack.empty()) opStack.push(s);
				else{
					char t = opStack.peek().charAt(0);
					//If the other things had a higher precedent, then pop operators onto queue
					if(t!='/'&&t!='*' && t!='+' && t!='-' && t!='('){
						while(t!='/'&&t!='*' && t!='+' && t!='-' && t!='('){
							god.push(opStack.pop());
						}
					}
					//If same/lower priority, push it onto opStack
					else{
						opStack.push(s);
					}
				}
			}
			
			//If it's an exponent, nothing has higher priority, so always push
			else if(c=='^'){
				opStack.push(s);
			}
			
			//If it's a pre-defined function like sin and log, then
			//it has a higher priority to everything
			else if(c=='s' || c=='c' || c=='t' || c=='l'){
				opStack.push(s);
			}
			
			//If it is an opening parentheses, then push it onto opstack
			else if(c=='('){
				opStack.push(s);
			}
			
			//If it is a closing parentheses, then pop all operators onto 
			//god stack, until reaching opening parentheses
			else if(c == ')'){
				while(opStack.peek().charAt(0)!='('){
					god.push(opStack.pop());
				}
				//Remove the opening paren from opStack
				//WE DON'T NEED ANY PAREN
				opStack.pop();
			}
		}
		//Pop all leftover operators onto god
		for(int i = 0; i < opStack.size(); i++){
			god.push(opStack.pop());
		}
	}

	//evaluates God at x
	public static double evaluate(double x){
		
		
		return x;
	}
	
	public static double integrate(double start, double end){
		double answer = 0;
		/*for(double i = start; i < end; i+=.00001){
			answer += god.traverse(i);
		}
		answer /= 100000;
		System.out.println(answer);*/
		return answer;
	}
	
	public static void print(Collection c){
		System.out.println(c);
	}
}