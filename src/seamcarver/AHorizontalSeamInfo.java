package seamcarver;

// abstract class for horizontal seams
abstract class AHorizontalSeamInfo extends ASeamInfo {

	@Override
	public abstract AHorizontalSeamInfo duplicate();

	@Override
	public abstract AHorizontalSeamInfo duplicateDontFixLinks();

	// will either return this if this is a HorizontalSeamInfo, else null
	// used by iterators
	abstract HorizontalSeamInfo asHorizontalSeamInfo();

}
