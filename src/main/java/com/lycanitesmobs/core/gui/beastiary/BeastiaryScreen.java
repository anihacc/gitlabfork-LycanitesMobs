package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.gui.BaseScreen;
import com.lycanitesmobs.core.gui.buttons.ButtonBase;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public abstract class BeastiaryScreen extends BaseScreen {
	public enum Page {
		INDEX((byte)0), CREATURES((byte)1), PETS((byte)2), SUMMONING((byte)3), ELEMENTS((byte)4);
		public byte id;
		Page(byte i) { id = i; }
	}
	
	/** A snapshot of the users GUI Scale setting so it can be restored on closing the Beastiary. **/
	static int OPENED_GUI_SCALE;
	/** Set to true when any Beastiary GUI is active in order to prevent the GUI Scaling going out of sync. **/
	static boolean GUI_ACTIVE;

	public Minecraft mc;

	public PlayerEntity player;
	public ExtendedPlayer playerExt;
	public LivingEntity creaturePreviewEntity;
	public float creaturePreviewTicks = 0;

	public int centerX;
	public int centerY;
	public int windowWidth;
	public int windowHeight;
	public int halfX;
	public int halfY;
	public int windowX;
	public int windowY;

	public int colLeftX;
	public int colLeftY;
	public int colLeftWidth;
	public int colLeftHeight;
	public int colLeftCenterX;
	public int colLeftCenterY;

	public int colRightX;
	public int colRightY;
	public int colRightWidth;
	public int colRightHeight;
	public int colRightCenterX;
	public int colRightCenterY;


	/**
	 * Constructor
	 * @param player The player to create the GUI instance for.
	 */
	public BeastiaryScreen(PlayerEntity player) {
		super(new TranslationTextComponent("gui.beastiary.name"));
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);

		this.mc = Minecraft.getInstance();
		if(this.mc.gameSettings.guiScale != 2 || GUI_ACTIVE) {
			OPENED_GUI_SCALE = this.mc.gameSettings.guiScale;
			this.mc.gameSettings.guiScale = 2 - OPENED_GUI_SCALE;
			//this.mc.gameSettings.set(GameSettings.Options.GUI_SCALE, 2 - OPENED_GUI_SCALE);
		}
		else {
			GUI_ACTIVE = true;
		}
	}

	@Override
	public void onClose() {
		if(this.mc.gameSettings.guiScale == 2 && !GUI_ACTIVE) {
			//this.mc.gameSettings.setOptionValue(GameSettings.Options.GUI_SCALE, OPENED_GUI_SCALE - 2);
			this.mc.gameSettings.guiScale = 2 - OPENED_GUI_SCALE;
		}
		GUI_ACTIVE = false;
		super.onClose();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void init() {
		super.init();
		if(this.scaledResolution == null) {
			this.scaledResolution = this.mc.mainWindow;
		}

		this.zLevel = -1000F;

		// Main Window:
		this.windowWidth = this.getScaledX(0.95F);
		this.windowHeight = this.getScaledY(0.95F);
		this.halfX = this.windowWidth / 2;
		this.halfY = this.windowHeight / 2;
		this.windowX = (this.width / 2) - (this.windowWidth / 2);
		this.windowY = (this.height / 2) - (this.windowHeight / 2);
		this.centerX = this.windowX + (this.windowWidth / 2);
		this.centerY = this.windowY + (this.windowHeight / 2);

		// Left Column:
		this.colLeftX = this.windowX + this.getScaledX(80F / 1920F);
		this.colLeftY = this.windowY + this.getScaledY(460F / 1080F);
		this.colLeftWidth = this.getScaledX(320F / 1920F);
		this.colLeftHeight = this.getScaledX(380F / 1080F);
		this.colLeftCenterX = this.colLeftX + Math.round(this.colLeftWidth / 2);
		this.colLeftCenterY = this.colLeftY + Math.round(this.colLeftHeight / 2);

		// Right Column:
		this.colRightX = this.windowX + this.getScaledX(480F / 1920F);
		this.colRightY = this.windowY + this.getScaledY(420F / 1080F);
		this.colRightWidth = this.getScaledX(1260F / 1920F);
		this.colRightHeight = this.getScaledX(400F / 1080F);
		this.colRightCenterX = this.colRightX + Math.round(this.colRightWidth / 2);
		this.colRightCenterY = this.colRightY + Math.round(this.colRightHeight / 2);
	}

	@Override
	protected void initWidgets() {
		int menuPadding = 6;
		int menuX = this.centerX - Math.round((float)this.windowWidth / 2) + menuPadding;
		int menuY = this.windowY + menuPadding;
		int menuWidth = this.windowWidth - (menuPadding * 2);

		int buttonCount = 5;
		int buttonPadding = 2;
		int buttonX = menuX + buttonPadding;
		int buttonWidth = Math.round((float)(menuWidth / buttonCount)) - (buttonPadding * 2);
		int buttonWidthPadded = buttonWidth + (buttonPadding * 2);
		int buttonHeight = 20;
		ButtonBase button;

		// Top Menu:
		button = new ButtonBase(Page.INDEX.id, buttonX + (buttonWidthPadded * this.buttons.size()), menuY, buttonWidth, buttonHeight, new TranslationTextComponent("gui.beastiary.index.title").getFormattedText(), this);
		this.buttons.add(button);
		button = new ButtonBase(Page.CREATURES.id, buttonX + (buttonWidthPadded * this.buttons.size()), menuY, buttonWidth, buttonHeight, new TranslationTextComponent("gui.beastiary.creatures").getFormattedText(), this);
		this.buttons.add(button);
		button = new ButtonBase(Page.PETS.id, buttonX + (buttonWidthPadded * this.buttons.size()), menuY, buttonWidth, buttonHeight, new TranslationTextComponent("gui.beastiary.pets").getFormattedText(), this);
		this.buttons.add(button);
		button = new ButtonBase(Page.SUMMONING.id, buttonX + (buttonWidthPadded * this.buttons.size()), menuY, buttonWidth, buttonHeight, new TranslationTextComponent("gui.beastiary.summoning").getFormattedText(), this);
		this.buttons.add(button);
		button = new ButtonBase(Page.ELEMENTS.id, buttonX + (buttonWidthPadded * this.buttons.size()), menuY, buttonWidth, buttonHeight, new TranslationTextComponent("gui.beastiary.elements").getFormattedText(), this);
		this.buttons.add(button);
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		this.drawTexture(AssetManager.getTexture("GUIBeastiaryBackground"), this.windowX, this.windowY, this.zLevel, 1, 1, this.windowWidth, this.windowHeight);
	}

	@Override
	public void renderForeground(int mouseX, int mouseY, float partialTicks) {
		ITextComponent title = new StringTextComponent("\u00A7l\u00A7n").appendSibling(this.getTitle());
		float width = this.getFontRenderer().getStringWidth(title.getFormattedText());
		this.getFontRenderer().drawString(title.getFormattedText(), this.colRightCenterX - Math.round(width / 2), this.colRightY, 0xFFFFFF);
	}

	/**
	 * Called when a GUI button is interacted with.
	 * @param buttonId The button that was interacted with.
	 */
	@Override
	public void actionPerformed(byte buttonId) {
		if(buttonId == Page.INDEX.id) {
			this.mc.displayGuiScreen(new IndexBeastiaryScreen(Minecraft.getInstance().player));
		}
		if(buttonId == Page.CREATURES.id) {
			this.mc.displayGuiScreen(new CreaturesBeastiaryScreen(Minecraft.getInstance().player));
		}
		if(buttonId == Page.PETS.id) {
			this.mc.displayGuiScreen(new PetsBeastiaryScreen(Minecraft.getInstance().player));
		}
		if(buttonId == Page.SUMMONING.id) {
			this.mc.displayGuiScreen(new SummoningBeastiaryScreen(Minecraft.getInstance().player));
		}
		if(buttonId == Page.ELEMENTS.id) {
			this.mc.displayGuiScreen(new ElementsBeastiaryScreen(Minecraft.getInstance().player));
		}
	}

	/**
	 * Called when a key is pressed.
	 */
	@Override
	public boolean keyReleased(int keyCode, int keyCodeB, int keyCodeC) {
		if(keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKey().getKeyCode()) {
			this.mc.player.closeScreen();
		}
		return super.keyReleased(keyCode, keyCodeB, keyCodeC);
	}

	/**
	 * Returns the title of this Beastiary Page.
	 * @return The title text string to display.
	 */
	public ITextComponent getTitle() {
		return new TranslationTextComponent("gui.beastiary.name");
	}

	/**
	 * Draws a level bar for the provided creature info.
	 * @param creatureInfo The creature info to get stats from.
	 * @param texture The texture to use as a level dot/star.
	 * @param x The x position to draw from.
	 * @param y The y position to draw from.
	 */
	public void drawLevel(CreatureInfo creatureInfo, ResourceLocation texture, int x, int y) {
		int level = creatureInfo.summonCost;
		if(level <= 10) {
			this.drawBar(texture, x, y, 0, 9, 9, level, 10);
		}
	}

	public void renderCreature(CreatureInfo creatureInfo, int x, int y, int mouseX, int mouseY, float partialTicks) {
		// Clear:
		if(creatureInfo == null) {
			this.creaturePreviewEntity = null;
			return;
		}

		try {
			// Subspecies:
			boolean subspeciesMatch = true;
			if(this.creaturePreviewEntity instanceof BaseCreatureEntity) {
				subspeciesMatch = ((BaseCreatureEntity)this.creaturePreviewEntity).getSubspeciesIndex() == this.getDisplaySubspecies(creatureInfo);
			}

			// Create New:
			if(this.creaturePreviewEntity == null || this.creaturePreviewEntity.getClass() != creatureInfo.entityClass || !subspeciesMatch) {
				this.creaturePreviewEntity = creatureInfo.entityClass.getConstructor(new Class[]{World.class}).newInstance(this.player.getEntityWorld());
				this.creaturePreviewEntity.onGround = true;
				if (this.creaturePreviewEntity instanceof BaseCreatureEntity) {
					((BaseCreatureEntity) this.creaturePreviewEntity).setSubspecies(this.getDisplaySubspecies(creatureInfo));
				}
				if (this.creaturePreviewEntity instanceof AgeableCreatureEntity) {
					((AgeableCreatureEntity) this.creaturePreviewEntity).setGrowingAge(0);
				}
				this.onCreateDisplayEntity(creatureInfo, this.creaturePreviewEntity);
				this.playCreatureSelectSound(creatureInfo);
			}

			// Render:
			if(this.creaturePreviewEntity != null) {
				int creatureSize = 70;
				double creatureWidth = creatureInfo.width;
				double creatureHeight = creatureInfo.height;
				int scale = (int)Math.round((1.8F / Math.max(creatureWidth, creatureHeight)) * creatureSize);
				int posX = x;
				int posY = y + 32 + creatureSize;
				float lookX = (float)posX - mouseX;
				float lookY = (float)posY - mouseY;
				this.creaturePreviewTicks += partialTicks;
				if(this.creaturePreviewEntity instanceof BaseCreatureEntity) {
					BaseCreatureEntity previewCreatureBase = (BaseCreatureEntity)this.creaturePreviewEntity;
					previewCreatureBase.onlyRenderTicks = this.creaturePreviewTicks;
				}

				GlStateManager.enableColorMaterial();
				GlStateManager.pushMatrix();
				GlStateManager.translatef((float)posX, (float)posY, -500.0F);
				GlStateManager.scalef((float)(-scale), (float)scale, (float)scale);
				GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
				float f = this.creaturePreviewEntity.renderYawOffset;
				float f1 = this.creaturePreviewEntity.rotationYaw;
				float f2 = this.creaturePreviewEntity.rotationPitch;
				float f3 = this.creaturePreviewEntity.prevRotationYawHead;
				float f4 = this.creaturePreviewEntity.rotationYawHead;
				GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
				RenderHelper.enableStandardItemLighting();
				GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotatef(-((float)Math.atan((double)(lookY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
				this.creaturePreviewEntity.renderYawOffset = (float)Math.atan((double)(lookX / 40.0F)) * 20.0F;
				this.creaturePreviewEntity.rotationYaw = (float)Math.atan((double)(lookX / 40.0F)) * 40.0F;
				this.creaturePreviewEntity.rotationPitch = -((float)Math.atan((double)(lookY / 40.0F))) * 20.0F;
				this.creaturePreviewEntity.rotationYawHead = this.creaturePreviewEntity.rotationYaw;
				this.creaturePreviewEntity.prevRotationYawHead = this.creaturePreviewEntity.rotationYaw;
				GlStateManager.translatef(0.0F, 0.0F, 0.0F);
				EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
				renderManager.setPlayerViewY(180.0F);
				renderManager.setRenderShadow(false);
				renderManager.renderEntity(this.creaturePreviewEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
				renderManager.setRenderShadow(true);
				this.creaturePreviewEntity.renderYawOffset = f;
				this.creaturePreviewEntity.rotationYaw = f1;
				this.creaturePreviewEntity.rotationPitch = f2;
				this.creaturePreviewEntity.prevRotationYawHead = f3;
				this.creaturePreviewEntity.rotationYawHead = f4;
				GlStateManager.popMatrix();
				RenderHelper.disableStandardItemLighting();
				GlStateManager.disableRescaleNormal();
				GlStateManager.activeTexture(GLX.GL_TEXTURE1);
				GlStateManager.disableTexture();
				GlStateManager.activeTexture(GLX.GL_TEXTURE0);
			}
		}
		catch (Exception e) {
			LycanitesMobs.logWarning("", "An exception occurred when trying to preview a creature in the Beastiary.");
			e.printStackTrace();
		}
	}

	/**
	 * Gets the Subspecies to use for the display creature.
	 * @param creatureInfo The Creature Info being displayed.
	 */
	public int getDisplaySubspecies(CreatureInfo creatureInfo) {
		return this.playerExt.selectedSubspecies;
	}

	/**
	 * Plays an idle or tame sound of the provided creature when it is selected in the GUI.
	 * @param creatureInfo The creature to play the sound from.
	 */
	public void playCreatureSelectSound(CreatureInfo creatureInfo) {
		this.player.getEntityWorld().playSound(this.player, this.player.posX, this.player.posY, this.player.posZ, ObjectManager.getSound(creatureInfo.getName() + "_say"), SoundCategory.NEUTRAL, 1, 1);
	}

	/**
	 * Called when a display entity is created.
	 * @param creatureInfo The Creature Info used to create the entity.
	 * @param entity The display entity instance.
	 */
	public void onCreateDisplayEntity(CreatureInfo creatureInfo, LivingEntity entity) {}
}