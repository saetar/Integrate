import java.util.*;
import java.util.regex.*;


public class Integrator{

	public static char variable;
	public static ArrayList<Character> ops;

	public static void main(String[] args){

		Scanner scan = new Scanner(System.in);
		System.out.println("What expression would you like to integrate?");
		String expression = scan.nextLine();
		System.out.println("And with respect to what variable?");
		variable = scan.nextLine().charAt(0);
		ArrayList<Function> functions = splice(split(expression));
		
		System.out.println("Starting from: ");
		double start = Double.parseDouble(scan.nextLine());
		System.out.println("Ending at: ");
		double end = Double.parseDouble(scan.nextLine());
		print(functions);
		integrate(functions,start,end);

	}

	public static ArrayList<String> split(String express){
		ArrayList<String> inputs = new ArrayList<String>();
		Pattern paren = Pattern.compile("\\((.*)\\)");
		Matcher parenmatch = paren.matcher(express);
		if(parenmatch.find()){
			inputs.add(parenmatch.group(1));
		}
		int i = 0;
		while(i < express.length()){
			String s = "";
			char c = express.charAt(i);

			if(c == '+' || c == '-' || c == '*' || c == '/'){
				s+=c;
				i++;
			}

			else{
				while(c != '+' && c != '-' && c != '*' && c != '/'){
					s+=c;
					i++;
					if( i == express.length()){
						break;
					}
					c=express.charAt(i);
				}
			}
			inputs.add(s);
		}
		return inputs;
	}

	public static double evaluate(ArrayList<Function> func, double x){
		double sum = 0;
		for(int i = 0; i < func.size(); i++){
			if(i>0){
				switch(ops.get(i-1)){
					case '+':
						sum += func.get(i).evaluate(x);
						break;
					case '-':
						sum -= func.get(i).evaluate(x);
						break;
					case '*':
						sum *= func.get(i).evaluate(x);
						break;
					case '/':
						sum /= func.get(i).evaluate(x);
						break;
					default:
						break;
				}
			}
			else{
				sum+=func.get(i).evaluate(x);
			}
		}

		//System.out.println(sum);
		return sum;

	}
	
	public static ArrayList<Function> splice(ArrayList<String> inputs){
		//Establish a list of functions to fill from the inputs
		ArrayList<Function> functions = new ArrayList<Function>();
		
		//Establish patterns of possible functions
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		Pattern polynomial = Pattern.compile("(.*)\\^(.*)");
		Pattern log = Pattern.compile("\\d*(log|ln)(.*)");
		Pattern constant = Pattern.compile("^\\d*$");
		Pattern sin = Pattern.compile("(.*)(sin)(.*)");
		Pattern cos = Pattern.compile("(.*)(cos)(.*)");
		Pattern tan = Pattern.compile("(.*)(tan)(.*)");
		Pattern linear = Pattern.compile("(\\d*)"+variable);
		patterns.add(polynomial);
		patterns.add(log);
		patterns.add(constant);
		patterns.add(sin);
		patterns.add(cos);
		patterns.add(tan);
		patterns.add(linear);
		ArrayList<Character> operators = new ArrayList<Character>();

		//Test each pattern to each input function
		for(int icount = 0; icount < inputs.size(); icount++){
			String str = inputs.get(icount);
				if(str.charAt(0) == '+' ||str.charAt(0) == '-' ||str.charAt(0) == '*' ||str.charAt(0) == '/'){
					operators.add(str.charAt(0));
				}
			for(int pcount = 0; pcount < patterns.size(); pcount++){
				Matcher matcher = patterns.get(pcount).matcher(str);
				//If it matches, find which it matches with
				if(matcher.matches()){
					Function f;
					switch(pcount){

						//Create a new function for exponent
						case 0:
							f = new Polynomial(str, variable);
							functions.add(f);
							break;

						//Create a new function for log
						case 1:
							f = new Log(str, variable);
							functions.add(f);
							break;

						//Create a constant function
						case 2:
							f = new Constant(str, variable);
							functions.add(f);
							break;
							
						case 3:
							f = new Sin(str, variable);
							functions.add(f);
							break;
							
						case 4:
							f = new Cos(str, variable);
							functions.add(f);
							break;
							
						case 5:
							f = new Tan(str, variable);
							functions.add(f);
							break;
							
						case 6:
							f = new Linear(str, variable);
							functions.add(f);
							break;
							
						default:
							break;
					}
				}
			}
		}
		ops=operators;
		return functions;
	}

	public static double integrate(ArrayList<Function> func, double start, double end){
		double answer = 0;
		for(double i = start; i < end; i+=.00001){
			answer += evaluate(func,i);
		}
		answer /= 100000;
		System.out.println(answer);
		return answer;
	}

	public static void print(ArrayList list){
		for(int i = 0; i < list.size(); i++){
			System.out.println(list.get(i));
		}
	}
}