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
	public static Queue<String> god;

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
		god = new ArrayDeque<String>();
		makeGod();
		print(god);
		System.out.println(evaluate(2));
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
				str=str.substring(interest.length());
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
				god.add(tokens.get(i));
			}
			
			//If c is addition or subtraction
			else if(c=='+'|| c=='-'){
				if(opStack.empty()) opStack.push(s);
				else{
					//If the other things had a higher precedent, then pop operators onto queue
					if(opStack.peek().charAt(0)!='+' && opStack.peek().charAt(0)!='-'){
						while(opStack.peek().charAt(0) != '(' && opStack.peek().charAt(0)!='+' && opStack.peek().charAt(0)!='-' && !opStack.empty()){
							god.add(opStack.pop());
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
							god.add(opStack.pop());
						}
					}
					//If same/lower priority, push it onto opStack
					else{
						opStack.push(s);
					}
				}
			}
			
			//If it's an exponent, only sin, log, etc. will have higher precedence
			else if(c=='^'){
				if(opStack.empty()) opStack.push(s);
				else{
					char t = opStack.peek().charAt(0);
					//If the other things had a higher precedent, then pop operators onto queue
					if(t!='*' && t!='/'&& t!='s' && t!='c' && t!='t' && t!='l' && t!='('){
						while(t!='/' && t!='*' && t!='s'&&t!='c' && t!='t' && t!='l' && t!='('){
							god.add(opStack.pop());
							t = opStack.peek().charAt(0);
						}
						opStack.push(s);
					}
					//If same/lower priority, push it onto opStack
					else{
						opStack.push(s);
					}
				}
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
					god.add(opStack.pop());
				}
				//Remove the opening paren from opStack
				//WE DON'T NEED ANY PAREN
				opStack.pop();
			}
		}
		print(opStack);
		//Pop all leftover operators onto god
		while(!opStack.empty()){
			god.add(opStack.pop());
		}
	}

	//evaluates god at x
	public static double evaluate(double x){
		Queue<String> temp = god;
		Stack<String> eval = new Stack<String>();
		String a;
		double z;
		double y;
		
		while(!temp.isEmpty()){
			String toke = temp.poll();
			char t = toke.charAt(0);

			if(Character.isDigit(t)){
				eval.push(toke);
			}
			
			else if(t == variable){
				eval.push(Double.toString(x));
			}
			
			if(t=='s'){
				a = eval.pop();
				eval.push(String.valueOf(Math.sin(Double.parseDouble(a))));
			}
			
			if(t=='c'){
				a = eval.pop();
				eval.push(String.valueOf(Math.cos(Double.parseDouble(a))));
			}
			
			if(t=='t'){
				a = eval.pop();
				eval.push(String.valueOf(Math.tan(Double.parseDouble(a))));
			}
			
			if(t=='l'){
				a = eval.pop();
				eval.push(String.valueOf(Math.log(Double.parseDouble(a))));
			}
	
			if(t=='+'){
				z = Double.parseDouble(eval.pop());
				y = Double.parseDouble(eval.pop());
				eval.add(String.valueOf(z+y));
			}
			
			if(t=='-'){
				z = Double.parseDouble(eval.pop());
				y = Double.parseDouble(eval.pop());
				eval.add(String.valueOf(y-z));
			}
			
			if(t=='*'){
				z = Double.parseDouble(eval.pop());
				y = Double.parseDouble(eval.pop());
				eval.add(String.valueOf(z*y));
			}
			
			if(t=='/'){
				z = Double.parseDouble(eval.pop());
				y = Double.parseDouble(eval.pop());
				eval.add(String.valueOf(y/z));
			}
			
			if(t=='^'){
				z = Double.parseDouble(eval.pop());
				y = Double.parseDouble(eval.pop());
				eval.add(String.valueOf(Math.pow(z, y)));
			}
		}
		return Double.parseDouble(eval.pop());
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
	
	public static void print(Collection<String> c){
		System.out.println(c);
	}
}