package seamcarver;

import java.util.ArrayList;
import java.util.Iterator;

// the left border pixel
class LeftBorderPixel extends ALeftBorderPixel {

	private final EmptyHorizontalSeam emptySeam;
	private ColoredPixel right;
	private ABorderPixel top;
	private ALeftBorderPixel bottom;

	LeftBorderPixel(ABorderPixel top, ALeftBorderPixel bottom, ColoredPixel right) {

		this(top, bottom);

		right.linkLeft(this);

	}

	// initialized right with null, USE WITH CAUTION, null links are B A D
	LeftBorderPixel(ABorderPixel top, ALeftBorderPixel bottom) {

		top.linkBottomMutual(this);
		bottom.linkTopMutual(this);

		this.right = null;

		this.emptySeam = new EmptyHorizontalSeam(this);

	}

	// just returns array list containing this empty seam info
	@Override
	public ArrayList<AHorizontalSeamInfo> collectHorizontalSeamInfos() {
		ArrayList<AHorizontalSeamInfo> seamInfos = new ArrayList<AHorizontalSeamInfo>();

		seamInfos.add(this.emptySeam);

		return seamInfos;
	}

	@Override
	public void linkRight(IPixel right) {
		right.linkLeft(this);
	}

	@Override
	public void linkRight(ColoredPixel right) {
		this.right = right;
	}

	@Override
	public void linkTop(IPixel top) {
		top.linkBottom(this);
	}

	@Override
	public void linkTop(LeftBorderPixel top) {
		this.top = top;
	}

	@Override
	public void linkTop(TopLeftBorderPixel top) {
		this.top = top;
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
	public boolean isTop(IPixel pixel) {
		return pixel == this.top;
	}

	@Override
	public boolean isBottom(IPixel pixel) {
		return pixel == this.bottom;
	}

	@Override
	LeftBorderPixel asLeftBorderPixel() {
		return this;
	}

	void remove() {
		this.bottom.linkTopMutual(this.top);
	}
	
	LeftBorderPixel duplicate() {
		return new LeftBorderPixel(this.top, this, this.right);
	}

	void reinsert() {
		this.bottom.linkTop(this);
		this.top.linkBottom(this);
		this.right.linkLeft(this);
	}
	
	VerticalSeamInfo cheapestVerticalSeam() {
		return this.right.cheapestVerticalSeamInRow();
	}

	// iterates through the row of colored pixels to the right of this
	Iterable<ColoredPixel> rowIterable() {
		return this.right.rowIterable();
	}

	// iterates through the column of left border pixels
	@Override
	public Iterator<LeftBorderPixel> iterator() {
		return new LeftBorderPixelIterator();
	}

	// an iterator for left border pixels, top to bottom
	private class LeftBorderPixelIterator implements Iterator<LeftBorderPixel> {

		private LeftBorderPixel current;

		LeftBorderPixelIterator() {
			this.current = LeftBorderPixel.this;
		}

		@Override
		public boolean hasNext() {
			return this.current != null;
		}

		@Override
		public LeftBorderPixel next() {
			LeftBorderPixel temp = this.current;
			this.current = this.current.bottom.asLeftBorderPixel();
			return temp;
		}

	}

}
