package seamcarver;

//represents a horizontal seam info
class HorizontalSeamInfo extends ASeamInfo {

	HorizontalSeamInfo(APixel pixel, double energy) {
		super(pixel, energy);
	}

	HorizontalSeamInfo(APixel pixel, ASeamInfo parent1, ASeamInfo parent2, ASeamInfo parent3) {
		super(pixel, parent1, parent2, parent3);
	}

	// removes the seam horizontally, remembers the first pixel on this seam
	// ACCUMULATOR: remembers the start of this seam
	@Override
	void remove(APixel start) {
		if (this.cameFrom != null) {
			this.pixel.removeHorizontally(this.cameFrom.pixel, start);
			this.cameFrom.remove(start);
		} else {
			this.pixel.removeHorizontally(null, start);
		}
	}

	// estimates the color for each pixel using its top and bottom pixels
	// EFFECT: changes the color of each pixel in this seam to an estimate
	void estimateColor() {
		if (this.cameFrom != null) {
			this.pixel.estimateColorHorizontally(this.cameFrom.pixel);
			this.cameFrom.estimateColor();
		} else {
			this.pixel.estimateColorHorizontally(null);
		}
	}

	// since this is a horizontal seam, it has a width of 0
	@Override
	int width() {
		return 0;
	}

	// since this is a horizontal seam, it has a height of 1
	@Override
	int height() {
		return 1;
	}

}
