package seamcarver;

import java.util.ArrayList;
import java.util.Iterator;

class HorizontalSeamInfo extends AHorizontalSeamInfo {

	private final ColoredPixel pixel;
	// this is null until calculuated
	private AHorizontalSeamInfo cameFrom = null;

	HorizontalSeamInfo(ColoredPixel pixel) {
		this.pixel = pixel;
	}
	
	private HorizontalSeamInfo(ColoredPixel pixel, double energy, AHorizontalSeamInfo cameFrom) {
		this(pixel);

		this.energy = energy;
		this.cameFrom = cameFrom;

	}

	void calculate(ArrayList<AHorizontalSeamInfo> parents) {
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
		this.pixel.removeFromHorizontalSeam(this.cameFrom.pixel());
		this.cameFrom.remove();
	}

	@Override
	public void removeDontFixLinks() {
		this.pixel.removeFromHorizontalSeam();
		this.cameFrom.removeDontFixLinks();
	}

	@Override
	public HorizontalSeamInfo duplicate() {
		AHorizontalSeamInfo cameFromDupe = this.cameFrom.duplicate();

		ColoredPixel newPixel =
				this.pixel.duplicateToTop(cameFromDupe.pixel(), this.cameFrom.pixel());

		return new HorizontalSeamInfo(newPixel, this.energy, cameFromDupe);
	}

	@Override
	public HorizontalSeamInfo duplicateDontFixLinks() {
		AHorizontalSeamInfo cameFromDupe = this.cameFrom.duplicateDontFixLinks();

		ColoredPixel newPixel = this.pixel.duplicateToTop();

		return new HorizontalSeamInfo(newPixel, this.energy, cameFromDupe);
	}

	@Override
	ColoredPixel pixel() {
		return this.pixel;
	}

	@Override
	public Iterator<ColoredPixel> iterator() {
		return new HorizontalSeamIterator();
	}

	@Override
	HorizontalSeamInfo asHorizontalSeamInfo() {
		return this;
	}

	// iterator over all pixels in this seam info and all seam infos before this
	class HorizontalSeamIterator implements Iterator<ColoredPixel> {

		// will equal null when there's nothing left of the seam
		private HorizontalSeamInfo current = HorizontalSeamInfo.this;

		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public ColoredPixel next() {
			ColoredPixel temp = this.current.pixel;
			this.current = this.current.cameFrom.asHorizontalSeamInfo();
			return temp;
		}

	}

}
