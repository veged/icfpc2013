import bv.Language;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;

public class Server extends Language {
	private String host;
	private String auth;

	public Server(String host, String auth) {
		this.host = host;
		this.auth = auth;
	}

	public Object request(String path, JSONObject request) {
		StringBuilder result = new StringBuilder();
		try {
			URL url = new URL(host + "/" + path + "?auth=" + auth);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();

			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(request.toJSONString());
			writer.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
				result.append("\n");
			}
			reader.close();

			connection.disconnect();

			return JSONValue.parse(result.toString());

		} catch (Exception e) {

			e.printStackTrace();
			return "";

		}
	}
}
