package seamcarver;

// represents a sentinel pixel on a row
class SentinelRowPixel extends ASentinelPixel {

	// EFFECT: changes left and right connections to link to this
	SentinelRowPixel(ASentinelPixel left, ASentinelPixel right) {
		super(0.0, Double.MAX_VALUE);

		this.top = this;
		this.bottom = this;

		this.left = left;
		this.right = right;

		this.left.right = this;
		this.right.left = this;

	}

	// checks if the given pixel is below this, but since this is a row
	// sentinel, gives false
	@Override
	boolean isBottom(APixel pixel) {
		return false;
	}

	// reinserts this row sentinel back into the image
	// the row of sentinels will shift down one, causing the pixels from this to
	// start to be linked incorrectly
	@Override
	void reinsert(APixel start, boolean estimateColor) {

		super.reinsert(start, estimateColor);

		if (this != start.bottom) {
			
			if (this.isRight(start.bottom)) {

				for (APixel pixel : new PixelRowIterable(this)) {

					if (pixel == start.bottom) {
						break;
					}
					
					pixel.top = pixel.top.right;
					pixel.top.bottom = pixel;

				}

			} else {

				for (APixel pixel : new PixelRowIterable(start.bottom)) {

					if (pixel == this) {
						break;
					}
					
					pixel.top = pixel.top.left;
					pixel.top.bottom = pixel;


				}

			}

		}

	}

}