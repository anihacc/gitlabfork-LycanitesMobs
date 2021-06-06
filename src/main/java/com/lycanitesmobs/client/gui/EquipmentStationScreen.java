package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.container.BaseContainer;
import com.lycanitesmobs.core.container.EquipmentStationContainer;
import com.lycanitesmobs.core.container.EquipmentStationRepairSlot;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.network.MessageTileEntityButton;
import com.lycanitesmobs.core.tileentity.EquipmentStationTileEntity;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

public class EquipmentStationScreen extends BaseContainerScreen {
	public EquipmentStationTileEntity equipmentStation;
	public InventoryPlayer playerInventory;

	public EquipmentStationScreen(EquipmentStationTileEntity tileEntity, InventoryPlayer playerInventory) {
		super(new EquipmentStationContainer(tileEntity, playerInventory));
		this.equipmentStation = tileEntity;
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

			if(forgeSlot instanceof EquipmentStationRepairSlot) {
				slotV += slotHeight * 9;
			}

			this.drawTexturedModalRect(slotX, slotY, slotU, slotV, slotWidth, slotHeight);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawString(this.equipmentStation.getName(), 8, 6, 4210752);
        this.fontRenderer.drawString(LanguageManager.translate(this.playerInventory.getName()), 8, this.ySize - 96 + 2, 4210752);
		int backX = (this.width - this.xSize) / 2;
		int backY = (this.height - this.ySize) / 2;
		this.drawBars(0, 0);
    }

	protected void drawBars(int backX, int backY) {
		int barWidth = 100;
		int barHeight = 11;
		int barX = (barWidth / 2) - 10;
		int barY = backY + 48;
		int manaBarY = barY + 12;
		int barCenter = barX + (barWidth / 2);
		this.drawTexture(AssetManager.getTexture("GUIPetBarEmpty"), barX, barY, 0, 1, 1, barWidth, barHeight);
		this.drawTexture(AssetManager.getTexture("GUIPetBarEmpty"), barX, barY, 0, 1, 1, barWidth, barHeight);
		this.drawTexture(AssetManager.getTexture("GUIPetBarEmpty"), barX, manaBarY, 0, 1, 1, barWidth, barHeight);
		this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIEquipmentForge"));
		this.drawTexturedModalRect(barX - 14, barY, 225, 158, 13, 10);
		this.drawTexturedModalRect(barX - 14, manaBarY, 225, 170, 13, 10);
		ItemStack partStack = this.equipmentStation.getStackInSlot(1);
		if(!(partStack.getItem() instanceof ItemEquipment)) {
			return;
		}
		ItemEquipment equipmentItem = (ItemEquipment)partStack.getItem();

		// Sharpness:
		int sharpness = equipmentItem.getSharpness(partStack);
		int sharpnessMax = ItemEquipment.SHARPNESS_MAX;
		float sharpnessNormal = (float)sharpness / sharpnessMax;
		this.drawTexture(AssetManager.getTexture("GUIPetBarEmpty"), barX, barY, 0, 1, 1, barWidth, barHeight);
		this.drawTexture(AssetManager.getTexture("GUIPetBarHealth"), barX, barY, 0, sharpnessNormal, 1, barWidth * sharpnessNormal, barHeight);
		String sharpnessText = sharpness + "/" + sharpnessMax;
		this.fontRenderer.drawString(sharpnessText, barCenter - (this.fontRenderer.getStringWidth(sharpnessText) / 2), barY + 2, 0xFFFFFF, true);

		// Mana:
		int mana = equipmentItem.getMana(partStack);
		int manaMax = ItemEquipment.SHARPNESS_MAX;
		float manaNormal = (float)mana / manaMax;
		this.drawTexture(AssetManager.getTexture("GUIPetBarEmpty"), barX, manaBarY, 0, 1, 1, barWidth, barHeight);
		this.drawTexture(AssetManager.getTexture("GUIPetBarRespawn"), barX, manaBarY, 0, manaNormal, 1, barWidth * manaNormal, barHeight);
		String manaText = mana + "/" + manaMax;
		this.fontRenderer.drawString(manaText, barCenter - (this.fontRenderer.getStringWidth(manaText) / 2), manaBarY + 2, 0xFFFFFF, true);
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		if(guiButton != null) {
			MessageTileEntityButton message = new MessageTileEntityButton((byte)guiButton.id, this.equipmentStation.getPos());
			LycanitesMobs.packetHandler.sendToServer(message);
		}
		super.actionPerformed(guiButton);
	}
}
