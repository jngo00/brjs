package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.core.plugin.bundler.js.NamespacedJsBundlerPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectUserLibBundling extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib userLib;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			
		app = brjs.app("app1");
			aspect = app.aspect("default");
			
			userLib = app.jsLib("userLib");
	}

	// ----------------------------- U S E R   J S   L I B S --------------------------------
	@Test
	public void aspectBundlesContainUserLibrLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(userLib).hasPackageStyle("src/userLib", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(userLib).hasClass("userLib.Class1")
			.and(aspect).indexPageRefersTo("userLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("userLib.Class1");
	}
	
	@Test
	public void aspectBundlesContainUserLibsIfTheyAreReferencedInAClass() throws Exception {
		given(userLib).hasBeenCreated()
			.and(userLib).hasPackageStyle("src/userLib", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(userLib).hasClass("userLib.Class1")
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).hasPackageStyle("src/mypkg", NamespacedJsBundlerPlugin.JS_STYLE)
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).classRefersTo("mypkg.Class1", "userLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("userLib.Class1");
	}
	
	@Test
	public void aspectBundlesContainUserLibsIfTheyAreRequiredInAClass() throws Exception {
		given(userLib).hasClass("userLib.Class1")
			.and(aspect).indexPageRefersTo("mypkg.Class1")
			.and(aspect).hasClass("mypkg.Class1")
			.and(aspect).classRequires("mypkg.Class1", "userLib.Class1");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("userLib.Class1");
	}
}