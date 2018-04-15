package seamcarver;

// abstract class for pixel iterables, convenience for using pixel iterators
abstract class APixelIterable implements Iterable<APixel> {

	APixel pixel;

	APixelIterable(APixel pixel) {
		this.pixel = pixel;
	}

}