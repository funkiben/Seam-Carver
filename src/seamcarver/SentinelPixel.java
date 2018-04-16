package seamcarver;

// represents a sentinel pixel that can be on both a row and a column
// the "master" sentinel
class SentinelPixel extends ASentinelPixel {

	SentinelPixel() {
		super(Double.MAX_VALUE, Double.MAX_VALUE);

		this.vSeamInfo = new SentinelSeamInfo(this);
		this.hSeamInfo = new SentinelSeamInfo(this);

		this.top = this;
		this.right = this;
		this.bottom = this;
		this.left = this;

	}

	// checks if the given pixel lies to the right of this, but since this is
	// the
	// origin sentinel, gives false
	@Override
	boolean isRight(APixel pixel) {
		return false;
	}

	// checks if the given pixel is below this, but since this is the
	// origin sentinel, gives false
	@Override
	boolean isBottom(APixel pixel) {
		return false;
	}

	// does nothing since this is a sentinel, energy is always infinity
	@Override
	void calcVerticalSeamInfo() {

	}

	// does nothing since this is a sentinel, energy is always infinity
	@Override
	void calcHorizontalSeamInfo() {

	}

}