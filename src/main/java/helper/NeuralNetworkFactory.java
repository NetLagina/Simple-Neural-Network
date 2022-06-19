package helper;

import java.util.Map;

import neuralNetwork.InputNeuronImpl;
import neuralNetwork.NeuralLayer;
import neuralNetwork.NeuralLayerImpl;
import neuralNetwork.NeuralNetwork;
import neuralNetwork.NeuralNetworkImpl;
import neuralNetwork.Neuron;
import neuralNetwork.NeuronImpl;

public class NeuralNetworkFactory {

    public static NeuralNetwork getNeuralNetwork(final int[] layersSize) {
        NeuralLayer[] layers = new NeuralLayer[layersSize.length];
        var i = 0;
        for (var size : layersSize) {
            NeuralLayer layer;
            if (i == 0) {
                layer = getNewInputLayer(size);
            } else {
                layer = getNewLayer(size);
            }
            layers[i++] = layer;
        }
        return new NeuralNetworkImpl(layers);
    }

    public static NeuralNetwork getNeuralNetwork(final int[] layersSize, final Map<Pair<NeuronIndexPair>, Pair<Double>> weights) {
        NeuralLayer[] layers = new NeuralLayer[layersSize.length];
        var i = 0;
        for (var size : layersSize) {
            NeuralLayer layer;
            if (i == 0) {
                layer = getNewInputLayer(size);
            } else {
                layer = getNewLayer(size);
            }
            layers[i++] = layer;
        }
        return new NeuralNetworkImpl(layers, weights);
    }

    private static Neuron getNewNeuron() {
        return new NeuronImpl(0);
    }

    private static Neuron getNewInputNeuron() {
        return new InputNeuronImpl(0);
    }

    private static NeuralLayer getNewLayer(int size) {
        Neuron[] neurons = new Neuron[size];
        for (int i = 0; i < size; i++) {
            neurons[i] = getNewNeuron();
        }
        return new NeuralLayerImpl(neurons);
    }

    private static NeuralLayer getNewInputLayer(int size) {
        Neuron[] neurons = new Neuron[size];
        for (int i = 0; i < size; i++) {
            neurons[i] = getNewInputNeuron();
        }
        return new NeuralLayerImpl(neurons);
    }

}
