1. _Did you do any preparation for the contest before the start date?_
   - Did you learn to use some new software packages?
   - Did you make use of our hints prior to the contest?
      - Did you read any research papers on program synthesis?
      - Did you write any JSON or S-expression utilities?

 The answer is "No" for all questions, though members of our team have rather strong background
 in program compilation and transformation techniques (for example, supercompilation, partial evaluation and the like).

2. _After you read the problem description, what were all the strategies you considered using?_

 Apart from the brute force (which obviously could assuredly handle only small problems) we considered using a genetic algorithm.
 The problem was we couldn't define a sensible notion of "similarity" between programs, so we eventually gave up on the idea.
 
 Another idea that came up again and again was to try to find some representation for a set of programs which would allow to filter out unsatisfactory programs without storing and computing them all. In some way this idea was very succesfull for bonus problems.
 
 We tried to implement an idea to have some choices for possible expressions in nodes of a program tree (and even unknown, wildcard, nodes). Traces of this attempt can be seen in the Alt and Wildcard Expression's subclasses and also in its filter method implemented for some oeprators. By our reckoning this approach could allow assured solving of all problems up to size 18 or even 19. And may be not. We hadn't finished the implementation in time (even weren't close to it). BTW, this approach was based on an interesting observation that only a small subset of all 64-bit values can be obtained from a given input by programs of a given length (this is why GenerValues class is presented in our sources).

 Another idea (also haven't been used in the final version) is that any program of size <=32 can be represented as 128-bit integer (16 bytes): 4 bits per operators, consts and variables (see Tree and Operator classes). Using of such representation instead of tree of Java objects could reduce time and memory needed for programs generation.

3. _Which strategy did you end up choosing and why?_
   - _What was your strategy for the /eval requests?_
   - _Once you received some set of input/output pairs for a program, what did your program do?_
   - _How did you make use of the counter-examples that we provided for each guess?_

 We ended up with a brute force algotihm:
     1. Generate a set of programs that potentially could be a solution.
     2. Choose 256 random inputs `{x_i}` and ask `/eval` for corresonding outputs `{y_i}`.
     3. For each `i` filter out programs not satisfying the condition `p(x_i) = y_i`.
     4. Start guessing with the first of the programs we've got.
     5. In case of mismatch filter and try again.
     6. If a set of potential solutions is empty (could be if we weren't able to generate a set af _all_ possible programs for a given size and a set of operators) give up on the problem.

 The algorithm was choosen not because of some exceptional properties, but because we didn't manage to come up with something better in time.
     
 Bonus problems were solved in a more interesting way, see below.

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

 Our possible programs generator builds all programs of given size and operators. It exploited the following expression equivalence rules:
    - binary operators commutativity
    - `shr1/shr4/shr16` reordering
    - `shl1/shr1/shr4/shr16` of 0
    - `shr1/shr4/shr16` of 1
    - double `not` elimination
    - constant condition `if` elimination
    - `if` with equal branches elimination
    - `and/or/xor/plus` of equal arguments
    - `and/or/xor/plus` of 0
 
 But for programs up to size 12 no knowledge of operators in programs were used except the `tfold`.
 Generated candidate programs were no longer than a program we tried to guess.
 
 More close to the contest end, for higher-sized programs we had choosen another strategy:
     1. For a given set of operators generate a maximum set of programs of all sizes (from small to large)
        that our resources (memory and CPU) allow. This was about 100-200M of programs.
     2. For all problems (of any size) with that set of operators try to find a solution among the programs generated in step 1.

 We didn't make use of iterative structure of fold.


5. _What steps did you take to parallelize the search?  
   How much parallelism did you manage to achieve?  
   For example, how many threads/processes did you have going in parallel?  
   How did you manage the concurrency? Language support, libraries, OS-level process support?  
   Did you solve any of the contest problems in parallel? If so, what was your strategy for doing that?_

 Didn't try to parallelize as it was obvious that a better algorithm could have a much greater effect, so there was no reason to invest efforts in parallelization instead of algorithmic improvements.

 Several hours before the contest end we did understand that there is no time for any further improvements and we should start running our existing algorithms for all problems.  The most time-consuming part of our solution was filtering out programs not satisfying the `p(x_i) = y_i` equations for known pairs of `x_i` and `y_i`. We calculated that we can run several instances of our algorithm without reaching the limit on server responses. We ended up running 5 instances of our program: 3 for regular problems and 2 for bonus problems. We selected a limit for a number of programs each instance could generate such that we can try each of our problems in time (we wanted overall rate to be 1 problem per 10s). The problems were exhausted in about 15 minutes before the contest end.


6. _Did you use different strategies for each class of problem?  
   The classes of problems were:_
      - small fold-free problems
      - large fold-free problems
      - tfold problems
      - fold problems
      - bonus problem set 1 (size 42)
      - bonus problem set 2 (size 137)

 We used different strategies for bonus and for all other (regular) problems.  For regular problems see the description of the algorithm above.  Bonus problems solver deserves a separate description.
 
 A list of alternatives is used to represent a set of possible solutions. Each alternative being a direct product of possible condition expressions set, and left and right branches expressions sets. To filter through a Exp( x_i ) = y_i constraint each alternative is split into two: one is where we demand Exp( x_i ) evaluation to go through the left branch and the other - through the right. Contradictory alternatives are stripped out from the list.
https://github.com/veged/icfpc2013/blob/master/src/all/SolverBonus.java#L87

 Unfortunately we didn't notice during the contest that the upper `if0`'s condition in the bonus problems always is in the form `(and e 1)`.  Pretty sure we've lost a couple dozens of points with upper-sized bonuses due to that.  But nevertheless performance of our bonus solver is pretty good: solved all the problems from the set 1 and about 1/3 of problems from the set 2.

7. _How did you cope with the following elements of the game:_
      - _Did you use the training mode a lot before attempting the contest problems?_
      - _How did you deal with the element of risk in the game?  
        For example, many of the large fold-free problems could in fact be solved with a small guess._

 We didn't use the training mode a lot.  For small-sized prolems (up to size 14) our algorithm with certainty had a solution program among the generated.  The only question was if it could find it with a limited number of guesses.  With a help from the training mode we had quickly found that it isn't a problem: after filtering out programs using `/eval` for some small number (8-16) of random inputs any problem could be solved with just a few mismatches.

 When the time had been running out we run our algorithm for larger-sized programs in a hope to find solutions within sizes we could reach.

8. _How many lines of code did you write, and in which languages?_

 2879 nonempty lines in Java language.

9. _How was that code structured? You could indicate the number of modules, the structure of their interfaces, and, if you wish, you could even include a picture of the module dependences._
 
 OMG, this is Java! Don't expect any beauty here.

 The strong sides of Java are its maturity, popularity, simplicity, IDEs, sufficient speed, the ease of concurrent simultaneous development...  But not the beauty.
 
 Here is how our code is organized:
    1. Namespace `bv` contains 12 classes with bv programming language constructions and some special constructions (Alt and Wildcard) which represents set of programs (Alt class contains Set of expressions, Wildcard represents any program with specific size).
    2. Namespace `all` contains 11 classes for communicate with server (Server class), several proram generators (Gener, GenerProgram, Tree/Operator classes), value generator (GenerValues), three solvers (Solver, SolverBonus, SolverMeta) which filter set of programs built by a generator.

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

 Well, thanks for organizing the event! ;-)
 
 Can't wait for your report to know what software was running on your side. Impressive, really!

