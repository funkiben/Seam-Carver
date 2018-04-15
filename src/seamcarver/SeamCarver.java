package seamcarver;

import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// performs seam carving for images
public class SeamCarver {

	private final SentinelPixel pixel00;

	// the current width and height of the carved image
	private int width;
	private int height;

	private final Stack<ASeamInfo> removedSeams = new Stack<ASeamInfo>();

	// creates pixels data structure from the given image that are all properly
	// linked up
	public SeamCarver(Image image) {
		this.width = (int) image.getWidth();
		this.height = (int) image.getHeight();

		this.pixel00 = new SentinelPixel();

		ArrayList<ASentinelPixel> sentinelColumn = new ArrayList<ASentinelPixel>();
		ArrayList<ASentinelPixel> sentinelRow = new ArrayList<ASentinelPixel>();

		// represents the previous sentinel in the row or column
		ASentinelPixel prev = this.pixel00;

		// first create a row of row sentinels around pixel00
		for (int col = 0; col < this.width; col += 1) {
			prev = new SentinelRowPixel(prev, this.pixel00);
			sentinelRow.add(prev);
		}

		prev = this.pixel00;

		// then create a column of column sentinels around pixel00
		for (int row = 0; row < this.height; row += 1) {
			prev = new SentinelColumnPixel(prev, this.pixel00);
			sentinelColumn.add(prev);
		}

		// the row above the current row
		ArrayList<APixel> aboveRow = new ArrayList<APixel>();
		aboveRow.addAll(sentinelRow);

		PixelReader pixels = image.getPixelReader();

		for (int row = 0; row < this.height; row += 1) {

			// the pixel to the left of the current pixel in the current row
			APixel left = sentinelColumn.get(row);

			for (int col = 0; col < this.width; col += 1) {

				Color color = pixels.getColor(col, row);

				Pixel pixel = new Pixel(color, aboveRow.get(col), sentinelColumn.get(row),
						sentinelRow.get(col), left);

				// record the new pixel in the above row for next iteration
				aboveRow.set(col, pixel);

				// save the new pixel as the new left for next iteration
				left = pixel;

			}

		}
	}

	// biases the given pixels
	public void biasPixels(Set<Point2D> points) {

		int curX = 0;
		int curY = 0;

		Point2D point;

		for (APixel row : new PixelColumnIterable(this.pixel00)) {

			curX = 0;

			for (APixel pixel : new PixelRowIterable(row)) {

				point = new Point2D(curX, curY);

				if (points.contains(point)) {
					pixel.bias();
				}

				curX += 1;

			}

			curY += 1;

		}

	}

	// gets the current width of the carved image
	public int getWidth() {
		return this.width;
	}

	// gets the current height of the carved image
	public int getHeight() {
		return this.height;
	}

	// computes all the vertical seams
	// EFFECT: changes the seamInfo fields of all pixels
	private void calculateVerticalSeams() {

		for (APixel rowSentinel : new PixelRowIterable(this.pixel00)) {
			rowSentinel.calcVerticalSeamInfo();
		}

		for (APixel columnSentinel : new PixelColumnIterable(this.pixel00)) {
			columnSentinel.calcVerticalSeamInfo();
		}

		for (APixel row : new PixelColumnIterable(this.pixel00)) {

			for (APixel pixel : new PixelRowIterable(row)) {

				pixel.calcVerticalSeamInfo();

			}

		}

	}

	// computes all the horizontal seams
	// EFFECT: changes the seamInfo fields of all pixels
	private void calculateHorizontalSeams() {

		for (APixel rowSentinel : new PixelRowIterable(this.pixel00)) {
			rowSentinel.calcHorizontalSeamInfo();
		}

		for (APixel columnSentinel : new PixelColumnIterable(this.pixel00)) {
			columnSentinel.calcHorizontalSeamInfo();
		}

		for (APixel column : new PixelRowIterable(this.pixel00)) {

			for (APixel pixel : new PixelColumnIterable(column)) {

				pixel.calcHorizontalSeamInfo();

			}

		}

	}

	// creates the carved image from the current pixels
	public Image makeImage() {

		WritableImage carvedImage = new WritableImage(this.width, this.height);

		PixelWriter pixelWriter = carvedImage.getPixelWriter();

		int curX = 0;
		int curY = 0;

		for (APixel row : new PixelColumnIterable(this.pixel00)) {

			curX = 0;

			for (APixel pixel : new PixelRowIterable(row)) {

				pixel.setColor(pixelWriter, curX, curY);

				curX += 1;

			}

			curY += 1;

		}

		return carvedImage;
	}

	// reinserts the last seam removed
	// EFFECT: puts the pixels of the last removed seam back into the image, and
	// increases the width or height by one
	public void reinsertSeam(boolean estimateColor) {

		if (this.removedSeams.isEmpty()) {
			throw new IllegalStateException("no seams to reinsert!");
		}

		ASeamInfo seam = this.removedSeams.pop();
		seam.reinsert(estimateColor);

		this.width += seam.width();
		this.height += seam.height();
		
		this.testStructualInvariant();

	}

	// finds the cheapest vertical seam info in the last row of pixels and
	// removes the entire seam
	public void removeVerticalSeam() {

		if (this.width == 0) {
			throw new IllegalStateException("no more pixels to remove!");
		}

		this.calculateVerticalSeams();

		ASeamInfo seam = this.cheapestSeam(new PixelRowIterable(this.pixel00.top));

		seam.remove();

		this.removedSeams.push(seam);

		this.width -= 1;
	}

	// finds the cheapest vertical seam info in the last row of pixels and
	// removes the entire seam
	public void removeHorizontalSeam() {

		if (this.height == 0) {
			throw new IllegalStateException("no more pixels to remove!");
		}

		this.calculateHorizontalSeams();

		ASeamInfo seam = this.cheapestSeam(new PixelColumnIterable(this.pixel00.left));

		seam.remove();

		this.removedSeams.push(seam);

		this.height -= 1;
	}

	// finds the cheapest seam in the given pixel iterator
	private ASeamInfo cheapestSeam(Iterable<APixel> pixels) {

		ASeamInfo cheapest = this.pixel00.seamInfo;

		for (APixel pixel : pixels) {

			if (pixel.seamInfo.compare(cheapest) < 0) {
				cheapest = pixel.seamInfo;
			}

		}

		return cheapest;

	}

	// gets the number of removed seams
	public int countRemovedSeams() {
		return this.removedSeams.size();
	}

	public void testStructualInvariant() {

		for (APixel row : new PixelColumnIterable(this.pixel00)) {

			for (APixel pixel : new PixelRowIterable(row)) {

				if (pixel.right.left != pixel) {
					System.out.println("found a pixel where its right does not refer to it");
				}

				if (pixel.left.right != pixel) {
					System.out.println("found a pixel where its left does not refer to it");
				}
				
				if (pixel.top.bottom != pixel) {
					System.out.println("found a pixel where its top does not refer to it");
				}
				
				if (pixel.bottom.top != pixel) {
					System.out.println("found a pixel where its bottom does not refer to it");
				}

			}

		}

	}

}
