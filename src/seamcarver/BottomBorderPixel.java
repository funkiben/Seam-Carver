package seamcarver;

// the bottom border pixel
// top: ColoredPixel
// left: none
// right: none
// bottom: none
class BottomBorderPixel extends ABorderPixel {

	@Override
	public void linkTop(IPixel top) {
		top.linkBottom(this);
	}

	// bottom border pixels can have colored pixels as top, but we don't need to
	// remember the top
	@Override
	public void linkTop(ColoredPixel top) {

	}

}
