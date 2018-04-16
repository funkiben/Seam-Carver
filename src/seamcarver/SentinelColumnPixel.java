package seamcarver;

// represents a sentinel pixel on a column
class SentinelColumnPixel extends ASentinelPixel {

	// EFFECT: changes top and bottom connections to link to this
	SentinelColumnPixel(ASentinelPixel top, ASentinelPixel bottom) {
		super(Double.MAX_VALUE, 0.0);

		this.left = this;
		this.right = this;

		this.top = top;
		this.bottom = bottom;

		this.top.bottom = this;
		this.bottom.top = this;

	}

	// checks if the given pixel lies to the right of this, but since this is a
	// column sentinel, gives false
	@Override
	boolean isRight(APixel pixel) {
		return false;
	}

	// reinserts this column sentinel back into the image
	// the columns of sentinels will shift down one, causing the pixels from
	// this to start to be linked incorrectly
	@Override
	void reinsert(APixel start, boolean estimateColor) {

		super.reinsert(start, estimateColor);

		if (this != start.right) {

			if (this.isBottom(start.right)) {

				for (APixel pixel : new PixelColumnIterable(this)) {

					if (pixel == start.right) {
						break;
					}

					pixel.left = pixel.left.bottom;
					pixel.left.right = pixel;

				}

			} else {

				for (APixel pixel : new PixelColumnIterable(start.right)) {

					if (pixel == this) {
						break;
					}

					pixel.left = pixel.left.top;
					pixel.left.right = pixel;

				}

			}

		}
	}

}