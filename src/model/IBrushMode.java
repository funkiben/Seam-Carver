package model;

import java.util.Collection;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import seamcarver.SeamCarver;

/// different bias modes
// can bias for, against, or erase bias

public interface IBrushMode {

	Color color();

	void finish(SeamCarver seamCarver, Collection<Point2D> points);

	void undo(SeamCarver seamCarver, Collection<Point2D> points);

}