package de.metalcon.imageStorageServer;

/**
 * image scaling types supported
 * 
 * @author sebschlicht
 * 
 */
public enum ScalingType {

	/**
	 * down-scale image until it fits the frame
	 */
	FIT,

	/**
	 * down-scale image until its width matches the frame width
	 */
	WIDTH,

	/**
	 * down-scale image until its height matches the frame height
	 */
	HEIGHT;

}