package seamcarver;

// a seam of pixels
interface ISeam extends Iterable<ColoredPixel> {

	// reinserts this seam back into the image
	void reinsert();

	// removes this seam from the image
	// seam must be continuous for this method
	void remove();

	// removes this seam from the image
	// fixes only either vertical or horizontal links
	// can be used on seams that aren't continuous
	void removeDontFixLinks();

	// creates a new seam to the left or top of this
	// cant be used on seams that aren't continuous
	ISeam duplicate();

	// creates a new seam to the left or top of this
	// can be used on seams that aren't continuous
	ISeam duplicateDontFixLinks();

}
