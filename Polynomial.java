import java.util.regex.*;
import java.util.*;

public class Polynomial implements Function{
	private ArrayList<Function> exponent;
	private ArrayList<Function> coef;
	private boolean isConstant;


	public Polynomial(String input, char variable){
		//Matches regex "\\d*" + variable + "\\^\\d*"
		//Finds the coef
		if(input.indexOf(variable)==-1){
			isConstant=true;
		}
		else{
			isConstant=false;
		}
		Pattern coeffind = Pattern.compile("(.*)\\^");
		Matcher comatch = coeffind.matcher(input);

		if(comatch.find()){
			String str = comatch.group(1);
			System.out.println(str);
			if(str.equals("")){
				Function f = new Constant("1",variable);
				coef.add(f);
			}
			else{
				coef = Integrator.splice(Integrator.split(str));
			}
		}

		//Finds the exponent
		Pattern expofind = Pattern.compile("\\^(.*)");
		Matcher exmatch = expofind.matcher(input);
		boolean found = exmatch.find();
		System.out.println(found);
		if(found){
			String str = exmatch.group(1);
			if(str.equals("")){
				Function f = new Constant("1",variable);
				exponent.add(f);
			}
			else{
				exponent = Integrator.splice(Integrator.split(str));
			}
		}
	}
	
	public String toString(){
		String str = "";
		for(int i = 0; i < coef.size()-1; i++){
			str+=coef.get(i).toString()+"*";
		}
		str+=coef.get(coef.size()-1).toString();
		str+="^(";
		for(int i = 0; i < exponent.size()-1; i++){
			str+=exponent.get(i).toString()+"*";
		}
		str+=exponent.get(exponent.size()-1).toString();
		str+=")";
		return str;
	}

	public double evaluate(double x){
		double answer;
		if(isConstant){
			answer = Math.pow(Integrator.evaluate(coef,x),Integrator.evaluate(exponent,x));
		}else{
			answer = Integrator.evaluate(coef,x)*Math.pow(x,Integrator.evaluate(exponent,x));
		}
		System.out.println(Integrator.evaluate(coef,x) + "^" + Integrator.evaluate(exponent,x)+"="+answer);
		return answer;
	}
}