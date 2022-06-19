package neuralNetwork;

public final class InputNeuronImpl extends NeuronImpl {

    /**
     * @param value getValue of neuron
     *              activation function is set to sigmoid by default
     */
    public InputNeuronImpl(double value) {
        super(value);
    }

    /**
     * @param value              getValue of neuron
     * @param activationFunction activation function of neuron
     */
    public InputNeuronImpl(double value, ActivationFunction activationFunction) {
        super(value, activationFunction);
    }


    @Override
    public double getValue() {
        return this.getRawValue();
    }

}
