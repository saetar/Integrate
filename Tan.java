import java.util.regex.*;
import java.util.*;

public class Tan implements Function{
	private ArrayList<Function> coef;
	private ArrayList<Function> argument;
	
	public Tan(String input, char variable){
		//Finds the coef
		Pattern coeffind = Pattern.compile("^(.*)tan");
		Matcher comatch = coeffind.matcher(input);
		boolean found = comatch.find();
		if(found){
			String str = comatch.group(1);
			if(str.equals("")){
				Function temp = new Constant("1",variable);
				coef = new ArrayList<Function>();
				coef.add(temp);
			}
			else{
				coef = Integrator.splice(Integrator.split(str));
			}
		}

		//Finds the argument
		Pattern argfind = Pattern.compile("tan(.*)");
		Matcher argmatch = argfind.matcher(input);
		if(argmatch.find()){
			String str = argmatch.group(1);
			if(str.equals(" ")){
				Function temp = new Constant("1", variable);
				argument = new ArrayList<Function>();
				argument.add(temp);
			}
			else{
				argument = Integrator.splice(Integrator.split(str));
			}
		}
	}
	
	public String toString(){
		String str = "";
		for(int i = 0; i < coef.size()-1; i++){
			str+=coef.get(i).toString()+"*";
		}
		str+=coef.get(coef.size()-1).toString();
		str+="tan(";
		for(int i = 0; i < argument.size()-1; i++){
			str+=argument.get(i).toString()+"*";
		}
		str+=argument.get(argument.size()-1).toString();
		str+=")";
		return str;
	}
	
	public double evaluate(double x){
		double ans = Integrator.evaluate(coef,x)*Math.tan(Integrator.evaluate(argument,x));
		return ans;
	}
}