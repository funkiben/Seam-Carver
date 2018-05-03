package model;

import java.util.Collection;

import javafx.geometry.Point2D;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

// different brush shapes

public interface IBrushShape {

	void doStroke(Collection<Point2D> points, Color color, PixelWriter pixelWriter,
			int size, int px, int py, int width, int height);

	// checks if the given x and y are inside a canvas with the given width and
	// height
	default boolean isInside(int x, int y, int width, int height) {

		return x >= 0 && y >= 0 && x < width && y < height;
	}

}
