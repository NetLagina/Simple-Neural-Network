package teacher;

import java.util.List;

import helper.NeuronIndexPair;
import neuralNetwork.ErrorRateCalculationMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import helper.Helper;
import helper.Pair;
import helper.TrainSet;
import neuralNetwork.NeuralLayer;
import neuralNetwork.NeuralNetwork;

public class Teacher {

    private static final Logger LOGGER = LogManager.getLogger(Teacher.class);

    private int maxEpoch = 50_000;
    private double minErrorRate = 0.00001;
    private ErrorRateCalculationMethod errorRateCalculationMethod = ErrorRateCalculationMethod.MSE;
    private double learningSpeed = 0.5;
    private double moment = 0.005;

    private final NeuralNetwork network;
    private final List<TrainSet> trainSets;

    private static double[] currentLayerDeltas;
    private static double[] nextLayerDeltas;

    public Teacher(final NeuralNetwork network, final List<TrainSet> trainSets) {
        this.network = network;
        this.trainSets = trainSets;
    }

    public void setMaxEpoch(int maxEpoch) {
        this.maxEpoch = maxEpoch;
    }

    public void setMinErrorRate(double minErrorRate) {
        this.minErrorRate = minErrorRate;
    }

    public void setErrorRateCalculationMethod(ErrorRateCalculationMethod errorRateCalculationMethod) {
        this.errorRateCalculationMethod = errorRateCalculationMethod;
    }

    public void setLearningSpeed(double learningSpeed) {
        this.learningSpeed = learningSpeed;
    }

    public void setMoment(double moment) {
        this.moment = moment;
    }

    public void teachBackpropagation() {
        LOGGER.info("BACKPROPAGATION TRAIN STARTS");
        for (int epoch = 0; epoch < maxEpoch; epoch++) {
            double errorRate;
            var trainSet = trainSets.get(epoch % trainSets.size());
            network.setInputLayer(trainSet.inputLayer());
            network.calc();
            var answer = network.getOutputLayer();

            errorRate = switch (errorRateCalculationMethod) {
                case MSE -> Helper.getMSE(trainSet.outputLayer(), answer);
                case ROOT_MSE -> Helper.getRootMSE(trainSet.outputLayer(), answer);
                case ARCTAN -> Helper.getArctan(trainSet.outputLayer(), answer);
            };

            LOGGER.info("Epoch: " + (epoch + 1) + "/" + maxEpoch + " Error Rate: " + errorRate);
            if (errorRate < minErrorRate) {
                return;
            }

            for (var layerIndex = network.size() - 1; 0 < layerIndex; layerIndex--) {
                currentLayerDeltas = new double[network.getLayer(layerIndex).size()];
                for (var neuronIndex = 0; neuronIndex < network.getLayer(layerIndex).size(); neuronIndex++) {
                    for (var i = 0; i < network.getLayer(layerIndex - 1).size(); i++) {
                        var weightIndex = new Pair<>(new NeuronIndexPair(layerIndex - 1, i), new NeuronIndexPair(layerIndex, neuronIndex));
                        var oldWeightPair = network.getWeights().get(weightIndex);
                        var deltaWeight = getDeltaWeight(weightIndex, oldWeightPair.getSecondValue(), answer, trainSet.outputLayer());
                        network.getWeights().put(weightIndex, new Pair<>(oldWeightPair.getFirstValue() + deltaWeight, deltaWeight));
                    }
                }
                nextLayerDeltas = currentLayerDeltas;
            }
        }
    }

    private double getDelta(int index, NeuralLayer answer, NeuralLayer expectedAnswer) {
        var expAnswer = expectedAnswer.getNeuron(index).getRawValue();
        var realAnswer = answer.getNeuron(index).getValue();
        var delta = (expAnswer - realAnswer) * (1 - realAnswer) * realAnswer;
        currentLayerDeltas[index] = delta;
        return delta;
    }

    private double getDelta(int layerIndex, int index) {
        var weights = network.getWeights();
        var neuronValue = network.getLayer(layerIndex).getNeuron(index).getValue();
        var delta = (1 - neuronValue) * neuronValue;
        for (var j = 0; j < nextLayerDeltas.length; j++) {
            var weight = weights.get(new Pair<>(new NeuronIndexPair(layerIndex, index), new NeuronIndexPair(layerIndex + 1, j))).getFirstValue();
            delta *= weight * nextLayerDeltas[j];
        }
        currentLayerDeltas[index] = delta;
        return delta;
    }

    private double getGrad(Pair<NeuronIndexPair> weightIndex, NeuralLayer answer, NeuralLayer expectedAnswer) {
        double delta;
        if (weightIndex.getSecondValue().getLayerIndex() != network.size() - 1) {
            delta = getDelta(weightIndex.getSecondValue().getLayerIndex(), weightIndex.getSecondValue().getNeuronIndex());
        } else {
            delta = getDelta(weightIndex.getSecondValue().getNeuronIndex(), answer, expectedAnswer);
        }
        var out = network.getLayer(weightIndex.getFirstValue().getLayerIndex()).getNeuron(weightIndex.getFirstValue().getNeuronIndex()).getValue();
        return delta * out;
    }

    private double getDeltaWeight(Pair<NeuronIndexPair> weightIndex, double deltaWeight, NeuralLayer answer, NeuralLayer expectedAnswer) {
        return learningSpeed * getGrad(weightIndex, answer, expectedAnswer) + moment * deltaWeight;
    }

}
