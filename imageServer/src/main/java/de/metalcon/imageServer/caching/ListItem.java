package de.metalcon.imageServer.caching;

public class ListItem<T> {

	private ListItem<T> previous, next;

	private final CacheEntry<T> value;

	public ListItem(final CacheEntry<T> value, final ListItem<T> previous,
			final ListItem<T> next) {
		this.value = value;
		this.previous = previous;
		this.next = next;
	}

	public ListItem<T> getPrevious() {
		return this.previous;
	}

	public void setPrevious(final ListItem<T> previous) {
		this.previous = previous;
	}

	public ListItem<T> getNext() {
		return this.next;
	}

	public void setNext(final ListItem<T> next) {
		this.next = next;
	}

	public CacheEntry<T> getValue() {
		return this.value;
	}

}