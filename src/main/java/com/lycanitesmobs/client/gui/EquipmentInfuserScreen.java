package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.container.BaseContainer;
import com.lycanitesmobs.core.container.EquipmentInfuserChargeSlot;
import com.lycanitesmobs.core.container.EquipmentInfuserContainer;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.network.MessageTileEntityButton;
import com.lycanitesmobs.core.tileentity.EquipmentInfuserTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class EquipmentInfuserScreen extends BaseContainerScreen<EquipmentInfuserContainer> {
	public EquipmentInfuserTileEntity equipmentInfuser;

	public EquipmentInfuserScreen(EquipmentInfuserContainer container, PlayerInventory playerInventory, ITextComponent name) {
		super(container, playerInventory, name);
		this.equipmentInfuser = container.equipmentInfuser;
	}

	@Override
	public void init() {
		super.init();
		this.xSize = 176;
        this.ySize = 166;
	}

	@Override
	protected void initWidgets() {

	}

	@Override
	protected void renderBackground(int mouseX, int mouseY, float partialTicks) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.getMinecraft().getTextureManager().bindTexture(TextureManager.getTexture("GUIEquipmentForge"));
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
		this.getMinecraft().getTextureManager().bindTexture(TextureManager.getTexture("GUIEquipmentForge"));

		BaseContainer container = this.getContainer();
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
	protected void renderForeground(int mouseX, int mouseY, float partialTicks) {
		this.fontRenderer.drawString(this.equipmentInfuser.getName().getFormattedText(), this.guiLeft + 8, this.guiTop + 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getName().getFormattedText(), this.guiLeft + 8, this.guiTop + this.ySize - 96 + 2, 4210752);
		int backX = (this.width - this.xSize) / 2;
		int backY = (this.height - this.ySize) / 2;
		this.drawBars(backX, backY);
    }

	protected void drawBars(int backX, int backY) {
		int barWidth = 100;
		int barHeight = 11;
		int barX = (this.width / 2) - (barWidth / 2);
		int barY = backY + 58;
		int barCenter = barX + (barWidth / 2);
		this.drawTexture(TextureManager.getTexture("GUIPetBarEmpty"), barX, barY, 0, 1, 1, barWidth, barHeight);
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
		this.drawTexture(TextureManager.getTexture("GUIBarExperience"), barX, barY, 0, experienceNormal, 1, barWidth * experienceNormal, barHeight);
		String experienceText = new TranslationTextComponent("entity.experience").getFormattedText() + ": " + experience + "/" + experienceMax;
		this.fontRenderer.drawString(experienceText, barCenter - (this.fontRenderer.getStringWidth(experienceText) / 2), barY + 2, 0xFFFFFF);
	}
    
	@Override
	public void actionPerformed(int buttonid) {
		MessageTileEntityButton message = new MessageTileEntityButton(buttonid, this.equipmentInfuser.getPos());
		LycanitesMobs.packetHandler.sendToServer(message);
	}
}
