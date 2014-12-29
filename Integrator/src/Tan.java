
public class Tan implements Function{
	
	private Tree argument;
	
	public Tan(String input, char variable){

		argument = new Tree(input);
		
	}
	
	public String toString(){
		String str = "tan(" + argument + ")";
		return str;
	}
	
	public double evaluate(double x){
		double ans = Math.tan(argument.traverse(x));
		return ans;
	}
}