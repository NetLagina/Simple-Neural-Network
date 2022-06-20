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

    public boolean teachBackpropagation() {
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
            if (errorRate < minErrorRate || Double.isNaN(errorRate) || Double.isInfinite(errorRate)) {
                return false;
            }

            for (var layerIndex = network.size() - 1; 0 < layerIndex; layerIndex--) {
                currentLayerDeltas = new double[network.getLayer(layerIndex).size()];
                for (var neuronIndex = 0; neuronIndex < network.getLayer(layerIndex).size(); neuronIndex++) {
                    for (var previousLayerNeuronIndex = 0; previousLayerNeuronIndex < network.getLayer(layerIndex - 1).size(); previousLayerNeuronIndex++) {
                        var weightIndex = new Pair<>(new NeuronIndexPair(layerIndex - 1, previousLayerNeuronIndex), new NeuronIndexPair(layerIndex, neuronIndex));
                        var oldWeightPair = network.getWeights().get(weightIndex);
                        var deltaWeight = getDeltaWeight(weightIndex, oldWeightPair.getSecondValue(), answer, trainSet.outputLayer());
                        network.getWeights().put(weightIndex, new Pair<>(oldWeightPair.getFirstValue() + deltaWeight, deltaWeight));
                    }
                    if (network.isBiasEnabled()) {
                        var biasWeightIndex = new Pair<>(new NeuronIndexPair(layerIndex - 1, -1), new NeuronIndexPair(layerIndex, neuronIndex));
                        var biasOldWeightPair = network.getBiasWeights().get(biasWeightIndex);
                        var biasDeltaWeight = getDeltaWeight(biasWeightIndex, biasOldWeightPair.getSecondValue(), answer, trainSet.outputLayer());
                        network.getBiasWeights().put(biasWeightIndex, new Pair<>(biasOldWeightPair.getFirstValue() + biasDeltaWeight, biasDeltaWeight));
                    }
                }
                nextLayerDeltas = currentLayerDeltas;
            }
        }
        return true;
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
        for (var nextLayerNeuronIndex = 0; nextLayerNeuronIndex < nextLayerDeltas.length; nextLayerNeuronIndex++) {
            var weight = weights.get(new Pair<>(new NeuronIndexPair(layerIndex, index), new NeuronIndexPair(layerIndex + 1, nextLayerNeuronIndex))).getFirstValue();
            delta *= weight * nextLayerDeltas[nextLayerNeuronIndex];
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
        var out = 1.0;
        if (weightIndex.getFirstValue().getNeuronIndex() != -1) { // not bias neuron
            out = network.getLayer(weightIndex.getFirstValue().getLayerIndex()).getNeuron(weightIndex.getFirstValue().getNeuronIndex()).getValue();
        }
        return delta * out;
    }

    private double getDeltaWeight(Pair<NeuronIndexPair> weightIndex, double deltaWeight, NeuralLayer answer, NeuralLayer expectedAnswer) {
        return learningSpeed * getGrad(weightIndex, answer, expectedAnswer) + moment * deltaWeight;
    }

}
