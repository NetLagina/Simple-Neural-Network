package neuralNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;

import helper.Helper;
import helper.NeuronIndexPair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import helper.Pair;

public class NeuralNetworkImpl implements NeuralNetwork {

    private static final Logger LOGGER = LogManager.getLogger(NeuralNetworkImpl.class);

    private final boolean isBiasEnabled;
    private final List<NeuralLayer> layers;
    private final Map<Pair<NeuronIndexPair>, Pair<Double>> weights;
    private Map<Pair<NeuronIndexPair>, Pair<Double>> biasWeights;

    public NeuralNetworkImpl(final NeuralLayer[] layers, boolean isBiasEnabled) {
        this.layers = new ArrayList<>(Arrays.asList(layers));
        this.isBiasEnabled = isBiasEnabled;

        this.weights = new TreeMap<>();
        for (var firstLayer = 0; firstLayer < this.layers.size() - 1; firstLayer++) {
            for (var firstLayerNeuron = 0; firstLayerNeuron < this.layers.get(firstLayer).size(); firstLayerNeuron++) {
                for (var secondLayerNeuron = 0; secondLayerNeuron < this.layers.get(firstLayer + 1).size(); secondLayerNeuron++) {
                    weights.put(new Pair<>(new NeuronIndexPair(firstLayer, firstLayerNeuron), new NeuronIndexPair(firstLayer + 1, secondLayerNeuron)), new Pair<>(Helper.getRandom(), 0.0));
                }
            }
        }

        if (isBiasEnabled) {
            this.biasWeights = new TreeMap<>();
            for (var biasLayer = 0; biasLayer < this.layers.size() - 1; biasLayer++) {
                for (var secondLayerNeuron = 0; secondLayerNeuron < this.layers.get(biasLayer + 1).size(); secondLayerNeuron++) {
                    biasWeights.put(new Pair<>(new NeuronIndexPair(biasLayer, -1), new NeuronIndexPair(biasLayer + 1, secondLayerNeuron)), new Pair<>(Helper.getRandom(), 0.0));
                }
            }
        }
    }

    public NeuralNetworkImpl(final NeuralLayer[] layers, final Map<Pair<NeuronIndexPair>, Pair<Double>> weights, boolean isBiasEnabled) {
        this.layers = new ArrayList<>(Arrays.asList(layers));
        this.weights = weights;
        this.isBiasEnabled = isBiasEnabled;
        if (isBiasEnabled) {
            this.biasWeights = new TreeMap<>();
            for (var biasLayer = 0; biasLayer < this.layers.size() - 1; biasLayer++) {
                for (var secondLayerNeuron = 0; secondLayerNeuron < this.layers.get(biasLayer + 1).size(); secondLayerNeuron++) {
                    biasWeights.put(new Pair<>(new NeuronIndexPair(biasLayer, -1), new NeuronIndexPair(biasLayer + 1, secondLayerNeuron)), new Pair<>(Helper.getRandom(), 0.0));
                }
            }
        }
    }

    public void setInputLayer(NeuralLayer layer) {
        layers.set(0, layer);
    }

    public void calc() {
        LOGGER.debug("Calculation started");
        for (var layerIndex = 1; layerIndex < layers.size(); layerIndex++) {
            Neuron[] neurons = new Neuron[layers.get(layerIndex).size()];
            for (var neuronIndex = 0; neuronIndex < layers.get(layerIndex).size(); neuronIndex++) {
                neurons[neuronIndex] = new NeuronImpl(calcNeuronValue(new NeuronIndexPair(layerIndex, neuronIndex)));
            }
            layers.set(layerIndex, new NeuralLayerImpl(neurons));
        }
    }

    private double calcNeuronValue(NeuronIndexPair neuronPairIndex) {
        double newValue = 0.0;
        for (var previousLayerNeuronIndex = 0; previousLayerNeuronIndex < layers.get(neuronPairIndex.getLayerIndex() - 1).size(); previousLayerNeuronIndex++) {
            var weight = weights.get(new Pair<>(new NeuronIndexPair(neuronPairIndex.getLayerIndex() - 1, previousLayerNeuronIndex), neuronPairIndex));
            var value = weight.getFirstValue() * layers.get(neuronPairIndex.getLayerIndex() - 1).getNeuron(previousLayerNeuronIndex).getValue();
            newValue += value;
        }
        if (isBiasEnabled) {
            var biasWeight = biasWeights.get(new Pair<>(new NeuronIndexPair(neuronPairIndex.getLayerIndex() - 1, -1), neuronPairIndex));
            newValue += biasWeight.getFirstValue();
        }
        return newValue;
    }

    public boolean calcMultiThreads() throws InterruptedException {
        var executor = Executors.newCachedThreadPool();

        for (var layerIndex = 1; layerIndex < layers.size(); layerIndex++) {
            Neuron[] neurons = new Neuron[layers.get(layerIndex).size()];
            var value = new Future[layers.get(layerIndex).size()];
            for (var neuronIndex = 0; neuronIndex < layers.get(layerIndex).size(); neuronIndex++) {
                value[neuronIndex] = executor.submit(new CalcNeuronValue(new NeuronIndexPair(layerIndex, neuronIndex)));
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
        return executor.awaitTermination(1, TimeUnit.HOURS);
    }

    private class CalcNeuronValue implements Callable<Double> {

        private final NeuronIndexPair neuronPairIndex;

        public CalcNeuronValue(final NeuronIndexPair neuronPairIndex) {
            this.neuronPairIndex = neuronPairIndex;
        }

        @Override
        public Double call() {
            double newValue = 0.0;
            for (var i = 0; i < layers.get(neuronPairIndex.getLayerIndex() - 1).size(); i++) {
                var weight = weights.get(new Pair<>(new NeuronIndexPair(neuronPairIndex.getLayerIndex() - 1, i), neuronPairIndex));
                var value = weight.getFirstValue() * layers.get(neuronPairIndex.getLayerIndex() - 1).getNeuron(i).getValue();
                newValue += value;
            }
            if (isBiasEnabled) {
                var biasWeight = biasWeights.get(new Pair<>(new NeuronIndexPair(neuronPairIndex.getLayerIndex() - 1, -1), neuronPairIndex));
                newValue += biasWeight.getFirstValue();
            }
            return newValue;
        }

    }

    public boolean isBiasEnabled() {
        return isBiasEnabled;
    }

    public NeuralLayer getOutputLayer() {
        return layers.get(layers.size() - 1);
    }

    public NeuralLayer getLayer(int index) {
        return layers.get(index);
    }

    public Map<Pair<NeuronIndexPair>, Pair<Double>> getWeights() {
        return weights;
    }

    public Map<Pair<NeuronIndexPair>, Pair<Double>> getBiasWeights() {
        return biasWeights;
    }

    @Override
    public Iterator<NeuralLayer> iterator() {
        return layers.iterator();
    }

    public int size() {
        return layers.size();
    }
}
