package seamcarver;

// the left border pixel
// top: none
// bottom: none
// left: ColoredPixel
// right: none
class RightBorderPixel extends ABorderPixel {

	@Override
	public void linkLeft(IPixel left) {
		left.linkRight(this);
	}

	@Override
	public void linkLeft(ColoredPixel left) {

	}

}
