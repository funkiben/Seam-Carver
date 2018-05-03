package seamcarver;

// the bottom border pixel
class BottomBorderPixel extends ABorderPixel {

	@Override
	public void linkTop(IPixel top) {
		top.linkBottom(this);
	}
	
	@Override
	public void linkTop(ColoredPixel top) {
		
	}

}
