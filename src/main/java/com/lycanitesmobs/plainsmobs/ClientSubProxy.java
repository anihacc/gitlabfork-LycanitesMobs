package com.lycanitesmobs.plainsmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.model.creature.*;
import com.lycanitesmobs.core.renderer.RenderRegister;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
        // Add Models:
		AssetManager.addModel("kobold", new ModelKobold());
		AssetManager.addModel("ventoraptor", new ModelVentoraptor());
		AssetManager.addModel("maka", new ModelMaka());
		AssetManager.addModel("makaalpha", new ModelMakaAlpha());
		AssetManager.addModel("zoataur", new ModelZoataur());
		AssetManager.addModel("roc", new ModelRoc());
		AssetManager.addModel("feradon", new ModelFeradon());
		AssetManager.addModel("quillbeast", new ModelQuillbeast());
		AssetManager.addModel("morock", new ModelMorock());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}