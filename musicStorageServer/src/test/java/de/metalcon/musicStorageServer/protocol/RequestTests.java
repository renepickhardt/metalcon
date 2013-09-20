package de.metalcon.musicStorageServer.protocol;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CreateRequestTest.class, ReadMusicItemRequestTest.class,
		ReadMetaDataRequestTest.class, DeleteRequestTest.class,
		UpdateRequestTest.class })
public class RequestTests {

}