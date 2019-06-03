package com.lycanitesmobs.mountainmobs;

import com.lycanitesmobs.core.model.creature.*;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.renderer.RenderRegister;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("jabberwock", new ModelJabberwock());
		AssetManager.addModel("troll", new ModelTroll());
		AssetManager.addModel("yale", new ModelYale());
		AssetManager.addModel("beholder", new ModelBeholder());
		AssetManager.addModel("barghest", new ModelBarghest());
		AssetManager.addModel("wildkin", new ModelWildkin());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}