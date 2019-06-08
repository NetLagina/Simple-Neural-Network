package neuralNetwork;

import helper.Helper;

public class NeuronImpl implements Neuron {
	
	private final double value;
	
	public NeuronImpl(final double value) {
		this.value = value;
	}
	
	@Override
	public double getValue() {
		return Helper.activationFuncSigmoid(value);
	}
	
	@Override
	public double getRawValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Neuron[").append(getValue()).append("]");
		return builder.toString();
	}

}
