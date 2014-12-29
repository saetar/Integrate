import java.util.*;

/*
	Has main function
*/
public class Integrator{

	public static char variable;
	public static Tree god;
	//public static Operator god;

	public static void main(String[] args){
		//Finds the String representation of the function, and the variable with respect to which it is integrated.
		//Creates functions, initializes local variables
		Scanner scan = new Scanner(System.in);
		System.out.println("What expression would you like to integrate?");
		String expression = scan.nextLine();
		System.out.println("And with respect to what variable?");
		variable = scan.nextLine().charAt(0);
		god = new Tree(expression);
		System.out.println(integrate(0,2));
		scan.close();
	}

	
	public static double integrate(double start, double end){
		double answer = 0;
		for(double i = start; i < end; i+=.00001){
			answer += god.traverse(i);
		}
		answer /= 100000;
		System.out.println(answer);
		return answer;
	}
}