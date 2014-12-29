public class Constant implements Function{

	private double thing;

	public Constant(String str, char variable){
		thing = Double.parseDouble(str);
	}

	public double evaluate(double x){
		return thing;
	}
	
	public String toString(){
		return Double.toString(thing);
	}
}