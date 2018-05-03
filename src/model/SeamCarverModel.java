package model;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import seamcarver.SeamCarver;

// the models for the seam carver app
// interacts with the seam carving library
public class SeamCarverModel {

	private SeamCarver seamCarver;
	private final Image originalImage;
	private final UndoManager undoManager;

	private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();

	public SeamCarverModel(Image originalImage, UndoManager undoManager) {

		this.originalImage = originalImage;
		this.undoManager = undoManager;

		this.revertToOriginal();

	}

	// binds the given image property to the carved image
	// EFFECT: imageProperty
	public void bindToCarvedImage(ObjectProperty<Image> imageProperty) {
		imageProperty.bind(this.imageProperty);
	}

	// updates the image property to the current carved image
	// EFFECT: this.image
	public void updateImage() {
		this.imageProperty.set(this.seamCarver.makeImage());
	}

	// creates a new seam carver object from the original object
	// EFFECT: this.seamCarver
	public void revertToOriginal() {
		this.seamCarver = new SeamCarver(this.originalImage);
		this.updateImage();
	}

	// removes some vertical seams from the carved image
	// EFFECT: this.seamCarver, this.imageProperty
	public void shrinkVertically(int amount) {

		this.seamCarver.removeHorizontalSeams(amount);

		this.addUndoOperationForSeamCarver("Undo Vertical Shrink");

		this.updateImage();

	}

	// removes some horizontal seams from the carved image
	// EFFECT: this.seamCarver, this.imageProperty
	public void shrinkHorizontally(int amount) {

		this.seamCarver.removeVerticalSeams(amount);

		this.addUndoOperationForSeamCarver("Undo Horizontal Shrink");

		this.updateImage();

	}

	// fabricates artificial horizontal seams to expand the image vertically
	public void expandVertically(int amount) {

		this.seamCarver.insertHorizontalSeams(amount);

		this.addUndoOperationForSeamCarver("Undo Vertical Expand");

		this.updateImage();

	}

	// fabricates artificial vertical seams to expand the image horizontally
	public void expandHorizontally(int amount) {

		this.seamCarver.insertVerticalSeams(amount);

		this.addUndoOperationForSeamCarver("Undo Horizontal Expand");

		this.updateImage();

	}

	// adds an undo operation for expansion given the seams that were inserted
	private void addUndoOperationForSeamCarver(String name) {
		this.undoManager.push(name, () -> {
			this.seamCarver.undoLastOperation();
			this.updateImage();
		});
	}

	// gets the width of the current carved image
	public int getWidth() {
		return this.seamCarver.getWidth();
	}

	// gets the height of the current carved image
	public int getHeight() {
		return this.seamCarver.getHeight();
	}

	// gets the width of the current carved image
	public int getOriginalWidth() {
		return (int) this.originalImage.getWidth();
	}

	// gets the height of the current carved image
	public int getOriginalHeight() {
		return (int) this.originalImage.getHeight();
	}
	
	// gets the underlying seam carver object
	SeamCarver getSeamCarver() {
		return this.seamCarver;
	}

}
