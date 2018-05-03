package model;

import java.util.Collection;
import java.util.HashSet;

import javafx.geometry.Point2D;
import javafx.scene.image.PixelWriter;
import seamcarver.SeamCarver;

// represents a brush stroke that has a mode, a shape, and points
class BrushStroke {

	private final int size;
	private final IBrushMode mode;
	private final IBrushShape shape;
	private final Collection<Point2D> points = new HashSet<Point2D>();

	BrushStroke(int size, IBrushShape shape, IBrushMode mode) {
		this.size = size;
		this.shape = shape;
		this.mode = mode;
	}

	// undos this stroke
	void undo(SeamCarver seamCarver) {
		this.mode.undo(seamCarver, this.points);
	}

	// finishes this stroke and applies the changes to the seam carver using the
	// mode
	void finish(SeamCarver seamCarver) {
		this.mode.finish(seamCarver, this.points);
	}

	// adds to this stroke
	void add(PixelWriter pixelWriter, int x, int y, int width, int height) {
		this.shape.doStroke(this.points, mode.color(), pixelWriter, this.size, x, y, width, height);
	}

	@Override
	public String toString() {
		return this.mode.toString() + " Stroke";
	}

}