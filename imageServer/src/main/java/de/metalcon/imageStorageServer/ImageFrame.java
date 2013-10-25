package de.metalcon.imageStorageServer;

/**
 * wrapper class for values specifying an image frame
 * 
 * @author sebschlicht
 * 
 */
public class ImageFrame {

	private final int left;
	private final int top;

	private final int width;
	private final int height;

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

	/**
	 * @return distance to upper border specifying where the image begins
	 */
	public int getTop() {
		return this.top;
	}

	/**
	 * @return width of the image frame
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * @return height of the image frame
	 */
	public int getHeight() {
		return this.height;
	}

}