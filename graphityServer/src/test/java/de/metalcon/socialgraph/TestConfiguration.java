package de.metalcon.socialgraph;

/**
 * Graphity configuration for testing
 * 
 * @author sebschlicht
 * 
 */
public class TestConfiguration implements Configuration {

	@Override
	public String getDatabasePath() {
		return "target/database/testlocation";
	}

	@Override
	public String getTemplatesPath() {
		return "target/templates/";
	}

	@Override
	public boolean getReadOnly() {
		return false;
	}

	@Override
	public String getAlgorithm() {
		return null;
	}

	@Override
	public String getUseMemoryMappedBuffers() {
		return "false";
	}

	@Override
	public String getCacheType() {
		return "strong";
	}

}
