package seamcarver;

// abstract class for IPixel
abstract class APixel implements IPixel {

	// helper methods for throwing runtime exceptions

	private void linkErr(String side, IPixel other) {
		throw new UnsupportedOperationException("Can't link " + other.getClass().getSimpleName()
				+ " to the " + side + " of a " + this.getClass().getSimpleName());
	}

	void linkLeftErr(IPixel other) {
		this.linkErr("left", other);
	}

	void linkRightErr(IPixel other) {
		this.linkErr("right", other);
	}

	void linkTopErr(IPixel other) {
		this.linkErr("top", other);
	}

	void linkBottomErr(IPixel other) {
		this.linkErr("bottom", other);
	}

	// methods for mutually linking pixels

	@Override
	public void linkLeftMutual(IPixel left) {
		this.linkLeft(left);
		left.linkRight(this);
	}

	@Override
	public void linkRightMutual(IPixel right) {
		this.linkRight(right);
		right.linkLeft(this);
	}

	@Override
	public void linkTopMutual(IPixel top) {
		this.linkTop(top);
		top.linkBottom(this);
	}

	@Override
	public void linkBottomMutual(IPixel bottom) {
		this.linkBottom(bottom);
		bottom.linkTop(this);
	}

	// have all link methods throw errors by default

	@Override
	public void linkLeft(IPixel left) {
		this.linkLeftErr(left);
	}

	@Override
	public void linkRight(IPixel right) {
		this.linkRightErr(right);
	}

	@Override
	public void linkTop(IPixel top) {
		this.linkTopErr(top);
	}

	@Override
	public void linkBottom(IPixel bottom) {
		this.linkBottomErr(bottom);
	}

	@Override
	public void linkLeft(ColoredPixel left) {
		this.linkLeftErr(left);
	}

	@Override
	public void linkRight(ColoredPixel right) {
		this.linkRightErr(right);
	}

	@Override
	public void linkTop(ColoredPixel top) {
		this.linkTopErr(top);
	}

	@Override
	public void linkBottom(ColoredPixel bottom) {
		this.linkBottomErr(bottom);
	}

	@Override
	public void linkLeft(TopBorderPixel left) {
		this.linkLeftErr(left);
	}

	@Override
	public void linkRight(TopBorderPixel right) {
		this.linkRightErr(right);
	}

	@Override
	public void linkTop(TopBorderPixel top) {
		this.linkTopErr(top);
	}

	@Override
	public void linkBottom(TopBorderPixel bottom) {
		this.linkBottomErr(bottom);
	}

	@Override
	public void linkLeft(BottomBorderPixel left) {
		this.linkLeftErr(left);
	}

	@Override
	public void linkRight(BottomBorderPixel right) {
		this.linkRightErr(right);
	}

	@Override
	public void linkTop(BottomBorderPixel top) {
		this.linkTopErr(top);
	}

	@Override
	public void linkBottom(BottomBorderPixel bottom) {
		this.linkBottomErr(bottom);
	}

	@Override
	public void linkLeft(LeftBorderPixel left) {
		this.linkLeftErr(left);
	}

	@Override
	public void linkRight(LeftBorderPixel right) {
		this.linkRightErr(right);
	}

	@Override
	public void linkTop(LeftBorderPixel top) {
		this.linkTopErr(top);
	}

	@Override
	public void linkBottom(LeftBorderPixel bottom) {
		this.linkBottomErr(bottom);
	}

	@Override
	public void linkLeft(RightBorderPixel left) {
		this.linkLeftErr(left);
	}

	@Override
	public void linkRight(RightBorderPixel right) {
		this.linkRightErr(right);
	}

	@Override
	public void linkTop(RightBorderPixel top) {
		this.linkTopErr(top);
	}

	@Override
	public void linkBottom(RightBorderPixel bottom) {
		this.linkBottomErr(bottom);
	}

	@Override
	public void linkLeft(TopLeftBorderPixel left) {
		this.linkLeftErr(left);
	}

	@Override
	public void linkRight(TopLeftBorderPixel right) {
		this.linkRightErr(right);
	}

	@Override
	public void linkTop(TopLeftBorderPixel top) {
		this.linkTopErr(top);
	}

	@Override
	public void linkBottom(TopLeftBorderPixel bottom) {
		this.linkBottomErr(bottom);
	}

	@Override
	public void linkLeft(TopRightBorderPixel left) {
		this.linkLeftErr(left);
	}

	@Override
	public void linkRight(TopRightBorderPixel right) {
		this.linkRightErr(right);
	}

	@Override
	public void linkTop(TopRightBorderPixel top) {
		this.linkTopErr(top);
	}

	@Override
	public void linkBottom(TopRightBorderPixel bottom) {
		this.linkBottomErr(bottom);
	}

	@Override
	public void linkLeft(BottomLeftBorderPixel left) {
		this.linkLeftErr(left);
	}

	@Override
	public void linkRight(BottomLeftBorderPixel right) {
		this.linkRightErr(right);
	}

	@Override
	public void linkTop(BottomLeftBorderPixel top) {
		this.linkTopErr(top);
	}

	@Override
	public void linkBottom(BottomLeftBorderPixel bottom) {
		this.linkBottomErr(bottom);
	}

}
