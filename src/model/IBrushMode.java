package model;

import java.util.Collection;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import seamcarver.SeamCarver;

// represents a brush mode
public interface IBrushMode {

	Color color();

	// applies the brush stroke modification to the pixels
	void finish(SeamCarver seamCarver, Collection<Point2D> points);

	// undos the brush stroke modifications
	void undo(SeamCarver seamCarver, Collection<Point2D> points);

}