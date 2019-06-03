package com.lycanitesmobs.desertmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.model.creature.*;
import com.lycanitesmobs.core.renderer.RenderRegister;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("cryptzombie", new ModelCryptZombie());
		AssetManager.addModel("sutiramu", new ModelSutiramu());
		AssetManager.addModel("crusk", new ModelCrusk());
		AssetManager.addModel("clink", new ModelClink());
		AssetManager.addModel("joust", new ModelJoust());
		AssetManager.addModel("joustalpha", new ModelJoustAlpha());
		AssetManager.addModel("erepede", new ModelErepede());
		AssetManager.addModel("gorgomite", new ModelGorgomite());
		AssetManager.addModel("manticore", new ModelManticore());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}