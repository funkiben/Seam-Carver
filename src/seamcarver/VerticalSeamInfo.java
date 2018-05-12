package seamcarver;

import java.util.ArrayList;
import java.util.Iterator;

// the body of a vertical seam, has at least one pixel
class VerticalSeamInfo extends AVerticalSeamInfo {

	private final ColoredPixel pixel;
	// null until calculuated
	private AVerticalSeamInfo cameFrom = null;

	VerticalSeamInfo(ColoredPixel pixel) {
		this.pixel = pixel;
	}

	private VerticalSeamInfo(ColoredPixel pixel, double energy, AVerticalSeamInfo cameFrom) {
		this(pixel);

		this.energy = energy;
		this.cameFrom = cameFrom;

	}

	// calculates this.cameFrom and this.energy from the given parents
	// EFFECT: this.cameFrom, this.energy
	void calculate(ArrayList<AVerticalSeamInfo> parents) {
		this.cameFrom = ASeamInfo.lowestEnergy(parents);
		this.energy = this.pixel.energy() + this.cameFrom.energy;
	}

	@Override
	public void reinsert() {
		this.pixel.reinsert();
		this.cameFrom.reinsert();
	}

	@Override
	public void remove() {
		this.pixel.removeFromVerticalSeam(this.cameFrom.pixel());
		this.cameFrom.remove();
	}

	@Override
	public void removeDontFixLinks() {
		this.pixel.removeFromVerticalSeam();
		this.cameFrom.removeDontFixLinks();
	}

	@Override
	public VerticalSeamInfo duplicate() {
		AVerticalSeamInfo cameFromDupe = this.cameFrom.duplicate();

		ColoredPixel newPixel =
				this.pixel.duplicateToLeft(cameFromDupe.pixel(), this.cameFrom.pixel());

		return new VerticalSeamInfo(newPixel, this.energy, cameFromDupe);
	}

	@Override
	public VerticalSeamInfo duplicateDontFixLinks() {
		AVerticalSeamInfo cameFromDupe = this.cameFrom.duplicateDontFixLinks();

		ColoredPixel newPixel = this.pixel.duplicateToLeft();

		return new VerticalSeamInfo(newPixel, this.energy, cameFromDupe);
	}

	@Override
	ColoredPixel pixel() {
		return this.pixel;
	}

	@Override
	public Iterator<ColoredPixel> iterator() {
		return new VerticalSeamIterator();
	}

	@Override
	VerticalSeamInfo asVerticalSeamInfo() {
		return this;
	}

	// iterator over all pixels in this seam info and all seam infos before this
	class VerticalSeamIterator implements Iterator<ColoredPixel> {

		// will equal null when there's nothing left of the seam
		private VerticalSeamInfo current = VerticalSeamInfo.this;

		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public ColoredPixel next() {
			ColoredPixel temp = this.current.pixel;
			this.current = this.current.cameFrom.asVerticalSeamInfo();
			return temp;
		}

	}
}
