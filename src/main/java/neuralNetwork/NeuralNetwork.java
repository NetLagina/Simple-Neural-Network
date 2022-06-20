package neuralNetwork;

import java.util.Iterator;
import java.util.Map;

import helper.NeuronIndexPair;
import helper.Pair;

public interface NeuralNetwork extends Iterable<NeuralLayer> {
    void setInputLayer(NeuralLayer layer);

    void calc();

    boolean calcMultiThreads() throws InterruptedException;

    boolean isBiasEnabled();

    NeuralLayer getOutputLayer();

    NeuralLayer getLayer(int index);

    Map<Pair<NeuronIndexPair>, Pair<Double>> getWeights();

    Map<Pair<NeuronIndexPair>, Pair<Double>> getBiasWeights();

    Iterator<NeuralLayer> iterator();

    int size();
}
