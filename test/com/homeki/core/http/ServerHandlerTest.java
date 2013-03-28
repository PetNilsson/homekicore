package com.homeki.core.http;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.homeki.core.TestUtil;


public class ServerHandlerTest {
	public class JsonServerInfo {
		public Long uptimeMs;
		public Long timeMs;
		public String time;
		public String version;
		public String name;
		public Double locationLongitude;
		public Double locationLatitude;
	}
	
	@Test
	public void testGet() throws Exception {
		JsonServerInfo jinfo = TestUtil.sendGetAndParseAsJson("/server/get", JsonServerInfo.class);
		assertEquals("Homeki", jinfo.name);
		TestUtil.getDateTimeFormat().parse(jinfo.time);
		assertTrue(jinfo.timeMs > 0);
		assertTrue(jinfo.uptimeMs > 0);
		assertTrue(jinfo.version.length() > 0);
	}
	
	@Test(dependsOnMethods="testGet")
	public void testSet() throws Exception {
		JsonServerInfo get = TestUtil.sendGetAndParseAsJson("/server/get", JsonServerInfo.class);
		assertEquals("Homeki", get.name);
		
		JsonServerInfo set = new JsonServerInfo();
		assertEquals(TestUtil.sendPost("/server/set", set).statusCode, 400);
		set.name = "";
		assertEquals(TestUtil.sendPost("/server/set", set).statusCode, 400);
		
		set.name = "MyServer";
		set.locationLongitude = 12.03;
		set.locationLatitude = 15.05;
		assertEquals(TestUtil.sendPost("/server/set", set).statusCode, 200);
		
		get = TestUtil.sendGetAndParseAsJson("/server/get", JsonServerInfo.class);
		assertEquals("MyServer", get.name);
		assertEquals(get.locationLongitude, 12.03, 0.01);
		assertEquals(get.locationLatitude, 15.05, 0.01);
	}
}
