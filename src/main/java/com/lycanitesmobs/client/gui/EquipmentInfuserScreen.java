package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.container.BaseContainer;
import com.lycanitesmobs.core.container.EquipmentInfuserChargeSlot;
import com.lycanitesmobs.core.container.EquipmentInfuserContainer;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.network.MessageTileEntityButton;
import com.lycanitesmobs.core.tileentity.EquipmentInfuserTileEntity;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

public class EquipmentInfuserScreen extends BaseContainerScreen {
	public EquipmentInfuserTileEntity equipmentInfuser;
	public InventoryPlayer playerInventory;

	public EquipmentInfuserScreen(EquipmentInfuserTileEntity equipmentInfuser, InventoryPlayer playerInventory) {
		super(new EquipmentInfuserContainer(equipmentInfuser, playerInventory));
		this.equipmentInfuser = equipmentInfuser;
		this.playerInventory = playerInventory;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.xSize = 176;
        this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIEquipmentForge"));
		this.xSize = 176;
		this.ySize = 166;
		int backX = (this.width - this.xSize) / 2;
		int backY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(backX, backY, 0, 0, this.xSize, this.ySize);

		this.drawSlots(backX, backY);
	}

	/**
	 * Draws each Equipment Slot.
	 * @param backX
	 * @param backY
	 */
	protected void drawSlots(int backX, int backY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIEquipmentForge"));

		BaseContainer container = (BaseContainer)this.inventorySlots;
		List<Slot> forgeSlots = container.inventorySlots.subList(container.inventoryStart, container.inventoryFinish);
		int slotWidth = 18;
		int slotHeight = 18;
		int slotU = 238;
		int slotVBase = 0;
		for(Slot forgeSlot : forgeSlots) {
			int slotX = backX + forgeSlot.xPos - 1;
			int slotY = backY + forgeSlot.yPos - 1;
			int slotV = slotVBase;

			if(forgeSlot instanceof EquipmentInfuserChargeSlot) {
				slotV += slotHeight * 9;
			}

			this.drawTexturedModalRect(slotX, slotY, slotU, slotV, slotWidth, slotHeight);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawString(this.equipmentInfuser.getName(), 8, 6, 4210752);
        this.fontRenderer.drawString(LanguageManager.translate(this.playerInventory.getName()), 8, this.ySize - 96 + 2, 4210752);
		int backX = (this.width - this.xSize) / 2;
		int backY = (this.height - this.ySize) / 2;
		this.drawBars(0, 0);
    }

	protected void drawBars(int backX, int backY) {
		int barWidth = 100;
		int barHeight = 11;
		int barX = (barWidth / 2) - 10;
		int barY = backY + 58;
		int barCenter = barX + (barWidth / 2);
		this.drawTexture(AssetManager.getTexture("GUIPetBarEmpty"), barX, barY, 0, 1, 1, barWidth, barHeight);
		ItemStack partStack = this.equipmentInfuser.getStackInSlot(1);
		if(!(partStack.getItem() instanceof ItemEquipmentPart)) {
			return;
		}
		ItemEquipmentPart partItem = (ItemEquipmentPart)partStack.getItem();
		if(partItem.getLevel(partStack) >= partItem.levelMax) {
			return;
		}
		int experience = partItem.getExperience(partStack);
		int experienceMax = partItem.getExperienceForNextLevel(partStack);
		float experienceNormal = (float)experience / experienceMax;
		this.drawTexture(AssetManager.getTexture("GUIBarExperience"), barX, barY, 0, experienceNormal, 1, barWidth * experienceNormal, barHeight);
		String experienceText = LanguageManager.translate("entity.experience") + ": " + experience + "/" + experienceMax;
		this.fontRenderer.drawString(experienceText, barCenter - (this.fontRenderer.getStringWidth(experienceText) / 2), barY + 2, 0xFFFFFF);
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		if(guiButton != null) {
			MessageTileEntityButton message = new MessageTileEntityButton((byte)guiButton.id, this.equipmentInfuser.getPos());
			LycanitesMobs.packetHandler.sendToServer(message);
		}
		super.actionPerformed(guiButton);
	}
}
