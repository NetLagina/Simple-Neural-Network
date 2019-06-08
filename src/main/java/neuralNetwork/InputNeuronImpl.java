package neuralNetwork;

public class InputNeuronImpl implements Neuron {

private final double value;
	
	public InputNeuronImpl(final double value) {
		this.value = value;
	}
	
	@Override
	public double getValue() {
		return value;
	}
	
	@Override
	public double getRawValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Neuron[").append(value).append("]");
		return builder.toString();
	}

}
