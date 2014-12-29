
public class Log implements Function{
	
	private Tree argument;

	public Log(String input, char variable){
		argument = new Tree(input);
	}
	
	public String toString(){
		String str = "log("+argument+")";
		return str;
	}

	public double evaluate(double x){
		double a = Math.log(argument.traverse(x));
		return a;
	}


}