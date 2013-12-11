package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.NameValidator;

public class JsLib extends AbstractAssetContainer implements AssetContainer, NamedNode
{
	private final NodeItem<DeepAssetLocation> assetLocationNodeItem = new NodeItem<>(DeepAssetLocation.class, "");
	private String name;
	private JsLibConf libConf;
	private Node parent;
	
	public JsLib(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, dir);
		init(rootNode, parent, dir);
		this.name = name;
		this.parent = parent;
	}
	
	public JsLib(RootNode rootNode, Node parent, File dir)
	{
		// TODO: can we avoid having to have a null name for a NamedNode that is available as a single item through the model
		this(rootNode, parent, dir, null);
	}
	
	public static NodeItem<JsLib> createSdkNodeItem()
	{
		return new NodeItem<>(JsLib.class, "sdk/libs/javascript/caplin");
	}
	
	public static NodeMap<JsLib> createAppNodeSet()
	{
		NodeMap<JsLib> appNodeSet = new NodeMap<>(JsLib.class, "libs", null);
		
		return appNodeSet;
	}
	
	public static NodeMap<JsLib> createSdkNonBladeRunnerLibNodeSet()
	{
		return new NodeMap<>(JsLib.class, "sdk/libs/javascript/thirdparty", null);
	}
	
	public static NodeMap<JsLib> createAppNonBladeRunnerLibNodeSet()
	{
		return new NodeMap<>(JsLib.class, "thirdparty-libraries", null);
	}
	
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		try {
			transformations.put("libns", libConf().getLibNamespace());
		}
		catch(ConfigException e) {
			throw new ModelUpdateException(e);
		}
	}
	
	@Override
	public String getName()
	{
		if (name != null)
		{
			return name;			
		}
		return dir().getName();
	}
	
	@Override
	public boolean isValidName()
	{
		return NameValidator.isValidDirectoryName(name);
	}
	
	@Override
	public void assertValidName() throws InvalidNameException
	{
		NameValidator.assertValidDirectoryName(this);
	}
	
	@Override
	public Node parentNode()
	{
		return parent;
	}
	
	@Override
	public App getApp()
	{
		if (parent == root())
		{
			return root().systemApp("SDK");			
		}
		return super.getApp();
	}
	
	public AssetLocation getRootAssetLocation()
	{
		return item(assetLocationNodeItem);
	}
	
	public JsLibConf libConf() throws ConfigException
	{
		if(libConf == null) {
			libConf = new JsLibConf(this);
		}
		
		return libConf ;
	}
	
	public void populate(String libNamespace) throws InvalidNameException, ModelUpdateException
	{
		NameValidator.assertValidRootPackageName(this, libNamespace);
		
		try {
			libConf().setLibNamespace(libNamespace);
			populate();
			libConf().write();
		}
		catch (ConfigException e) {
			if(e.getCause() instanceof InvalidNameException) {
				throw (InvalidNameException) e.getCause();
			}
			else {
				throw new ModelUpdateException(e);
			}
		}
	}
	
	@Override
	public String namespace() {
		return getName();
	}
	

	@Override
	public String toString()
	{
		return super.toString()+" - "+getName();
	}
	
	@Override
	public String getTemplateName()
	{
		return BRJSNodeHelper.getTemplateName(this);
	}
	
	@Override
	public List<AssetLocation> assetLocations() {
		List<AssetLocation> assetLocations = new ArrayList<>();
		assetLocations.add(getRootAssetLocation());
		assetLocations.addAll( super.assetLocations() );
		return assetLocations;
	}
}
