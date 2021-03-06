package org.bladerunnerjs.core.plugin;

import static org.junit.Assert.*;
import static org.bladerunnerjs.core.plugin.PluginLoader.Messages.*;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.core.plugin.PluginLoader;
import org.bladerunnerjs.core.plugin.utilClasses.AnotherTestInterface;
import org.bladerunnerjs.core.plugin.utilClasses.ClassImplementingNestedInterface;
import org.bladerunnerjs.core.plugin.utilClasses.MyTestInterface;
import org.bladerunnerjs.core.plugin.utilClasses.MyTestInterfaceImplementer;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.junit.Before;
import org.junit.Test;



public class PluginLoaderTest
{

	private BRJS brjs;
	private LogMessageStore logStore;

	@Before
	public void setup()
	{
		logStore = new LogMessageStore(true);
		brjs = BRJSTestFactory.createBRJS(new File("src/test/resources/BRJSTest"), new TestLoggerFactory(logStore));
	}

	@Test
	public void testInstancesOfInterfaceAreReturned()
	{
		List<MyTestInterface> newObjectList = PluginLoader.createPluginsOfType(brjs, MyTestInterface.class);
		assertEquals("List should contain 1 item", 1, newObjectList.size());
		assertEquals("List item should be of type MyTestInterfaceImplementer", MyTestInterfaceImplementer.class, newObjectList.get(0).getClass());
	}
	
	@Test
	public void testErroMessageIsPrintedIfConstructorCantBeFound()
	{
		PluginLoader.createPluginsOfType(brjs, MyTestInterface.class);
		logStore.verifyErrorLogMessage(CANNOT_CREATE_INSTANCE_LOG_MSG, "org.bladerunnerjs.core.plugin.utilClasses.MyTestInterfaceImplementerBadConstructor");
	}
	
	@Test
	public void testInterfacesExtendingAnotherArentLoaded()
	{
		PluginLoader.createPluginsOfType(brjs, AnotherTestInterface.class);
		/* make sure we haven't tried to instantiate the sub interface InterfaceExtendingMyTestInterface */
		// TODO: talk to Andy about the testing of logs inside this test -- do we really want to be testing behavior outside of our spec tests
		//logStore.verifyNoUnhandledMessage();
	}
	
	@Test
	public void classesImplementingNestedInterfacesAreCreated()
	{
		PluginLoader.createPluginsOfType(brjs, AnotherTestInterface.class);
		List<AnotherTestInterface> newObjectList = PluginLoader.createPluginsOfType(brjs, AnotherTestInterface.class);
		assertEquals("List should contain 1 item", 1, newObjectList.size());
		assertEquals("List item should be of type ClassImplementingNestedInterface", ClassImplementingNestedInterface.class, newObjectList.get(0).getClass());
	}
	
}
