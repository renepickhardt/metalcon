package de.metalcon.imageServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.metalcon.imageStorageServer.ImageMetaDatabase;

public class ImageMetaDatabaseTest {

	/**
	 * valid identifier for reading
	 */
	protected static final String VALID_READ_IDENTIFIER = "img1";

	/**
	 * invalid identifier for reading
	 */
	protected static final String INVALID_READ_IDENTIFIER = "noimg";

	/**
	 * valid meta data for a new entry
	 */
	protected static final JSONObject VALID_META_DATA = new JSONObject();

	private static final int INVALID_READ_WIDTH = 300;

	private static final int INVALID_READ_HEIGHT = 300;

	private static final int VALID_WIDTH_1 = 100;

	private static final int VALID_HEIGHT_1 = 100;

	private static final int VALID_WIDTH_2 = 200;

	private static final int VALID_HEIGHT_2 = 200;

	private static final String VALID_IMAGE_PATH_1 = "mypath";

	private static final String VALID_IMAGE_PATH_2 = "setup";

	/**
	 * image meta database being tested
	 */
	private static ImageMetaDatabase DB;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DB = new ImageMetaDatabase("localhost", 27017, "testdb");
		DB.clear();
		VALID_META_DATA.put("pos", "home");
	}

	@Before
	public void setUp() throws Exception {
		assertTrue(DB.addDatabaseEntry(VALID_READ_IDENTIFIER, VALID_META_DATA));
		assertTrue(DB.registerImageSize(VALID_READ_IDENTIFIER, VALID_WIDTH_2,
				VALID_HEIGHT_2, VALID_IMAGE_PATH_2));
	}

	@After
	public void tearDown() {
		DB.clear();
	}

	@Test
	public void testRegisterImageSize() {
		// test existing
		assertTrue(DB.registerImageSize(VALID_READ_IDENTIFIER, VALID_WIDTH_1,
				VALID_HEIGHT_1, VALID_IMAGE_PATH_1));

		// test not existing
		assertFalse(DB.registerImageSize(INVALID_READ_IDENTIFIER,
				VALID_WIDTH_1, VALID_HEIGHT_1, VALID_IMAGE_PATH_1));
	}

	@Test
	public void testRegisteredImageHasSizeRegistered() {
		this.testRegisterImageSize();
		assertTrue(DB.imageHasSizeRegistered(VALID_READ_IDENTIFIER,
				VALID_WIDTH_1, VALID_HEIGHT_1));
	}

	@Test
	public void testRegisteredGetSmallestImagePath() {
		this.testRegisterImageSize();

		final String path1 = DB.getSmallestImagePath(VALID_READ_IDENTIFIER,
				VALID_WIDTH_1, VALID_HEIGHT_1);
		assertEquals(VALID_IMAGE_PATH_1, path1);
	}

	@Test
	public void testRegisteredGetRegisteredImagePaths() {
		this.testRegisterImageSize();

		final String[] paths = DB
				.getRegisteredImagePaths(VALID_READ_IDENTIFIER);
		assertEquals(2, paths.length);
	}

	@Test
	public void testImageHasSizeRegistered() {
		// test registered
		assertTrue(DB.imageHasSizeRegistered(VALID_READ_IDENTIFIER,
				VALID_WIDTH_2, VALID_HEIGHT_2));

		// test not registered
		assertFalse(DB.imageHasSizeRegistered(VALID_READ_IDENTIFIER,
				INVALID_READ_WIDTH, INVALID_READ_HEIGHT));

		// test not existing
		assertFalse(DB.imageHasSizeRegistered(INVALID_READ_IDENTIFIER,
				VALID_WIDTH_2, VALID_HEIGHT_2));
	}

	@Test
	public void testGetSmallestImagePath() {
		// test smaller image
		String path2 = DB.getSmallestImagePath(VALID_READ_IDENTIFIER,
				VALID_WIDTH_1, VALID_HEIGHT_1);
		assertEquals(VALID_IMAGE_PATH_2, path2);

		// test image with same size
		path2 = DB.getSmallestImagePath(VALID_READ_IDENTIFIER, VALID_WIDTH_2,
				VALID_HEIGHT_2);
		assertEquals(VALID_IMAGE_PATH_2, path2);

		// test image too large/not existing
		assertNull(DB.getSmallestImagePath(VALID_READ_IDENTIFIER,
				INVALID_READ_WIDTH, INVALID_READ_HEIGHT));
		assertNull(DB.getSmallestImagePath(INVALID_READ_IDENTIFIER,
				VALID_WIDTH_1, VALID_HEIGHT_1));
	}

	@Test
	public void testGetRegisteredImagePaths() {
		// test one path registered
		String[] paths = DB.getRegisteredImagePaths(VALID_READ_IDENTIFIER);
		assertNotNull(paths);
		assertEquals(1, paths.length);
		assertEquals(VALID_IMAGE_PATH_2, paths[0]);
	}

	@Test
	public void testDeleteDatabaseEntry() {
		// deletion successful only once
		assertTrue(DB.deleteDatabaseEntry(VALID_READ_IDENTIFIER));
		assertFalse(DB.deleteDatabaseEntry(VALID_READ_IDENTIFIER));
	}

	@Test
	public void testDeletedGetRegisteredImagePath() {
		this.testDeleteDatabaseEntry();

		assertFalse(DB.imageHasSizeRegistered(VALID_READ_IDENTIFIER,
				VALID_WIDTH_2, VALID_HEIGHT_2));
	}

	@Test
	public void testDeletedGetRegisteredImagePaths() {
		this.testDeleteDatabaseEntry();

		final String[] paths = DB
				.getRegisteredImagePaths(VALID_READ_IDENTIFIER);
		assertNull(paths);
	}

}