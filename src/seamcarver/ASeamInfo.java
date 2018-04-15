package seamcarver;

import java.util.ArrayList;
import java.util.Arrays;

// represents a linked-list for seams
abstract class ASeamInfo {

	final APixel pixel;
	final double energy;
	// cameFrom may be null if no seam info came before this
	final ASeamInfo cameFrom;

	// creates a seam info on the edge of the image, has no parent
	ASeamInfo(APixel pixel, double energy) {

		this.pixel = pixel;
		this.energy = energy;
		this.cameFrom = null;

	}

	// creates a seam info given a pixel and three parents, uses the cheapest
	// parent to construct this
	ASeamInfo(APixel pixel, ASeamInfo parent1, ASeamInfo parent2, ASeamInfo parent3) {

		ArrayList<ASeamInfo> parents =
				new ArrayList<ASeamInfo>(Arrays.asList(parent1, parent2, parent3));
		parents.sort((a, b) -> a.compare(b));

		this.pixel = pixel;
		this.cameFrom = parents.get(0);
		this.energy = this.cameFrom.energy + pixel.energy();

	}

	// reinserts this seam into the image
	// if estimateColor is true, pixels will use their neighbors colors to
	// approximate their own color
	// EFFECT: changes all pixel neighbors in this seam seam to refer back to
	// the pixels
	void reinsert(boolean estimateColor) {
		this.pixel.reinsert(estimateColor);

		if (this.cameFrom != null) {
			this.cameFrom.reinsert(estimateColor);
		}
	}

	// removes this seam
	// EFFECT: removes all pixels in this seam
	void remove() {
		this.remove(this.pixel);
	}

	// removes the seam
	// ACCUMULATOR: start is the pixel at the start of this seam
	abstract void remove(APixel start);

	// gets the width of this seam, i.e. how much bigger will the image get
	// horizontally when this seam is inserted
	abstract int width();

	// gets the height of this seam, i.e. how much bigger will the image get
	// vertically when this seam is inserted
	abstract int height();

	// compares this seam info with another
	public int compare(ASeamInfo other) {
		return Double.compare(this.energy, other.energy);
	}

}
