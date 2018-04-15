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
	// sentinel,
	// gives false
	@Override
	boolean isBottom(APixel pixel) {
		return false;
	}

}