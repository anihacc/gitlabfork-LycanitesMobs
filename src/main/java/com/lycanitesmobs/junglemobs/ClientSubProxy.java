package com.lycanitesmobs.junglemobs;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.model.creature.*;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.renderer.RenderRegister;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Register Models ==========
	@Override
    public void registerModels(GroupInfo groupInfo) {
		AssetManager.addModel("geken", new ModelGeken());
		AssetManager.addModel("uvaraptor", new ModelUvaraptor());
		AssetManager.addModel("concapede", new ModelConcapedeHead());
		AssetManager.addModel("concapedesegment", new ModelConcapedeSegment());
		AssetManager.addModel("tarantula", new ModelTarantula());
		AssetManager.addModel("conba", new ModelConba());
		AssetManager.addModel("vespid", new ModelVespid());
		AssetManager.addModel("vespidqueen", new ModelVespidQueen());
		AssetManager.addModel("dawon", new ModelDawon());
		AssetManager.addModel("cockatrice", new ModelCockatrice());

        // Register Renderers:
        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
	}
}