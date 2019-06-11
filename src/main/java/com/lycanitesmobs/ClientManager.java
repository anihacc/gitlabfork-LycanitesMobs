package com.lycanitesmobs;

import com.lycanitesmobs.core.block.BlockFluidBase;
import com.lycanitesmobs.core.entity.EntityFear;
import com.lycanitesmobs.core.entity.EntityHitArea;
import com.lycanitesmobs.core.entity.EntityPortal;
import com.lycanitesmobs.core.gui.GuiOverlay;
import com.lycanitesmobs.core.gui.GuiTabMain;
import com.lycanitesmobs.core.gui.TabManager;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.localisation.LanguageLoader;
import com.lycanitesmobs.core.localisation.LanguageManager;
import com.lycanitesmobs.core.model.EquipmentPartModelLoader;
import com.lycanitesmobs.core.model.ModelCustom;
import com.lycanitesmobs.core.model.projectile.ModelAetherwave;
import com.lycanitesmobs.core.model.projectile.ModelChaosOrb;
import com.lycanitesmobs.core.model.projectile.ModelCrystalShard;
import com.lycanitesmobs.core.model.projectile.ModelLightBall;
import com.lycanitesmobs.core.renderer.EquipmentPartRenderer;
import com.lycanitesmobs.core.renderer.EquipmentRenderer;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.core.tileentity.TileEntityEquipment;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentPart;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientManager extends CommonProxy {
	protected static ClientManager INSTANCE;

	public static ClientManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ClientManager();
		}
		return INSTANCE;
	}

	public static IItemColor itemColor = (stack, tintIndex) -> {
		Item item = stack.getItem();
		if(item == null || !(item instanceof ItemBase))
			return 16777215;
		ItemBase itemBase = (ItemBase)item;
		return itemBase.getColorFromItemstack(stack, tintIndex);
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
			ResourceLocation font = new ResourceLocation("textures/font/ascii.png");
			this.fontRenderer = new FontRenderer(Minecraft.getMinecraft().gameSettings, font, Minecraft.getMinecraft().renderEngine, true);
		}
		return this.fontRenderer;
	}


	// ========== Register Event Handlers ==========
	public void registerEvents() {
		// Event Listeners:
		FMLCommonHandler.instance().bus().register(new KeyHandler(Minecraft.getMinecraft()));
		MinecraftForge.EVENT_BUS.register(new GuiOverlay(Minecraft.getMinecraft()));
		MinecraftForge.EVENT_BUS.register(new ClientEventListener());
		IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
		if(resourceManager instanceof IReloadableResourceManager) {
			((IReloadableResourceManager)resourceManager).registerReloadListener(LanguageLoader.getInstance());
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
		TabManager.registerTab(new GuiTabMain(0));
    }
	
	
	// ========== Register Renders ==========
    public void registerRenders(ModInfo modInfo) {
		// Projectile Models:
		AssetManager.addModel("lightball", new ModelLightBall());
		AssetManager.addModel("crystalshard", new ModelCrystalShard());
		AssetManager.addModel("aetherwave", new ModelAetherwave());
		AssetManager.addModel("chaosorb", new ModelChaosOrb());

        // Equipment Parts:
		ModelLoaderRegistry.registerLoader(new EquipmentPartModelLoader());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEquipmentPart.class, new EquipmentPartRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEquipment.class, new EquipmentRenderer());

        RenderRegister renderRegister = new RenderRegister(modInfo);
        renderRegister.registerRenderFactories();
    }

	// ========== Register Models ==========
	public void registerItemModels() {
		ObjectManager.RegisterModels();
	}


	// ========== Creatures ==========
	public void loadCreatureModel(CreatureInfo creature, String modelClassName) throws ClassNotFoundException {
		creature.modelClass = (Class<? extends ModelCustom>) Class.forName(modelClassName);
	}

	public void loadSubspeciesModel(Subspecies subspecies, String modelClassName) throws ClassNotFoundException {
		subspecies.modelClass = (Class<? extends ModelCustom>) Class.forName(modelClassName);
	}

	
	// ========== Get Client Player Entity ==========
    public ClientPlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}


    // ========== Renders ==========
    public void addBlockRender(ModInfo group, Block block) {
        // Fluids:
        if(block instanceof BlockFluidBase) {
            BlockFluidBase blockFluid = (BlockFluidBase)block;
            Item item = Item.getItemFromBlock(block);
            ModelBakery.registerItemVariants(item);
            ModelResourceLocation fluidLocation = new ModelResourceLocation(blockFluid.group.filename + ":fluid", blockFluid.getFluid().getName());
            ModelLoader.setCustomMeshDefinition(item, itemStack -> fluidLocation);
            ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
                @Override
                protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                    return fluidLocation;
                }
            });
            return;
        }

        this.addItemRender(group, Item.getItemFromBlock(block));
    }

    public void addItemRender(Item item) {
        if(item instanceof ItemEquipmentPart) {
			//ForgeHooksClient.registerTESRItemStack(item, 0, TileEntityEquipmentPart.class); // Deprecated yet the only way to render dynamic OBJ models that can be animated, rendered in stages, layers and mixed with other models.
		}

		if(item instanceof ItemEquipment) {
			//ForgeHooksClient.registerTESRItemStack(item, 0, TileEntityEquipment.class); // Deprecated yet the only way to render dynamic OBJ models that can be animated, rendered in stages, layers and mixed with other models.
		}
    }
}