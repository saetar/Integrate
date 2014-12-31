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
	public static ArrayDeque<String> god;

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
		//print(tokens);
		preprocess();
		//print(tokens);
		
		opStack = new Stack<String>();
		god = new ArrayDeque<String>();
		makeGod();
		print(god);
		System.out.println(evaluate(2));
		/*double test = romberg(0,10,.001);
		System.out.println(test);
		int round = (int)(test+.5);
		if(Math.abs(test-round) < .001){
			System.out.println(round);
		}
		*/
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
				interest = matcher.group();
				System.out.println(interest);
				tokens.add(interest);
				str=str.substring(interest.length());
				break;
			}
		}
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
			//It will always have lowest precedent
			else if(c=='+'|| c=='-'){
				while(!opStack.isEmpty()){
					char t = opStack.peek().charAt(0);
					if(t=='(') break;
					god.add(opStack.pop());
				}
				//push it onto the stack
				opStack.push(s);
			}
			
			//If multiplication or division
			else if(c=='*' || c=='/'){
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
			
			//If it's an exponent, only another exponent, sin, log, etc. will have higher precedence 
			else if(c=='^'){
				if(opStack.empty()) opStack.push(s);
				else{
					char t = opStack.peek().charAt(0);
					//If the other things had a higher precedent, then pop operators onto queue
					
					while(t=='s' || t=='c' || t=='t' || t=='l'){
						god.add(opStack.pop());
						if(opStack.isEmpty()) break;
						t = opStack.peek().charAt(0);
					}
					opStack.push(s);
					//If same/lower priority, push it onto opStack
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
		//Pop all leftover operators onto god
		while(!opStack.empty()){
			god.add(opStack.pop());
		}
	}

	//evaluates god at x
	public static double evaluate(double x){
		Queue<String> temp = god.clone();
		Stack<String> eval = new Stack<String>();
		double z;
		double y;
		//int counter = 0;
		while(!temp.isEmpty()){
			//counter++;
			//System.out.println(counter);
			String toke = temp.poll();
			char t = toke.charAt(0);
			//print(eval);
			if(Character.isDigit(t)){
				eval.push(toke);
			}
			
			else if(t == variable){
				eval.push(Double.toString(x));
			}
			
			if(t=='s'){
				z = Double.parseDouble(eval.pop());
				eval.push(String.valueOf(Math.sin(z)));
			}
			
			if(t=='c'){
				z = Double.parseDouble(eval.pop());
				eval.push(String.valueOf(Math.cos(z)));
			}
			
			if(t=='t'){
				z = Double.parseDouble(eval.pop());
				eval.push(String.valueOf(Math.tan(z)));
			}
			
			if(t=='l'){
				z = Double.parseDouble(eval.pop());
				eval.push(String.valueOf(Math.log(z)));
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
				eval.add(String.valueOf(Math.pow(y, z)));
			}
		}
		return Double.parseDouble(eval.pop());
	}
	
	
	//Uses the romberg method of numerically integrating
	public static double romberg(double start, double end, double maxErr){
		int steps = 2;
		double t1 = integrate(start,end,steps);
		steps *= 2;
		double t2 = integrate(start,end,steps);
		while(Math.abs(t2-t1) > maxErr){
			steps *= 2;
			t1 = t2;
			t2 = integrate(start,end,steps);
		}
		return t2;
	}
	
	public static double integrate(double start, double end, int numOfStep){
		double step = (end-start)/numOfStep;
		double answer = 0;
		//Note: this is a midpoint Riemann Sum
		for(double i = start; i < end; i+=step){
			answer += evaluate(i+step/2);
		}
		answer *= step;
		return answer;
	}
	
	public static void print(Collection<String> c){
		System.out.println(c);
	}
}