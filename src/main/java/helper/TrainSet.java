package helper;

import neuralNetwork.NeuralLayer;

public record TrainSet(NeuralLayer inputLayer, NeuralLayer outputLayer) {

    /**
     * @return the inputLayer
     */
    @Override
    public NeuralLayer inputLayer() {
        return inputLayer;
    }

    /**
     * @return the outputLayer
     */
    @Override
    public NeuralLayer outputLayer() {
        return outputLayer;
    }

}
