package org.bladerunnerjs.specutil.engine;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.specutil.AppVerifier;
import org.bladerunnerjs.specutil.AspectVerifier;
import org.bladerunnerjs.specutil.BRJSVerifier;
import org.bladerunnerjs.specutil.BladeVerifier;
import org.bladerunnerjs.specutil.BladesetVerifier;
import org.bladerunnerjs.specutil.DirNodeVerifier;
import org.bladerunnerjs.specutil.DirectoryVerifier;
import org.bladerunnerjs.specutil.JsLibVerifier;
import org.bladerunnerjs.specutil.LoggerVerifier;
import org.bladerunnerjs.specutil.NamedDirNodeVerifier;
import org.bladerunnerjs.specutil.NamedNodeVerifier;
import org.bladerunnerjs.specutil.WorkbenchVerifier;
import org.bladerunnerjs.testing.utility.LogMessageStore;


public class VerifierChainer {
	private final SpecTest modelTest;
	
	public VerifierChainer(SpecTest modelTest) {
		this.modelTest = modelTest;
	}
	
	public ExceptionsVerifier and(List<Throwable> exceptions) { return new ExceptionsVerifier(exceptions); }
	public NodeObserverVerifier and(EventObserver observer) { return new NodeObserverVerifier(modelTest, observer); }
	public NamedNodeVerifier and(NamedNode namedDirNode) { return new NamedNodeVerifier(modelTest, namedDirNode); }
	public BRJSVerifier and(BRJS brjs) { return new BRJSVerifier(modelTest, brjs); }
	public AppVerifier and(App app) { return new AppVerifier(modelTest, app); }
	public AspectVerifier and(Aspect aspect) { return new AspectVerifier(modelTest, aspect); }
	public BladeVerifier and(Blade blade) { return new BladeVerifier(modelTest, blade); }
	public BladesetVerifier and(Bladeset bladeset) { return new BladesetVerifier(modelTest, bladeset); }
	public WorkbenchVerifier and(Workbench workbench) { return new WorkbenchVerifier(modelTest, workbench); }
	public JsLibVerifier and(JsLib jsLib) { return new JsLibVerifier(modelTest, jsLib); }
	public DirNodeVerifier and(DirNode dirNode) { return new DirNodeVerifier(modelTest, dirNode); }
	public NamedDirNodeVerifier and(NamedDirNode namedDirNode) { return new NamedDirNodeVerifier(modelTest, namedDirNode); }
	public LoggerVerifier and(LogMessageStore logStore) { return new LoggerVerifier(modelTest, logStore); }
	public ConsoleWriterVerifier and(ConsoleMessageStore consoleMessageStore) { return new ConsoleWriterVerifier(modelTest, consoleMessageStore); }
	public AppServerVerifier and(ApplicationServer appServer) { return new AppServerVerifier(modelTest, appServer); }
	public AppConfVerifier and(AppConf appConf) { return new AppConfVerifier(appConf); }
	public DirectoryVerifier and(File dir) { return new DirectoryVerifier(modelTest, dir); }
	public StringVerifier and(StringBuffer stringBuffer) { return new StringVerifier(modelTest, stringBuffer); }
}
