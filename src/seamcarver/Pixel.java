package seamcarver;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

// represents the pixel of an image with a color, linked directly to its 4 neighbors
class Pixel extends APixel {

	private static final double BIAS = -1000000000.0D;

	private Color color;
	private boolean biased = false;

	// constructs the pixel with the given neighbors, and links up this to
	// neighbors appropriately
	// EFFECT: changes the appropriate links on each of the neighbors to
	// reference this
	Pixel(Color color, APixel top, APixel right, APixel bottom, APixel left) {

		this.color = color;

		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;

		this.top.bottom = this;
		this.right.left = this;
		this.bottom.top = this;
		this.left.right = this;

	}

	// gets the average of the red, green, and blue values of this pixel divided
	// by 255 to get a double between 0 and 1 that represents the brightness of
	// this pixel
	@Override
	double brightness() {
		return ((this.color.getRed() + this.color.getGreen() + this.color.getBlue()) / 3.0) / 255.0;
	}

	// sets the color to this.color
	@Override
	void setColor(PixelWriter pixelWriter, int x, int y) {
		pixelWriter.setColor(x, y, this.biased ? this.color.invert() : this.color);
	}

	// uses neighbors average colors to predict this pixels color if
	// estimateColor is true
	// EFFECT: changes this.biased to false
	@Override
	void reinsert(APixel start, boolean estimateColor) {
		super.reinsert(start, estimateColor);
		
		this.biased = false;
		
		if (estimateColor) {
			this.color = Pixel.averageColor(this.top.color(), this.top.right.color(),
					this.top.left.color(), this.right.color(), this.left.color(),
					this.bottom.color(), this.bottom.left.color(), this.bottom.right.color());
		}
	}

	// gets the average color of the given colors
	private static Color averageColor(Color... colors) {
		int r = 0;
		int g = 0;
		int b = 0;

		for (Color color : colors) {

			r += Math.pow(color.getRed() * 255, 2);
			g += Math.pow(color.getGreen() * 255, 2);
			b += Math.pow(color.getBlue() * 255, 2);

		}

		r /= colors.length;
		g /= colors.length;
		b /= colors.length;

		return Color.rgb((int) Math.sqrt(r), (int) Math.sqrt(g), (int) Math.sqrt(b));
	}

	// calculates vertical seam info using top neighbors seam infos
	// EFFECT: changes this.seamInfo to new calculates seam info
	@Override
	void calcVerticalSeamInfo() {
		this.vSeamInfo = new VerticalSeamInfo(this, this.top.vSeamInfo, this.left.top.vSeamInfo,
				this.right.top.vSeamInfo);
	}

	// calculates horizontal seam info using left neighbors seam infos
	// EFFECT: changes this.seamInfo to new calculates seam info
	@Override
	void calcHorizontalSeamInfo() {
		this.hSeamInfo = new HorizontalSeamInfo(this, this.left.hSeamInfo, this.left.top.hSeamInfo,
				this.left.bottom.hSeamInfo);
	}

	// adds this.bias to the energy
	@Override
	double energy() {
		return super.energy() + (this.biased ? Pixel.BIAS : 0);
	}

	// EFFECT: changes this.bias to be true
	@Override
	void bias() {
		this.biased = true;
	}

	// EFFECT: changes this.bias to be false
	@Override
	void unbias() {
		this.biased = false;
	}

	// gets the color of this pixel
	@Override
	Color color() {
		return this.color;
	}

}
