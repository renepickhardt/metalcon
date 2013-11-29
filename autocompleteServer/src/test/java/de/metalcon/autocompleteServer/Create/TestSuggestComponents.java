package de.metalcon.autocompleteServer.Create;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

/**
 * This class test the writing operations of the Container class.
 * 
 * @author Christian Schowalter
 * 
 */
public class TestSuggestComponents {

	SuggestionComponents testComponents = new SuggestionComponents();

	File testFile = new File("TestFile.save");

	@Before
	public void initializeTest() {
		this.testComponents
				.setIndexName(ProtocolTestConstants.VALID_SUGGESTION_INDEX);
		this.testComponents.setKey(ProtocolTestConstants.VALID_SUGGESTION_KEY);
		this.testComponents
				.setSuggestString(ProtocolTestConstants.VALID_SUGGESTION_STRING);
		this.testComponents.setWeight(Integer
				.parseInt(ProtocolTestConstants.VALID_SUGGESTION_WEIGHT));
	}

	@Test
	public void testFileSaving() {

		// new file to write to

		// new object to load stored-to-disc data for comparison with
		// original-data
		SuggestionComponents restoredComponents = null;

		// write file to disc
		this.testComponents.saveToDisc(this.testFile);

		try {
			FileInputStream saveFile = new FileInputStream(this.testFile);
			ObjectInputStream restore = new ObjectInputStream(saveFile);

			restoredComponents = (SuggestionComponents) restore.readObject();
			restore.close();
		} catch (IOException | ClassNotFoundException e1) {

			e1.printStackTrace();

		}
		// testing each component for correct recreation
		assertEquals(this.testComponents.getSuggestString(),
				restoredComponents.getSuggestString());
		assertEquals(this.testComponents.getKey(), restoredComponents.getKey());
		assertEquals(this.testComponents.getIndexName(),
				restoredComponents.getIndexName());
		assertEquals(this.testComponents.getWeight(),
				restoredComponents.getWeight());

	}

	/**
	 * This test contains the actual implementation of the initialization method
	 * as calling the method without a server would otherwise fail.
	 * 
	 * @author Christian Schowalter
	 */
	@Test
	public void testServerInitialization() {
		SuggestTree suggestTree = new SuggestTree(
				ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS);

		HashMap<String, String> imageIndex = new HashMap<String, String>();

		try {
			FileInputStream saveFile = new FileInputStream("TestFile.save");
			ObjectInputStream restore = new ObjectInputStream(saveFile);

			while (true) {
				try {
					SuggestionComponents suggestTreeEntry = (SuggestionComponents) restore
							.readObject();
					suggestTree.put(suggestTreeEntry.getSuggestString(),
							suggestTreeEntry.getWeight(),
							suggestTreeEntry.getKey());
					imageIndex.put(suggestTreeEntry.getKey(),
							suggestTreeEntry.getImageBase64());
				} catch (EOFException e) {
					restore.close();
					break;
				}

			}

		} catch (IOException | ClassNotFoundException e1) {

			e1.printStackTrace();
			fail("Data could not be restored");

		}
		assertEquals(suggestTree.getBestSuggestions("test").getSuggestion(0),
				"testTerm");
		this.testFile.deleteOnExit();
	}

}
