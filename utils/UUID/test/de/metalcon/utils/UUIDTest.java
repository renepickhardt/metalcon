package de.metalcon.utils;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UUIDTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void smokeTest(){
		for (int i = 0;i<1000000;i++){
	    	long tmp = (long) (Math.random()*Long.MAX_VALUE);
	    	String s = UUIDConverter.serialize(tmp);
	    	long res = UUIDConverter.deserialize(s);
	    	assertEquals(tmp, res);
    	}	
	}
	
//	@Test
//	public void testUUID() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testUUIDLong() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testUUIDString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testToString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetUUID() {
//		fail("Not yet implemented");
//	}

}
