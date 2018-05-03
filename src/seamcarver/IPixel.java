package seamcarver;

import java.util.ArrayList;

import javafx.scene.paint.Color;

// represents a pixel
interface IPixel {

	// mutually links the two pixels
	void linkLeftMutual(IPixel left);
	void linkRightMutual(IPixel right);
	void linkTopMutual(IPixel top);
	void linkBottomMutual(IPixel bottom);

	// link methods for any pixel
	void linkLeft(IPixel left);
	void linkRight(IPixel right);
	void linkTop(IPixel top);
	void linkBottom(IPixel bottom);

	// link methods for colored pixels
	void linkLeft(ColoredPixel left);
	void linkRight(ColoredPixel right);
	void linkTop(ColoredPixel top);
	void linkBottom(ColoredPixel bottom);

	// link methods for top border pixels
	void linkLeft(TopBorderPixel left);
	void linkRight(TopBorderPixel right);
	void linkTop(TopBorderPixel top);
	void linkBottom(TopBorderPixel bottom);

	// link methods for bottom border pixels
	void linkLeft(BottomBorderPixel left);
	void linkRight(BottomBorderPixel right);
	void linkTop(BottomBorderPixel top);
	void linkBottom(BottomBorderPixel bottom);

	// link methods for left border pixels
	void linkLeft(LeftBorderPixel left);
	void linkRight(LeftBorderPixel right);
	void linkTop(LeftBorderPixel top);
	void linkBottom(LeftBorderPixel bottom);

	// link methods for right border pixels
	void linkLeft(RightBorderPixel left);
	void linkRight(RightBorderPixel right);
	void linkTop(RightBorderPixel top);
	void linkBottom(RightBorderPixel bottom);

	// links methods for top left corner border pixels
	void linkLeft(TopLeftBorderPixel left);
	void linkRight(TopLeftBorderPixel right);
	void linkTop(TopLeftBorderPixel top);
	void linkBottom(TopLeftBorderPixel bottom);

	// link methods for top right corner border pixels
	void linkLeft(TopRightBorderPixel left);
	void linkRight(TopRightBorderPixel right);
	void linkTop(TopRightBorderPixel top);
	void linkBottom(TopRightBorderPixel bottom);

	// link methods for bottom left corner border pixels
	void linkLeft(BottomLeftBorderPixel left);
	void linkRight(BottomLeftBorderPixel right);
	void linkTop(BottomLeftBorderPixel top);
	void linkBottom(BottomLeftBorderPixel bottom);

	// gets the brightness of this pixel
	double brightness();

	// gets the brightness of this pixel times 2 plus left and right neighbor
	// brightnesses
	double verticalBrightnessSum();

	// gets the brightness of this pixel times 2 plus top and bottom neighbor
	// brightnesses
	double horizontalBrightnessSum();

	// collects the vertical seam infos of this and horizontal neighbors
	ArrayList<AVerticalSeamInfo> collectVerticalSeamInfos();

	// collects the horizontal seam infos of this and vertical neighbors
	ArrayList<AHorizontalSeamInfo> collectHorizontalSeamInfos();

	// adds this pixels vertical seam info to the array list
	void addVerticalSeamInfo(ArrayList<AVerticalSeamInfo> seamInfos);

	// adds this pixels horizontal seam info to the array list
	void addHorizontalSeamInfo(ArrayList<AHorizontalSeamInfo> seamInfos);

	// adds this pixelx color to the array list
	void addColor(ArrayList<Color> colors);

	// checks if this pixel has the given pixel as a left neighbor
	boolean isLeft(IPixel pixel);

	// checks if this pixel has the given pixel as a right neighbor
	boolean isRight(IPixel pixel);

	// checks if this pixel has the given pixel as a top neighbor
	boolean isTop(IPixel pixel);

	// checks if this pixel has the given pixel as a bottom neighbor
	boolean isBottom(IPixel pixel);

	// the below methods are for iterators, and they really suck

	// if this is a colored pixel, returns this, otherwise returns null
	// pretty much casting but a little better i think
	// necessary for iterators
	ColoredPixel asColoredPixel();

}
