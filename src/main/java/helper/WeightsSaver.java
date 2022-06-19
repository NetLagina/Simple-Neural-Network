package helper;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Map;

public class WeightsSaver {

    private static final Logger LOGGER = LogManager.getLogger(WeightsSaver.class);

    private final static String WEIGHTS_FILE_PATH = ".\\weights.json";

    public void save(Map<Pair<NeuronIndexPair>, Pair<Double>> weights) {
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
    public Map<Pair<NeuronIndexPair>, Pair<Double>> load() {
        String json = "";
        File file = new File(WEIGHTS_FILE_PATH);
        Map<Pair<NeuronIndexPair>, Pair<Double>> weights = null;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            json = br.readLine();
            Gson gson = new Gson();
            weights = gson.fromJson(json, Map.class);
        } catch (IOException e) {
            LOGGER.fatal(e.getStackTrace());
        }
        return weights;
    }

}
