package seamcarver;

// represents a seam info on a sentinel pixel
class SentinelSeamInfo extends ASeamInfo {

	SentinelSeamInfo(SentinelPixel pixel) {
		super(pixel, Double.MAX_VALUE);
	}

	// does nothing, because not supposed to remove sentinel pixels
	@Override
	void remove(APixel start) {

	}

	// this is a sentinel seam info, no colors to estimate here
	@Override
	void estimateColor() {

	}

	// since this is a sentinel seam info, it should have no impact on the
	// images width
	@Override
	int width() {
		return 0;
	}

	// since this is a sentinel seam info, it should have no impact on the
	// images width
	@Override
	int height() {
		return 0;
	}

}
