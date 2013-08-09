import bv.Language;
import org.json.simple.JSONObject;

/*

TODO:

  - генерим все программы размера 3 ([p])
  - берём 1024 случайных чисел ([xi])
  - подставляем числа в программы (получаем ответы [yi])
  - строим индекс <xy, [p]>
  - взять задачу длины 3
  - спрашиваем у сервера случайные 256 чисел из [xi]
  - ответы используем в индексе, получая пересечение всех удовлетворяющих программ

 */


public class Solver extends Language {

	public Solver() {
	}

	public Void solve() {
		Server server = new Server("http://icfpc2013.cloudapp.net", "02555GzpmfL7UKS3Xx39tc5BrT44eUtqme3wo2EyvpsH1H");
		JSONObject request = new JSONObject() {{
			put("size", 3);
			// JSONArray
		}};

		System.out.print(server.request("train", request).toString());

		return null;
	}
}
