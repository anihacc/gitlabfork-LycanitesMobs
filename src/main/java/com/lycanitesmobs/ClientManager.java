package com.lycanitesmobs;

import com.lycanitesmobs.client.ClientEventListener;
import com.lycanitesmobs.client.KeyHandler;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.container.CreatureContainer;
import com.lycanitesmobs.core.container.EquipmentForgeContainer;
import com.lycanitesmobs.core.container.SummoningPedestalContainer;
import com.lycanitesmobs.client.gui.CreatureInventoryScreen;
import com.lycanitesmobs.client.gui.EquipmentForgeScreen;
import com.lycanitesmobs.client.gui.SummoningPedestalScreen;
import com.lycanitesmobs.client.gui.beastiary.SummoningBeastiaryScreen;
import com.lycanitesmobs.client.gui.overlays.BaseOverlay;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.ItemColorCustomSpawnEgg;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.client.localisation.LanguageLoader;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.client.model.ModelCreatureBase;
import com.lycanitesmobs.client.renderer.RenderRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientManager {
	public static int GL_TEXTURE0 = 33984;
	public static int GL_TEXTURE1 = 33986;

	protected static ClientManager INSTANCE;
	public static ClientManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ClientManager();
		}
		return INSTANCE;
	}

	protected FontRenderer fontRenderer;

	/**
	 * Sets up the Language Manager used for additional language files.
	 */
	public void initLanguageManager() {
		LanguageManager.getInstance();
	}

	/**
	 * Registers all GUI Screens, etc used by this mod.
	 */
	public void registerScreens() {
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
	 * Initialises the Render Register which loads and registers models and sets up render factories, etc.
	 */
	public void initRenderRegister() {
		RenderRegister renderRegister = new RenderRegister();
		renderRegister.registerModelLoaders();
		renderRegister.registerRenderFactories();
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
		if("beastiary".equals(screenName)) {
			Minecraft.getInstance().displayGuiScreen(new SummoningBeastiaryScreen(player));
		}
	}

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
			if(creatureType.spawnEgg != null) {
				event.getItemColors().register(new ItemColorCustomSpawnEgg(), creatureType.spawnEgg);
			}
		}
	}
}