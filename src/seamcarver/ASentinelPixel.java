package seamcarver;

import java.util.ArrayList;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

// abstract pixel for a sentinel pixel
// sentinel pixels frame an image
abstract class ASentinelPixel extends APixel {

	// the energies of this pixel for horizontal and vertical seams
	ASentinelPixel(double verticalSeamEnergy, double horizontalSeamEnergy) {
		this.vSeamInfo = new VerticalSeamInfo(this, verticalSeamEnergy);
		this.hSeamInfo = new HorizontalSeamInfo(this, horizontalSeamEnergy);
	}

	// since this is a sentinel, always returns brightness of 0.0
	@Override
	double brightness() {
		return 0.0;
	}

	// sets the color to black since this is a sentinel pixel
	@Override
	void setColor(PixelWriter pixelWriter, int x, int y) {
		pixelWriter.setColor(x, y, Color.BLACK);
	}

	// changes this.seamInfo to be a seam info with energy of 0
	@Override
	void calcVerticalSeamInfo() {

	}

	// calculuates horizontal seam info, but since this is a sentinel does
	// nothing because the seam info should be constant
	@Override
	void calcHorizontalSeamInfo() {

	}

	// since this is a sentinel pixel, there can be no bias
	@Override
	void bias() {

	}

	// since this is a sentinel pixel, there can be no bias
	@Override
	void unbias() {

	}
	
	// since this is a sentinel pixel, there is no color of estimate
	@Override
	void estimateColor(APixel nextInSeam) {
		
	}
	
	// this is a sentinel pixel, which has no color
	@Override
	void addColor(ArrayList<Color> colors, APixel ignore) {
		
	}

	// sentinels are assumed to be black, so returns the color black
	@Override
	Color color() {
		return Color.BLACK;
	}

	// the methods below are so messy :(

	// EFFECT: removes the sentinel and fixes up all links caused by the
	// sentinel row shifting
	@Override
	void removeVertically(APixel nextInSeam, APixel start) {

		if (this != start.bottom) {

			if (this.isRight(start.bottom)) {

				for (APixel pixel : new PixelRowIterable(this)) {
					pixel.top = pixel.top.left;
					pixel.top.bottom = pixel;

					if (pixel == start.bottom) {
						break;
					}

				}
			} else {

				for (APixel pixel : new PixelRowIterable(start.bottom)) {
					pixel.top.bottom = pixel.left;
					pixel.left.top = pixel.top;

					if (pixel == this) {
						break;
					}

				}
			}
		}

		this.left.right = this.right;
		this.right.left = this.left;

	}

	// EFFECT: removes the sentinel and fixes up all links caused by the
	// sentinel row shifting
	@Override
	void removeHorizontally(APixel nextInSeam, APixel start) {

		if (this != start.right) {

			if (this.isBottom(start.right)) {

				for (APixel pixel : new PixelColumnIterable(this)) {
					pixel.left = pixel.left.top;
					pixel.left.right = pixel;

					if (pixel == start.right) {
						break;
					}

				}
			} else {

				for (APixel pixel : new PixelColumnIterable(start.right)) {
					pixel.left.right = pixel.top;
					pixel.top.left = pixel.left;

					if (pixel == this) {
						break;
					}

				}
			}
		}

		this.top.bottom = this.bottom;
		this.bottom.top = this.top;

	}

}