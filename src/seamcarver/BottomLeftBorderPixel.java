package seamcarver;

import java.util.Iterator;

// bottom left corner border pixel, just linked to above left border pixel or TopLeftBorderPixel
class BottomLeftBorderPixel extends ALeftBorderPixel {

	@Override
	public void linkTop(IPixel top) {
		top.linkBottom(this);
	}

	@Override
	public void linkTop(LeftBorderPixel top) {

	}

	@Override
	public void linkTop(TopLeftBorderPixel top) {

	}

	@Override
	public Iterator<LeftBorderPixel> iterator() {
		return NothingIterator.inst;
	}

	// iterator that has no elements in it, returned by corner pixels
	private static class NothingIterator implements Iterator<LeftBorderPixel> {

		static final NothingIterator inst = new NothingIterator();

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public LeftBorderPixel next() {
			throw new IllegalStateException("no more elements in the iterator");
		}
		
	}

}
