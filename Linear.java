import java.util.regex.*;
import java.util.*;

public class Linear implements Function{
	private double coef;
	private char variable;
	
	public Linear(String input, char variable){
		//Finds the coef
		this.variable = variable;
		Pattern coeffind = Pattern.compile("(\\d*)"+variable);
		Matcher comatch = coeffind.matcher(input);
		boolean found = comatch.find();
		if(found){
			String str = comatch.group(1);
			if(str.equals("")){
				coef=1;
			}
			else{
				coef = Double.parseDouble(str);
			}
		}
	}
	
	public String toString(){
		String str = coef + "" + variable;
		return str;
	}
	
	public double evaluate(double x){
		double ans = coef*x;
		return ans;
	}
}