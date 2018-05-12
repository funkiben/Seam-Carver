package seamcarver;

// represents a seam operation, can be undone
interface ISeamOperation {

	// undos the oepration
	void undo();

}
