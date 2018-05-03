package seamcarver;

// abstract class for vertical seams
abstract class AVerticalSeamInfo extends ASeamInfo {

	@Override
	public abstract AVerticalSeamInfo duplicate();

	@Override
	public abstract AVerticalSeamInfo duplicateDontFixLinks();

	// will either return this if this is a VerticalSeamInfo, else null
	// used by iterators
	abstract VerticalSeamInfo asVerticalSeamInfo();

}
