package seamcarver;
import java.util.Iterator;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

// abstract class for a pixel
abstract class APixel {

	// links to this pixels neighbors, may be null if this pixel has not been
	// fully initialized yet, or if this is an edge pixel
	APixel top;
	APixel right;
	APixel bottom;
	APixel left;

	// null until calculated
	ASeamInfo seamInfo = null;

	// gets the brightness of this pixel
	abstract double brightness();

	// sets the color of this pixel at the x and y on the image
	abstract void setColor(PixelWriter pixelWriter, int x, int y);

	// biases this pixels energy
	abstract void bias();

	// gets the color of this pixel
	abstract Color color();

	// reinserts this pixel back into the image
	// EFFECT: changes all neighbors to refer to this
	void reinsert(boolean estimateColor) {
		this.top.bottom = this;
		this.right.left = this;
		this.left.right = this;
		this.bottom.top = this;
	}

	// A B C
	// D E F
	// G H I
	//
	// Then the energies of E are defined by (assume 𝑏𝑟(⋅) is the brightness
	// of a pixel, defined above):
	// 𝐻𝑜𝑟𝑖𝑧𝐸𝑛𝑒𝑟𝑔𝑦(𝐸)=(𝑏𝑟(𝐴)+2𝑏𝑟(𝐷)+𝑏𝑟(𝐺))−(𝑏𝑟(𝐶)+2𝑏𝑟(𝐹)+𝑏𝑟(𝐼))
	// 𝑉𝑒𝑟𝑡𝐸𝑛𝑒𝑟𝑔𝑦(𝐸)=(𝑏𝑟(𝐴)+2𝑏𝑟(𝐵)+𝑏𝑟(𝐶))−(𝑏𝑟(𝐺)+2𝑏𝑟(𝐻)+𝑏𝑟(𝐼))
	// 𝐸𝑛𝑒𝑟𝑔𝑦(𝐸)=sqrt(𝐻𝑜𝑟𝑖𝑧𝐸𝑛𝑒𝑟𝑔𝑦(𝐸)2+𝑉𝑒𝑟𝑡𝐸𝑛𝑒𝑟𝑔𝑦(𝐸)2)

	// gets the horizontal energy of this pixel
	private double verticalEnergy() {
		return this.top.left.brightness() + 2 * this.top.brightness() + this.top.right.brightness()
				- (this.bottom.left.brightness() + 2 * this.bottom.brightness()
						+ this.bottom.right.brightness());
	}

	// gets the vertical energy of this pixel
	private double horiztonalEnergy() {
		return this.top.left.brightness() + 2 * this.left.brightness()
				+ this.bottom.left.brightness() - (this.top.right.brightness()
						+ 2 * this.right.brightness() + this.bottom.right.brightness());
	}

	// gets the energy of this pixel
	double energy() {
		return Math.sqrt(Math.pow(this.horiztonalEnergy(), 2) + Math.pow(this.verticalEnergy(), 2));
	}

	// calculates the vertical seam info for this pixel
	abstract void calcVerticalSeamInfo();

	// calculates the horizontal seam info for this pixel
	abstract void calcHorizontalSeamInfo();

	// removes this pixel vertically given the next top pixel in the seam
	// EFFECT: relinks all neighbors appropriately
	void removeVertically(APixel nextTop, APixel start) {

		this.left.right = this.right;
		this.right.left = this.left;

		if (nextTop == this.left.top) {
			this.left.top = this.top;
			this.top.bottom = this.left;
		} else if (nextTop == this.right.top) {
			this.right.top = this.top;
			this.top.bottom = this.right;
		}

	}

	// removes this pixel horizontally given the next left pixel in the seam
	// EFFECT: relinks all neighbors appropriately
	void removeHorizontally(APixel nextLeft, APixel start) {

		this.top.bottom = this.bottom;
		this.bottom.top = this.top;

		if (nextLeft == this.left.top) {
			this.top.left = this.left;
			this.left.right = this.top;
		} else if (nextLeft == this.left.bottom) {
			this.bottom.left = this.left;
			this.left.right = this.bottom;
		}

	}

	// checks if the given pixel lies to the right of this
	boolean isRight(APixel pixel) {
		if (this.right == pixel) {
			return true;
		}

		return this.right.isRight(pixel);
	}

	// checks if the given pixel is below this
	boolean isBottom(APixel pixel) {
		if (this.bottom == pixel) {
			return true;
		}

		return this.bottom.isBottom(pixel);
	}

	// iterator for the row this pixel on
	Iterator<APixel> rowIterator() {
		return new PixelRowIterator();
	}

	// iterator for the column this pixel on
	Iterator<APixel> columnIterator() {
		return new PixelColumnIterator();
	}

	// abstract pixel iterator
	private abstract class APixelIterator<T> implements Iterator<T> {

		APixel current;

		// checks if the current pixel has a right neighbor
		@Override
		public boolean hasNext() {
			return this.current != APixel.this;
		}

	}

	// iterator for a row from right to left, not including this
	private class PixelRowIterator extends APixelIterator<APixel> {

		PixelRowIterator() {
			this.current = APixel.this.right;
		}

		// returns the current and moves to the right neighbor
		// EFFECT: current becomes currents right neighbor
		@Override
		public APixel next() {
			APixel temp = this.current;
			this.current = this.current.right;
			return temp;
		}

	}

	// iterator for a column from top to bottom, not including this
	private class PixelColumnIterator extends APixelIterator<APixel> {

		PixelColumnIterator() {
			this.current = APixel.this.bottom;
		}

		// returns the current and moves to the bottom neighbor
		// EFFECT: current becomes currents bottom neighbor
		@Override
		public APixel next() {
			APixel temp = this.current;
			this.current = this.current.bottom;
			return temp;
		}

	}

}
