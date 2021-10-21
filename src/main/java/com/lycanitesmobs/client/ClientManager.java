package com.lycanitesmobs.client;

import com.lycanitesmobs.client.gui.*;
import com.lycanitesmobs.client.gui.beastiary.SummoningBeastiaryScreen;
import com.lycanitesmobs.client.gui.overlays.BaseOverlay;
import com.lycanitesmobs.client.localisation.LanguageLoader;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.client.renderer.RenderRegister;
import com.lycanitesmobs.core.container.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.CreatureType;
import com.lycanitesmobs.core.item.ItemColorCustomSpawnEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientManager {
	public static int GL_TEXTURE0 = 33984;
	public static int GL_TEXTURE1 = 33986;
	public static int GL_FULL_BRIGHT = 15728880;

	public static int FULL_BRIGHT = 240;

	protected static ClientManager INSTANCE;
	public static ClientManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ClientManager();
		}
		return INSTANCE;
	}

	protected Font fontRenderer;

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
		MenuScreens.register(CreatureContainer.TYPE, CreatureInventoryScreen::new);
		MenuScreens.register(SummoningPedestalContainer.TYPE, SummoningPedestalScreen::new);
		MenuScreens.register(EquipmentForgeContainer.TYPE, EquipmentForgeScreen::new);
		MenuScreens.register(EquipmentInfuserContainer.TYPE, EquipmentInfuserScreen::new);
		MenuScreens.register(EquipmentStationContainer.TYPE, EquipmentStationScreen::new);
	}

	/**
	 * Registers all client side events listeners.
	 */
	public void registerEvents() {
		// Event Listeners:
		MinecraftForge.EVENT_BUS.register(new KeyHandler(Minecraft.getInstance()));
		MinecraftForge.EVENT_BUS.register(new BaseOverlay(Minecraft.getInstance()));
		MinecraftForge.EVENT_BUS.register(new ClientEventListener());
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		if(resourceManager instanceof ReloadableResourceManager) {
			((ReloadableResourceManager)resourceManager).registerReloadListener(LanguageLoader.getInstance());
		}
	}

	/**
	 * Initialises the Render Register which loads and registers models and sets up render factories, etc.
	 */
	public void initRenderRegister() {
		RenderRegister renderRegister = new RenderRegister();
		renderRegister.registerModelLoaders();
		renderRegister.registerRenderFactories();
		renderRegister.registerBlockRenderLayers();
	}

	/**
	 * Returns the client player entity.
	 * @return Client player entity.
	 */
	public Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	/**
	 * Displays a Screen GUI on the client.
	 */
	public void displayGuiScreen(String screenName, Player player) {
		if("beastiary".equals(screenName)) {
			Minecraft.getInstance().setScreen(new SummoningBeastiaryScreen(player));
		}
	}

	/**
	 * Returns the Font Renderer used by Lycanites Mobs.
	 * @return A sexy Font Renderer, thanks for the heads up CedKilleur!
	 */
    public Font getFontRenderer() {
    	return Minecraft.getInstance().font;
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