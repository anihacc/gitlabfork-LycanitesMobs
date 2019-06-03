package com.lycanitesmobs.shadowmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.model.creature.*;
import com.lycanitesmobs.core.renderer.RenderRegister;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("phantom", new ModelPhantom());
		AssetManager.addModel("epion", new ModelEpion());
        AssetManager.addModel("geist", new ModelGeist());
        AssetManager.addModel("chupacabra", new ModelChupacabra());
        AssetManager.addModel("shade", new ModelShade());
        AssetManager.addModel("darkling", new ModelDarkling());
		AssetManager.addModel("wraamon", new ModelWraamon());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}