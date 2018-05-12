package seamcarver;

// the top left border pixel, kind of like a master pixel
// top: none
// bottom: LeftBorderPixel or BottomLeftBorderPixel
// left: none
// right: TopBorderPixel or TopRightBorderPixel
class TopLeftBorderPixel extends ABorderPixel {

	private ATopBorderPixel right;
	private ALeftBorderPixel bottom;

	TopLeftBorderPixel(ATopBorderPixel right, ALeftBorderPixel bottom) {
		right.linkLeftMutual(this);
		bottom.linkTopMutual(this);
	}

	@Override
	public void linkRight(IPixel right) {
		right.linkLeft(this);
	}

	@Override
	public void linkRight(TopBorderPixel right) {
		this.right = right;
	}

	@Override
	public void linkRight(TopRightBorderPixel right) {
		this.right = right;
	}

	@Override
	public void linkBottom(IPixel bottom) {
		bottom.linkTop(this);
	}

	@Override
	public void linkBottom(LeftBorderPixel bottom) {
		this.bottom = bottom;
	}

	@Override
	public void linkBottom(BottomLeftBorderPixel bottom) {
		this.bottom = bottom;
	}

	@Override
	public boolean isRight(IPixel pixel) {
		return pixel == this.right;
	}

	@Override
	public boolean isBottom(IPixel pixel) {
		return pixel == this.bottom;
	}

	// iterator for left border pixels below this
	Iterable<TopBorderPixel> columns() {
		return this.right;
	}

	// iterator for top border pixels to right of this
	Iterable<LeftBorderPixel> rows() {
		return this.bottom;
	}

}
