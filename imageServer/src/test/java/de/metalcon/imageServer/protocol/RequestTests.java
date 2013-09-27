package de.metalcon.imageServer.protocol;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CreateFromUrlTest.class, CreateRequestTest.class,
		CreateWithCroppingRequestTest.class, DeleteRequestTest.class,
		ReadBulkRequestTest.class, ReadOriginalRequestTest.class,
		ReadScaledRequestTest.class, UpdateRequestTest.class })
public class RequestTests {

}