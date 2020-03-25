package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.client.gui.buttons.ButtonBase;
import com.lycanitesmobs.core.container.BaseContainer;
import com.lycanitesmobs.core.container.CreatureContainer;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.inventory.InventoryCreature;
import com.lycanitesmobs.core.network.MessageEntityGUICommand;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class CreatureInventoryScreen extends BaseContainerScreen<CreatureContainer> {
	public BaseCreatureEntity creature;
	public InventoryCreature creatureInventory;

	/**
	 * Constructor
	 */
	public CreatureInventoryScreen(CreatureContainer container, PlayerInventory playerInventory, ITextComponent name) {
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

		String buttonText = new TranslationTextComponent("gui.pet.follow").getFormattedText();
		buttonY += buttonSpacing;
		Button button = new ButtonBase(BaseCreatureEntity.PET_COMMAND_ID.FOLLOW.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(pet.isFollowing() && !pet.isSitting()) {
			button.active = false;
		}
		this.addButton(button);

		buttonText = new TranslationTextComponent("gui.pet.wander").getFormattedText();
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new ButtonBase(BaseCreatureEntity.PET_COMMAND_ID.WANDER.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(!pet.isFollowing() && !pet.isSitting()) {
			button.active = false;
		}
		this.addButton(button);

		buttonText = new TranslationTextComponent("gui.pet.sit").getFormattedText();
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new ButtonBase(BaseCreatureEntity.PET_COMMAND_ID.SIT.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(!pet.isFollowing() && pet.isSitting()) {
			button.active = false;
		}
		this.addButton(button);

		buttonText = new TranslationTextComponent("gui.pet.passive").getFormattedText();
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new ButtonBase(BaseCreatureEntity.PET_COMMAND_ID.PASSIVE.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(pet.isPassive()) {
			button.active = false;
		}
		this.addButton(button);

		buttonText = new TranslationTextComponent("gui.pet.defensive").getFormattedText();
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new ButtonBase(BaseCreatureEntity.PET_COMMAND_ID.DEFENSIVE.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(!pet.isPassive() && !pet.isAssisting() && !pet.isAggressive()) {
			button.active = false;
		}
		this.addButton(button);

		buttonText = new TranslationTextComponent("gui.pet.assist").getFormattedText();
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new ButtonBase(BaseCreatureEntity.PET_COMMAND_ID.ASSIST.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(!pet.isPassive() && pet.isAssisting() && !pet.isAggressive()) {
			button.active = false;
		}
		this.addButton(button);

		buttonText = new TranslationTextComponent("gui.pet.aggressive").getFormattedText();
		buttonY += buttonHeight + (buttonSpacing * 2);
		button = new ButtonBase(BaseCreatureEntity.PET_COMMAND_ID.AGGRESSIVE.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this);
		if(!pet.isPassive() && pet.isAggressive()) {
			button.active = false;
		}
		this.addButton(button);

		buttonText = new TranslationTextComponent("gui.pet.pvp").appendText(": ").appendSibling(pet.isPVP() ? new TranslationTextComponent("common.yes") : new TranslationTextComponent("common.no")).getFormattedText();
		buttonY += buttonHeight + (buttonSpacing * 2);
		this.addButton(new ButtonBase(BaseCreatureEntity.PET_COMMAND_ID.PVP.id, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this));
	}

	@Override
	protected void renderForeground(int mouseX, int mouseY, float partialTicks) {
		this.fontRenderer.drawString(this.creatureInventory.getName(), this.guiLeft + 8, this.guiTop + 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getName().getFormattedText(), this.guiLeft + 8, this.guiTop + this.ySize - 96 + 2, 4210752);
		int backX = (this.width - this.xSize) / 2;
		int backY = (this.height - this.ySize) / 2;
		this.drawBars(backX, backY);
    }

	protected void drawBars(int backX, int backY) {
		int barWidth = 100;
		int barHeight = 11;
		int barX = backX - barWidth;
		int barY = backY + 54 + 18;
		int barCenter = barX + (barWidth / 2);
		this.drawTexture(TextureManager.getTexture("GUIPetBarEmpty"), barX, barY, 0, 1, 1, barWidth, barHeight);
		float healthNormal = Math.min(1, this.creature.getHealth() / this.creature.getMaxHealth());
		this.drawTexture(TextureManager.getTexture("GUIPetBarHealth"), barX, barY, 0, healthNormal, 1, barWidth * healthNormal, barHeight);
		String healthText = new TranslationTextComponent("entity.health").getFormattedText() + ": " + String.format("%.0f", this.creature.getHealth()) + "/" + String.format("%.0f", this.creature.getMaxHealth());
		this.fontRenderer.drawString(healthText, barCenter - (this.fontRenderer.getStringWidth(healthText) / 2), barY + 2, 0xFFFFFF);

		barY += barHeight + 1;
		this.drawTexture(TextureManager.getTexture("GUIPetBarEmpty"), barX, barY, 0, 1, 1, barWidth, barHeight);
		float experienceNormal = Math.min(1, (float)this.creature.getExperience() / this.creature.creatureStats.getExperienceForNextLevel());
		this.drawTexture(TextureManager.getTexture("GUIBarExperience"), barX, barY, 0, experienceNormal, 1, barWidth * experienceNormal, barHeight);
		String experienceText = new TranslationTextComponent("entity.experience").getFormattedText() + ": " + this.creature.getExperience() + "/" + this.creature.creatureStats.getExperienceForNextLevel();
		this.fontRenderer.drawString(experienceText, barCenter - (this.fontRenderer.getStringWidth(experienceText) / 2), barY + 2, 0xFFFFFF);
	}

	@Override
	protected void renderBackground(int mouseX, int mouseY, float partialTicks) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(TextureManager.getTexture("GUIInventoryCreature"));
        this.xSize = 176;
        this.ySize = 166;
        int backX = (this.width - this.xSize) / 2;
        int backY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(backX, backY, 0, 0, this.xSize, this.ySize);

		this.drawFrames(backX, backY, mouseX, mouseY);
		this.drawSlots(backX, backY);
	}

	protected void drawFrames(int backX, int backY, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(TextureManager.getTexture("GUIInventoryCreature"));
        
        // Status Frame:
        int statusWidth = 90;
        int statusHeight = 54;
        this.drawTexturedModalRect(backX + 79, backY + 17, 0, 256 - statusHeight, statusWidth, statusHeight);
        
        // Creature Frame:
        int creatureWidth = 54;
        int creatureHeight = 54;
        this.drawTexturedModalRect(backX - creatureWidth + 1, backY + 17, statusWidth, 256 - creatureHeight, creatureWidth, creatureHeight);
		BaseGui.renderLivingEntity(backX + 26 - creatureWidth + 1, backY + 60, 17, (float) backX - mouseX, (float) backY - mouseY, this.creature); // drawEntityOnScreen()
	}

	protected void drawSlots(int backX, int backY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(TextureManager.getTexture("GUIInventoryCreature"));
        
		BaseContainer container = this.getContainer();
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
	public void actionPerformed(int buttonId) {
		MessageEntityGUICommand message = new MessageEntityGUICommand(buttonId, this.creature);
		LycanitesMobs.packetHandler.sendToServer(message);
	}
}
