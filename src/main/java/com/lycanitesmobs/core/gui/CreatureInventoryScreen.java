package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.container.ContainerBase;
import com.lycanitesmobs.core.container.ContainerCreature;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.gui.buttons.ButtonBase;
import com.lycanitesmobs.core.inventory.InventoryCreature;
import com.lycanitesmobs.core.localisation.LanguageManager;
import com.lycanitesmobs.core.network.MessageEntityGUICommand;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class CreatureInventoryScreen extends BaseContainerScreen<ContainerCreature> {
	public EntityCreatureBase creature;
	public InventoryCreature creatureInventory;

	/**
	 * Constructor
	 */
	public CreatureInventoryScreen(ContainerCreature container, PlayerInventory playerInventory, ITextComponent name) {
		super(container, playerInventory, name);
		this.creature = container.creature;
		this.creatureInventory = container.creature.inventory;
	}

	@Override
	public void init() {
		super.init();
		this.xSize = 176;
        this.ySize = 166;
	}

	@Override
	protected void initWidgets() {
		int backX = (this.width - this.xSize) / 2;
		int backY = (this.height - this.ySize) / 2;

		if(!(this.creature instanceof EntityCreatureTameable))
			return;
		EntityCreatureTameable pet = (EntityCreatureTameable)this.creature;
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
		Button button = new ButtonBase(EntityCreatureBase.PET_COMMAND_ID.FOLLOW.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(pet.isFollowing() && !pet.isSitting()) {
			button.active = false;
		}
		this.buttons.add(button);

		buttonText = LanguageManager.translate("gui.pet.wander");
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new ButtonBase(EntityCreatureBase.PET_COMMAND_ID.WANDER.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(!pet.isFollowing() && !pet.isSitting()) {
			button.active = false;
		}
		this.buttons.add(button);

		buttonText = LanguageManager.translate("gui.pet.sit");
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new ButtonBase(EntityCreatureBase.PET_COMMAND_ID.SIT.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(!pet.isFollowing() && pet.isSitting()) {
			button.active = false;
		}
		this.buttons.add(button);

		buttonText = LanguageManager.translate("gui.pet.passive");
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new ButtonBase(EntityCreatureBase.PET_COMMAND_ID.PASSIVE.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(pet.isPassive()) {
			button.active = false;
		}
		this.buttons.add(button);

		buttonText = LanguageManager.translate("gui.pet.defensive");
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new ButtonBase(EntityCreatureBase.PET_COMMAND_ID.DEFENSIVE.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(!pet.isPassive() && !pet.isAssisting() && !pet.isAggressive()) {
			button.active = false;
		}
		this.buttons.add(button);

		buttonText = LanguageManager.translate("gui.pet.assist");
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new ButtonBase(EntityCreatureBase.PET_COMMAND_ID.ASSIST.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(!pet.isPassive() && pet.isAssisting() && !pet.isAggressive()) {
			button.active = false;
		}
		this.buttons.add(button);

		buttonText = LanguageManager.translate("gui.pet.aggressive");
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new ButtonBase(EntityCreatureBase.PET_COMMAND_ID.AGGRESSIVE.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(!pet.isPassive() && pet.isAggressive()) {
			button.active = false;
		}
		this.buttons.add(button);

		buttonText = LanguageManager.translate("gui.pet.pvp") + ": " + (pet.isPVP() ? LanguageManager.translate("common.yes") : LanguageManager.translate("common.no"));
		buttonY += buttonHeight + (buttonSpacing * 2);
		this.buttons.add(new ButtonBase(EntityCreatureBase.PET_COMMAND_ID.PVP.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this));
	}

	@Override
	protected void renderForeground(int mouseX, int mouseY, float partialTicks) {
		this.fontRenderer.drawString(this.creatureInventory.getName(), 8, 6, 4210752);
        this.fontRenderer.drawString(LanguageManager.translate(this.playerInventory.getName().toString()), 8, this.ySize - 96 + 2, 4210752);
    }

	@Override
	protected void renderBackground(int mouseX, int mouseY, float partialTicks) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
        this.xSize = 176;
        this.ySize = 166;
        int backX = (this.width - this.xSize) / 2;
        int backY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(backX, backY, 0, 0, this.xSize, this.ySize);

		this.drawFrames(backX, backY, mouseX, mouseY);
		this.drawHealth(backX, backY);
		this.drawSlots(backX, backY);
	}

	protected void drawFrames(int backX, int backY, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
        
        // Status Frame:
        int statusWidth = 90;
        int statusHeight = 54;
        this.drawTexturedModalRect(backX + 79, backY + 17, 0, 256 - statusHeight, statusWidth, statusHeight);
        
        // Creature Frame:
        int creatureWidth = 54;
        int creatureHeight = 54;
        this.drawTexturedModalRect(backX - creatureWidth + 1, backY + 17, statusWidth, 256 - creatureHeight, creatureWidth, creatureHeight);
		InventoryScreen.drawEntityOnScreen(backX + 26 - creatureWidth + 1, backY + 60, 17, (float) backX - mouseX, (float) backY - mouseY, this.creature);
	}

	protected void drawHealth(int backX, int backY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
        
        // Empty:
        int barWidth = 80;
        int barHeight = 11;
        int barX = backX + 91;
        int barY = backY + 5;
        int barU = 144;
        int barV = 256 - (barHeight * 2);
        this.drawTexturedModalRect(barX, barY, barU, barV, barWidth, barHeight);
        
        // Full:
        barWidth = Math.round(barWidth * (this.creature.getHealth() / this.creature.getMaxHealth()));
        barV = barV + barHeight;
        this.drawTexturedModalRect(barX, barY, barU, barV, barWidth, barHeight);
	}

	protected void drawSlots(int backX, int backY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
        
		ContainerBase container = this.getContainer();
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

	@Override
	public void actionPerformed(byte buttonId) {
		MessageEntityGUICommand message = new MessageEntityGUICommand(buttonId, this.creature);
		LycanitesMobs.packetHandler.sendToServer(message);
	}
}
