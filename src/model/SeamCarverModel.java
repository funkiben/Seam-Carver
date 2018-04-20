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

	// reinserts some seams, either using original color or an estimated color
	// EFFECT: this.seamCarver, this.imageProperty
	public void reinsertSeam(int amount, boolean estimateColor) {
		for (int i = 0; i < amount; i++) {

			if (this.countRemovedSeams() == 0) {
				break;
			}

			this.seamCarver.reinsertSeam(estimateColor);

		}

		this.updateImage();
	}

	// removes some vertical seams from the carved image
	// EFFECT: this.seamCarver, this.imageProperty
	public void removeVerticalSeams(int amount) {
		for (int i = 0; i < amount; i++) {

			if (this.getWidth() == 1) {
				break;
			}

			this.seamCarver.removeVerticalSeam();

		}

		this.updateImage();
	}

	// removes some horizontal seams from the carved image
	// EFFECT: this.seamCarver, this.imageProperty
	public void removeHorizontalSeams(int amount) {
		for (int i = 0; i < amount; i++) {

			if (this.getHeight() == 1) {
				break;
			}

			this.seamCarver.removeHorizontalSeam();

		}

		this.updateImage();
	}

	// removes horizontal and vertical seams randomly
	// EFFECT: this.seamCarver, this.imageProperty
	public void removeRandomSeams(int amount) {
		for (int i = 0; i < amount; i++) {

			if (this.getHeight() == 1 || this.getWidth() == 1) {
				break;
			}

			if (Math.random() < 0.5) {
				this.seamCarver.removeHorizontalSeam();
			} else {
				this.seamCarver.removeVerticalSeam();
			}
		}

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
