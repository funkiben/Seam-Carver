package seamcarver;

import java.util.Iterator;

// an empty horizontal seam
class EmptyHorizontalSeam extends AHorizontalSeamInfo {

	private final LeftBorderPixel pixel;

	EmptyHorizontalSeam(LeftBorderPixel pixel) {
		this.pixel = pixel;
	}

	@Override
	public void reinsert() {
		this.pixel.reinsert();
	}

	@Override
	public void remove() {
		this.pixel.remove();
	}

	@Override
	public void removeDontFixLinks() {
		this.pixel.remove();
	}

	@Override
	public EmptyHorizontalSeam duplicate() {
		return new EmptyHorizontalSeam(this.pixel.duplicate());
	}

	@Override
	public EmptyHorizontalSeam duplicateDontFixLinks() {
		return new EmptyHorizontalSeam(this.pixel.duplicate());
	}

	@Override
	LeftBorderPixel pixel() {
		return this.pixel;
	}

	@Override
	public Iterator<ColoredPixel> iterator() {
		return ASeamInfo.NothingIterator.inst;
	}

	@Override
	HorizontalSeamInfo asHorizontalSeamInfo() {
		return null;
	}

}
