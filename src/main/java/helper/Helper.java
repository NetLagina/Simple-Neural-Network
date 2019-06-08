package helper;

import neuralNetwork.NeuralLayer;

public class Helper {
	
	public static double activationFuncSigmoid(final double value) {
		return 1.0 / (1.0 + Math.pow(Math.E, -value));
	}
	
	public static double activationFuncHyperbolicTan(final double value) {
		return (Math.pow(Math.E, 2.0 * value) - 1.0) / (Math.pow(Math.E, 2.0 * value) + 1.0);
	}
	
	public static double getMSE(final NeuralLayer expectedAnswers, final NeuralLayer answers) {
		double sum = 0;
		for (int i = 0; i < answers.size(); i++) {
			sum += Math.pow(expectedAnswers.getNeuron(i).getRawValue() - answers.getNeuron(i).getValue(), 2.0);
		}
		return sum / answers.size();
	}
	
	public static double getRootMSE(final NeuralLayer expectedAnswer, final NeuralLayer answer) {
		double sum = 0;
		for (int i = 0; i < answer.size(); i++) {
			sum += Math.pow(expectedAnswer.getNeuron(i).getRawValue() - answer.getNeuron(i).getValue(), 2.0);
		}
		return Math.sqrt(sum / answer.size());
	}
	
	public static double getArctan(final NeuralLayer expectedAnswers, final NeuralLayer answers) {
		double sum = 0;
		for (int i = 0; i < answers.size(); i++) {
			sum += Math.pow(Math.atan(expectedAnswers.getNeuron(i).getRawValue() - answers.getNeuron(i).getValue()), 2.0);
		}
		return sum / answers.size();
	}

}
