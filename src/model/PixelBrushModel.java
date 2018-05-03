package model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

// manages biasing pixels on the image
public class PixelBrushModel {

	private final SeamCarverModel seamCarverModel;
	private final UndoManager undoManager;
	private final Deque<BrushStroke> strokes = new ArrayDeque<BrushStroke>();
	// null until user creates a stroke
	private BrushStroke currentStroke = null;

	private final ObjectProperty<WritableImage> canvasProperty =
			new SimpleObjectProperty<WritableImage>();

	public PixelBrushModel(SeamCarverModel seamCarver, UndoManager undoManager) {
		this.seamCarverModel = seamCarver;
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
		this.strokes.push(this.currentStroke);

		this.currentStroke.finish(this.seamCarverModel.getSeamCarver());

		this.seamCarverModel.updateImage();

		this.undoManager.push("Undo " + this.currentStroke.toString(), () -> this.undoLastStroke());

		this.currentStroke = null;

	}

	// undos the last bias stroke
	// returns false if there are no bias strokes
	// EFFECT: this.strokes, this.seamCarver, this.canvas
	public boolean undoLastStroke() {

		if (this.strokes.isEmpty()) {
			return false;
		}

		BrushStroke lastStroke = this.strokes.pop();

		lastStroke.undo(this.seamCarverModel.getSeamCarver());

		Iterator<BrushStroke> revItr = this.strokes.descendingIterator();
		
		// this isnt great...
		while (revItr.hasNext()) {
			revItr.next().finish(this.seamCarverModel.getSeamCarver());
		}

		this.seamCarverModel.updateImage();

		return true;
	}

	// removes all pixel bias
	// EFFECT: this.strokes, this.canvasProperty, this.seamCarver
	public void removeAllBias() {

		this.clearStrokes();

		this.currentStroke = null;

		this.seamCarverModel.getSeamCarver().unbiasAllPixels();
		this.seamCarverModel.updateImage();

	}

	// clears all strokes
	// EFFECT: this.strokes
	public void clearStrokes() {
		this.strokes.clear();
	}

	// creates a new canvas matching the current size of the carved image
	// EFFECT: this.canvas
	public void makeNewCanvas() {
		this.canvasProperty.set(new WritableImage(this.seamCarverModel.getWidth(),
				this.seamCarverModel.getHeight()));
	}

	// makes a brush stroke at the given coordinates with the given brush size
	// EFFECT: this.currentStroke, this.canvasProperty
	public void addToCurrentStroke(int x, int y) {

		PixelWriter pixelWriter = this.canvasProperty.get().getPixelWriter();

		this.currentStroke.add(pixelWriter, x, y, this.seamCarverModel.getWidth(),
				this.seamCarverModel.getHeight());

	}

	// makes a brush stroke at the given coordinates with the given brush size
	// EFFECT: this.currentStroke, this.canvasProperty
	public void startStroke(int brushSize, IBrushShape shape, IBrushMode mode) {
		this.currentStroke = new BrushStroke(brushSize, shape, mode);
	}

}
