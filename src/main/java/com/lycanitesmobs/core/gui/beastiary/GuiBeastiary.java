package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.*;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.gui.ButtonBase;
import com.lycanitesmobs.core.gui.GuiBaseScreen;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.localisation.LanguageManager;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.net.URI;

public abstract class GuiBeastiary extends GuiBaseScreen {
	/** A snapshot of the users GUI Scale setting so it can be restored on closing the Beastiary. **/
	static int OPENED_GUI_SCALE;
	/** Set to true when any Beastiary GUI is active in order to prevent the GUI Scaling going out of sync. **/
	static boolean GUI_ACTIVE;

	public Minecraft mc;

	public PlayerEntity player;
	public ExtendedPlayer playerExt;
	public LivingEntity creaturePreviewEntity;
	public float creaturePreviewTicks = 0;

	public MainWindow scaledResolution;
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
	public GuiBeastiary(PlayerEntity player) {
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


	/**
	 * Returns the title of this Beastiary Page.
	 * @return The title text string to display.
	 */
	public ITextComponent getTitle() {
		return new TranslationTextComponent("gui.beastiary.name");
	}


	/**
	 * Whether this GUI should pause a single player game or not.
	 * @return True to pause the game.
	 */
	@Override
	public boolean isPauseScreen() {
		return false;
	}


	/**
	 * Returns a scaled x coordinate.
	 * @param x The x float to scale where 1.0 is the entire GUI width.
	 * @return A scaled x position.
	 */
	public int getScaledX(float x) {
		if(this.scaledResolution == null) {
			this.scaledResolution = this.mc.mainWindow;
		}

		// Aspect Ratio:
		float targetAspect = 0.5625f; // 16:9
		float scaledHeight = scaledResolution.getScaledHeight();
		float scaledWidth = scaledResolution.getScaledWidth();
		float currentAspect = (scaledHeight * x) / (scaledWidth * x);

		// Wider Than target:
		if(currentAspect < targetAspect) {
			scaledWidth = scaledHeight + (scaledHeight * targetAspect);
		}

		// Taller Than target:
		else if(currentAspect > targetAspect) {
			scaledHeight = scaledWidth + (scaledWidth * targetAspect);
		}

		float guiWidth = scaledWidth * x;
		return Math.round(Math.max(x, guiWidth));
	}


	/**
	 * Returns a scaled y coordinate based on the scaled width with an aspect ratio applied to it.
	 * @param y The y float to scale where 1.0 is the entire GUI height.
	 * @return A scaled y position.
	 */
	public int getScaledY(float y) {
		float baseHeight = Math.round((float)this.getScaledX(y) * 0.5625f);
		return Math.round(baseHeight * y);
	}


	/**
	 * Initializes this gui, called when first opening or on window resizing.
	 */
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

		this.buttons.clear();
		this.initControls();
	}


	/**
	 * Draws the buttons and other controls for this GUI.
	 */
	protected void initControls() {
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
		button = new ButtonBase(GuiHandler.Beastiary.INDEX.id, buttonX + (buttonWidthPadded * this.buttons.size()), menuY, buttonWidth, buttonHeight, LanguageManager.translate("gui.beastiary.index.title"), this);
		this.buttons.add(button);
		button = new ButtonBase(GuiHandler.Beastiary.CREATURES.id, buttonX + (buttonWidthPadded * this.buttons.size()), menuY, buttonWidth, buttonHeight, LanguageManager.translate("gui.beastiary.creatures"), this);
		this.buttons.add(button);
		button = new ButtonBase(GuiHandler.Beastiary.PETS.id, buttonX + (buttonWidthPadded * this.buttons.size()), menuY, buttonWidth, buttonHeight, LanguageManager.translate("gui.beastiary.pets"), this);
		this.buttons.add(button);
		button = new ButtonBase(GuiHandler.Beastiary.SUMMONING.id, buttonX + (buttonWidthPadded * this.buttons.size()), menuY, buttonWidth, buttonHeight, LanguageManager.translate("gui.beastiary.summoning"), this);
		this.buttons.add(button);
		button = new ButtonBase(GuiHandler.Beastiary.ELEMENTS.id, buttonX + (buttonWidthPadded * this.buttons.size()), menuY, buttonWidth, buttonHeight, LanguageManager.translate("gui.beastiary.elements"), this);
		this.buttons.add(button);
	}


	/**
	 * Draws and updates the GUI.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 * @param partialTicks Ticks for animation.
	 */
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawBackground(mouseX, mouseY, partialTicks);
		this.drawForeground(mouseX, mouseY, partialTicks);
		this.updateControls(mouseX, mouseY, partialTicks);

		super.render(mouseX, mouseY, partialTicks);
	}


	/**
	 * Draws the background image.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 * @param partialTicks Ticks for animation.
	 */
	public void drawBackground(int mouseX, int mouseY, float partialTicks) {
		this.drawTexture(AssetManager.getTexture("GUIBeastiaryBackground"), this.windowX, this.windowY, this.zLevel, 1, 1, this.windowWidth, this.windowHeight);
	}


	/**
	 * Updates buttons and other controls for this GUI.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 * @param partialTicks Ticks for animation.
	 */
	protected void updateControls(int mouseX, int mouseY, float partialTicks) {
		for(Widget button : this.buttons) {
			button.renderButton(mouseX, mouseY, partialTicks);
		}
	}


	/**
	 * Draws foreground elements.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 * @param partialTicks Ticks for animation.
	 */
	public void drawForeground(int mouseX, int mouseY, float partialTicks) {
		String title = "§l§n" + this.getTitle();
		float width = this.getFontRenderer().getStringWidth(title);
		this.getFontRenderer().drawString(title, this.colRightCenterX - Math.round(width / 2), this.colRightY, 0xFFFFFF);
	}


	/**
	 * Called when a GUI button is interacted with.
	 * @param buttonId The button that was interacted with.
	 */
	@Override
	public void actionPerformed(byte buttonId) {
		if(buttonId == GuiHandler.Beastiary.INDEX.id) {
			GuiBeastiaryIndex.openToPlayer(this.player);
		}
		if(buttonId == GuiHandler.Beastiary.CREATURES.id) {
			GuiBeastiaryCreatures.openToPlayer(this.player);
		}
		if(buttonId == GuiHandler.Beastiary.PETS.id) {
			GuiBeastiaryPets.openToPlayer(this.player);
		}
		if(buttonId == GuiHandler.Beastiary.SUMMONING.id) {
			GuiBeastiarySummoning.openToPlayer(this.player);
		}
		if(buttonId == GuiHandler.Beastiary.ELEMENTS.id) {
			GuiBeastiaryElements.openToPlayer(this.player);
		}

		super.actionPerformed(buttonId);
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
	 * Opens a URI in the users default web browser.
	 * @param uri The URI link to open.
	 */
	protected void openURI(URI uri) {
		try {
			Class oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
			oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, uri);
		}
		catch (Throwable throwable) {
			LycanitesMobs.logWarning("", "Unable to open link: " + uri.toString());
		}
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
			if(this.creaturePreviewEntity instanceof EntityCreatureBase) {
				subspeciesMatch = ((EntityCreatureBase)this.creaturePreviewEntity).getSubspeciesIndex() == this.getDisplaySubspecies(creatureInfo);
			}

			// Create New:
			if(this.creaturePreviewEntity == null || this.creaturePreviewEntity.getClass() != creatureInfo.entityClass || !subspeciesMatch) {
				this.creaturePreviewEntity = creatureInfo.entityClass.getConstructor(new Class[]{World.class}).newInstance(this.player.getEntityWorld());
				this.creaturePreviewEntity.onGround = true;
				if (this.creaturePreviewEntity instanceof EntityCreatureBase) {
					((EntityCreatureBase) this.creaturePreviewEntity).setSubspecies(this.getDisplaySubspecies(creatureInfo));
				}
				if (this.creaturePreviewEntity instanceof EntityCreatureAgeable) {
					((EntityCreatureAgeable) this.creaturePreviewEntity).setGrowingAge(0);
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
				if(this.creaturePreviewEntity instanceof EntityCreatureBase) {
					EntityCreatureBase previewCreatureBase = (EntityCreatureBase)this.creaturePreviewEntity;
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
