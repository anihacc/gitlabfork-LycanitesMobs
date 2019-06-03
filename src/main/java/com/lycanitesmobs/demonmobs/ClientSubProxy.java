package com.lycanitesmobs.demonmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.model.creature.*;
import com.lycanitesmobs.core.renderer.RenderRegister;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("belph", new ModelBelph());
		AssetManager.addModel("behemoth", new ModelBehemoth());
		AssetManager.addModel("pinky", new ModelPinky());
		AssetManager.addModel("trite", new ModelTrite());
		AssetManager.addModel("astaroth", new ModelAstaroth());
		AssetManager.addModel("cacodemon", new ModelCacodemon());
        AssetManager.addModel("rahovart", new ModelRahovart());
        AssetManager.addModel("asmodeus", new ModelAsmodeus());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}