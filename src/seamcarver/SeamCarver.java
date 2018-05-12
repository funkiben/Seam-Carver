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

	}

	// undos the last operation done on the image
	public void undoLastOperation() {
		if (!this.operations.isEmpty()) {
			this.operations.pop().undo();
		}
	}

	// function object for acting upon pixels given the pixels coordinates
	private interface PixelCoordOperation {

		// does something to a pixel given the pixel and its coords
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
	public void biasForPixels(Collection<Point2D> points) {

		this.coordIterate((pixel, x, y) -> {

			if (points.contains(new Point2D(x, y))) {
				pixel.biasFor();
			}

		});

	}

	// makes all pixels with the given coordinates avoided
	public void biasAgainstPixels(Collection<Point2D> points) {

		this.coordIterate((pixel, x, y) -> {

			if (points.contains(new Point2D(x, y))) {
				pixel.biasAgainst();
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

		// add an undo operation to undo the removal
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

		// add an undo operation to undo the removal
		this.operations.push(() -> {

			while (!seams.isEmpty()) {
				seams.pop().reinsert();
			}

			this.height += clampedAmount;

		});

	}

	// inserts vertical seams
	// removes n seams to figure out which seams to duplicate, and reinserts
	// them after
	public void insertVerticalSeams(int amount) {

		Deque<ISeam> removedSeams = new ArrayDeque<ISeam>();

		// first remove the given number of seams, keep track of the seams
		// removed
		for (int i = 0; i < amount; i++) {
			ISeam seam = this.calculateCheapestVerticalSeam();

			seam.remove();

			removedSeams.push(seam);

		}

		// reinsert the removed seams
		for (ISeam seam : removedSeams) {
			seam.reinsert();
		}

		Deque<ISeam> newSeams = new ArrayDeque<ISeam>();

		// now duplicate the previously removed then reinserted seams
		// dont fix up vertical links, because the seams become disconnected
		while (!removedSeams.isEmpty()) {
			ISeam seam = removedSeams.pop();
			ISeam dupe = seam.duplicateDontFixLinks();
			newSeams.push(dupe);
		}

		// fix all the broken vertical links
		this.fixBrokenVerticalLinks();

		this.width += amount;

		// add undo operation to undo the insertion
		this.operations.push(() -> {

			while (!newSeams.isEmpty()) {
				newSeams.pop().removeDontFixLinks();
			}

			this.fixBrokenVerticalLinks();

			this.width -= amount;

		});

	}

	// inserts horizontal seams
	// removes n seams to figure out which seams to duplicate, and reinserts
	// them after
	public void insertHorizontalSeams(int amount) {

		// same operation as inserting vertical seams, just with horizontal
		// seams

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

	// fixes broken vertical links caused by removing or duplicating
	// disconnected vertical seams
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

	// fixes broken horizontal links causing by removing or duplicating
	// disconnected horizontal seams
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

	// gets the current carved images width
	public int getWidth() {
		return this.width;
	}

	// gets the current carved images height
	public int getHeight() {
		return this.height;
	}

	// for testing the structural invariant of the pixel data structure
	private void testStructuralIntegrity() {

		for (TopBorderPixel column : this.topLeft.columns()) {

			for (ColoredPixel pixel : column.columnIterable()) {
				pixel.testStructuralIntegrity();

			}

		}

	}

}
