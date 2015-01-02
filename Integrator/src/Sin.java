
public class Sin implements Function{
	private Tree argument;
	
	public Sin(String input, char variable){
		argument = new Tree(input);
	}
	
	public String toString(){
		String str = "sin(" + argument + ")";
		return str;
	}
	
	public double evaluate(double x){
		double ans = Math.sin(argument.traverse(x));
		return ans;
	}
}