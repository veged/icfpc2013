### ICFP Programming Contest 2013

 - [Official site](http://icfpc2013.cloudapp.net)

### Team
 - Team name: Error 404
 - Members: [druxa](https://github.com/druxa/), [orlovan](https://github.com/orlovan/), [yuklimov](https://github.com/yuklimov/), [veged](https://github.com/veged/)

### Realization
 - Language: Java
 - Algorithm: Vulgar brute force mostly.

### Overall impression: bewildered

From the start we had clearly seen that the problem could be solved only by applying some deep and intelligent ideas. We had not a single one. 

By the end of the lightning division we had got some brute force algorithm that could with certainty solve all problems up to size 11 and `tfold` problems for some upper sizes.
Eventually, when the time had been running out, we had run an improved version of that algorithm on all the rest of the problems. Could assuredly solve all problems up to the size 14 to the moment. Of course we used all the possible means we could think out to reduce the search space, but still it was nothing of resembling a beautiful solution.

Bonus problems were more interesting. We hit upon an idea that branches for `if0` could be computed and filtered out separately. This led to the addition of choices for possible programs for `if0`'s branches instead of their production (see https://github.com/veged/icfpc2013/blob/master/src/all/SolverBonus.java#L87). All the bonuses from a first pack we successfully solved by this algorithm.

People say all the bonus problems were in the following form: `f(x) = (lambda (x) (if0 (and e1 1) e2 e3))`.  We hadn't noticed that.  Only the upper `if0`.  What a pity!  Pretty sure we've lost a couple dozens of points with upper-sized bonuses due to that.

Inspired by success with bonuses we tried to implement a vast generalization of the idea to have some choices for possible expressions in the nodes of a program tree (and even unknown, wildcard, nodes). Traces of this attempt can be seen in the `Alt` and `Wildcard` `Expression`'s subclasses and also in its `filter` method implemented for some oeprators.  By our reckoning this approach could allow assured solving of all problems up to a zize 18 or 19.  And may be not. We hadn't finished the implementation in time (even weren't close to it).

Still don't know how the task could be solved. Could it?

### Results

Total score: 1456

#### Regural problems
Size|Number|Solved|Failed
----|----|----|----
 3  | 20 | 20 |  0
 4  | 20 | 20 |  0
 5  | 20 | 20 |  0
 6  | 20 | 20 |  0
 7  | 20 | 20 |  0
 8  | 40 | 40 |  0
 9  | 40 | 40 |  0
10  | 40 | 40 |  0
11  | 60 | 60 |  0
12  | 60 | 60 |  0
13  | 60 | 60 |  0
14  | 60 | 60 |  0
15  | 60 | 56 |  4
16  | 60 | 54 |  6
17  | 60 | 54 |  6
18  | 60 | 50 | 10
19  | 60 | 52 |  8
20  | 60 | 53 |  7
21  | 60 | 53 |  7
22  | 60 | 44 | 16
23  | 60 | 41 | 19
24  | 60 | 40 | 20
25  | 60 | 41 | 19
26  | 60 | 44 | 16
27  | 60 | 37 | 23
28  | 60 | 34 | 26
29  | 60 | 36 | 24
30  | 60 | 40 | 20

#### Bonus problems
Size|Number|Solved|Failed
----|----|----|----
19  |  19 | 19 |  0
20  |  10 | 10 |  0
21  |  60 | 60 |  0
22  |   9 |  9 |  0
23  |  78 | 78 |  0
24  |   5 |  5 |  0
25  |  19 | 19 |  0
--- | --- | ---| ---
31  |   1 |  1 |  0
33  |   1 |  1 |  0
34  |   1 |  1 |  0
35  |   3 |  2 |  1
36  |   5 |  3 |  2
37  |   5 |  3 |  2
38  |   1 |  1 |  0
39  |  40 | 23 | 17
40  |  14 |  6 |  8
41  |   5 |  2 |  3
42  |   1 |  0 |  1
43  | 123 | 24 | 99
