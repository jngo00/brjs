package org.bladerunnerjs.model;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


public class AssetLocationUtility
{
	private final Map<String, Asset> assetFiles = new HashMap<>();
	private final AssetLocation assetLocation;
	
	public AssetLocationUtility(AssetLocation assetLocation) {
		this.assetLocation = assetLocation;
	}
	
	@SuppressWarnings("unchecked")
	public <A extends Asset> A obtainAsset(Class<? extends A> assetClass, File dir, String assetName) throws AssetFileInstantationException {
		String absolutePath = new File(dir, assetName).getAbsolutePath();
		A asset;
		
		if(assetFiles.containsKey(absolutePath)) {
			asset = (A) assetFiles.get(absolutePath);
		}
		else {
			asset = createAssetInstance(assetClass, dir, assetName);
			assetFiles.put(absolutePath, asset);
		}
		
		return asset;
	}
	
	@SuppressWarnings("unchecked")
	private <A extends Asset> A createAssetInstance(Class<? extends Asset> assetClass, File dir, String assetName) throws AssetFileInstantationException
	{
		try
		{
			Constructor<? extends Asset> ctor = assetClass.getConstructor();
			A assetFile = (A) ctor.newInstance();
			assetFile.initialize(assetLocation, dir, assetName);
			
			return assetFile;
		}
		catch(AssetFileInstantationException ex) {
			throw ex;
		}
		catch (SecurityException ex)
		{
			throw new RuntimeException(ex);
		}
		catch (Exception ex)
		{
			throw new AssetFileInstantationException(ex, assetClass);
		}		
	}
}
