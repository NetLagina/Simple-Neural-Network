package neuralNetwork;

public interface NeuralLayer extends Iterable<Neuron> {
	
	Neuron getNeuron(int index);
	
	int size();

}
