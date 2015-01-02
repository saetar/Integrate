
public class Cos implements Function{

	private Tree argument;
	
	public Cos(String input, char variable){
		argument = new Tree(input);
	}
	
	public String toString(){
		String str = "cos(" + argument + ")";
		return str;
	}
	
	public double evaluate(double x){
		double ans = Math.cos(argument.traverse(x));
		return ans;
	}
}