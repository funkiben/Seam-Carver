package model;

// represents some operation that can be undone
interface IUndoableOperation {

	// undos the operation
	void undo();

}