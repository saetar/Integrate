import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Tree {
	
	private Node root;
	private char variable;
	private ArrayList<Pattern> patterns;
	
	public Tree(String str) {
		variable = 'x';
		System.out.println("Making tree with: " + str);
		//Establish patterns of possible functions
		patterns = new ArrayList<Pattern>();
		//Negative lookahead on trig/log functions and operators at the end (adds a * at end)
		Pattern log = Pattern.compile("\\d*(log|ln)(.*)");
		Pattern sin = Pattern.compile("(.*)sin(.*)");
		Pattern cos = Pattern.compile("(.*)cos(.*)");
		Pattern tan = Pattern.compile("(.*)tan(.*)");
		Pattern paren1 = Pattern.compile("(?!sin|cos|tan|log|ln)(.*)\\((.*)\\)");
		Pattern paren2 = Pattern.compile("(?!sin|cos|tan|log|ln)(.*)\\((.*)\\)(\\-\\+\\/)?(.*)");
		Pattern expon = Pattern.compile("(.*)\\^(.*)");
		Pattern md = Pattern.compile("(.*)(\\*|\\/)(.*)");
		Pattern linear = Pattern.compile("(\\d*)"+variable);
		Pattern as = Pattern.compile("(.*)(\\+|\\-)(.*)");
		Pattern constant = Pattern.compile("^\\d*$");
		//Try making sin/cos/tan... operators
		
		patterns.add(log);
		patterns.add(sin);
		patterns.add(cos);
		patterns.add(tan);
		patterns.add(paren1);
		patterns.add(paren2);
		patterns.add(as);
		patterns.add(md);
		patterns.add(expon);
		patterns.add(linear);
		patterns.add(constant);
		root = insert(str);
	}
	
	public Node insert(String strd){
		Node temp = null;
		Function f = null;
		if(strd.equals("")){
			f = new Constant("1", variable);
			temp = new Node(f);
			return temp;
		}
		for(int pcount = 0; pcount < patterns.size(); pcount++){
			Matcher matcher = patterns.get(pcount).matcher(strd);
			
			if(matcher.find()){
				String str = matcher.group();
				switch(pcount){
					//recursive call to evaluate before and inside paren 
					//Log	
					case 0:
						if(matcher.group(1).equals("")){
							f = new Log(str, variable);
							temp = new Node(f);
						}
						else{
							temp = new Node('*');
							temp.setLeft(insert(matcher.group(1)));
							String foo = str.replace(matcher.group(1), "");
							temp.setRight(insert(foo));
						}
						return temp;
					
					//Sin
					case 1:
						String strfoo = matcher.group(2);
						System.out.println("Found sin");
						if(matcher.group(1).equals("")){
							f = new Sin(strfoo, variable);
							temp = new Node(f);
						}
						else{
							temp = new Node('*');
							temp.setLeft(insert(matcher.group(1)));
							String foo = str.replace(matcher.group(1), "");
							temp.setRight(insert(foo));
						}
						return temp;
				
					//Cos	
					case 2:
						if(matcher.group(1).equals("")){
							f = new Cos(str, variable);
							temp = new Node(f);
						}
						else{
							temp = new Node('*');
							temp.setLeft(insert(matcher.group(1)));
							String foo = str.replace(matcher.group(1), "");
							temp.setRight(insert(foo));
						}
						return temp;
					
					//Tan
					case 3:
						if(matcher.group(1).equals("")){
							f = new Tan(str, variable);
							temp = new Node(f);
						}
						else{
							temp = new Node('*');
							temp.setLeft(insert(matcher.group(1)));
							String foo = str.replace(matcher.group(1), "");
							temp.setRight(insert(foo));
						}
						return temp;
						
					case 4:
						System.out.println(str);
						temp = new Node('*');
						temp.setLeft(insert(matcher.group(1)));
						String str2 = str.replace(matcher.group(1),"");
						if(str2.equals(str)){
							str2 = matcher.group(2);
						}
						temp.setRight(insert(str2));
						return temp;
	
					//recursive call to evaluate inside paren and after
					case 5:
						//If has group 4, then it has that op, otherwise it's *
						if(matcher.group(4).equals("")){
							temp = new Node('*');
						}
						else{
							temp = new Node(matcher.group(4).charAt(0));
						}
						temp.setLeft(insert(matcher.group(3)));
						String str3 = matcher.group(5);
						temp.setRight(insert(str3));
						return temp;

					//Addition/Subtraction
					case 6:
						temp = new Node(matcher.group(2).charAt(0));
						temp.setLeft(insert(matcher.group(1)));
						temp.setRight(insert(matcher.group(3)));
						return temp;
						
					//Multiplication or Division	
					case 7:
						temp = new Node(matcher.group(2).charAt(0));
						temp.setLeft(insert(matcher.group(1)));
						temp.setRight(insert(matcher.group(3)));
						return temp;
												
					//Create an exponent
					case 8:
						temp = new Node('^');
						temp.setLeft(insert(matcher.group(1)));
						temp.setRight(insert(matcher.group(2)));
						return temp;

					//Linear	
					case 9:
						f = new Linear(str, variable);
						temp = new Node(f);
						return temp;
					
					//Constant number
					case 10:
						f = new Constant(str, variable);
						temp = new Node(f);
						return temp;
						
					default:
						break;
				}
			}
		}
		return temp;
	}
	
	public double traverse(double x){
		return traverse(root, x);
	}
	
	//Recursive method to traverse/evaluate tree
	public double traverse(Node t, double x){
		//Base case for recursion (All nodes will be operators except leaves)
		if(t.isFunc()){
			return t.evaluate(x);
		}
		
		//Recursion to handle operators
		char op = t.getOp();
		switch(op){
			case '^':
				return Math.pow(traverse(t.left, x), traverse(t.right,x));
			case '+':
				return traverse(t.left, x) + traverse(t.right,x);
			case '-':
				return traverse(t.left, x) - traverse(t.right,x);
			case '*':
				return traverse(t.left, x) * traverse(t.right,x);
			case '/':
				return traverse(t.left, x) / traverse(t.right,x);
		}
		return -1;
	}
	
	//Inner Node class
	public class Node{
		private char operator;
		private Function func;
		private Node left;
		private Node right;
		private boolean isFunc;
		
		public Node(char op){
			operator=op;
			isFunc = false;
			right=left=null;
		}
		
		
		public Node(Function f){
			func = f;
			isFunc = true;
			right=left=null;
		}
		
		public boolean isFunc(){
			return isFunc;
		}
		
		public char getOp(){
			return operator;
		}
		
		public Function getFunc(){
			return func;
		}
		
		public void setLeft(Node n){
			left=n;
		}
		
		public void setRight(Node n){
			right = n;
		}
		
		public double evaluate(double x){
			return func.evaluate(x);
		}
	}
}