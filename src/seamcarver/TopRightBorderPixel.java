package seamcarver;

import java.util.Iterator;

// top right corner border pixel, linked only to left TopBorderPixel or TopLeftBorderPixel
class TopRightBorderPixel extends ATopBorderPixel {

	@Override
	public void linkLeft(IPixel left) {
		left.linkRight(this);
	}
	
	@Override
	public void linkLeft(TopBorderPixel left) {
		
	}

	@Override
	public void linkLeft(TopLeftBorderPixel left) {
		
	}

	@Override
	public Iterator<TopBorderPixel> iterator() {
		return NothingIterator.inst;
	}
	
	// iterator that has no elements in it, returned by corner pixels
	private static class NothingIterator implements Iterator<TopBorderPixel> {

		static final NothingIterator inst = new NothingIterator();

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public TopBorderPixel next() {
			throw new IllegalStateException("no more elements in the iterator");
		}

	}
	

}
