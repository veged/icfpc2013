import bv.Language;
import bv.Program;
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


public class Solver extends Language {

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
	private int sampleSize = 1;
	private Long[] inputs;
	private Map<IOKey, HashSet<Program>> progsByIO;

	public Solver(int size) {
		this.size = size;

		Gener gen = new Gener();
		ArrayList<Program> allProgs = gen.GenAllProg(size);
		System.out.println("Number of programs: " + allProgs.size());
		progsByIO = new HashMap<IOKey, HashSet<Program>>();

		inputs = new Long[sampleSize];
		Random random = new Random();
		for (int i = 0; i < sampleSize; i++) {
			Long input = inputs[i] = random.nextLong();
			for (Program p : allProgs) {
				IOKey key = new IOKey(input, p.run(input));
				if (!progsByIO.containsKey(key)) {
					progsByIO.put(key, new HashSet<Program>());
				}
				progsByIO.get(key).add(p);
			}
		}
	}

	public static void main(String[] args) {
		Solver solver = new Solver(11);
		while (true) {
			solver.solveTraining();
		}
//		solver.solveAll();
	}

	public void solveTraining() {
		solve(getTrainingProblem());
	}

	public void solveAll() {
		for (String problemId : getProblems()) {
			solve(problemId);
//			break;
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

		HashSet<Program> guesses = null;

		for (int i = 0; i < sampleSize; i++) {
			IOKey key = new IOKey(
				inputs[i],
				JSONValueToLong(outputs.get(i)));
			HashSet<Program> ps = progsByIO.get(key);

			if (guesses == null) {
				guesses = new HashSet<Program>(ps);
			} else {
				guesses.retainAll(ps);
			}
		}

		request.remove("arguments");
		while (true) {
			System.out.println("Guesses size: " + guesses.size());
			if (guesses.isEmpty()) throw new Error("Empty guesses!");

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
			} else {
				System.out.println("error: " + result.get("message"));
				guesses.remove(guess);
			}
		}
	}

	public String getTrainingProblem() {
		JSONObject request = new JSONObject();
		request.put("size", size);

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
				System.out.println(problem.toString());
				if(!problem.containsKey("solved") || !((Boolean) problem.get("solved"))) {
					problems.add(problem.get("id").toString());
				}
			}
		}

		return problems;
	}
}
