package neuralNetwork;

import java.util.Iterator;
import java.util.Map;

import helper.Pair;

public interface NeuralNetwork extends Iterable<NeuralLayer>{
	void setInputLayer(NeuralLayer layer);
	void calc();
	void calcMultiThreads();
	NeuralLayer getOutputLayer();
	NeuralLayer getLayer(int index);
	Map<Pair<Pair<Integer>>, Pair<Double>> getWeights();
	Iterator<NeuralLayer> iterator();
	int size();
}
