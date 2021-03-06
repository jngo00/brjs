package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasDefinitionsFile;
import org.bladerunnerjs.model.aliasing.AliasName;
import org.bladerunnerjs.model.aliasing.AliasUtility;
import org.bladerunnerjs.model.aliasing.AliasesFile;
import org.bladerunnerjs.model.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.model.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;

public abstract class AbstractBundlableNode extends AbstractAssetContainer implements BundlableNode {
	private AliasesFile aliasesFile;
	
	public AbstractBundlableNode(RootNode rootNode, File dir) {
		super(rootNode, dir);
	}
	
	public abstract List<LinkedAssetFile> getSeedFiles();
	
	@Override
	public List<LinkedAssetFile> seedFiles() {
		List<LinkedAssetFile> seedFiles = new ArrayList<>();
		
		seedFiles.addAll(getSeedFiles());
		seedFiles.addAll(resources().seedResources());
		
		return seedFiles;
	}
	
	@Override
	public AliasesFile aliasesFile() {
		if(aliasesFile == null) {
			aliasesFile = new AliasesFile(dir(), "resources/aliases.xml");
		}
		
		return aliasesFile;
	}
	
	@Override
	public SourceFile getSourceFile(String requirePath) throws RequirePathException {
		SourceFile sourceFile = null;
		
		for(AssetContainer assetContainer : getAssetContainers()) {
			SourceFile locationSourceFile = assetContainer.sourceFile(requirePath);
			
			if(locationSourceFile != null) {
				if(sourceFile == null) {
					sourceFile = locationSourceFile;
				}
				else {
					throw new AmbiguousRequirePathException("'" + sourceFile.getUnderlyingFile().getPath() + "' and '" +
						locationSourceFile.getUnderlyingFile().getPath() + "' source files both available via require path '" +
						sourceFile.getRequirePath() + "'.");
				}
			}
		}
		
		if(sourceFile == null) {
			throw new UnresolvableRequirePathException(requirePath);
		}
		
		return sourceFile;
	}
	
	@Override
	public AliasDefinition getAlias(AliasName aliasName) throws UnresolvableAliasException, AmbiguousAliasException, BundlerFileProcessingException {
		return AliasUtility.getAlias(aliasName, aliasesFile, getAliasDefinitionFiles());
	}
	
	@Override
	public BundleSet getBundleSet() throws ModelOperationException {
		return BundleSetCreator.createBundleSet(this);
	}
	
	private List<AliasDefinitionsFile> getAliasDefinitionFiles() {
		List<AliasDefinitionsFile> aliasDefinitionFiles = new ArrayList<>();
		
		for(AssetContainer assetContainer : getAssetContainers()) {
			for(AssetLocation assetLocation : assetContainer.getAllAssetLocations()) {
				aliasDefinitionFiles.add(assetLocation.aliasDefinitionsFile());
			}
		}
		
		return aliasDefinitionFiles;
	}
}
