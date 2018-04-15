package seamcarver;
import java.util.Iterator;

// iterable for a pixel row iterator
class PixelRowIterable extends APixelIterable {

	public PixelRowIterable(APixel pixel) {
		super(pixel);
	}

	// gives a new pixel row iterator
	@Override
	public Iterator<APixel> iterator() {
		return this.pixel.rowIterator();
	}

}