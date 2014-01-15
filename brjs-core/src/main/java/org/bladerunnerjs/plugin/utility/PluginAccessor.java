package org.bladerunnerjs.plugin.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.MinifierPlugin;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.plugin.TagHandlerPlugin;
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRJSConformantAssetPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.aliasing.AliasingContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.brjsthirdparty.BRJSThirdpartyContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsContentPlugin;
import org.bladerunnerjs.plugin.utility.command.CommandList;

public class PluginAccessor {
	private final PluginLocator pluginLocator;
	private final CommandList commandList;
	
	public PluginAccessor(BRJS brjs, PluginLocator pluginLocator) {
		this.pluginLocator = pluginLocator;
		commandList = new CommandList(brjs, pluginLocator.getCommandPlugins());
	}
	
	public CommandList commandList() {
		return commandList;
	}
	
	public List<CommandPlugin> commands() {
		return commandList.getPluginCommands();
	}
	
	public ContentPlugin contentProvider(BladerunnerUri requestUri) {
		String requestPrefix = (requestUri.logicalPath.indexOf('/') == -1) ? requestUri.logicalPath : requestUri.logicalPath.substring(0, requestUri.logicalPath.indexOf('/'));
		
		return contentProvider(requestPrefix);
	}
	
	public ContentPlugin contentProvider(String requestPrefix) {
		ContentPlugin contentProvider = null;
		
		for (ContentPlugin nextContentPlugin : contentProviders()) {
			if(nextContentPlugin.getRequestPrefix().equals(requestPrefix)) {
				contentProvider = nextContentPlugin;
				break;
			}
		}
		
		return contentProvider;
	}
	
	public List<ContentPlugin> contentProviders() {
		List<ContentPlugin> contentProviders = pluginLocator.getContentPlugins();
		
		orderContentPlugins(contentProviders);
		
		return contentProviders;
	}

	public List<ContentPlugin> contentProviders(String groupName) {
		List<ContentPlugin> contentProviders = new ArrayList<>();
		
		for (ContentPlugin contentPlugin : contentProviders()) {
			if (contentPlugin.getGroupName().equals(groupName)) {
				contentProviders.add(contentPlugin);
			}
		}
		
		return contentProviders;
	}
	
	public List<TagHandlerPlugin> tagHandlers() {
		return pluginLocator.getTagHandlerPlugins();
	}
	
	public List<TagHandlerPlugin> tagHandlers(String groupName) {
		List<TagHandlerPlugin> tagHandlerPlugins = new ArrayList<>();
		
		for (TagHandlerPlugin tagHandlerPlugin : tagHandlers()) {
			if (tagHandlerPlugin.getGroupName().equals(groupName)) {
				tagHandlerPlugins.add(tagHandlerPlugin);
			}
		}
		
		return tagHandlerPlugins;
	}
	
	public List<MinifierPlugin> minifiers() {
		return pluginLocator.getMinifierPlugins();
	}
	
	public MinifierPlugin minifier(String minifierSetting) {
		List<String> validMinificationSettings = new ArrayList<String>();
		MinifierPlugin pluginForMinifierSetting = null;
		
		for (MinifierPlugin minifierPlugin : minifiers()) {
			for (String setting : minifierPlugin.getSettingNames()) {
				validMinificationSettings.add(setting);
				
				if (setting.equals(minifierSetting)) {
					pluginForMinifierSetting = (pluginForMinifierSetting == null) ? minifierPlugin : pluginForMinifierSetting;
				}
			}
		}
		
		if (pluginForMinifierSetting != null) {
			return pluginForMinifierSetting;
		}
		
		throw new RuntimeException("No minifier plugin for minifier setting '" + minifierSetting + "'. Valid settings are: "
			+ StringUtils.join(validMinificationSettings, ", "));
	}
	
	public List<ModelObserverPlugin> modelObservers() {
		return pluginLocator.getModelObserverPlugins();
	}
	
	public List<AssetPlugin> assetProducers() {
		List<AssetPlugin> plugins = pluginLocator.getAssetPlugins();
		
		orderAssetPlugins(plugins);
		
		return plugins;
	}
	
	// TODO: it's becoming more and more obvious that we need a proper ordering mechanism for bundler content plug-ins, so external plug-in developers can have their plug-ins correctly ordered too
	private void orderContentPlugins(List<ContentPlugin> bundlerContentProviders) {
		Collections.sort(bundlerContentProviders, new Comparator<ContentPlugin>() {
			@Override
			public int compare(ContentPlugin contentPlugin1, ContentPlugin contentPlugin2) {
				return score(contentPlugin1) - score(contentPlugin2);
			}
			
			private int score(ContentPlugin contentPlugin) {
				int score = 0;
				
				if(contentPlugin.instanceOf(BRJSThirdpartyContentPlugin.class)) {
					score = -1;
				}
				else if(contentPlugin.instanceOf(NamespacedJsContentPlugin.class)) {
					score = 1;
				}
				else if(contentPlugin.instanceOf(AliasingContentPlugin.class)) {
					score = 2;
				}
				
				return score;
			}
		});
	}
	
	private void orderAssetPlugins(List<AssetPlugin> plugins) {
		Collections.sort(plugins, new Comparator<AssetPlugin>() {
			@Override
			public int compare(AssetPlugin assetPlugin1, AssetPlugin assetPlugin2) {
				return score(assetPlugin1) - score(assetPlugin2);
			}
			
			private int score(AssetPlugin assetPlugin) {
				return (assetPlugin.instanceOf(BRJSConformantAssetPlugin.class)) ? 1 : 0;
			}
		});
	}
}
