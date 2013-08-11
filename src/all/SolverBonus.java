package all;
import bv.Language;
import bv.Program;
import bv.Expression;
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


public class SolverBonus extends Language {

    private Long JSONValueToLong(Object object) {
        return new BigInteger(object.toString().substring(2), 16).longValue();
    }

    private class IOKey {
        private final Long input;
        private final Long output;

        public IOKey(long input, long output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IOKey)) return false;
            IOKey key = (IOKey) o;
            return input.equals(key.input) && output.equals(key.output);
        }

        @Override
        public int hashCode() {
            return input.hashCode() ^ output.hashCode();
        }
    }

    static Server server = new Server("http://icfpc2013.cloudapp.net", "02555GzpmfL7UKS3Xx39tc5BrT44eUtqme3wo2EyvpsH1H");
    private int size;
    private int sampleSize;
    private Long[] inputs;
    private Long[] outputs;
    private ArrayList<Program> allProgs;
    private Map<IOKey, HashSet<Program>> progsByIO;

    private ArrayList<ArrayList<Expression>[]> guesses;
    private ArrayList<Expression>[] start_guess;

    private ArrayList<Expression> GenAllExp(Gener gener, int size) {
        ArrayList<Expression> exprs = new ArrayList<Expression>();
        for (int i = 1; i <= size; i++) {
            exprs.addAll(gener.GenExp(false, i));
        }
        return exprs;
    }

    public SolverBonus(int size) {
        this.size = size;
        Gener gener = new Gener();
        ArrayList<Expression> conds = GenAllExp(gener, 10);
        ArrayList<Expression> lefts = conds; //GenAllExp(gener, 10);
        ArrayList<Expression> rights = lefts;
        guesses = new ArrayList<ArrayList<Expression>[]>();
        start_guess = new ArrayList[]{conds, lefts, rights};
        guesses.add(start_guess);
        System.out.println("Init: " + conds.size() + " " + lefts.size() + " " + rights.size());
    }


    public void filterGuesses(long x, long y) {
        long size = 0;
        Language.x.value = x;
        ArrayList<ArrayList<Expression>[]> new_guesses = new ArrayList<ArrayList<Expression>[]>();
        for (ArrayList<Expression>[] guess : guesses) {
            ArrayList<Expression> conds = guess[0];
            ArrayList<Expression> lefts = guess[1];
            ArrayList<Expression> rights = guess[2];
            ArrayList<Expression> left_conds = new ArrayList<Expression>();
            ArrayList<Expression> right_conds = new ArrayList<Expression>();
            for (Expression cond : conds) {
                if (cond.eval() == 0) {
                    left_conds.add(cond);
                } else {
                    right_conds.add(cond);
                }
            }
            ArrayList<Expression> lefts_ok = new ArrayList<Expression>();
            for (Expression left : lefts) {
                if (left.eval() == y) {
                    lefts_ok.add(left);
                }
            }
            ArrayList<Expression> rights_ok = new ArrayList<Expression>();
            for (Expression right : rights) {
                if (right.eval() == y) {
                    rights_ok.add(right);
                }
            }
            if (!left_conds.isEmpty() && !lefts_ok.isEmpty()) {
                ArrayList<Expression>[] new_guess = new ArrayList[]{left_conds, lefts_ok, rights};
                new_guesses.add(new_guess);
                size += (long) left_conds.size() * lefts_ok.size() * rights.size();
            }
            if (!right_conds.isEmpty() && !rights_ok.isEmpty()) {
                ArrayList<Expression>[] new_guess = new ArrayList[]{right_conds, lefts, rights_ok};
                new_guesses.add(new_guess);
                size += (long) right_conds.size() * rights_ok.size() * lefts.size();
            }
        }
        guesses = new_guesses;
        System.out.println("Filtered: new size = " + size + ", guesses size = " + guesses.size());
    }

    public static void main(String[] args) {
        SolverBonus solver = new SolverBonus(50);
        solver.sampleSize = 16;
        solver.inputs = new Long[solver.sampleSize];
        Random random = new Random();
        for (int i = 0; i < solver.sampleSize; i++) {
            solver.inputs[i] = random.nextLong();
        }
//		while (true) {
//			solver.solveTraining();
//			solver.guesses = new ArrayList<ArrayList<Expression>[]>();
//			solver.guesses.add(solver.start_guess);
//		}
        solver.solveAll();
    }

    public void solveTraining() {
        solve(getTrainingProblem());
    }

    public void solveAll() {
        for (String problemId : getProblems()) {
            solve(problemId);
            guesses = new ArrayList<ArrayList<Expression>[]>();
            guesses.add(start_guess);
            //break;
        }
    }

    public void solve(String problemId) {
        JSONObject request = new JSONObject();
        request.put("id", problemId);
        JSONArray arguments = new JSONArray();
        request.put("arguments", arguments);
        for (int i = 0; i < sampleSize; i++) {
            arguments.add("0x" + Long.toHexString(inputs[i]));
        }
//		System.out.print(request.toString());

        JSONArray outputs = (JSONArray) ((JSONObject) server.eval(request)).get("outputs");
//		System.out.print(outputs.toString());

        for (int j = 0; j < sampleSize; j++) {
            Long output = JSONValueToLong(outputs.get(j));
            filterGuesses(inputs[j], output);
        }

        guess(problemId);

    }

    public void guess(String problemId) {
        JSONObject request = new JSONObject();
        request.put("id", problemId);

        request.remove("arguments");
        while (true) {
            System.out.println("Guesses size: " + guesses.size());
            if (guesses.isEmpty()) {
                System.out.println("Kaput! Empty guesses!");
                break;
            }

            ArrayList<Expression>[] guess = guesses.get(0);
            Program p = program(if0(guess[0].get(0), guess[1].get(0), guess[2].get(0)));
            System.out.println(p);
            request.put("program", p.toString());
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

                filterGuesses(input, output);
            } else {
                System.out.println("error: " + result.get("message"));
            }
        }
    }

    public String getTrainingProblem() {
        JSONObject request = new JSONObject();
//		request.put("size", size);
        request.put("size", 137);

//		JSONArray operators = new JSONArray();
//		request.put("operators", operators);
//		operators.add("tfold");
//		operators.add("fold");

        System.out.println("training request: " + request.toString());

        JSONObject problem = (JSONObject) server.train(request);
        System.out.println("training: " + problem.toString());

        return problem.get("id").toString();
    }

    public ArrayList<String> getProblems() {
        JSONObject request = new JSONObject();
        request.put("size", size);

        JSONArray allProblems = (JSONArray) server.myproblems();
//		System.out.println("myproblems: " + allProblems.toString());

        System.out.println("myproblems: ");
        ArrayList<String> problems = new ArrayList<String>();
        for (Object p : allProblems) {
            JSONObject problem = (JSONObject) p;
            if ((Long) problem.get("size") <= size) {
                if (!problem.containsKey("solved") /*|| !((Boolean) problem.get("solved"))*/) {
                    JSONArray operators = (JSONArray) problem.get("operators");
                    if (operators.contains("bonus")) {
                        System.out.println(problem.toString());
                        problems.add(problem.get("id").toString());
                    }
                }
            }
        }

        return problems;
    }
}
