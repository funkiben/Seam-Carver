package seamcarver;

import java.util.ArrayList;

import javafx.scene.paint.Color;

// abstract class for border pixels
abstract class ABorderPixel extends APixel {

	@Override
	public double brightness() {
		return 0.0;
	}

	// technically some border pixels can have 0 vertical and horizontal
	// brightness sums, but it doesn't matter
	@Override
	public double verticalBrightnessSum() {
		return 0.0;
	}

	@Override
	public double horizontalBrightnessSum() {
		return 0.0;
	}

	// collecting seam infos will do nothing by default

	@Override
	public ArrayList<AVerticalSeamInfo> collectVerticalSeamInfos() {
		return new ArrayList<AVerticalSeamInfo>();
	}

	@Override
	public ArrayList<AHorizontalSeamInfo> collectHorizontalSeamInfos() {
		return new ArrayList<AHorizontalSeamInfo>();
	}

	// have these methods do nothing on default, we won't want to include border
	// pixels in seams in some cases

	@Override
	public void addVerticalSeamInfo(ArrayList<AVerticalSeamInfo> seamInfos) {

	}

	@Override
	public void addHorizontalSeamInfo(ArrayList<AHorizontalSeamInfo> seamInfos) {

	}

	// border pixels have no color
	@Override
	public void addColor(ArrayList<Color> colors) {

	}

	// this is not a colored pixel, return null
	@Override
	public ColoredPixel asColoredPixel() {
		return null;
	}

	// the implementation of border pixels doesn't have to compute the below
	// methods accurately, so just return false

	@Override
	public boolean isLeft(IPixel pixel) {
		return false;
	}

	@Override
	public boolean isRight(IPixel pixel) {
		return false;
	}

	@Override
	public boolean isTop(IPixel pixel) {
		return false;
	}

	@Override
	public boolean isBottom(IPixel pixel) {
		return false;
	}

	// these methods are for iterators

	TopBorderPixel asTopBorderPixel() {
		return null;
	}

	LeftBorderPixel asLeftBorderPixel() {
		return null;
	}

}
