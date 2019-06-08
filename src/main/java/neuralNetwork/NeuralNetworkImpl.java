package neuralNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import helper.Pair;

public class NeuralNetworkImpl implements NeuralNetwork {
	
	private static final Logger LOGGER = Logger.getLogger(NeuralNetworkImpl.class);

	private final List<NeuralLayer> layers;
	private final Map<Pair<Pair<Integer>>, Pair<Double>> weights;

	public NeuralNetworkImpl(final NeuralLayer[] layers) {
		this.layers = new ArrayList<>(Arrays.asList(layers));
		
		this.weights = new TreeMap<>();
		for (var firstLayer = 0; firstLayer < this.layers.size() - 1; firstLayer++) {
			for (var firstLayerNeuron = 0; firstLayerNeuron < this.layers.get(firstLayer).size(); firstLayerNeuron++) {
				for (var secondLayerNeuron = 0; secondLayerNeuron < this.layers.get(firstLayer + 1).size(); secondLayerNeuron++) {
					weights.put(new Pair<Pair<Integer>>(new Pair<Integer>(firstLayer, firstLayerNeuron), new Pair<Integer>(firstLayer + 1, secondLayerNeuron)), new Pair<Double>(Math.random(), 0.0));
				}
			}
		}
	}
	
	public NeuralNetworkImpl(final NeuralLayer[] layers, final Map<Pair<Pair<Integer>>, Pair<Double>> weights) {
		this.layers = new ArrayList<>(Arrays.asList(layers));	
		this.weights = weights;
	}

	public void setInputLayer(NeuralLayer layer) {
		layers.set(0, layer);
	}

	public void calc() {
		LOGGER.debug("Calculation started");
		for (var layerIndex = 1; layerIndex < layers.size(); layerIndex++) {
			Neuron[] neurons = new Neuron[layers.get(layerIndex).size()];
			for (var neuronIndex = 0; neuronIndex < layers.get(layerIndex).size(); neuronIndex++) {
				neurons[neuronIndex] = new NeuronImpl(calcNeuronValue(new Pair<Integer>(layerIndex, neuronIndex)));
			}
			layers.set(layerIndex, new NeuralLayerImpl(neurons));
		}
	}

	private double calcNeuronValue(Pair<Integer> neuronPairIndex) {
		double newValue = 0.0;
		for (var i = 0; i < layers.get(neuronPairIndex.getFirstValue() - 1).size(); i++) {
			newValue += weights.get(new Pair<Pair<Integer>>(new Pair<Integer>(neuronPairIndex.getFirstValue() - 1, i),
					                                        neuronPairIndex)
					               ).getFirstValue() *
					    layers.get(neuronPairIndex.getFirstValue() - 1).getNeuron(i).getValue();
		}
		return newValue;
	}

	public void calcMultiThreads() {
		var executor = Executors.newCachedThreadPool();
		
		for (var layerIndex = 1; layerIndex < layers.size(); layerIndex++) {
			Neuron[] neurons = new Neuron[layers.get(layerIndex).size()];
			var value = new Future[layers.get(layerIndex).size()];
			for (var neuronIndex = 0; neuronIndex < layers.get(layerIndex).size(); neuronIndex++) {
				value[neuronIndex] = executor.submit(new CalcNeuronValue(new Pair<Integer>(layerIndex, neuronIndex)));
			}
			for (var neuronIndex = 0; neuronIndex < layers.get(layerIndex).size(); neuronIndex++) {
				try {
					neurons[neuronIndex] = new NeuronImpl((double) value[neuronIndex].get());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
			layers.set(layerIndex, new NeuralLayerImpl(neurons));
		}
		executor.shutdown();
	}

	private class CalcNeuronValue implements Callable<Double> {

		private final Pair<Integer> neuronPairIndex;

		public CalcNeuronValue(final Pair<Integer> neuronPairIndex) {
			this.neuronPairIndex = neuronPairIndex;
		}

		@Override
		public Double call() throws Exception {
			double newValue = 0.0;
			for (var i = 0; i < layers.get(neuronPairIndex.getFirstValue() - 1).size(); i++) {
				newValue += weights.get(new Pair<Pair<Integer>>(new Pair<Integer>(neuronPairIndex.getFirstValue() - 1, i),
						                                        neuronPairIndex)
						               ).getFirstValue() *
						    layers.get(neuronPairIndex.getFirstValue() - 1).getNeuron(i).getValue();
			}
			return newValue;
		}

	};

	public NeuralLayer getOutputLayer() {
		return layers.get(layers.size() - 1);
	}
	
	public NeuralLayer getLayer(int index) {
		return layers.get(index);
	}

	public Map<Pair<Pair<Integer>>, Pair<Double>> getWeights() {
		return weights;
	}

	@Override
	public Iterator<NeuralLayer> iterator() {
		return layers.iterator();
	}
	
	public int size() {
		return layers.size();
	}
}
