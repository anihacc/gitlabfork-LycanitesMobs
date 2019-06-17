package com.lycanitesmobs;

import com.lycanitesmobs.core.gui.GuiOverlay;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.localisation.LanguageLoader;
import com.lycanitesmobs.core.localisation.LanguageManager;
import com.lycanitesmobs.core.model.EquipmentPartModelLoader;
import com.lycanitesmobs.core.model.ModelCreatureBase;
import com.lycanitesmobs.core.model.projectile.ModelAetherwave;
import com.lycanitesmobs.core.model.projectile.ModelChaosOrb;
import com.lycanitesmobs.core.model.projectile.ModelCrystalShard;
import com.lycanitesmobs.core.model.projectile.ModelLightBall;
import com.lycanitesmobs.core.renderer.RenderRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;

public class ClientManager {
	protected static ClientManager INSTANCE;

	public static ClientManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ClientManager();
		}
		return INSTANCE;
	}

	public static IItemColor itemColor = (stack, tintIndex) -> {
		Item item = stack.getItem();
		if(!(item instanceof ItemBase))
			return 16777215;
		ItemBase itemBase = (ItemBase)item;
		return itemBase.getColor(stack, tintIndex);
	};

	protected FontRenderer fontRenderer;

	/**
	 * Sets up the Language Manager used for additional lang files.
	 */
	public void initLanguageManager() {
		LanguageManager.getInstance();
	}

	/**
	 * Returns the Font Renderer used by Lycanites Mobs.
	 * @return A sexy Font Renderer, thanks for the heads up CedKilleur!
	 */
    public FontRenderer getFontRenderer() {
		if(this.fontRenderer == null) {
			ResourceLocation fontResource = new ResourceLocation("textures/font/ascii.png");
			this.fontRenderer = new FontRenderer(Minecraft.getInstance().getTextureManager(), new Font(Minecraft.getInstance().getTextureManager(), fontResource));
		}
		return this.fontRenderer;
	}


	// ========== Register Event Handlers ==========
	public void registerEvents() {
		// Event Listeners:
		MinecraftForge.EVENT_BUS.register(new KeyHandler(Minecraft.getInstance()));
		MinecraftForge.EVENT_BUS.register(new GuiOverlay(Minecraft.getInstance()));
		MinecraftForge.EVENT_BUS.register(new ClientEventListener());
		IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		if(resourceManager instanceof IReloadableResourceManager) {
			((IReloadableResourceManager)resourceManager).func_219534_a(LanguageLoader.getInstance());
		}
	}

	// ========== Register Assets ==========
    public void registerTextures() {
		// ========== Add GUI Textures ==========
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

		// ========== Add GUI Tabs ==========
		//TabManager.registerTab(new GuiTabMain()); TODO Figure out new method.
    }
	
	
	// ========== Register Renders ==========
    public void registerRenders(ModInfo modInfo) {
		// Projectile Models:
		AssetManager.addOldProjectileModel("lightball", new ModelLightBall());
		AssetManager.addOldProjectileModel("crystalshard", new ModelCrystalShard());
		AssetManager.addOldProjectileModel("aetherwave", new ModelAetherwave());
		AssetManager.addOldProjectileModel("chaosorb", new ModelChaosOrb());

        // Equipment Parts:
		ModelLoaderRegistry.registerLoader(new EquipmentPartModelLoader());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySummoningPedestal.class, new RenderTileEntityBase());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEquipmentForge.class, new RenderTileEntityBase());

        RenderRegister renderRegister = new RenderRegister(modInfo);
        renderRegister.registerRenderFactories();
    }

	// ========== Register Models ==========
	public void registerItemModels() {
		ObjectManager.RegisterModels();
	}


	// ========== Creatures ==========
	public void loadCreatureModel(CreatureInfo creature, String modelClassName) throws ClassNotFoundException {
		creature.modelClass = (Class<? extends ModelCreatureBase>) Class.forName(modelClassName);
	}

	public void loadSubspeciesModel(Subspecies subspecies, String modelClassName) throws ClassNotFoundException {
		subspecies.modelClass = (Class<? extends ModelCreatureBase>) Class.forName(modelClassName);
	}

	
	// ========== Get Client Player Entity ==========
    public ClientPlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}
}