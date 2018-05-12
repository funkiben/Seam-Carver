package seamcarver;

import java.util.Iterator;

// bottom left corner border pixel
// top: LeftBorderPixel or TopLeftBorderPixel
// bottom: none
// left: none
// right: none
class BottomLeftBorderPixel extends ALeftBorderPixel {

	@Override
	public void linkTop(IPixel top) {
		top.linkBottom(this);
	}

	// the bottom left border pixel doesn't need to remember its neighbors
	@Override
	public void linkTop(LeftBorderPixel top) {

	}

	@Override
	public void linkTop(TopLeftBorderPixel top) {

	}

	// just returns empty iterator, no more left border pixels in the column
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
