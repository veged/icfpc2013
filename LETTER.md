### 1. Did you do any preparation for the contest before the start date?
###     - Did you learn to use some new software packages?
###     - Did you make use of our hints prior to the contest?
###        -- Did you read any research papers on program synthesis?
###        -- Did you write any JSON or S-expression utilities?


### 2. After you read the problem description, what were all the strategies you considered using?


### 3. Which strategy did you end up choosing and why?
###     - What was your strategy for the /eval requests?
###     - Once you received some set of input/output pairs for a program, what did your program do?
###     - How did you make use of the counter-examples that we provided for each guess?


### 4. What steps did you take to limit the search space of programs?
###    For example, how did you exploit the following features:
###        Knowledge of the operators in the program
###        Knowledge of the size of the program
###        Commutative operators, e.g., (and x y) = (and y x)
###        Inverse operators,     e.g., (not (not x)) = x
###        Identity operators,    e.g., (or x 0) = x
###        Redundant operators,   e.g., (if0 0 x y) = x
###        Larger identities,     e.g., (shr1 (shr1 (shr1 (shr1 x)))) = (shr4 x)
###        The iterative structure of fold
###        The tfold hint
###    Aside from this, what were the three most important steps you took to limit the search space?


### 5. What steps did you take to parallelize the search?
###    How much parallelism did you manage to achieve? For example, how many threads/processes did you have going in parallel?
###    How did you manage the concurrency? Language support, libraries, OS-level process support?
###    Did you solve any of the contest problems in parallel? If so, what was your strategy for doing that?


### 6. Did you use different strategies for each class of problem?
###    The classes of problems were:
###       small fold-free problems
###       large fold-free problems
###       tfold problems
###       fold problems
###       bonus problem set 1 (size 42)
###       bonus problem set 2 (size 137)


### 7. How did you cope with the following elements of the game:
###       Did you use the training mode a lot before attempting the contest problems?
###       How did you deal with the element of risk in the game? For example, many of the large fold-free problems could in fact be solved with a small guess.


### 8. How many lines of code did you write, and in which languages?


### 9. How was that code structured? You could indicate the number of modules, the structure of their interfaces, and, if you wish, you could even include a picture of the module dependences.


### 10. What kind of bugs did you run into when developing your solution?
###     How did you discover and fix those bugs?


### 11. What hardware did you use?
###     e.g., laptop, university servers, cloud etc.


### 12. Anything else that you would like to add?


