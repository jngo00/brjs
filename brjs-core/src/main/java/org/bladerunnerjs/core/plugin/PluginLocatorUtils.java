package org.bladerunnerjs.core.plugin;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.model.BRJS;


public class PluginLocatorUtils
{

	public class Messages {
		public static final String INIT_PLGUIN_ERROR_MSG = "error initializing the plugin %s, the error was: '%s'";
	}
	
	public static List<? extends Plugin> setBRJSForPlugins(BRJS brjs, List<? extends Plugin> plugins)
	{
		for (Plugin p : plugins)
		{
			try 
			{
				p.setBRJS(brjs);
			} 
			catch (Throwable ex)
			{
				brjs.logger(LoggerType.UTIL, PluginLocatorUtils.class).error(Messages.INIT_PLGUIN_ERROR_MSG, p.getClass().getCanonicalName(), ExceptionUtils.getStackTrace(ex));
			}
		}
		return plugins;
	}
	
}
