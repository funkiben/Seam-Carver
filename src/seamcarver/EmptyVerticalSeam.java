package seamcarver;

import java.util.Iterator;

// an empty vertical seam
class EmptyVerticalSeam extends AVerticalSeamInfo {

	private final TopBorderPixel pixel;

	EmptyVerticalSeam(TopBorderPixel pixel) {
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
	public EmptyVerticalSeam duplicate() {
		return new EmptyVerticalSeam(this.pixel.duplicate());
	}
	
	@Override
	public EmptyVerticalSeam duplicateDontFixLinks() {
		return new EmptyVerticalSeam(this.pixel.duplicate());
	}

	TopBorderPixel pixel() {
		return pixel;
	}

	@Override
	public Iterator<ColoredPixel> iterator() {
		return ASeamInfo.NothingIterator.inst;
	}

	@Override
	VerticalSeamInfo asVerticalSeamInfo() {
		return null;
	}
	
}
