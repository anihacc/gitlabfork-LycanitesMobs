package com.lycanitesmobs;

import com.lycanitesmobs.core.container.CreatureContainer;
import com.lycanitesmobs.core.container.EquipmentForgeContainer;
import com.lycanitesmobs.core.container.SummoningPedestalContainer;
import com.lycanitesmobs.core.gui.CreatureInventoryScreen;
import com.lycanitesmobs.core.gui.EquipmentForgeScreen;
import com.lycanitesmobs.core.gui.SummoningPedestalScreen;
import com.lycanitesmobs.core.gui.beastiary.SummoningBeastiaryScreen;
import com.lycanitesmobs.core.gui.overlays.BaseOverlay;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.ItemColorCustomSpawnEgg;
import com.lycanitesmobs.core.localisation.LanguageLoader;
import com.lycanitesmobs.core.localisation.LanguageManager;
import com.lycanitesmobs.core.model.ModelCreatureBase;
import com.lycanitesmobs.core.renderer.RenderRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientManager {
	protected static ClientManager INSTANCE;
	public static ClientManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ClientManager();
		}
		return INSTANCE;
	}

	protected FontRenderer fontRenderer;

	/**
	 * Returns the Font Renderer used by Lycanites Mobs.
	 * @return A sexy Font Renderer, thanks for the heads up CedKilleur!
	 */
    public FontRenderer getFontRenderer() {
    	return Minecraft.getInstance().fontRenderer;
		/*if(this.fontRenderer == null) {
			ResourceLocation fontResource = new ResourceLocation(LycanitesMobs.MODID, "fonts/diavlo_light.otf");
			this.fontRenderer = new FontRenderer(Minecraft.getInstance().getTextureManager(), new Font(Minecraft.getInstance().getTextureManager(), fontResource));
		}
		return this.fontRenderer;*/
	}

	/**
	 * Registers Colored Items
	 */
	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		for(CreatureType creatureType : CreatureManager.getInstance().creatureTypes.values()) {
			event.getItemColors().register(new ItemColorCustomSpawnEgg(), creatureType.spawnEgg);
		}
	}

	/**
	 * Registers all GUI Screens, etc used by this mod.
	 */
	public void registerGUIs() {
    	ScreenManager.registerFactory(CreatureContainer.TYPE, CreatureInventoryScreen::new);
    	ScreenManager.registerFactory(SummoningPedestalContainer.TYPE, SummoningPedestalScreen::new);
    	ScreenManager.registerFactory(EquipmentForgeContainer.TYPE, EquipmentForgeScreen::new);
	}

	/**
	 * Registers all client side events listeners.
	 */
	public void registerEvents() {
		// Event Listeners:
		MinecraftForge.EVENT_BUS.register(new KeyHandler(Minecraft.getInstance()));
		MinecraftForge.EVENT_BUS.register(new BaseOverlay(Minecraft.getInstance()));
		MinecraftForge.EVENT_BUS.register(new ClientEventListener());
		IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		if(resourceManager instanceof IReloadableResourceManager) {
			((IReloadableResourceManager)resourceManager).addReloadListener(LanguageLoader.getInstance());
		}
	}

	/**
	 * Sets up the Language Manager used for additional language files.
	 */
	public void initLanguageManager() {
		LanguageManager.getInstance();
	}

	/**
	 * Registers GUI and misc textures.
	 */
    public void registerTextures() {
		ModInfo group = LycanitesMobs.modInfo;
		AssetManager.addTexture("GUIInventoryCreature", group, "textures/guis/inventory_creature.png");

		// Beastiary:
		AssetManager.addTexture("GUIBeastiaryBackground", group, "textures/guis/beastiary/background.png");
		AssetManager.addTexture("GUIPetLevel", group, "textures/guis/beastiary/level.png");
		AssetManager.addTexture("GUIPetSpirit", group, "textures/guis/beastiary/spirit.png");
		AssetManager.addTexture("GUIPetSpiritEmpty", group, "textures/guis/beastiary/spirit_empty.png");
		AssetManager.addTexture("GUIPetSpiritUsed", group, "textures/guis/beastiary/spirit_used.png");
		AssetManager.addTexture("GUIPetSpiritFilling", group, "textures/guis/beastiary/spirit_filling.png");
		AssetManager.addTexture("GUIPetBarHealth", group, "textures/guis/beastiary/bar_health.png");
		AssetManager.addTexture("GUIPetBarRespawn", group, "textures/guis/beastiary/bar_respawn.png");
		AssetManager.addTexture("GUIPetBarEmpty", group, "textures/guis/beastiary/bar_empty.png");

		AssetManager.addTexture("GUILMMainMenu", group, "textures/guis/lmmainmenu.png");
		AssetManager.addTexture("GUIBeastiary", group, "textures/guis/beastiary.png");
		AssetManager.addTexture("GUIPet", group, "textures/guis/pet.png");
		AssetManager.addTexture("GUIMount", group, "textures/guis/mount.png");
        AssetManager.addTexture("GUIFamiliar", group, "textures/guis/familiar.png");
        AssetManager.addTexture("GUIMinion", group, "textures/guis/minion.png");
        AssetManager.addTexture("GUIMinionLg", group, "textures/guis/minion_lg.png");
		AssetManager.addTexture("GUIEquipmentForge", group, "textures/guis/equipmentforge.png");
    }

	/**
	 * Initialises the Render Register which loads and registers models and sets up render factories, etc.
	 */
    public void initRenderRegister() {
        RenderRegister renderRegister = new RenderRegister();
		renderRegister.registerModels();
        renderRegister.registerModelLoaders();
        renderRegister.registerRenderFactories();
    }

	/**
	 * Loads a Creature Model
	 * @param creature The creature info to load the model from.
	 * @param modelClassName The Model java class name to instantiate.
	 * @throws ClassNotFoundException
	 */
	public void loadCreatureModel(CreatureInfo creature, String modelClassName) throws ClassNotFoundException {
		creature.modelClass = (Class<? extends ModelCreatureBase>) Class.forName(modelClassName);
	}

	/**
	 * Loads a Creature Subspecies Model
	 * @param subspecies The subspecies to load the model from.
	 * @param modelClassName The Model java class name to instantiate.
	 * @throws ClassNotFoundException
	 */
	public void loadSubspeciesModel(Subspecies subspecies, String modelClassName) throws ClassNotFoundException {
		subspecies.modelClass = (Class<? extends ModelCreatureBase>) Class.forName(modelClassName);
	}

	/**
	 * Returns the client player entity.
	 * @return Client player entity.
	 */
    public PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	/**
	 * Displays a Screen GUI on the client.
	 */
	public void displayGuiScreen(String screenName, PlayerEntity player) {
		if("beastiary".equals(screenName))
			Minecraft.getInstance().displayGuiScreen(new SummoningBeastiaryScreen(player));
	}
}