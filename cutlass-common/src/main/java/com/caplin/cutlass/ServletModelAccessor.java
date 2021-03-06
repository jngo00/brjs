package com.caplin.cutlass;

import java.io.File;

import javax.servlet.ServletContext;

import org.bladerunnerjs.core.log.NullLogConfigurator;
import org.bladerunnerjs.model.BRJS;
import com.caplin.cutlass.BRJSAccessor;

public class ServletModelAccessor {
	private static BRJS model;
	
	public static synchronized BRJS initializeModel(ServletContext servletContext) {
		return initializeModel( new File(servletContext.getRealPath("/")) );
	}
	
	public static synchronized BRJS initializeModel(File path) {
		if(model == null) {
			model = BRJSAccessor.initialize( new BRJS(path, new NullLogConfigurator()) );
		}
		
		return model;
	}
	
	// for testing purposes only
	public static void reset() {
		model = null;
	}
}