package model;

import java.util.ArrayDeque;
import java.util.Deque;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// manages undoable operations
public class UndoManager {

	// these two stacks will always be the same name
	private final Deque<String> names = new ArrayDeque<String>();
	private final Deque<IUndoableOperation> undoOps = new ArrayDeque<IUndoableOperation>();
	private final StringProperty nextOpNameProperty = new SimpleStringProperty("Undo");

	// adds a new undoable operation
	// EFFECT: this.undoOps, this.names
	public void push(String name, IUndoableOperation item) {
		this.names.push(name);
		this.undoOps.push(item);
		this.nextOpNameProperty.set(name);
	}

	// undos the last operation
	// effect: this.undoOps, this.nameOps
	public void undoNext() {
		this.names.pop();
		this.undoOps.pop().undo();

		if (!this.names.isEmpty()) {
			this.nextOpNameProperty.set(this.names.peek());
		} else {
			this.nextOpNameProperty.set("Undo");
		}
	}

	// checks if there are no more operations to undo
	public boolean empty() {
		return this.undoOps.isEmpty();
	}

	// binds the given property to the next operation that can be undone
	// EFFECT: property
	public void bindToNextOpName(StringProperty property) {
		property.bind(this.nextOpNameProperty);
	}

	// clears all undoable operations
	// EFFECT: this.names, this.undoOps, this.nextOpNameProperty
	public void clear() {
		this.names.clear();
		this.undoOps.clear();
		this.nextOpNameProperty.set("Undo");
	}

}
