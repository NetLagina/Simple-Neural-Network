package helper;

public class NeuronIndexPair implements Comparable<NeuronIndexPair> {

    private final int layerIndex;
    private final int neuronIndex;

    public NeuronIndexPair(int layerIndex, int neuronIndex) {
        this.layerIndex = layerIndex;
        this.neuronIndex = neuronIndex;
    }

    public Integer getLayerIndex() {
        return layerIndex;
    }

    public Integer getNeuronIndex() {
        return neuronIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NeuronIndexPair that = (NeuronIndexPair) o;

        if (layerIndex != that.layerIndex) return false;
        return neuronIndex == that.neuronIndex;
    }

    @Override
    public int hashCode() {
        int result = layerIndex;
        result = 31 * result + neuronIndex;
        return result;
    }

    @Override
    public String toString() {
        return "NeuronIndexPair[" + layerIndex + ", " + neuronIndex + "]";
    }

    public int compareTo(NeuronIndexPair o) {
        if (getLayerIndex() > o.getLayerIndex()) {
            return 1;
        } else if (getLayerIndex() < o.getLayerIndex()) {
            return -1;
        } else {
            return getNeuronIndex().compareTo(o.getNeuronIndex());
        }
    }
}
