package helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import neuralNetwork.InputNeuronImpl;
import neuralNetwork.NeuralLayerImpl;
import neuralNetwork.Neuron;
import neuralNetwork.NeuronImpl;

public class TrainSetsLoader {

	private static final Logger LOGGER = Logger.getLogger(TrainSetsLoader.class);
	
	private String filepath;

	public TrainSetsLoader(String filepath) {
		this.filepath = filepath;
	}

	public List<TrainSet> loadTrainSets() throws IOException {
		List<TrainSet> trainSets = new ArrayList<>();
		File file = new File(filepath);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while (br.ready()) {
				String input = br.readLine();
				String output = br.readLine();
				var inputArray = input.split(" ");
				var outputArray = output.split(" ");
				Neuron[] inputNeurons = new Neuron[inputArray.length];
				Neuron[] outputNeurons = new Neuron[outputArray.length];
				for (var i = 0; i < inputArray.length; i++) {
					inputNeurons[i] = new InputNeuronImpl(Double.parseDouble(inputArray[i]));
				}
				for (var i = 0; i < outputArray.length; i++) {
					outputNeurons[i] = new NeuronImpl(Double.parseDouble(outputArray[i]));
				}
				trainSets.add(new TrainSet(new NeuralLayerImpl(inputNeurons), new NeuralLayerImpl(outputNeurons)));
			}
		} catch (IOException e) {
			LOGGER.fatal(e.getStackTrace());
			throw e;
		}
		return trainSets;
	}

}
