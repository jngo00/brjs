package org.bladerunnerjs.model;

import org.bladerunnerjs.model.conf.YamlTestRunnerConf;
import org.bladerunnerjs.model.exception.ConfigException;

public class TestRunnerConf extends ConfFile<YamlTestRunnerConf> {
	public TestRunnerConf(BRJS brjs) throws ConfigException {
		super(brjs, YamlTestRunnerConf.class, brjs.file("conf/test-runner.conf"));
	}
	
	public String getDefaultBrowser() throws ConfigException {
		reloadConf();
		return conf.defaultBrowser;
	}
	
	public void setDefaultBrowser(String defaultBrowser) throws ConfigException {
		conf.defaultBrowser = defaultBrowser;
		conf.verify();
	}
}
