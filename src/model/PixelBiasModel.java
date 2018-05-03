package model;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// manages biasing pixels on the image
public class PixelBiasModel {

	private static final Color BRUSH_COLOR = Color.RED;

	private final SeamCarverModel seamCarver;
	private final UndoManager undoManager;
	private final Deque<Collection<Point2D>> strokes = new ArrayDeque<Collection<Point2D>>();
	private final Collection<Point2D> currentStroke = new HashSet<Point2D>();

	private final ObjectProperty<WritableImage> canvasProperty =
			new SimpleObjectProperty<WritableImage>();

	public PixelBiasModel(SeamCarverModel seamCarver, UndoManager undoManager) {
		this.seamCarver = seamCarver;
		this.undoManager = undoManager;
	}

	// binds the given image property to this canvas image
	// EFFECT: imageProperty
	public void bindToCanvas(ObjectProperty<Image> imageProperty) {
		imageProperty.bind(this.canvasProperty);
	}

	// finishes the current stroke and applies bias to all the pixels
	// EFFECT: this.currentStroke, strokes,
	public void finishStroke() {
		this.strokes.push(new HashSet<Point2D>(this.currentStroke));
		
		this.undoManager.push("Undo Bias Stroke", () -> this.undoLastStroke());
		
		this.seamCarver.biasPixels(this.currentStroke);

		this.currentStroke.clear();

	}

	// undos the last bias stroke
	// returns false if there are no bias strokes
	// EFFECT: this.strokes, this.seamCarver, this.canvas
	public boolean undoLastStroke() {

		if (this.strokes.isEmpty()) {
			return false;
		}

		Collection<Point2D> stroke = this.strokes.pop();

		this.seamCarver.unbiasPixels(stroke);

		return true;
	}

	// removes all pixel bias
	// EFFECT: this.strokes, this.canvasProperty, this.seamCarver
	public void removeAllBias() {
		
		this.strokes.clear();
		this.currentStroke.clear();

		this.seamCarver.unbiasAllPixels();

	}

	// creates a new canvas matching the current size of the carved image
	// EFFECT: this.canvas
	public void makeNewCanvas() {
		this.canvasProperty
				.set(new WritableImage(this.seamCarver.getWidth(), this.seamCarver.getHeight()));
	}

	// makes a brush stroke at the given coordinates with the given brush size
	// EFFECT: this.currentStroke, this.canvasProperty
	public void addStroke(int px, int py, int brushSize) {

		PixelWriter pixelWriter = this.canvasProperty.get().getPixelWriter();

		for (int x = -brushSize; x <= brushSize; x++) {
			for (int y = -brushSize; y <= brushSize; y++) {

				if (x * x + y * y <= brushSize * brushSize) {

					int bx = x + px;
					int by = y + py;

					if (this.isInsideCanvas(bx, by)) {

						this.currentStroke.add(new Point2D(bx, by));
						pixelWriter.setColor(bx, by, BRUSH_COLOR);

					}

				}

			}
		}

	}

	// checks if the given x and y are inside the pixel bias canvas
	private boolean isInsideCanvas(int x, int y) {
		Image img = this.canvasProperty.get();

		return x >= 0 && y >= 0 && x < img.getWidth() && y < img.getHeight();
	}

}
