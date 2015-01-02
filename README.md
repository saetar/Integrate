This is a project that I started Winter break 2014 just to occupy my time. 
It is an integrator that uses Riemann sums to evaluate expressions given as a string.

The overall method used to evaluate the string was to tokenize the expression, dividing it up into necessary pieces of the expression, like sin(x^2) -> sin, (, x, ^, 2, ). Then, the program will process and rearrange the tokens into Reverse Polish Notation. Then an evaluator can evaluate this RPN stack at any given value. 

To make integration more efficient, I implemented the Romberg method of numerical integration. The idea is to get as close as we want to the actual answer (maxErr). To do this, we keep taking Riemann sums with a given step count, and then redo it with double the stepcounts. Keep doubling stepcounts until the difference between the last two times we integrated is less than maxErr.

TO USE:
This is just a command-line program, so download the .class or .java, compile (if necessary) and then type on the command-line: java Integrator
You will be prompted: What expression would you like to integrate? You may input an expression (no "f(x)=") using hyperbolic trig, normal trig, ln/log (both evaluate the natural log), e, binary ops (+-*/^) etc. Ex: 4sinh(sin(4x^3+2)-4).

Then you will be asked what variable the expression is with respect to. Just enter x for example. 

Next you will be asked the bounds of integration. They may be decimals or whole numbers, but no fractions.

Finally you will be asked the leniency, or the maxErr as described earlier for the Romberg integration. 

So far, this program should be able to handle most expressions. Try it out!
