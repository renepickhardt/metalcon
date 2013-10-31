package de.metalcon.imageStorageServer;

/**
 * wrapper class for values specifying an image frame
 * 
 * @author sebschlicht
 * 
 */
public class ImageFrame {

	private int left;
	private int top;

	private int width;
	private int height;

	/**
	 * create a new image frame
	 * 
	 * @param left
	 *            distance to left border specifying where the image begins
	 * @param top
	 *            distance to upper border specifying where the image begins
	 * @param width
	 *            width of the image frame
	 * @param height
	 *            height of the image frame
	 */
	public ImageFrame(final int left, final int top, final int width,
			final int height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	/**
	 * @return distance to left border specifying where the image begins
	 */
	public int getLeft() {
		return this.left;
	}

	public void setLeft(final int left) {
		this.left = left;
	}

	/**
	 * @return distance to upper border specifying where the image begins
	 */
	public int getTop() {
		return this.top;
	}

	public void setTop(final int top) {
		this.top = top;
	}

	/**
	 * @return width of the image frame
	 */
	public int getWidth() {
		return this.width;
	}

	public void setWidth(final int width) {
		this.width = width;
	}

	/**
	 * @return height of the image frame
	 */
	public int getHeight() {
		return this.height;
	}

	public void setHeight(final int height) {
		this.height = height;
	}

}