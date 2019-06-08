package helper;

public class Pair<T extends Comparable<T>> implements Comparable<Pair<T>> {
	
	private final T first;
	private final T second;
	
	public Pair(final T first, final T second) {
		this.first = first;
		this.second = second;
	}

	public T getFirstValue() {
		return first;
	}

	public T getSecondValue() {
		return second;
	}

	@Override
	public int hashCode() {
		final int prime = 42;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Pair)) {
			return false;
		}
		Pair<?> other = (Pair<?>) obj;
		if (first == null) {
			if (other.first != null) {
				return false;
			}
		} else if (!first.equals(other.first)) {
			return false;
		}
		if (second == null) {
			if (other.second != null) {
				return false;
			}
		} else if (!second.equals(other.second)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Pair[").append(first.toString()).append(", ").append(second.toString()).append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(Pair<T> arg0) {
		if (this.first.compareTo(arg0.first) > 0) {
			return 1;
		} else if (this.first.compareTo(arg0.first) < 0) {
			return -1;
		} else if (this.second.compareTo(arg0.second) > 0) {
			return 1;
		} else if (this.second.compareTo(arg0.second) < 0) {
			return -1;
		} else {
			return 0;
		}
	}

}
