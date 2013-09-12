package de.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MetaDatabaseTest {

	/**
	 * valid identifier for reading
	 */
	protected static final String VALID_READ_IDENTIFIER = "img1";

	/**
	 * invalid identifier for reading
	 */
	protected static final String INVALID_READ_IDENTIFIER = "noimg";

	/**
	 * valid identifier for a new entry
	 */
	protected static final String VALID_CREATE_IDENTIFIER = "img2";

	/**
	 * valid meta data for a new entry
	 */
	protected static final JSONObject VALID_META_DATA = new JSONObject();

	/**
	 * valid meta data for meta data updates
	 */
	protected static final JSONObject VALID_UPDATE_META_DATA = new JSONObject();

	/**
	 * JSON parser
	 */
	protected static final JSONParser PARSER = new JSONParser();

	/**
	 * meta database being tested
	 */
	protected static MetaDatabase DB;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DB = new MetaDatabase("localhost", 27017, "testdb");
		DB.clear();
		VALID_META_DATA.put("pos", "home");
		VALID_UPDATE_META_DATA.put("pos", "bridge");
	}

	@Before
	public void setUp() throws Exception {
		assertTrue(DB.addDatabaseEntry(VALID_READ_IDENTIFIER, VALID_META_DATA));
	}

	@After
	public void tearDown() {
		DB.clear();
	}

	@Test
	public void testAddDatabaseEntry() {
		assertTrue(DB
				.addDatabaseEntry(VALID_CREATE_IDENTIFIER, VALID_META_DATA));
		assertFalse(DB.addDatabaseEntry(VALID_CREATE_IDENTIFIER,
				VALID_META_DATA));
	}

	@Test
	public void testGetAddedMetaData() {
		// tested implicitly by all tests reading!
		this.testAddDatabaseEntry();

		assertNotNull(DB.getMetadata(VALID_CREATE_IDENTIFIER));
	}

	@Test
	public void testAddDatabaseEntryWithoutMetadata() {
		assertTrue(DB.addDatabaseEntry(VALID_CREATE_IDENTIFIER, null));
	}

	@Test
	public void testGetAddedEmptyMetaData() {
		this.testAddDatabaseEntryWithoutMetadata();

		final String metaData = DB.getMetadata(VALID_CREATE_IDENTIFIER);
		final JSONObject metaDataJson = parseToJSON(metaData);
		assertTrue(metaDataJson.isEmpty());
	}

	@Test
	public void testGetMetaData() {
		final String metaData = DB.getMetadata(VALID_READ_IDENTIFIER);
		assertNotNull(metaData);

		final JSONObject metaDataJSON = parseToJSON(metaData);
		assertTrue(VALID_META_DATA.equals(metaDataJSON));

		final String noMetaData = DB.getMetadata(INVALID_READ_IDENTIFIER);
		assertNull(noMetaData);
	}

	@Test
	public void testAppendMetaData() {
		assertTrue(DB.appendMetadata(VALID_READ_IDENTIFIER,
				VALID_UPDATE_META_DATA));
		assertFalse(DB.appendMetadata(INVALID_READ_IDENTIFIER,
				VALID_UPDATE_META_DATA));
	}

	@Test
	public void testGetAppendedMetaData() {
		this.testAppendMetaData();

		final String metaData = DB.getMetadata(VALID_READ_IDENTIFIER);
		assertNotNull(metaData);

		final JSONObject metaDataJSON = parseToJSON(metaData);
		assertTrue(VALID_UPDATE_META_DATA.equals(metaDataJSON));
	}

	@Test
	public void testDeleteDatabaseEntry() {
		assertTrue(DB.deleteDatabaseEntry(VALID_READ_IDENTIFIER));
		assertFalse(DB.deleteDatabaseEntry(VALID_READ_IDENTIFIER));
	}

	@Test
	public void testGetDeletedMetaData() {
		this.testDeleteDatabaseEntry();
		assertNull(DB.getMetadata(VALID_READ_IDENTIFIER));
	}

	/**
	 * parse a String to a JSON object<br>
	 * <b>fails the test</b> if the parsing failed
	 * 
	 * @param value
	 *            String to be parsed
	 * @return JSON object represented by the String passed<br>
	 *         <b>null</b> if the parsing failed
	 */
	protected static JSONObject parseToJSON(final String value) {
		try {
			return (JSONObject) PARSER.parse(value);
		} catch (final ParseException e) {
			fail("failed to parse to JSON object!");
		}

		return null;
	}

}