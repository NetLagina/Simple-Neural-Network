package neuralNetwork;

import helper.Helper;

public class NeuronImpl implements Neuron {

    private final double value;
    private final ActivationFunction activationFunction;

    /**
     * @param value getValue of neuron
     *              activation function is set to sigmoid by default
     */
    public NeuronImpl(double value) {
        this.value = value;
        this.activationFunction = ActivationFunction.SIGMOID;
    }

    /**
     * @param value              getValue of neuron
     * @param activationFunction activation function of neuron
     */
    public NeuronImpl(double value, ActivationFunction activationFunction) {
        this.value = value;
        this.activationFunction = activationFunction;
    }

    @Override
    public double getValue() {
        if (activationFunction == ActivationFunction.SIGMOID) {
            return Helper.activationFuncSigmoid(value);
        } else {
            return Helper.activationFuncHyperbolicTan(value);
        }
    }

    @Override
    public double getRawValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Neuron[" + value + "(" + getValue() + ")]";
    }

}
