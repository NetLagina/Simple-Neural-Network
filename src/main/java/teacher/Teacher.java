package teacher;

import java.util.List;

import org.apache.log4j.Logger;

import helper.Helper;
import helper.Pair;
import helper.TrainSet;
import neuralNetwork.NeuralLayer;
import neuralNetwork.NeuralNetwork;

public class Teacher {

	private static final Logger LOGGER = Logger.getLogger(Teacher.class);
	
	private int maxEpoch = 50_000;
	private double minErrorRate = 0.005;
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

	public void setLearningSpeed(double learningSpeed) {
		this.learningSpeed = learningSpeed;
	}

	public void setMoment(double moment) {
		this.moment = moment;
	}

	public void teachBackpropagation() {
		LOGGER.info("BACKPROPAGATION TRAIN STARTS");
		for (int epoch = 0; epoch < maxEpoch; epoch++) {
			
			LOGGER.info("Epoch: " + (epoch + 1) + "/" + maxEpoch);
			
			double errorRate = 1;
			double errorGlobalRate = 0;
			for (var iteration = 0; iteration < trainSets.size(); iteration++) {
				network.setInputLayer(trainSets.get(iteration).getInputLayer());
				network.calc();
				var answer = network.getOutputLayer();
				
				errorRate = Helper.getMSE(trainSets.get(iteration).getOutputLayer(), answer);

				LOGGER.debug("Error Rate: " + errorRate);
				
				errorGlobalRate += errorRate;
				
				for (var layerIndex = network.size() - 1; 0 < layerIndex; layerIndex--) {
					currentLayerDeltas = new double[network.getLayer(layerIndex).size()];
					for (var neuronIndex = 0; neuronIndex < network.getLayer(layerIndex).size(); neuronIndex++) {
						for (var i = 0; i < network.getLayer(layerIndex - 1).size(); i++) {
							var weightIndex = new Pair<Pair<Integer>>(new Pair<Integer>(layerIndex - 1, i), new Pair<Integer>(layerIndex, neuronIndex));
							var oldWeightPair = network.getWeights().get(weightIndex);
							var deltaWeight = getDeltaWeight(weightIndex, oldWeightPair.getSecondValue(), answer, trainSets.get(iteration).getOutputLayer());
							network.getWeights().put(weightIndex, new Pair<Double>(oldWeightPair.getFirstValue() + deltaWeight, deltaWeight));
						}
					}
					nextLayerDeltas = currentLayerDeltas;
				}
			}
			errorGlobalRate = errorGlobalRate / trainSets.size();
			
			LOGGER.info("ERROR RATE: " + errorGlobalRate);
			
			if (errorGlobalRate < minErrorRate) {
				return;
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
			delta *= weights.get(new Pair<Pair<Integer>>(new Pair<Integer>(layerIndex, index), new Pair<Integer>(layerIndex + 1, j))).getFirstValue() * nextLayerDeltas[j];
		}
		currentLayerDeltas[index] = delta;
		return delta;
	}

	private double getGrad(Pair<Pair<Integer>> weightIndex, NeuralLayer answer, NeuralLayer expectedAnswer) {
		double delta = 0;
		if (weightIndex.getSecondValue().getFirstValue() != network.size() - 1) {
			delta = getDelta(weightIndex.getFirstValue().getFirstValue(), weightIndex.getFirstValue().getSecondValue());
		} else {
			delta = getDelta(weightIndex.getSecondValue().getSecondValue(), answer, expectedAnswer);
		}
		var out = network.getLayer(weightIndex.getFirstValue().getFirstValue()).getNeuron(weightIndex.getFirstValue().getSecondValue()).getRawValue();
		return delta * out;
	}

	private double getDeltaWeight(Pair<Pair<Integer>> weightIndex, double deltaWeight, NeuralLayer answer, NeuralLayer expectedAnswer) {
		return learningSpeed * getGrad(weightIndex, answer, expectedAnswer) + moment * deltaWeight;
	}

}
