package org.bladerunnerjs.core.plugin.bundler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.core.plugin.VirtualProxyPlugin;
import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public class VirtualProxyBundlerPlugin extends VirtualProxyPlugin implements BundlerPlugin {
	private BundlerPlugin bundlerPlugin;
	
	public VirtualProxyBundlerPlugin(BundlerPlugin bundlerPlugin) {
		super(bundlerPlugin);
		this.bundlerPlugin = bundlerPlugin;
	}
	
	@Override
	public String getTagName() {
		return bundlerPlugin.getTagName();
	}
	
	@Override
	public String getMimeType() {
		return bundlerPlugin.getMimeType();
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		initializePlugin();
		bundlerPlugin.writeDevTagContent(tagAttributes, bundleSet, locale, writer);
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		initializePlugin();
		writeProdTagContent(tagAttributes, bundleSet, locale, writer);
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		initializePlugin();
		return bundlerPlugin.getContentPathParser();
	}
	
	@Override
	public void writeContent(ParsedContentPath request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		initializePlugin();
		bundlerPlugin.writeContent(request, bundleSet, os);
	}
	
	@Override
	public List<String> getValidDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		initializePlugin();
		return bundlerPlugin.getValidDevRequestPaths(bundleSet, locale);
	}
	
	@Override
	public List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		initializePlugin();
		return bundlerPlugin.getValidProdRequestPaths(bundleSet, locale);
	}

	@Override
	public List<SourceFile> getSourceFiles(AssetLocation assetLocation) {
		initializePlugin();
		return bundlerPlugin.getSourceFiles(assetLocation);
	}

	@Override
	public List<LinkedAssetFile> getLinkedResourceFiles(AssetLocation assetLocation) {
		initializePlugin();
		return bundlerPlugin.getLinkedResourceFiles(assetLocation);
	}

	@Override
	public List<AssetFile> getResourceFiles(AssetLocation assetLocation) {
		initializePlugin();
		return bundlerPlugin.getResourceFiles(assetLocation);
	}
}
