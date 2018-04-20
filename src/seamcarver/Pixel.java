package seamcarver;

import java.util.ArrayList;

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
	
	// estimates the color of this pixel by looking at the colors left and right
	// neighbors, but only if this is biased
	// EFFECT: changes this.color to the estimated color, and unbiases the pixel if its biased
	@Override
	void estimateColorVertically(APixel nextInSeam) {

		if (!this.biased) {
			return;
		}

		this.biased = false;

		ArrayList<Color> colors = new ArrayList<Color>();

		// this.top.addColor(colors, nextInSeam);
		//this.top.left.addColor(colors, nextInSeam);
		//this.top.right.addColor(colors, nextInSeam);
		// this.bottom.addColor(colors, nextInSeam);
		//this.bottom.left.addColor(colors, nextInSeam);
		//this.bottom.right.addColor(colors, nextInSeam);
		this.right.addColor(colors, nextInSeam);
		this.left.addColor(colors, nextInSeam);

		this.color = this.averageColor(colors);
	}

	// estimates the color of this pixel by looking at the top and bottom
	// neighbors, but only if this is biased
	// EFFECT: changes this.color to the estimated color
	@Override
	void estimateColorHorizontally(APixel nextInSeam) {

		if (!this.biased) {
			return;
		}

		this.biased = false;

		ArrayList<Color> colors = new ArrayList<Color>();

		this.top.addColor(colors, nextInSeam);
		//this.top.left.addColor(colors, nextInSeam);
		//this.top.right.addColor(colors, nextInSeam);
		this.bottom.addColor(colors, nextInSeam);
		//this.bottom.left.addColor(colors, nextInSeam);
		//this.bottom.right.addColor(colors, nextInSeam);
		// this.right.addColor(colors, nextInSeam);
		// this.left.addColor(colors, nextInSeam);

		this.color = this.averageColor(colors);
	}
	// gets the average color of the array list of colors
	Color averageColor(ArrayList<Color> colors) {
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

	// adds this pixels color to the array list if it isn't equal to ignore
	// EFFECT: possibly adds this.color to colors
	@Override
	void addColor(ArrayList<Color> colors, APixel ignore) {
		if (this != ignore) {
			colors.add(this.color);
		}
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
