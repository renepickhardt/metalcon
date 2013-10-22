/**
 * 
 */
package de.metalcon.storage;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author kunzejo
 * 
 */
public class LevelDBHandlerTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LevelDBHandler.initialize("/dev/shm/levelDB");
	}

	/**
	 * Test method for
	 * {@link de.metalcon.storage.LevelDBHandler#setAdd(long, long)}.
	 */
	@Test
	public void testAddToSet() {
		LevelDBHandler handler = new LevelDBHandler("prefix");
		handler.setAdd(1, 2);

		if (!handler.setContainsElement(1, 2)) {
			fail("Element not in the list after addToSet was called");
		}
	}

}
