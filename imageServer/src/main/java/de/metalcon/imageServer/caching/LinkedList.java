package de.metalcon.imageServer.caching;

public class LinkedList<T> {

	private ListItem<T> first, last;

	public boolean isEmpty() {
		return (this.first == null);
	}

	public void add(final CacheEntry<T> value) {
		final ListItem<T> item = new ListItem<T>(value, null, this.first);
		value.setItem(item);

		if (this.first != null) {
			this.first.setPrevious(item);
		} else {
			this.last = item;
		}
		this.first = item;
	}

	public ListItem<T> getLast() {
		return this.last;
	}

	public void removeLast() {
		if (this.last != null) {
			this.last = this.last.getPrevious();
			if (this.last != null) {
				this.last.setNext(null);
			}
		}
	}

	public void moveToFront(final ListItem<T> item) {
		final ListItem<T> previous = item.getPrevious();
		final ListItem<T> next = item.getNext();

		if (item != this.first) {
			if (previous != null) {
				previous.setNext(next);
			}
			if (next != null) {
				next.setPrevious(previous);
			}

			item.setPrevious(null);
			item.setNext(this.first);
			this.first = item;
		}
	}

}