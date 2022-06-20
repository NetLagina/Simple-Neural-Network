package neuralNetwork;

import java.io.IOException;
import java.util.List;

import helper.TrainSetsLoader;
import helper.WeightsSaver;
import helper.NeuralNetworkFactory;
import helper.TrainSet;
import teacher.Teacher;

public class Main {

    public static void main(String[] args) {
        var network = NeuralNetworkFactory.getNeuralNetwork(new int[]{15, 15, 10});

        var fileLoader = new TrainSetsLoader(".\\trainSet.txt");
        List<TrainSet> trainSets;
        try {
            trainSets = fileLoader.loadTrainSets();
        } catch (IOException e) {
            return;
        }
        var teacher = new Teacher(network, trainSets);
        if (!teacher.teachBackpropagation()) {
            return;
        }
        WeightsSaver ws = new WeightsSaver();
        ws.save(network.getWeights());

        // digit "2"
        network.setInputLayer(new NeuralLayerImpl(new Neuron[]{
                new InputNeuronImpl(1), new InputNeuronImpl(1), new InputNeuronImpl(1),
                new InputNeuronImpl(0), new InputNeuronImpl(0), new InputNeuronImpl(1),
                new InputNeuronImpl(1), new InputNeuronImpl(1), new InputNeuronImpl(1),
                new InputNeuronImpl(1), new InputNeuronImpl(0), new InputNeuronImpl(0),
                new InputNeuronImpl(1), new InputNeuronImpl(1), new InputNeuronImpl(1)
        }));

        network.calc();
        var output = network.getOutputLayer();
        System.out.println("Answer is");
        for (var i = 0; i < output.size(); i++) {
            System.out.printf("%d: %.1f%%%n", ((i + 1) % 10), output.getNeuron(i).getValue() * 100);
        }
    }

}
