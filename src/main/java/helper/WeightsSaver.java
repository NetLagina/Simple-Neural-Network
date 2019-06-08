package helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class WeightsSaver {

	private static final Logger LOGGER = Logger.getLogger(WeightsSaver.class);
	
	private final static String WEIGHTS_FILE_PATH = ".\\weights.json";
	
	public void save(Map<Pair<Pair<Integer>>, Pair<Double>> weights) {
		Gson gson = new Gson();
		String json = gson.toJson(weights);
		File file = new File(WEIGHTS_FILE_PATH);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				LOGGER.fatal(e1.getStackTrace());
			}
		}
		if (file.isFile() && file.canWrite()) {
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
				bw.write(json);
				bw.flush();
			} catch (IOException e) {
				LOGGER.fatal(e.getStackTrace());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Map<Pair<Pair<Integer>>, Pair<Double>> load() {
		String json = "";
		File file = new File(WEIGHTS_FILE_PATH);
		Map<Pair<Pair<Integer>>, Pair<Double>> weights = null;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			json = br.readLine();
			Gson gson = new Gson();
			weights = gson.fromJson(json, Map.class);
		} catch (FileNotFoundException e) {
			LOGGER.fatal(e.getStackTrace());
		} catch (IOException e) {
			LOGGER.fatal(e.getStackTrace());
		}
		return weights;
	}
	
	public boolean isExist() {
		File file = new File(WEIGHTS_FILE_PATH);
		return file.exists();
	}

}
