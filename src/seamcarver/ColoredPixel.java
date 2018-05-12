package seamcarver;

import java.util.ArrayList;
import java.util.Iterator;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

// a pixel with a color, stores the content of an image
// always has four neighbors
// top: ColoredPixel or TopBorderPixel
// bottom: ColoredPixel or BottomBorderPixel
// left: ColoredPixel or LeftBorderPixel
// right: ColoredPixel or RightBorderPixel
class ColoredPixel extends APixel {

	private static final double LARGE_ENERGY_AMOUNT = 1000000000;

	private Color color;
	private IPixel right, left, top, bottom;

	// either -1, 0, or 1: -1 is biased for, 1 is biased against, 0 is no bias
	private int bias = 0;

	private boolean highlight = false;

	private final VerticalSeamInfo vSeamInfo;
	private final HorizontalSeamInfo hSeamInfo;

	ColoredPixel(Color color, IPixel left, IPixel right, IPixel top, IPixel bottom) {
		this.color = color;

		this.vSeamInfo = new VerticalSeamInfo(this);
		this.hSeamInfo = new HorizontalSeamInfo(this);

		this.linkLeftMutual(left);
		this.linkRightMutual(right);
		this.linkTopMutual(top);
		this.linkBottomMutual(bottom);

	}

	// baises this pixel
	// pixels can't be biased and avoided simultaneously
	// EFFECT: this.bias
	void biasFor() {
		this.bias = -1;
	}

	// unbiased this pixel
	// EFFECT: this.bias
	void unbias() {
		this.bias = 0;
	}

	// makes it so this pixel is avoided by adding a large amount to this pixels
	// energy
	// pixels can't be biased and avoided simultaneously
	// EFFECT: this.bias
	void biasAgainst() {
		this.bias = 1;
	}

	// highlights this pixel by inverting its color
	// EFFECT: this.highlight
	void highlight() {
		this.highlight = true;
	}

	// removes the highlighting from this pixel
	// EFFECT: this.highlight
	void unhighlight() {
		this.highlight = false;
	}

	// sets the color of the given x and y to be this.color
	// EFFECT: pixelWriter
	void draw(PixelWriter pixelWriter, int x, int y) {

		if (this.highlight) {
			pixelWriter.setColor(x, y, this.color.invert());
		} else if (this.bias == -1) {
			pixelWriter.setColor(x, y, this.addGreenTintToColor());
		} else if (this.bias == 1) {
			pixelWriter.setColor(x, y, this.addRedTintToColor());
		} else {
			pixelWriter.setColor(x, y, this.color);
		}

	}

	// gives a color with a green tint added to this.color
	private Color addGreenTintToColor() {
		int r = (int) Math.min(255, (this.color.getRed() * 255));
		int g = (int) Math.min(255, (this.color.getGreen() * 255 + 128));
		int b = (int) Math.min(255, (this.color.getBlue() * 255));
		return Color.rgb(r, g, b);
	}

	// gives a color with a red tint added to this.color
	private Color addRedTintToColor() {
		int r = (int) Math.min(255, (this.color.getRed() * 255 + 128));
		int g = (int) Math.min(255, (this.color.getGreen() * 255));
		int b = (int) Math.min(255, (this.color.getBlue() * 255));
		return Color.rgb(r, g, b);
	}

	// gets the average of the red, green, and blue values of this pixel divided
	// by 255 to get a double between 0 and 1 that represents the brightness of
	// this pixel
	@Override
	public double brightness() {
		return ((this.color.getRed() + this.color.getGreen() + this.color.getBlue()) / 3.0);
	}

	// A B C
	// D E F
	// G H I

	// Then the energies of E are defined by (assume 𝑏𝑟(⋅) is the brightness
	// of a pixel, defined above):
	// 𝐻𝑜𝑟𝑖𝑧𝐸𝑛𝑒𝑟𝑔𝑦(𝐸)=(𝑏𝑟(𝐴)+2𝑏𝑟(𝐷)+𝑏𝑟(𝐺))−(𝑏𝑟(𝐶)+2𝑏𝑟(𝐹)+𝑏𝑟(𝐼))
	// 𝑉𝑒𝑟𝑡𝐸𝑛𝑒𝑟𝑔𝑦(𝐸)=(𝑏𝑟(𝐴)+2𝑏𝑟(𝐵)+𝑏𝑟(𝐶))−(𝑏𝑟(𝐺)+2𝑏𝑟(𝐻)+𝑏𝑟(𝐼))
	// 𝐸𝑛𝑒𝑟𝑔𝑦(𝐸)=sqrt(𝐻𝑜𝑟𝑖𝑧𝐸𝑛𝑒𝑟𝑔𝑦(𝐸)2+𝑉𝑒𝑟𝑡𝐸𝑛𝑒𝑟𝑔𝑦(𝐸)2)

	// just to avoid field of field access, I split the above equations into
	// smaller equations

	// HorizBrSum(B)=br(A)+2br(B)+br(C)
	// HorizBrSum(H)=br(G)+2br(H)+br(I)

	// VertBrSum(D)=br(A)+2br(D)+br(G)
	// VertBrSum(F)=br(C)+2br(F)+br(I)

	// so now we have:
	// 𝐻𝑜𝑟𝑖𝑧𝐸𝑛𝑒𝑟𝑔𝑦(𝐸)=VertBrSum(D)−VertBrSum(F)
	// 𝑉𝑒𝑟𝑡𝐸𝑛𝑒𝑟𝑔𝑦(𝐸)=HorizBrSum(B)-HorizBrSum(H)

	// gets this brightness times 2 plus left and right brightnesses
	@Override
	public double verticalBrightnessSum() {
		return this.top.brightness() + 2 * this.brightness() + this.bottom.brightness();
	}

	// gets this brightness times 2 plus left and right brightnesses
	@Override
	public double horizontalBrightnessSum() {
		return this.left.brightness() + 2 * this.brightness() + this.right.brightness();
	}

	// gets the energy of this pixel using adjacent pixel brightnesses
	// adds or subtracts a really large number to the energy if this pixel is
	// biased
	double energy() {

		double vEnergy = this.top.horizontalBrightnessSum() - this.bottom.horizontalBrightnessSum();
		double hEnergy = this.left.verticalBrightnessSum() - this.right.verticalBrightnessSum();

		return Math.sqrt(Math.pow(vEnergy, 2) + Math.pow(hEnergy, 2))
				+ this.bias * LARGE_ENERGY_AMOUNT;

	}

	// removes this pixel assuming it is part of a vertical seam
	// fixes up top and bottom links
	// only works on continuous seams
	// EFFECT: this.left, this.right, this.top, this.bottom
	void removeFromVerticalSeam(IPixel nextInSeam) {

		this.left.linkRightMutual(this.right);

		if (this.top.isLeft(nextInSeam)) {
			this.left.linkTopMutual(this.top);
		} else if (this.top.isRight(nextInSeam)) {
			this.right.linkTopMutual(this.top);
		} else if (nextInSeam != this.top) {
			throw new IllegalStateException(
					"nextInSeam is not a top neighbor, seam is not continuous");
		}

	}

	// removes this pixel assuming it is part of a horizontal seam
	// fixed up right and left links
	// only works on continuous seams
	// EFFECT: this.top, this.bottom, this.left, this.right
	void removeFromHorizontalSeam(IPixel nextInSeam) {

		this.top.linkBottomMutual(this.bottom);

		if (this.left.isTop(nextInSeam)) {
			this.top.linkLeftMutual(this.left);
		} else if (this.left.isBottom(nextInSeam)) {
			this.bottom.linkLeftMutual(this.left);
		} else if (nextInSeam != this.left) {
			throw new IllegalStateException(
					"nextInSeam is not a left neighbor, seam is not continuous");
		}

	}

	// same as other removeFromVerticalSeam but works on seams that aren't
	// continuous
	// DOES NOT FIX UP VERTICAL LINKS
	// EFFECT: this.left, this.right
	void removeFromVerticalSeam() {
		this.left.linkRightMutual(this.right);
	}

	// same as other removeFromHorizontalSeam but works on seams that aren't
	// continuous
	// DOES NOT FIX UP HORIZONTAL LINKS
	// EFFECT: this.top, this.bottom
	void removeFromHorizontalSeam() {
		this.top.linkBottomMutual(this.bottom);
	}

	// reinserts this pixel back into the image, assuming it was just removed
	// with no other operations in between
	// works on seams that aren't continuous
	// EFFECT: this.left, this.right, this.top, this.bottom
	void reinsert() {
		this.left.linkRight(this);
		this.right.linkLeft(this);
		this.top.linkBottom(this);
		this.bottom.linkTop(this);
	}

	// creates a new colored pixel to the left of this, with its color the
	// average of this and this.lefts color
	// fixes up top and bottom links
	// only works on seams that are continuous
	// EFFECT: this.right, this.left, this.top, this.bottom
	ColoredPixel duplicateToLeft(IPixel aboveNewPixel, IPixel abovePixelInSeam) {
		ColoredPixel pixel;

		if (this.top == aboveNewPixel) {
			pixel = new ColoredPixel(this.color, this.left, this, this.top, this.bottom);
			this.linkTopMutual(abovePixelInSeam);
		} else if (this.right.isTop(aboveNewPixel)) {
			pixel = new ColoredPixel(this.color, this.left, this, this.top, this.bottom);
			this.linkTopMutual(aboveNewPixel);
			this.right.linkTopMutual(abovePixelInSeam);
		} else if (this.left.isTop(aboveNewPixel)) {
			pixel = new ColoredPixel(this.color, this.left, this, abovePixelInSeam, this.bottom);
		} else {
			throw new IllegalStateException(
					"unable to locate aboveNewPixel, seam is not continuous");
		}

		pixel.estimateColorHorizontally();

		return pixel;
	}

	// creates a new colored pixel above this, with its color the average of
	// this and this.tops color
	// fixes up left and right links
	// only works on seams that are continuous
	// EFFECT: this.right, this.left, this.top, this.bottom
	ColoredPixel duplicateToTop(IPixel leftNewPixel, IPixel leftPixelInSeam) {

		ColoredPixel pixel;

		if (this.left == leftNewPixel) {
			pixel = new ColoredPixel(this.color, this.left, this.right, this.top, this);
			this.linkLeftMutual(leftPixelInSeam);
		} else if (this.bottom.isLeft(leftNewPixel)) {
			pixel = new ColoredPixel(this.color, this.left, this.right, this.top, this);
			this.linkLeftMutual(leftNewPixel);
			this.bottom.linkLeftMutual(leftPixelInSeam);
		} else if (this.top.isLeft(leftNewPixel)) {
			pixel = new ColoredPixel(this.color, leftPixelInSeam, this.right, this.top, this);
		} else {
			throw new IllegalStateException(
					"unable to locate leftNewPixel, seam is not continuous");
		}

		pixel.estimateColorVertically();

		return pixel;
	}

	// same as other duplicateToLeft but works on seams that aren't continuous
	// DOES NOT FIX UP VERTICAL LINKS
	// EFFECT: this.left
	ColoredPixel duplicateToLeft() {

		ColoredPixel pixel = new ColoredPixel(this.color, this.left, this, this.top, this.bottom);

		pixel.estimateColorHorizontally();

		return pixel;

	}

	// same as other duplicateToTop but works on seams that aren't continuous
	// DOES NOT FIX UP HORIZONTAL LINKS
	// EFFECT: this.top
	ColoredPixel duplicateToTop() {

		ColoredPixel pixel = new ColoredPixel(this.color, this.left, this.right, this.top, this);

		pixel.estimateColorVertically();

		return pixel;

	}

	// calculates this.vSeamInfo using above neighbors vertical seam infos
	// EFFECT: this.vSeamInfo
	void calculateVerticalSeamInfo() {
		ArrayList<AVerticalSeamInfo> seamInfos = this.top.collectVerticalSeamInfos();
		this.vSeamInfo.calculate(seamInfos);
	}

	// calculates this.hSeamInfo using left neighbors horizontal seam infos
	// EFFECT: this.hSeamInfo
	void calculateHorizontalSeamInfo() {
		ArrayList<AHorizontalSeamInfo> seamInfos = this.left.collectHorizontalSeamInfos();
		this.hSeamInfo.calculate(seamInfos);
	}

	// gets the vertical seam infos of this, right, and left
	@Override
	public ArrayList<AVerticalSeamInfo> collectVerticalSeamInfos() {
		ArrayList<AVerticalSeamInfo> seamInfos = new ArrayList<AVerticalSeamInfo>();

		this.addVerticalSeamInfo(seamInfos);
		this.right.addVerticalSeamInfo(seamInfos);
		this.left.addVerticalSeamInfo(seamInfos);

		return seamInfos;
	}

	// gets the horizontal seam infos of this, top, and bottom
	@Override
	public ArrayList<AHorizontalSeamInfo> collectHorizontalSeamInfos() {
		ArrayList<AHorizontalSeamInfo> seamInfos = new ArrayList<AHorizontalSeamInfo>();

		this.addHorizontalSeamInfo(seamInfos);
		this.bottom.addHorizontalSeamInfo(seamInfos);
		this.top.addHorizontalSeamInfo(seamInfos);

		return seamInfos;
	}

	// adds the vertical seam info for this pixel to the given array list
	// EFFECT: seamInfos
	@Override
	public void addVerticalSeamInfo(ArrayList<AVerticalSeamInfo> seamInfos) {
		seamInfos.add(this.vSeamInfo);
	}

	// adds the horizontal seam info for this pixel to the given array list
	// EFFECT: seamInfos
	@Override
	public void addHorizontalSeamInfo(ArrayList<AHorizontalSeamInfo> seamInfos) {
		seamInfos.add(this.hSeamInfo);
	}

	// adds the color for this pixel to the given array list
	// EFFECT: colors
	@Override
	public void addColor(ArrayList<Color> colors) {
		colors.add(this.color);
	}

	// estimates the color of this pixel by looking at the colors of left and
	// right neighbors
	// EFFECT: this.color
	private void estimateColorVertically() {

		ArrayList<Color> colors = new ArrayList<Color>();

		this.top.addColor(colors);
		this.bottom.addColor(colors);

		this.color = ColoredPixel.averageColor(colors);
	}

	// estimates the color of this pixel by looking at the top and bottom
	// neighbor colors
	// EFFECT: this.color
	private void estimateColorHorizontally() {

		ArrayList<Color> colors = new ArrayList<Color>();

		this.right.addColor(colors);
		this.left.addColor(colors);

		this.color = ColoredPixel.averageColor(colors);
	}

	// gets the average color of the given array list of colors
	private static Color averageColor(ArrayList<Color> colors) {
		int r = 0;
		int g = 0;
		int b = 0;

		for (Color color : colors) {
			r += color.getRed() * 255;
			g += color.getGreen() * 255;
			b += color.getBlue() * 255;
		}

		r /= colors.size();
		g /= colors.size();
		b /= colors.size();

		return Color.rgb(r, g, b);
	}

	// gets the cheapest vertical seam in the row, assumes this to be first in
	// row
	VerticalSeamInfo cheapestVerticalSeamInRow() {

		VerticalSeamInfo cheapest = this.vSeamInfo;

		for (ColoredPixel pixel : this.rowIterable()) {
			if (cheapest.compareTo(pixel.vSeamInfo) > 0) {
				cheapest = pixel.vSeamInfo;
			}
		}

		return cheapest;

	}

	// gets the cheapest horizontal seam in the column, assumes this to be first
	// in column
	HorizontalSeamInfo cheapestHorizontalSeamInColumn() {

		HorizontalSeamInfo cheapest = this.hSeamInfo;

		for (ColoredPixel pixel : this.columnIterable()) {
			if (cheapest.compareTo(pixel.hSeamInfo) > 0) {
				cheapest = pixel.hSeamInfo;
			}
		}

		return cheapest;

	}

	// the above methods check if the given pixel is this.left, this.right,
	// this.top, or this.bottom
	@Override
	public boolean isLeft(IPixel pixel) {
		return pixel == this.left;
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
	public void linkLeft(IPixel left) {
		left.linkRight(this);
	}

	@Override
	public void linkRight(IPixel right) {
		right.linkLeft(this);
	}

	@Override
	public void linkTop(IPixel top) {
		top.linkBottom(this);
	}

	@Override
	public void linkBottom(IPixel bottom) {
		bottom.linkTop(this);
	}

	@Override
	public void linkLeft(ColoredPixel left) {
		this.left = left;
	}

	@Override
	public void linkRight(ColoredPixel right) {
		this.right = right;
	}

	@Override
	public void linkTop(ColoredPixel top) {
		this.top = top;
	}

	@Override
	public void linkBottom(ColoredPixel bottom) {
		this.bottom = bottom;
	}

	@Override
	public void linkTop(TopBorderPixel top) {
		this.top = top;
	}

	@Override
	public void linkBottom(BottomBorderPixel bottom) {
		this.bottom = bottom;
	}

	@Override
	public void linkLeft(LeftBorderPixel left) {
		this.left = left;
	}

	@Override
	public void linkRight(RightBorderPixel right) {
		this.right = right;
	}

	@Override
	public ColoredPixel asColoredPixel() {
		return this;
	}

	// give iterables instead of iterators for easy use with for loops
	// not sure why i would ever need to return the iterator class instead?
	Iterable<ColoredPixel> rowIterable() {
		return () -> new ColoredPixelRowIterator();
	}

	Iterable<ColoredPixel> columnIterable() {
		return () -> new ColoredPixelColumnIterator();
	}

	// an iterator for rows, left to right
	private class ColoredPixelRowIterator implements Iterator<ColoredPixel> {

		private ColoredPixel current;

		ColoredPixelRowIterator() {
			this.current = ColoredPixel.this;
		}

		@Override
		public boolean hasNext() {
			return this.current != null;
		}

		@Override
		public ColoredPixel next() {
			ColoredPixel temp = this.current;
			this.current = this.current.right.asColoredPixel();
			return temp;
		}

	}

	// an iterator for columns, top to bottom
	private class ColoredPixelColumnIterator implements Iterator<ColoredPixel> {

		private ColoredPixel current;

		ColoredPixelColumnIterator() {
			this.current = ColoredPixel.this;
		}

		@Override
		public boolean hasNext() {
			return this.current != null;
		}

		@Override
		public ColoredPixel next() {
			ColoredPixel temp = this.current;
			this.current = this.current.bottom.asColoredPixel();
			return temp;
		}

	}

	// test method for testing the structural invariant
	// checks that all pixels neighbors link back to themselves
	void testStructuralIntegrity() {
		boolean leftGood = true, rightGood = true, topGood = true, bottomGood = true;

		if (this.left instanceof ColoredPixel) {
			leftGood = ((ColoredPixel) this.left).right == this;
		}

		if (this.right instanceof ColoredPixel) {
			rightGood = ((ColoredPixel) this.right).left == this;
		}

		if (this.top instanceof ColoredPixel) {
			topGood = ((ColoredPixel) this.top).bottom == this;
		}

		if (this.bottom instanceof ColoredPixel) {
			bottomGood = ((ColoredPixel) this.bottom).top == this;
		}

		if (!leftGood)
			System.out.println("left link broken");
		if (!rightGood)
			System.out.println("right link broken");
		if (!topGood)
			System.out.println("top link broken");
		if (!bottomGood)
			System.out.println("bottom link broken");
	}

}
