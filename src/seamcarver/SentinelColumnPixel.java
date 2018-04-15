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

}