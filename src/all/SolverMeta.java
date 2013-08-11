package all;

import bv.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.math.BigInteger;
import java.util.*;

/*

 TODO:

 + генерим все программы размера 3 ([p])
 + берём 1024 случайных чисел ([xi])
 + подставляем числа в программы (получаем ответы [yi])
 + строим индекс <xy, [p]>
 + взять задачу длины 3
 + спрашиваем у сервера случайные sampleSize чисел из [xi]
 + ответы используем в индексе, получая пересечение всех удовлетворяющих программ

 */

public class SolverMeta extends Language {

    private Long JSONValueToLong (Object object) {
        return new BigInteger(object.toString().substring(2), 16).longValue();
    }

    private class IOKey {
        private final Long input;
        private final Long output;

        public IOKey (long input, long output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public boolean equals (Object o) {
            if (this == o)
                return true;
            if (!(o instanceof IOKey))
                return false;
            IOKey key = (IOKey) o;
            return input.equals(key.input) && output.equals(key.output);
        }

        @Override
        public int hashCode () {
            return input.hashCode() ^ output.hashCode();
        }
    }

    static Server server = new Server("http://icfpc2013.cloudapp.net", "02555GzpmfL7UKS3Xx39tc5BrT44eUtqme3wo2EyvpsH1H");
    private static Random random = new Random();
    private Gener gener = new Gener();
    private int size;
    private int sampleSize;
    private long[] inputs;
    private long[] outputs;
    private ArrayList<Program> allProgs;
    private Map<IOKey, HashSet<Program>> progsByIO;
    private ArrayList<JSONObject> json_problems;

    public static GenerValues myGenVals;
    public static GenerPrograms myGenProgs;

    public SolverMeta (int size) {
        this.size = size;
        sampleAllProgs(1, gener.GenAllProg(size));
    }

    public SolverMeta (int size, ArrayList<JSONObject> problems) {
        this.size = size;
        json_problems = problems;
    }

    public void sampleAllProgs (int sampleSize, ArrayList<Program> allProgs) {
        this.sampleSize = sampleSize;
        this.allProgs = allProgs;

        System.out.println("Number of programs: " + this.allProgs.size());
        progsByIO = new HashMap<IOKey, HashSet<Program>>();

        inputs = new long[this.sampleSize];
        outputs = new long[allProgs.size()];
        for (int i = 0; i < this.sampleSize; i++) {
            Long input = inputs[i] = random.nextLong();
            int j = 0;
            for (Program p : this.allProgs) {
                outputs[j++] = p.run(input);
                // IOKey key = new IOKey(input, p.run(input));
                // if (!progsByIO.containsKey(key)) {
                // progsByIO.put(key, new HashSet<Program>());
                // }
                // progsByIO.get(key).add(p);
            }
        }
    }

    public static int intCompare (int x, int y) {
        if (x < y)
            return -1;
        if (x > y)
            return 1;
        return 0;
    }

    public static void main (String[] args) {
        int size = 14;
        // ArrayList<JSONObject> problems = getProblemsOfSize(size);
//        ArrayList<JSONObject> problems = getTrainProblemOfSize(size);
//        Collections.sort(problems, new Comparator<JSONObject>() {
//            public int compare (JSONObject p1, JSONObject p2) {
//                JSONArray ops1 = (JSONArray) p1.get("operators");
//                JSONArray ops2 = (JSONArray) p2.get("operators");
//                return intCompare(ops1.size(), ops2.size());
//            }
//        });
//        for (JSONObject p : problems) {
//            JSONArray ops = (JSONArray) p.get("operators");
//            ArrayList<String> str_ops = new ArrayList<String>();
//            for (Object op : ops) {
//                str_ops.add(op.toString());
//            }
//            break;
//        }
        ArrayList<String> ops = new ArrayList<String>(Arrays.asList(new String[]{"not", "shl1", "shr1", "xor"}));  // TODO: get operators from somewhere

        long x0 = random.nextLong();
        myGenVals = new GenerValues(ops, x0);
        myGenProgs = new GenerPrograms(ops);
        SolverMeta.generateExpsBySizeAndOutput(ops, Wildcard.metaSize, x0);

        Expression e = Language.alt(GenerPrograms.GenMetaExps(11, ops));
	Set<Long> vals = myGenVals.genValues(11);

	System.out.println("Before: " + e.any());
    Expression a = e.any();
    //long v = vals.iterator().next();
    long v = a.eval();
	e = e.filter(v);
    a = e.any();
	System.out.println("Filtered: " + "v = " + v + " eval = " + a.eval() + " a = " + a);

        //SolverMeta solver = new SolverMeta(size, problems);
        // while (true) {
        // solver.solveTraining();
        // }
        // solver.solveAll();
        //solver.solveAllWithOps();
    }

    public void solveTraining () {
        solve(getTrainingProblem());
    }

    public void solveAll () {
        for (String problemId : getProblems()) {
            solve(problemId);
            // break;
        }
    }

    public void solveAllWithOps () {
        for (JSONObject p : json_problems) {
            JSONArray ops = (JSONArray) p.get("operators");
            ArrayList<String> str_ops = new ArrayList<String>();
            for (Object op : ops) {
                str_ops.add(op.toString());
            }
            sampleAllProgs(1, GenerPrograms.GenAllProgs(size, str_ops));
            solve(p.get("id").toString());
            break;
        }
    }

    public void solve (String problemId) {
        JSONObject request = new JSONObject();
        request.put("id", problemId);
        JSONArray arguments = new JSONArray();
        request.put("arguments", arguments);
        for (int i = 0; i < sampleSize; i++) {
            arguments.add("0x" + Long.toHexString(inputs[i]));
        }
        // System.out.print(request.toString());

        JSONArray outputs = (JSONArray) ((JSONObject) server.eval(request)).get("outputs");
        // System.out.print(outputs.toString());

        HashSet<Program> guesses = new HashSet<Program>();
        int j = 0;
        long output = JSONValueToLong(outputs.get(0));
        for (Program p : this.allProgs) {
            long sampleOutput = this.outputs[j++];
            if (sampleOutput == output) {
                guesses.add(p);
            }
        }

        // HashSet<Program> guesses = null;
        // for (int i = 0; i < sampleSize; i++) {
        // IOKey key = new IOKey(
        // inputs[i],
        // JSONValueToLong(outputs.get(i)));
        // HashSet<Program> ps = progsByIO.get(key);
        //
        // if (guesses == null) {
        // guesses = new HashSet<Program>(ps);
        // } else {
        // guesses.retainAll(ps);
        // }
        // }

        guess(problemId, guesses);
    }

    public void guess (String problemId, HashSet<Program> guesses) {
        JSONObject request = new JSONObject();
        request.put("id", problemId);

        request.remove("arguments");
        while (true) {
            System.out.println("Guesses size: " + guesses.size());
            if (guesses.isEmpty())
                throw new Error("Empty guesses!");

            Program guess = guesses.iterator().next();
            System.out.println(guess);
            request.put("program", guess.toString());
            JSONObject result = (JSONObject) server.guess(request);
            String status = result.get("status").toString();
            if (status.equals("win")) {
                System.out.println("win");
                break;
            } else if (status.equals("mismatch")) {
                JSONArray values = (JSONArray) result.get("values");
                System.out.println("mismatch: " + values);
                Long input = JSONValueToLong(values.get(0));
                Long output = JSONValueToLong(values.get(1));

                HashSet<Program> newGuesses = new HashSet<Program>();
                for (Program oldGuess : guesses) {
                    if (oldGuess.run(input) == output) {
                        newGuesses.add(oldGuess);
                    }
                }
                guesses = newGuesses;
                // if(guesses.size() < 42000) {
                // sampleAllProgs(256, new ArrayList<Program>(guesses));
                // solve(problemId);
                // break;
                // }
            } else {
                System.out.println("error: " + result.get("message"));
                guesses.remove(guess);
            }
        }
    }

    public String getTrainingProblem () {
        JSONObject request = new JSONObject();
        request.put("size", size);

        // JSONArray operators = new JSONArray();
        // request.put("operators", operators);
        // operators.add("tfold");
        // operators.add("fold");

        System.out.println("training request: " + request.toString());

        JSONObject problem = (JSONObject) server.train(request);
        System.out.println("training: " + problem.toString());

        return problem.get("id").toString();
    }

    static public ArrayList<JSONObject> getTrainProblemOfSize (int size) {
        JSONObject request = new JSONObject();
        request.put("size", size);

        // JSONArray operators = new JSONArray();
        // request.put("operators", operators);
        // operators.add("tfold");
        // operators.add("fold");

        System.out.println("training request: " + request.toString());

        JSONObject problem = (JSONObject) server.train(request);
        System.out.println("training: " + problem.toString());

        ArrayList<JSONObject> res = new ArrayList<JSONObject>();
        res.add(problem);
        return res;
    }

    static public ArrayList<JSONObject> getProblemsOfSize (int size) {
        JSONObject request = new JSONObject();
        request.put("size", size);

        JSONArray allProblems = (JSONArray) server.myproblems();
        // System.out.println("myproblems: " + allProblems.toString());

        System.out.println("myproblems: ");
        ArrayList<JSONObject> problems = new ArrayList<JSONObject>();
        for (Object p : allProblems) {
            JSONObject problem = (JSONObject) p;
            if ((Long) problem.get("size") <= size) {
                if (!problem.containsKey("solved") || !((Boolean) problem.get("solved"))) {
                    // JSONArray operators = (JSONArray) problem.get("operators");
                    // if (operators.contains("tfold")) {
                    System.out.println(problem.toString());
                    problems.add(problem);
                    // }
                }
            }
        }
        return problems;
    }

    public ArrayList<String> getProblems () {
        JSONObject request = new JSONObject();
        request.put("size", size);

        JSONArray allProblems = (JSONArray) server.myproblems();
        // System.out.println("myproblems: " + allProblems.toString());

        System.out.println("myproblems: ");
        ArrayList<String> problems = new ArrayList<String>();
        for (Object p : allProblems) {
            JSONObject problem = (JSONObject) p;
            if ((Long) problem.get("size") <= size) {
                if (!problem.containsKey("solved") || !((Boolean) problem.get("solved"))) {
                    JSONArray operators = (JSONArray) problem.get("operators");
                    if (operators.contains("tfold")) {
                        System.out.println(problem.toString());
                        problems.add(problem.get("id").toString());
                    }
                }
            }
        }

        return problems;
    }

    public void estimateArgsFind (int sumSize, int size1, int size2) {
        System.out.println("Sizes: " + size + " by " + sumSize + " " + size1 + " " + size2);

        // берём рандомный инпут
        Long input = Language.x.value = random.nextLong();

        // генерируем HashSet всех значений по размерам
        Map<Integer, HashSet<Long>> outputsBySize = new HashMap<Integer, HashSet<Long>>();
        for (int i = 1; i <= size; i++) {
            if (!outputsBySize.containsKey(i)) {
                outputsBySize.put(i, new HashSet<Long>());
            }
            HashSet<Long> outputs = outputsBySize.get(i);

            ArrayList<Expression> exps = gener.GenExp(false, i);
            for (Expression exp : exps) {
                outputs.add(exp.eval());
            }
        }

        // берём два размера и получаем результат AND (OR)
        HashSet<Long> outputs1 = outputsBySize.get(size1);
        Long output1 = getRandomFromHashSet(outputs1);
        HashSet<Long> outputs2 = outputsBySize.get(size2);
        Long output2 = getRandomFromHashSet(outputs2);
        System.out.println("Outputs: " + output1 + " " + output2);
        Long output = output1 & output2;

        // пытаемся восстановить все способы получения такого результата

        long start = System.nanoTime();

        // фильтруем значения на предмет возможности получения через AND (OR)
        outputs1 = filterForAnd(outputs1, output);
        outputs2 = filterForAnd(outputs2, output);

        // ищем все подходящие пары аргументов
        HashSet<Long[]> result = new HashSet<Long[]>();
        for (Long i : outputs1) {
            for (Long j : outputs2) {
                if (output.equals(i & j))
                    result.add(new Long[] { i, j });
            }
        }
        System.out.println("Find " + result.size() + " pairs");
        long stop = System.nanoTime();
        System.out.println("Time for find all args: " + ((stop - start) / 1e9));
    }

    private Long getRandomFromHashSet (HashSet<Long> xs) {
        int rnd = random.nextInt(xs.size());
        int i = 0;
        Long result = 0L;
        for (Long x : xs) {
            if (i == rnd)
                result = x;
            i++;
        }
        return result;
    }

    private HashSet<Long> filterForAnd (HashSet<Long> xs, Long y) {
        HashSet<Long> result = new HashSet<Long>();
        for (Long x : xs) {
            if (y.equals(y & x))
                result.add(x);
        }
        System.out.println("Filtered " + result.size() + " from " + xs.size() + " by " + y);
        return result;
    }

    public static Map<Integer, Map<Long, Expression>> expsBySizeAndOutput = new HashMap<Integer, Map<Long, Expression>>();

    private static Map<Integer, Map<Long, Expression>> generateExpsBySizeAndOutput(ArrayList<String> operators, int size, Long input) {
        Language.x.value = input;
        for (int i = 1; i <= size; i++) {
            if (!expsBySizeAndOutput.containsKey(i)) {
                expsBySizeAndOutput.put(i, new HashMap<Long, Expression>());
            }
            Map<Long, Expression> expsByOutputs = expsBySizeAndOutput.get(i);
            Map<Long, ArrayList<Expression>> expsByOutputs_ = new HashMap<Long, ArrayList<Expression>>();

            ArrayList<Expression> allExps = GenerPrograms.GenExps(i, operators);
            for (Expression exp : allExps) {
                Long output = exp.eval();
                if (!expsByOutputs_.containsKey(output)) expsByOutputs_.put(output, new ArrayList<Expression>());
                expsByOutputs_.get(output).add(exp);
            }

            for (Map.Entry<Long, ArrayList<Expression>> entry : expsByOutputs_.entrySet()) {
                expsByOutputs.put(entry.getKey(), Language.alt(entry.getValue()));
            }

            System.out.println("expsByOutputs for size " + i + " " + expsByOutputs.size());
        }

        return expsBySizeAndOutput;
    }


}
