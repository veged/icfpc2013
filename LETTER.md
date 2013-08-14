1. _Did you do any preparation for the contest before the start date?_
   - Did you learn to use some new software packages?
   - Did you make use of our hints prior to the contest?
      - Did you read any research papers on program synthesis?
      - Did you write any JSON or S-expression utilities?

 The answer is "No" for all questions.


2. _After you read the problem description, what were all the strategies you considered using?_

 Apart from the brute force (which obviously could assuredly handle only small problems) we considered using of a genetic algorithm.
 The problem was we couldn't define a sensible notion of "similarity" between programs, so we eventually gave up on the idea.


3. _Which strategy did you end up choosing and why?_
   - What was your strategy for the /eval requests?
   - Once you received some set of input/output pairs for a program, what did your program do?
   - How did you make use of the counter-examples that we provided for each guess?

 We ended up with a brutal brute force algotihm. No because it was 


4. _What steps did you take to limit the search space of programs?  
  For example, how did you exploit the following features:_
      - _Knowledge of the operators in the program_
      - _Knowledge of the size of the program_
      - _Commutative operators, e.g., (and x y) = (and y x)_
      - _Inverse operators,     e.g., (not (not x)) = x_
      - _Identity operators,    e.g., (or x 0) = x_
      - _Redundant operators,   e.g., (if0 0 x y) = x_
      - _Larger identities,     e.g., (shr1 (shr1 (shr1 (shr1 x)))) = (shr4 x)_
      - _The iterative structure of fold_
      - _The tfold hint_
  _Aside from this, what were the three most important steps you took to limit the search space?_

 Our generator of a set of possible programs exploited the following expression equivalence rules:
    - binary operators commutativity
    - `shr1/shr4/shr16` reordering
    - `shl1/shr1/shr4/shr16` of 0
    - `shr1/shr4/shr16` of 1
    - double `not` elimination
    - constant condition `if` elimination
    - `if` with equal branches elimination
    - `and/or/xor/plus` of equal arguments
    - `and/or/xor/plus` of 0
 
 For programs up to size 12 no knowledge of operators in programs were used except the `tfold`.
 Generated candidate programs were no longer than a program we tried to guess.
 
 More close to the contest end, for higher-sized programs we had choosen another strategy:
     1. For a given set of operators generate a maximum set of programs of all sizes (from small to large)
        which our resources (memory and CPU) allow. This was about 100-200M of programs.
     2. For all problems (of any size) with that set of operators try to find a solution among the programs generated in step 1.

 We didn't make use of iterative structure of fold.


5. _What steps did you take to parallelize the search?  
   How much parallelism did you manage to achieve?  
   For example, how many threads/processes did you have going in parallel?  
   How did you manage the concurrency? Language support, libraries, OS-level process support?  
   Did you solve any of the contest problems in parallel? If so, what was your strategy for doing that?_

 Didn't try to parallelize as it was obvious that a better algorithm could have a much greater effect, so there was no reason to invest efforts in parallelization instead of algorithmic improvements. Another proglem is that our program require lot of memory.

 Several hours before the contest end we'd understand that there is no time for any further improvements and we should start running our existing algorithms for all problems.  The most time-consuming part of our solution was filtering out programs not satisfying the `p(x_i) = y_i` equations for known pairs of `x_i` and `y_i`. We calculated that we can run several instances of our algorithm without reaching the limit on server responses. We ended up running 5 instances of our program: 3 for regular problems and 2 for bonus problems. We selected a limit for a number of programs each instance could generate such that we can try each of our problems in time (we wanted overall rate to be 1 problem per 10s). The problems were exhausted in about 15 minutes before the contest end.


6. _Did you use different strategies for each class of problem?  
   The classes of problems were:_
      - small fold-free problems
      - large fold-free problems
      - tfold problems
      - fold problems
      - bonus problem set 1 (size 42)
      - bonus problem set 2 (size 137)


7. _How did you cope with the following elements of the game:_
      - _Did you use the training mode a lot before attempting the contest problems?_
      - _How did you deal with the element of risk in the game?  
        For example, many of the large fold-free problems could in fact be solved with a small guess._

 We didn't use the training mode a lot.  For small-sized problems (up to size 14) our algorithm with certainty had a solution program among the generated.  The only question was if it could find it with a limited number of guesses.  With a help from the training mode we had quickly found that it isn't a problem: after filtering out programs using `/eval` for some small number (8-16) of random inputs any problem could be solved with just a few mismathces.

 When the time had been running out we run our algorithm for larger-sized programs in a hope to find solutions within sizes we could reach.

8. _How many lines of code did you write, and in which languages?_

 2879 not empty lines in Java language.

9. _How was that code structured? You could indicate the number of modules, the structure of their interfaces, and, if you wish, you could even include a picture of the module dependences._
 
 OMG, this is Java! Don't expect any beauty here.

 The strong sides of Java are its maturity, popularity, simplicity, IDEs, sufficient speed, the ease of concurrent simultaneous development...  But not the beauty.
 
 Here is how our code is organized:
    1. Namespace `bv` contains 12 classes with bv programming language constructions and some special constructions (Alt and Wildcard) which represents set of programs (Alt class contains Set of expressions, Wildcard represents any program with specific size).
    2. Namespace `all` contains 11 classes for communicate with server (Server class), several proram generators (Gener, GenerProgram, Tree/Operator classes), value generator (GenerValues), three solvers (Solver, SolverBonus, SolverMeta) which filter set of programs builded by a generator.

10. _What kind of bugs did you run into when developing your solution?
    How did you discover and fix those bugs?_

 Just usual bugs such as using one variable instead of the other, making a stub for some method and forgetting about it (resulting in null pointer exception) and so on. All was easily caught with debuggers in our IDEs using small examples (a-la unit testing, but not arranged as such).

11. _What hardware did you use?
    e.g., laptop, university servers, cloud etc._

 Several single processor workstations:
  - Intel Core i5-3570 (3.40GHz), 32GB memory
  - Intel Core i3-2120 (3.30GHz), 8GB memory
  - Intel Core i7-2600K (3.40GHz), 8GB memory


12. _Anything else that you would like to add?_
