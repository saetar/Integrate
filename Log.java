import java.util.regex.*;
import java.util.*;

public class Log implements Function{
	//Matches alog(bx) and alogx and aln(bx) and alnx
	private ArrayList<Function> coef;
	private ArrayList<Function> argument;

	public Log(String input, char variable){
		//Finds the coef
		Pattern coeffind = Pattern.compile("^.*(log|ln)");
		Matcher comatch = coeffind.matcher(input);
		
		if(comatch.find()){
			String str = comatch.group();
			String sub = "";
			try{
				int index = str.indexOf("l");
				sub = str.substring(0,index);
				coef = Integrator.splice(Integrator.split(sub));
				if(sub.length() == 0) throw new Exception();
			}catch(Exception e){
				Function temp = new Constant("1",variable);
				coef.add(temp);
			}
		}
			
		//Finds the argument
		Pattern argfind1 = Pattern.compile("(log|ln)(.*)");
		Matcher arg1match = argfind1.matcher(input);
		if(arg1match.find()){
			String temp = arg1match.group(2);
			if(temp.equals("")){
				Function f = new Constant("1", variable);
				argument.add(f);
			}		
			else{
				argument = Integrator.splice(Integrator.split(temp));
			}
		}
	}
	
	public String toString(){
		String str = "";
		for(int i = 0; i < coef.size()-1; i++){
			str+=coef.get(i).toString()+"*";
		}
		str+=coef.get(coef.size()-1).toString();
		str+="log(";
		for(int i = 0; i < argument.size()-1; i++){
			str+=argument.get(i).toString()+"*";
		}
		str+=argument.get(argument.size()-1).toString();
		str+=")";
		return str;
	}

	public double evaluate(double x){
		double a = Integrator.evaluate(coef,x)*Math.log(Integrator.evaluate(argument, x));
		return a;
	}


}