package model;

import java.util.Collection;

import javafx.geometry.Point2D;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

// brush shape thats a circle
public class CircleBrushShape implements IBrushShape {

	@Override
	public void doStroke(Collection<Point2D> points, Color color, PixelWriter pixelWriter, int size,
			int px, int py, int width, int height) {

		for (int x = -size; x <= size; x++) {
			for (int y = -size; y <= size; y++) {

				if (x * x + y * y <= size * size) {

					int bx = x + px;
					int by = y + py;

					if (this.isInside(bx, by, width, height)) {

						points.add(new Point2D(bx, by));
						pixelWriter.setColor(bx, by, color);

					}

				}

			}

		}

	}

	@Override
	public String toString() {
		return "Circle";
	}

}