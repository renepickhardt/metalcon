package de.metalcon.imageServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImageMetaDatabaseTest extends MetaDatabaseTest {

	private static final int INVALID_READ_WIDTH = 300;

	private static final int INVALID_READ_HEIGHT = 300;

	private static final int VALID_WIDTH_1 = 100;

	private static final int VALID_HEIGHT_1 = 100;

	private static final int VALID_WIDTH_2 = 200;

	private static final int VALID_HEIGHT_2 = 200;

	private static final String VALID_IMAGE_PATH_1 = "mypath";

	private static final String VALID_IMAGE_PATH_2 = "setup";

	private static ImageMetaDatabase IMAGE_DB;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MetaDatabaseTest.setUpBeforeClass();
		IMAGE_DB = new ImageMetaDatabase("localhost", 27017, "testdb");
		DB = IMAGE_DB;
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		assertTrue(IMAGE_DB.registerImageSize(VALID_READ_IDENTIFIER,
				VALID_WIDTH_2, VALID_HEIGHT_2, VALID_IMAGE_PATH_2));
	}

	@Test
	public void testRegisterImageSize() {
		// test existing
		assertTrue(IMAGE_DB.registerImageSize(VALID_READ_IDENTIFIER,
				VALID_WIDTH_1, VALID_HEIGHT_1, VALID_IMAGE_PATH_1));

		// test not existing
		assertFalse(IMAGE_DB.registerImageSize(INVALID_READ_IDENTIFIER,
				VALID_WIDTH_1, VALID_HEIGHT_1, VALID_IMAGE_PATH_1));
	}

	@Test
	public void testRegisteredImageHasSizeRegistered() {
		this.testRegisterImageSize();
		assertTrue(IMAGE_DB.imageHasSizeRegistered(VALID_READ_IDENTIFIER,
				VALID_WIDTH_1, VALID_HEIGHT_1));
	}

	@Test
	public void testRegisteredGetSmallestImagePath() {
		this.testRegisterImageSize();

		final String path1 = IMAGE_DB.getSmallestImagePath(
				VALID_READ_IDENTIFIER, VALID_WIDTH_1, VALID_HEIGHT_1);
		assertEquals(VALID_IMAGE_PATH_1, path1);
	}

	@Test
	public void testRegisteredGetRegisteredImagePaths() {
		this.testRegisterImageSize();

		final String[] paths = IMAGE_DB
				.getRegisteredImagePaths(VALID_READ_IDENTIFIER);
		assertEquals(2, paths.length);
	}

	@Test
	public void testImageHasSizeRegistered() {
		// test registered
		assertTrue(IMAGE_DB.imageHasSizeRegistered(VALID_READ_IDENTIFIER,
				VALID_WIDTH_2, VALID_HEIGHT_2));

		// test not registered
		assertFalse(IMAGE_DB.imageHasSizeRegistered(VALID_READ_IDENTIFIER,
				INVALID_READ_WIDTH, INVALID_READ_HEIGHT));

		// test not existing
		assertFalse(IMAGE_DB.imageHasSizeRegistered(INVALID_READ_IDENTIFIER,
				VALID_WIDTH_2, VALID_HEIGHT_2));
	}

	@Test
	public void testGetSmallestImagePath() {
		// test smaller image
		String path2 = IMAGE_DB.getSmallestImagePath(VALID_READ_IDENTIFIER,
				VALID_WIDTH_1, VALID_HEIGHT_1);
		assertEquals(VALID_IMAGE_PATH_2, path2);

		// test image with same size
		path2 = IMAGE_DB.getSmallestImagePath(VALID_READ_IDENTIFIER,
				VALID_WIDTH_2, VALID_HEIGHT_2);
		assertEquals(VALID_IMAGE_PATH_2, path2);

		// test image too large/not existing
		assertNull(IMAGE_DB.getSmallestImagePath(VALID_READ_IDENTIFIER,
				INVALID_READ_WIDTH, INVALID_READ_HEIGHT));
		assertNull(IMAGE_DB.getSmallestImagePath(INVALID_READ_IDENTIFIER,
				VALID_WIDTH_1, VALID_HEIGHT_1));
	}

	@Test
	public void testGetRegisteredImagePaths() {
		// test one path registered
		String[] paths = IMAGE_DB
				.getRegisteredImagePaths(VALID_READ_IDENTIFIER);
		assertNotNull(paths);
		assertEquals(1, paths.length);
		assertEquals(VALID_IMAGE_PATH_2, paths[0]);

		// test no paths registered
		this.testAddDatabaseEntry();
		paths = IMAGE_DB.getRegisteredImagePaths(VALID_CREATE_IDENTIFIER);
		assertNotNull(paths);
		assertEquals(0, paths.length);
	}

	@Override
	@Test
	public void testDeleteDatabaseEntry() {
		// deletion successful only once
		assertTrue(IMAGE_DB.deleteDatabaseEntry(VALID_READ_IDENTIFIER));
		assertFalse(IMAGE_DB.deleteDatabaseEntry(VALID_READ_IDENTIFIER));
	}

	@Test
	public void testDeletedGetRegisteredImagePath() {
		this.testDeleteDatabaseEntry();

		assertFalse(IMAGE_DB.imageHasSizeRegistered(VALID_READ_IDENTIFIER,
				VALID_WIDTH_2, VALID_HEIGHT_2));
	}

	@Test
	public void testDeletedGetRegisteredImagePaths() {
		this.testDeleteDatabaseEntry();

		final String[] paths = IMAGE_DB
				.getRegisteredImagePaths(VALID_READ_IDENTIFIER);
		assertNull(paths);
	}

}