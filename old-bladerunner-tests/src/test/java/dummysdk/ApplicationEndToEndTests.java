package dummysdk;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.SDK_DIR;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.appserver.AppDeploymentObserver;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.model.sinbin.CutlassConfig;
import org.bladerunnerjs.model.utility.FileUtility;
import org.bladerunnerjs.model.utility.ServerUtility;
import org.bladerunnerjs.testing.utility.WebappTester;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;
import com.caplin.cutlass.ServletModelAccessor;

public class ApplicationEndToEndTests
{

	private static final File INSTALL_ROOT = new File("src/test/resources/MockInstall");

	private static int HTTP_PORT = ServerUtility.getTestPort();
	private static final String BASE_URL = "http://localhost:"+HTTP_PORT;
	private static final String APP1_URL = BASE_URL+"/test-app1";
//	private static final String APP2_URL = BASE_URL+"/test-app2";
	private static final String AUTH_APP_URL = BASE_URL+"/app-with-authentication";
	private static final String APP3_URL = BASE_URL+"/test-app3";
	private static final String APPS = CutlassConfig.APPLICATIONS_DIR;
	
	
	private static ApplicationServer appServer;
	private static WebappTester tester;

	private static File tempSdkInstall;

	private static BRJS brjs;
	
	@Before
	public void setup() throws Exception 
	{
		tempSdkInstall = FileUtility.createTemporarySdkInstall(INSTALL_ROOT).getParentFile();
		brjs = BRJSAccessor.initialize(BRJSTestFactory.createBRJS(tempSdkInstall));
		
		ModelObserverPlugin appDeploymentObserver = new AppDeploymentObserver();
		appDeploymentObserver.setBRJS(brjs);
		
		brjs.bladerunnerConf().setJettyPort(HTTP_PORT);	
		brjs.appJars().create();
		appServer = brjs.applicationServer(HTTP_PORT);
		appServer.start();
		ServletModelAccessor.reset();
		tester = new WebappTester(tempSdkInstall, 10000, 10000);
	}
	
	@After
	public void teardown() throws Exception
	{
		appServer.stop();
		assertFalse( "port is still bound!", ServerUtility.isPortBound(HTTP_PORT) );
	}
	
	/* start of tests */
	
	@Test
	public void testRequestToRootUrlRedirectsToDashboard() throws Exception 
	{
		tester.whenRequestMadeTo(BASE_URL)
			.statusCodeIs(200)
			.responseIsContentsOfFile(SDK_DIR + "/system-applications/dashboard/default-aspect/index.html");
	}
	
	@Test
	public void testRequestForRootIsSameAsDashboard() throws Exception 
	{
		tester.whenRequestMadeTo(BASE_URL)
			.sameAsRequestFor(BASE_URL+"/dashboard");
	}
	
	@Test
	public void testBladerunnerDoesntBreakAuthentication() throws Exception 
	{
		tester.whenRequestMadeTo(AUTH_APP_URL)
			.statusCodeIs(200)
			.sameAsRequestFor(AUTH_APP_URL+"/login/index.html");
	}
	
	@Test
	public void testThirdpartyBundleFilterSetsCorrectContentType() throws Exception
	{
		tester.whenRequestMadeTo(APP1_URL+"/v_1234/thirdparty-libraries/someLib/file.txt")
		.statusCodeIs(200).contentTypeIs("text/plain");

		tester.whenRequestMadeTo(APP1_URL+"/v_1234/thirdparty-libraries/someLib/file.xml")
		.statusCodeIs(200).contentTypeIs("application/xml");
		
		tester.whenRequestMadeTo(APP1_URL+"/v_1234/thirdparty-libraries/someLib/file.js")
		.statusCodeIs(200).contentTypeIs("text/javascript");
	}
	
	@Test
	public void testRequestToJsBundle() throws Exception 
	{
		tester.whenRequestMadeTo(APP1_URL+"/js/js.bundle")
			.statusCodeIs(200).contentTypeIs("text/javascript")
			.responseIsConcatenationOfFiles(new String[]{
					APPS + "/test-app1/default-aspect/src/section/xmlDepend.js",
					SDK_DIR + "/libs/javascript/caplin/src/caplin/bootstrap.js",
					SDK_DIR + "/libs/javascript/thirdparty/jquery/jQuery.js",
					SDK_DIR + "/libs/javascript/thirdparty/knockout/knockout.js",
					APPS + "/test-app1/default-aspect/src/section/app/main1.js", 
					APPS + "/test-app1/default-aspect/src/section/app/main2.js", 
					APPS + "/test-app1/a-bladeset/src/section/a/app/bladeset1.js", 
					APPS + "/test-app1/a-bladeset/src/section/a/app/bladeset2.js", 
					APPS + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade1.js", 
					APPS + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade2.js",
					APPS + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/xmlDepend.js",
					APPS + "/test-app1/a-bladeset/src/section/a/xmlDepend.js"});
	}
	
	@Test
	public void testRequestToJsBundleWithVersionString() throws Exception 
	{
		tester.whenRequestMadeTo(APP1_URL+"/default-aspect/v_1234/js/js.bundle")
		.statusCodeIs(200).contentTypeIs("text/javascript")
		.responseIsConcatenationOfFiles(new String[]{
				APPS + "/test-app1/default-aspect/src/section/xmlDepend.js",
				SDK_DIR + "/libs/javascript/caplin/src/caplin/bootstrap.js",
				SDK_DIR + "/libs/javascript/thirdparty/jquery/jQuery.js",
				SDK_DIR + "/libs/javascript/thirdparty/knockout/knockout.js",
				APPS + "/test-app1/default-aspect/src/section/app/main1.js", 
				APPS + "/test-app1/default-aspect/src/section/app/main2.js", 
				APPS + "/test-app1/a-bladeset/src/section/a/app/bladeset1.js", 
				APPS + "/test-app1/a-bladeset/src/section/a/app/bladeset2.js", 
				APPS + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade1.js", 
				APPS + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade2.js",
				APPS + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/xmlDepend.js",
				APPS + "/test-app1/a-bladeset/src/section/a/xmlDepend.js"})
		.sameAsRequestFor(APP1_URL+"/js/js.bundle");
	}
	
	@Test
	public void testRequestDoesnotContainNamespaceFromOtherApp() throws Exception 
	{
		tester.whenRequestMadeTo(APP3_URL+"/js/js.bundle")
			.statusCodeIs(200).contentTypeIs("text/javascript")
			.responseDoesntContain("section");
	}
	
	@Test
	public void testNewContextsCanBeAddedAfterServerHasStarted() throws Exception
	{		
		tester.whenRequestMadeTo(APP1_URL)
		.statusCodeIs(200);

		tester.whenRequestMadeTo(BASE_URL+"/newly-created-app")
		.statusCodeIs(404);
		
		App newApp = brjs.app("newly-created-app");
		newApp.create();
		newApp.file("index.html").createNewFile();
		newApp.deploy();
		
		tester.pollServerForStatusCode(BASE_URL+"/newly-created-app", 200);
	}
	
	@Test
	public void testRequestToJsBundleWhenInsideAnSVNEnvironment() throws Exception 
	{	
		File appsDir =  new File(tempSdkInstall.getParentFile(), CutlassConfig.APPLICATIONS_DIR);
		createDirectoryInAllSubDirs(appsDir, ".svn");	
		
		tester.whenRequestMadeTo(APP1_URL+"/js/js.bundle")
			.statusCodeIs(200).contentTypeIs("text/javascript")
			.responseIsConcatenationOfFiles(new String[]{
					APPS + "/test-app1/default-aspect/src/section/xmlDepend.js",
					SDK_DIR + "/libs/javascript/caplin/src/caplin/bootstrap.js",
					SDK_DIR + "/libs/javascript/thirdparty/jquery/jQuery.js",
					SDK_DIR + "/libs/javascript/thirdparty/knockout/knockout.js",
					APPS + "/test-app1/default-aspect/src/section/app/main1.js", 
					APPS + "/test-app1/default-aspect/src/section/app/main2.js", 
					APPS + "/test-app1/a-bladeset/src/section/a/app/bladeset1.js", 
					APPS + "/test-app1/a-bladeset/src/section/a/app/bladeset2.js", 
					APPS + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade1.js", 
					APPS + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade2.js",
					APPS + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/xmlDepend.js",
					APPS + "/test-app1/a-bladeset/src/section/a/xmlDepend.js"});
	}
	
	private void createDirectoryInAllSubDirs(File rootDir, String dirname)
	{
		File[] subDirs = rootDir.listFiles();
		if (subDirs != null)
		{
			for (File sub : subDirs)
			{
				createDirectoryInAllSubDirs(sub, dirname);
			}
		}
		File newDir = new File(rootDir, dirname);
		newDir.mkdir();
	}
	
}
