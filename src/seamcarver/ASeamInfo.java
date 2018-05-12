package seamcarver;

import java.util.ArrayList;
import java.util.Iterator;

// abstract class for ISeam
abstract class ASeamInfo implements ISeam, Comparable<ASeamInfo> {

	// returns the seam info with the lowest energy
	static <T extends ASeamInfo> T lowestEnergy(ArrayList<T> seamInfos) {
		seamInfos.sort((a, b) -> a.compareTo(b));

		return seamInfos.get(0);
	}

	// may be mutated when ever seams are calculated
	double energy = 0.0;

	// ugh a getter
	abstract IPixel pixel();

	// compares this seam to the other seam using this.energy and other.energy
	@Override
	public int compareTo(ASeamInfo other) {
		return Double.compare(this.energy, other.energy);
	}

	// iterator that has no elements in it, returned by empty seams
	static class NothingIterator implements Iterator<ColoredPixel> {

		static final NothingIterator inst = new NothingIterator();

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public ColoredPixel next() {
			throw new IllegalStateException("no more elements in the iterator");
		}

	}

}
