package helper;

import neuralNetwork.NeuralLayer;

public class TrainSet {
	
	private final NeuralLayer inputLayer;
	private final NeuralLayer outputLayer;
	
	public TrainSet(final NeuralLayer inputLayer, final NeuralLayer outputLayer) {
		this.inputLayer = inputLayer;
		this.outputLayer = outputLayer;
	}

	/**
	 * @return the inputLayer
	 */
	public NeuralLayer getInputLayer() {
		return inputLayer;
	}

	/**
	 * @return the outputLayer
	 */
	public NeuralLayer getOutputLayer() {
		return outputLayer;
	}

}
