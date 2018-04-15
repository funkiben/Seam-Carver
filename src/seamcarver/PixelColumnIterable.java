package seamcarver;
import java.util.Iterator;

// iterable for a pixel column iterator
class PixelColumnIterable extends APixelIterable {

	public PixelColumnIterable(APixel pixel) {
		super(pixel);
	}

	// gives a new pixel column iterator
	@Override
	public Iterator<APixel> iterator() {
		return this.pixel.columnIterator();
	}

}