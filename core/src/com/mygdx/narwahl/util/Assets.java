package com.mygdx.narwahl.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.narwahl.util.Constants;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class Assets implements Disposable, AssetErrorListener {

    public static final String TAG = Assets.class.getName();
    public static final Assets instance = new Assets();
    private AssetManager assetManager;
    public AssetNarwahl bunny;
    public AssetRock rock;
    public AssetGoldCoin goldCoin;
    public AssetFeather feather;
    
    
    // singleton: prevent instantiation from other classes
    private Assets () {}
    public void init (AssetManager assetManager) {
        this.assetManager = assetManager;
        // set asset manager error handler
        assetManager.setErrorListener(this);
        // load texture atlas
        assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS,
        TextureAtlas.class);
        // start loading assets and wait until finished
        assetManager.finishLoading();
        Gdx.app.debug(TAG, "# of assets loaded: "
          + assetManager.getAssetNames().size);
        for (String a : assetManager.getAssetNames())
            Gdx.app.debug(TAG, "asset: " + a);
      TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);
      // enable texture filtering for pixel smoothing
      for (Texture t : atlas.getTextures()) {
           t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
      }
      // create game resource objects
      bunny = new AssetNarwahl(atlas);
      rock = new AssetRock(atlas);
      goldCoin = new AssetGoldCoin(atlas);
      feather = new AssetFeather(atlas);
  }

  @Override
  public void dispose () {
    assetManager.dispose();
  }
  public void error (String filename, Class type,
    Throwable throwable) {
    Gdx.app.error(TAG, "Couldn't load asset '"
      + filename + "'", (Exception)throwable);
   }
   @Override
   public void error(AssetDescriptor asset, Throwable throwable) {
         Gdx.app.error(TAG, "Couldn't load asset '" +
asset.fileName + "'", (Exception)throwable);
   }
   
   public class AssetNarwahl {
       public final AtlasRegion head;
       public AssetNarwahl (TextureAtlas atlas) {
           head = atlas.findRegion("OriginalNarwhal");
   }
 }
   public class AssetRock {
       public final AtlasRegion edge;
       public final AtlasRegion middle;
       public AssetRock (TextureAtlas atlas) {
           edge = atlas.findRegion("grass copy");
           middle = atlas.findRegion("grass");
       }
}
   
   public class AssetGoldCoin {
       public final AtlasRegion goldCoin;
       public AssetGoldCoin (TextureAtlas atlas) {
           goldCoin = atlas.findRegion("Special");
}
}
   public class AssetFeather {
       public final AtlasRegion feather;
       public AssetFeather (TextureAtlas atlas) {
           feather = atlas.findRegion("star");
   } }


   		
   
}