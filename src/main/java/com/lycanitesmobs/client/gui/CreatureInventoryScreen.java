package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.container.BaseContainer;
import com.lycanitesmobs.core.container.CreatureContainer;
import com.lycanitesmobs.core.inventory.InventoryCreature;
import com.lycanitesmobs.core.network.MessageEntityGUICommand;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import com.lycanitesmobs.client.localisation.LanguageManager;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

public class CreatureInventoryScreen extends BaseContainerScreen {
	public BaseCreatureEntity creature;
	public InventoryCreature creatureInventory;
	public InventoryPlayer playerInventory;
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public CreatureInventoryScreen(BaseCreatureEntity creature, InventoryPlayer playerInventory) {
		super(new CreatureContainer(creature, playerInventory));
		this.creature = creature;
		this.creatureInventory = creature.inventory;
		this.playerInventory = playerInventory;
	}
	
	
	// ==================================================
  	//                       Init
  	// ==================================================
	@Override
	public void initGui() {
		super.initGui();
		this.xSize = 176;
        this.ySize = 166;
        int backX = (this.width - this.xSize) / 2;
        int backY = (this.height - this.ySize) / 2;
		this.drawControls(backX, backY);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		this.fontRenderer.drawString(this.creatureInventory.getName(), 8, 6, 4210752);
        this.fontRenderer.drawString(LanguageManager.translate(this.playerInventory.getName()), 8, this.ySize - 96 + 2, 4210752);
		int backX = (this.width - this.xSize) / 2;
		int backY = (this.height - this.ySize) / 2;
		this.drawBars(backX, backY);
    }

	// ========== Draw Creature Health ===========
	protected void drawBars(int backX, int backY) {
		int barWidth = 100;
		int barHeight = 11;
		int barX = backX - barWidth;
		int barY = backY + 54 + 18;
		int barCenter = barX + (barWidth / 2);
		this.drawTexture(AssetManager.getTexture("GUIPetBarEmpty"), barX, barY, 0, 1, 1, barWidth, barHeight);
		float healthNormal = this.creature.getHealth() / this.creature.getMaxHealth();
		this.drawTexture(AssetManager.getTexture("GUIPetBarHealth"), barX, barY, 0, healthNormal, 1, barWidth * healthNormal, barHeight);
		String healthText = LanguageManager.translate("entity.health") + ": " + String.format("%.0f", this.creature.getHealth()) + "/" + String.format("%.0f", this.creature.getMaxHealth());
		this.fontRenderer.drawString(healthText, barCenter - (this.fontRenderer.getStringWidth(healthText) / 2), barY + 2, 0xFFFFFF);

		barY += barHeight + 1;
		this.drawTexture(AssetManager.getTexture("GUIPetBarEmpty"), barX, barY, 0, 1, 1, barWidth, barHeight);
		float experienceNormal = (float)this.creature.getExperience() / this.creature.creatureStats.getExperienceForNextLevel();
		this.drawTexture(AssetManager.getTexture("GUIBarExperience"), barX, barY, 0, experienceNormal, 1, barWidth * experienceNormal, barHeight);
		String experienceText = LanguageManager.translate("entity.experience") + ": " + this.creature.getExperience() + "/" + this.creature.creatureStats.getExperienceForNextLevel();
		this.fontRenderer.drawString(experienceText, barCenter - (this.fontRenderer.getStringWidth(experienceText) / 2), barY + 2, 0xFFFFFF);
	}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
        this.xSize = 176;
        this.ySize = 166;
        int backX = (this.width - this.xSize) / 2;
        int backY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(backX, backY, 0, 0, this.xSize, this.ySize);

		this.drawFrames(backX, backY, i, j);
		this.drawSlots(backX, backY);
	}
	
	// ========== Draw Creature Frame ===========
	protected void drawFrames(int backX, int backY, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
        
        // Status Frame:
        int statusWidth = 90;
        int statusHeight = 54;
        this.drawTexturedModalRect(backX + 79, backY + 17, 0, 256 - statusHeight, statusWidth, statusHeight);
        
        // Creature Frame:
        int creatureWidth = 54;
        int creatureHeight = 54;
        this.drawTexturedModalRect(backX - creatureWidth + 1, backY + 17, statusWidth, 256 - creatureHeight, creatureWidth, creatureHeight);
        GuiInventory.drawEntityOnScreen(backX + 26 - creatureWidth + 1, backY + 60, 17, (float) backX - mouseX, (float) backY - mouseY, this.creature);
	}
	
	// ========== Draw Slots ===========
	protected void drawSlots(int backX, int backY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
        
		BaseContainer container = (BaseContainer)this.inventorySlots;
		List<Slot> creatureSlots = container.inventorySlots.subList(container.specialStart, container.inventoryFinish + 1);
		int slotWidth = 18;
		int slotHeight = 18;
		int slotU = 238;
		int slotVBase = 0;
		for(Slot creatureSlot : creatureSlots) {
			int slotX = backX + creatureSlot.xPos - 1;
			int slotY = backY + creatureSlot.yPos - 1;
			int slotV = slotVBase;
			String slotType = creatureInventory.getTypeFromSlot(creatureSlot.getSlotIndex());
			if(slotType != null) {
				if(slotType.equals("saddle"))
					slotV += slotHeight;
				else if(slotType.equals("bag"))
					slotV += slotHeight * 2;
				else if(slotType.equals("chest"))
					slotV += slotHeight * 3;
			}
			this.drawTexturedModalRect(slotX, slotY, slotU, slotV, slotWidth, slotHeight);
		}
	}
	
	// ========== Draw Controls ===========
	protected void drawControls(int backX, int backY) {
		if(!(this.creature instanceof TameableCreatureEntity))
			return;
		TameableCreatureEntity pet = (TameableCreatureEntity)this.creature;
		if(!pet.petControlsEnabled())
			return;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = 128;
        int buttonHeight = 20;
        int buttonX = backX + this.xSize;
        int buttonY = backY;

		String buttonText = LanguageManager.translate("gui.pet.follow");
		buttonY += buttonSpacing;
		GuiButton button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.FOLLOW.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText);
		if(pet.isFollowing() && !pet.isSitting()) {
			button.enabled = false;
		}
		this.buttonList.add(button);

		buttonText = LanguageManager.translate("gui.pet.wander");
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.WANDER.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText);
		if(!pet.isFollowing() && !pet.isSitting()) {
			button.enabled = false;
		}
		this.buttonList.add(button);
        
        buttonText = LanguageManager.translate("gui.pet.sit");
        buttonY += buttonHeight + (buttonSpacing * 2);
		button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.SIT.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText);
		if(!pet.isFollowing() && pet.isSitting()) {
			button.enabled = false;
		}
        this.buttonList.add(button);
        
        buttonText = LanguageManager.translate("gui.pet.passive");
        buttonY += buttonHeight + (buttonSpacing * 2);
		button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.PASSIVE.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText);
		if(pet.isPassive()) {
			button.enabled = false;
		}
        this.buttonList.add(button);

		buttonText = LanguageManager.translate("gui.pet.defensive");
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.DEFENSIVE.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText);
		if(!pet.isPassive() && !pet.isAssisting() && !pet.isAggressive()) {
			button.enabled = false;
		}
		this.buttonList.add(button);

		buttonText = LanguageManager.translate("gui.pet.assist");
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.ASSIST.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText);
		if(!pet.isPassive() && pet.isAssisting() && !pet.isAggressive()) {
			button.enabled = false;
		}
		this.buttonList.add(button);
        
        buttonText = LanguageManager.translate("gui.pet.aggressive");
        buttonY += buttonHeight + (buttonSpacing * 2);
		button = new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.AGGRESSIVE.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText);
		if(!pet.isPassive() && pet.isAggressive()) {
			button.enabled = false;
		}
        this.buttonList.add(button);
        
        buttonText = LanguageManager.translate("gui.pet.pvp") + ": " + (pet.isPVP() ? LanguageManager.translate("common.yes") : LanguageManager.translate("common.no"));
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(BaseCreatureEntity.PET_COMMAND_ID.PVP.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText));
    }
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		if(guiButton != null) {
			MessageEntityGUICommand message = new MessageEntityGUICommand((byte)guiButton.id, this.creature);
			LycanitesMobs.packetHandler.sendToServer(message);			
	    }
		super.actionPerformed(guiButton);
	}
}
