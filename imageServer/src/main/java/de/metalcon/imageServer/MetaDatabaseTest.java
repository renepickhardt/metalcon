package de.metalcon.imageServer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MetaDatabaseTest {

	private static final String VALID_READ_IDENTIFIER = "img1";

	private static final String VALID_CREATE_IDENTIFIER = "img2";

	private static final JSONObject META_DATA = new JSONObject();

	private static MetaDatabase DB;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DB = new MetaDatabase("localhost", 27017, "testdb");
		META_DATA.put("pos", "home");
	}

	@Before
	public void setUp() throws Exception {
		DB.clear();
		assertTrue(DB.addDatabaseEntry(VALID_READ_IDENTIFIER, META_DATA));
	}

	@Test
	public void testAddDatabaseEntry() {
		assertTrue(DB.addDatabaseEntry(VALID_CREATE_IDENTIFIER, META_DATA));
		assertFalse(DB.addDatabaseEntry(VALID_CREATE_IDENTIFIER, META_DATA));
	}

	@Test
	public void testGetMetaData() {
		final String metaData = DB.getMetadata(VALID_READ_IDENTIFIER);
		assertNotNull(metaData);
		System.out.println(metaData);
		System.out.println(META_DATA.toJSONString());
	}

}