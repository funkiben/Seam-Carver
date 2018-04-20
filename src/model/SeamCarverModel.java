package model;

import java.util.Collection;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import seamcarver.SeamCarver;

// the models for the seam carver app
// interacts with the seam carving library
public class SeamCarverModel {

	private SeamCarver seamCarver;
	private final Image originalImage;

	private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();

	public SeamCarverModel(Image originalImage) {

		this.originalImage = originalImage;

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

	// biases the given pixels
	// EFFECT: this.seamCarver
	public void biasPixels(Collection<Point2D> points) {
		this.seamCarver.biasPixels(points);
		this.updateImage();
	}

	// unbiases the given pixels
	// EFFECT: this.seamCarver
	public void unbiasPixels(Collection<Point2D> points) {
		this.seamCarver.unbiasPixels(points);
		this.updateImage();
	}

	// unbiases all pixels
	// EFFECT: this.seamCarver
	public void unbiasAllPixels() {
		this.seamCarver.unbiasAllPixels();
		this.updateImage();
	}

	// reinserts the seam, either using original color or esimated color
	// EFFECT: this.seamCarver, this.imageProperty
	public void reinsertSeam(boolean estimateColor) {
		this.seamCarver.reinsertSeam(estimateColor);
		this.updateImage();
	}

	// removes a vertical seam from the carved image
	// EFFECT: this.seamCarver, this.imageProperty
	public void removeVerticalSeam() {
		this.seamCarver.removeVerticalSeam();
		this.updateImage();
	}

	// removes a vertical seam from the carved image
	// EFFECT: this.seamCarver, this.imageProperty
	public void removeHorizontalSeam() {
		this.seamCarver.removeHorizontalSeam();
		this.updateImage();
	}

	// gets the width of the current carved image
	public int getWidth() {
		return seamCarver.getWidth();
	}

	// gets the height of the current carved image
	public int getHeight() {
		return seamCarver.getHeight();
	}

	// gets the total amount of seams that have been removed
	public int countRemovedSeams() {
		return seamCarver.countRemovedSeams();
	}

	// counts the amount of vertical seams that have been removed
	public int countRemovedVerticalSeams() {
		return (int) (this.originalImage.getWidth() - this.imageProperty.get().getWidth());
	}

	// counts the amount of vertical seams that have been removed
	public int countRemovedHorizontalSeams() {
		return (int) (this.originalImage.getHeight() - this.imageProperty.get().getHeight());
	}

}