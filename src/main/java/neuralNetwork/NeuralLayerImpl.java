package neuralNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class NeuralLayerImpl implements NeuralLayer {
	
	private final List<Neuron> layer;
	
	public NeuralLayerImpl(final Neuron[] layer) {
		this.layer = new ArrayList<>(Arrays.asList(layer));		
	}
	
	public Neuron getNeuron(final int index) {
		return layer.get(index);
	}

	@Override
	public Iterator<Neuron> iterator() {
		return layer.iterator();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NeuralLayer[").append(layer).append("]");
		return builder.toString();
	}
	
	public int size() {
		return layer.size();
	}

}
