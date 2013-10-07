package de.metalcon.like;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashMap;

import org.junit.Test;

public class CommonsTest {

	@Test
	public void deserializeCommonsFile() {
		CommonsFileRaw raw = null;
		try ( // resource Statement -> streams will be closed automatically
		InputStream file = new FileInputStream("commonsDB/2_commons");
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream(buffer);) {

			// deserialize the Map
			CommonsFileRaw fileContent = (CommonsFileRaw) input.readObject();
			raw = fileContent;

			for (long concept : raw.commonsMap.keySet()) {
				System.out.println(concept);
				for (long friend : raw.commonsMap.get(concept)) {
					if (friend != 0 && friend < 10)
						System.out.println("\t" + friend);
				}
			}
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			raw = new CommonsFileRaw();
			raw.lastUpdateTS = 0;
			raw.commonsMap = new HashMap<Long, long[]>();
		}
	}

	@Test
	public void testCommons() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCommonNodes() {
		fail("Not yet implemented");
	}

	@Test
	public void testFreeMemory() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testFriendAdded() {
		fail("Not yet implemented");
	}

	@Test
	public void testFriendRemoved() {
		fail("Not yet implemented");
	}

}
