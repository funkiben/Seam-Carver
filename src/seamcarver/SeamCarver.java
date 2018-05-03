package seamcarver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// a class for seam carving
public class SeamCarver {

	// the pixel at the top right of the image
	private final TopLeftBorderPixel topLeft;

	private int width, height;

	private final Deque<ISeamOperation> operations = new ArrayDeque<ISeamOperation>();

	public SeamCarver(Image image) {

		this.width = (int) image.getWidth();
		this.height = (int) image.getHeight();

		PixelReader pixelReader = image.getPixelReader();

		TopRightBorderPixel topRight = new TopRightBorderPixel();
		BottomLeftBorderPixel bottomLeft = new BottomLeftBorderPixel();

		this.topLeft = new TopLeftBorderPixel(topRight, bottomLeft);

		// represents the pixels above the current row being iterated through
		ArrayList<IPixel> topBorderPixels = new ArrayList<IPixel>();

		// the last top bprder pixel created
		ATopBorderPixel rightTopBorderPixel = topRight;

		for (int x = 0; x < image.getWidth(); x++) {
			rightTopBorderPixel = new TopBorderPixel(this.topLeft, rightTopBorderPixel);
			topBorderPixels.add(0, rightTopBorderPixel);
		}

		// represents the column of left border pixels
		ArrayList<IPixel> leftBorderPixels = new ArrayList<IPixel>();

		// the last left border pixel created
		ALeftBorderPixel bottomLeftBorderPixel = bottomLeft;

		for (int y = 0; y < image.getHeight(); y++) {
			bottomLeftBorderPixel = new LeftBorderPixel(this.topLeft, bottomLeftBorderPixel);
			leftBorderPixels.add(0, bottomLeftBorderPixel);
		}

		// represents the pixels above the current row being iterated through
		ArrayList<IPixel> aboveRow = new ArrayList<IPixel>();
		aboveRow.addAll(topBorderPixels);

		// we can reuse the same right and bottom border pixel objects, they
		// don't need to be unique
		RightBorderPixel right = new RightBorderPixel();
		BottomBorderPixel bottom = new BottomBorderPixel();

		for (int y = 0; y < image.getHeight(); y++) {

			IPixel left = leftBorderPixels.get(y);

			for (int x = 0; x < image.getWidth(); x++) {

				Color color = pixelReader.getColor(x, y);

				ColoredPixel pixel = new ColoredPixel(color, left, right, aboveRow.get(x), bottom);

				aboveRow.set(x, pixel);
				left = pixel;

			}

		}
		
		this.testStructuralIntegrity();

	}

	// undos the last operation done on the image
	public void undoLastOperation() {
		if (!this.operations.isEmpty()) {
			this.operations.pop().undo();
		}
	}

	// function object for acting upon pixels given the pixels coordinates
	private interface PixelCoordOperation {

		void op(ColoredPixel pixel, int x, int y);

	}

	// nice little abstraction for iterating through a pixel while keeping track
	// of their coordinates
	private void coordIterate(PixelCoordOperation op) {

		int x = 0, y = 0;

		for (LeftBorderPixel row : this.topLeft.rows()) {

			x = 0;

			for (ColoredPixel pixel : row.rowIterable()) {

				op.op(pixel, x, y);

				x += 1;

			}

			y += 1;

		}

	}

	// biases all pixels with the given coordinates
	public void biasPixels(Collection<Point2D> points) {

		this.coordIterate((pixel, x, y) -> {

			if (points.contains(new Point2D(x, y))) {
				pixel.bias();
			}

		});

	}

	// unbiases all pixels with the given coordinates
	public void unbiasPixels(Collection<Point2D> points) {

		this.coordIterate((pixel, x, y) -> {

			if (points.contains(new Point2D(x, y))) {
				pixel.unbias();
			}

		});

	}

	// unbiases all pixels
	public void unbiasAllPixels() {

		for (LeftBorderPixel row : this.topLeft.rows()) {

			for (ColoredPixel pixel : row.rowIterable()) {

				pixel.unbias();

			}

		}
	}

	// makes all pixels avoided
	public void avoidPixels(Collection<Point2D> points) {

		this.coordIterate((pixel, x, y) -> {

			if (points.contains(new Point2D(x, y))) {
				pixel.avoid();
			}

		});

	}

	// unavoids all pixels with the given coordinates
	public void unavoidPixel(Collection<Point2D> points) {

		this.coordIterate((pixel, x, y) -> {

			if (points.contains(new Point2D(x, y))) {
				pixel.unavoid();
			}

		});

	}

	// unavoids all pixels
	public void unavoidAllPixels() {

		for (LeftBorderPixel row : this.topLeft.rows()) {

			for (ColoredPixel pixel : row.rowIterable()) {

				pixel.unbias();

			}

		}
	}

	// makes the carved image
	public Image makeImage() {

		WritableImage image = new WritableImage(this.width, this.height);
		PixelWriter pixelWriter = image.getPixelWriter();

		this.coordIterate((pixel, x, y) -> {

			pixel.draw(pixelWriter, x, y);

		});

		return image;

	}

	// calculates the cheapest vertical seam
	private ISeam calculateCheapestVerticalSeam() {
		LeftBorderPixel lastRow = null;

		for (LeftBorderPixel row : this.topLeft.rows()) {

			for (ColoredPixel pixel : row.rowIterable()) {
				pixel.calculateVerticalSeamInfo();
			}

			lastRow = row;
		}

		return lastRow.cheapestVerticalSeam();
	}

	// calculates the cheapest horizontal seam
	private ISeam calculateCheapestHorizontalSeam() {
		TopBorderPixel lastColumn = null;

		for (TopBorderPixel column : this.topLeft.columns()) {

			for (ColoredPixel pixel : column.columnIterable()) {
				pixel.calculateHorizontalSeamInfo();
			}

			lastColumn = column;
		}

		return lastColumn.cheapestHorizontalSeam();
	}

	// removes vertical seams from the image
	public void removeVerticalSeams(int amount) {

		int clampedAmount = Math.max(1, amount);

		Deque<ISeam> seams = new ArrayDeque<ISeam>();

		for (int i = 0; i < clampedAmount; i++) {

			ISeam seam = this.calculateCheapestVerticalSeam();
			seam.remove();

			seams.push(seam);

		}

		this.width -= clampedAmount;

		this.operations.push(() -> {

			while (!seams.isEmpty()) {
				seams.pop().reinsert();
			}

			this.width += clampedAmount;

		});

	}

	// removes horizontal seams from the image
	public void removeHorizontalSeams(int amount) {

		int clampedAmount = Math.max(1, amount);

		Deque<ISeam> seams = new ArrayDeque<ISeam>();

		for (int i = 0; i < clampedAmount; i++) {

			ISeam seam = this.calculateCheapestHorizontalSeam();
			seam.remove();

			seams.push(seam);

		}

		this.height -= clampedAmount;

		this.operations.push(() -> {

			while (!seams.isEmpty()) {
				seams.pop().reinsert();
			}

			this.height += clampedAmount;

		});

	}

	// inserts vertical seams, returns all the seams inserted
	public void insertVerticalSeams(int amount) {

		Deque<ISeam> removedSeams = new ArrayDeque<ISeam>();

		for (int i = 0; i < amount; i++) {
			ISeam seam = this.calculateCheapestVerticalSeam();

			seam.remove();

			removedSeams.push(seam);

		}

		for (ISeam seam : removedSeams) {
			seam.reinsert();
		}

		Deque<ISeam> newSeams = new ArrayDeque<ISeam>();

		while (!removedSeams.isEmpty()) {
			ISeam seam = removedSeams.pop();
			ISeam dupe = seam.duplicateDontFixLinks();
			newSeams.push(dupe);
		}

		this.fixBrokenVerticalLinks();
		
		this.testStructuralIntegrity();

		this.width += amount;

		this.operations.push(() -> {
			
			while (!newSeams.isEmpty()) {
				newSeams.pop().removeDontFixLinks();
			}
			
			this.fixBrokenVerticalLinks();
			
			this.width -= amount;

		});

	}

	// inserts horizontal seams, returns all the seams inserted
	public void insertHorizontalSeams(int amount) {

		Deque<ISeam> removedSeams = new ArrayDeque<ISeam>();

		for (int i = 0; i < amount; i++) {
			ISeam seam = this.calculateCheapestHorizontalSeam();

			seam.remove();

			removedSeams.push(seam);

		}

		for (ISeam seam : removedSeams) {
			seam.reinsert();
		}

		Deque<ISeam> newSeams = new ArrayDeque<ISeam>();

		while (!removedSeams.isEmpty()) {
			ISeam seam = removedSeams.pop();
			ISeam dupe = seam.duplicateDontFixLinks();
			newSeams.push(dupe);
		}

		this.fixBrokenHorizontalLinks();

		this.height += amount;

		this.operations.push(() -> {
			
			while (!newSeams.isEmpty()) {
				newSeams.pop().removeDontFixLinks();
			}
			
			this.fixBrokenHorizontalLinks();
			
			this.height -= amount;

		});

	}

	// fixed vertical broken links, used after inserting vertical seams because
	// the seams become broken
	private void fixBrokenVerticalLinks() {

		Deque<IPixel> prevRow = new ArrayDeque<IPixel>();

		for (TopBorderPixel pixel : this.topLeft.columns()) {
			prevRow.addFirst(pixel);
		}

		for (LeftBorderPixel row : this.topLeft.rows()) {

			for (ColoredPixel pixel : row.rowIterable()) {

				pixel.linkTopMutual(prevRow.removeLast());

				prevRow.addFirst(pixel);

			}

		}

	}

	// fixes horizontal broken links, used after inserting horizontal seams
	// because seams become broken
	private void fixBrokenHorizontalLinks() {

		Deque<IPixel> prevColumn = new ArrayDeque<IPixel>();

		for (LeftBorderPixel pixel : this.topLeft.rows()) {
			prevColumn.addFirst(pixel);
		}

		for (TopBorderPixel column : this.topLeft.columns()) {

			for (ColoredPixel pixel : column.columnIterable()) {

				pixel.linkLeftMutual(prevColumn.removeLast());

				prevColumn.addFirst(pixel);

			}

		}

	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	private void testStructuralIntegrity() {

		for (TopBorderPixel column : this.topLeft.columns()) {

			for (ColoredPixel pixel : column.columnIterable()) {
				pixel.testStructuralIntegrity();

			}

		}

	}

}
