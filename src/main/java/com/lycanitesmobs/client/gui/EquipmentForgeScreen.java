package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.container.BaseContainer;
import com.lycanitesmobs.core.container.EquipmentForgeContainer;
import com.lycanitesmobs.core.container.EquipmentForgeSlot;
import com.lycanitesmobs.core.network.MessageTileEntityButton;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class EquipmentForgeScreen extends BaseContainerScreen<EquipmentForgeContainer> {
	public TileEntityEquipmentForge equipmentForge;
	public String currentMode = "empty";
	public boolean confirmation = false;

	public EquipmentForgeScreen(EquipmentForgeContainer container, PlayerInventory playerInventory, ITextComponent name) {
		super(container, playerInventory, name);
		this.equipmentForge = container.equipmentForge;
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
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int buttonSpacing = 2;
		int buttonWidth = 128;
		int buttonHeight = 20;
		int buttonX = backX + this.xSize;
		int buttonY = backY;

		String buttonText = "";
		if("construct".equals(this.currentMode)) {
			buttonText = new TranslationTextComponent("gui.equipmentforge.forge").getString();
		}
		else if("deconstruct".equals(this.currentMode)) {
			buttonText = new TranslationTextComponent("gui.equipmentforge.deconstruct").getString();
		}
		buttonY += buttonSpacing;
		//this.addButton(new ButtonBase(1, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this));
	}

	@Override
	protected void renderBackground(int mouseX, int mouseY, float partialTicks) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.getMinecraft().getTextureManager().bindTexture(TextureManager.getTexture("GUIEquipmentForge"));
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

			if(forgeSlot instanceof EquipmentForgeSlot) {
				EquipmentForgeSlot equipmentSlot =(EquipmentForgeSlot)forgeSlot;
				if("base".equals(equipmentSlot.type)) {
					slotV += slotHeight;
				}
				else if("head".equals(equipmentSlot.type)) {
					slotV += slotHeight * 2;
				}
				else if("blade".equals(equipmentSlot.type)) {
					slotV += slotHeight * 3;
				}
				else if("axe".equals(equipmentSlot.type)) {
					slotV += slotHeight * 4;
				}
				else if("pike".equals(equipmentSlot.type)) {
					slotV += slotHeight * 5;
				}
				else if("pommel".equals(equipmentSlot.type)) {
					slotV += slotHeight * 6;
				}
				else if("jewel".equals(equipmentSlot.type)) {
					slotV += slotHeight * 7;
				}
				else if("aura".equals(equipmentSlot.type)) {
					slotV += slotHeight * 8;
				}
			}

			this.drawTexturedModalRect(slotX, slotY, slotU, slotV, slotWidth, slotHeight);
		}
	}

	@Override
	protected void renderForeground(int mouseX, int mouseY, float partialTicks) {
		this.fontRenderer.drawString(this.equipmentForge.getName().getString(), this.guiLeft + 8, this.guiTop + 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getName().getString(), this.guiLeft + 8, this.guiTop + this.ySize - 96 + 2, 4210752);
    }
    
	@Override
	public void actionPerformed(int buttonid) {
		MessageTileEntityButton message = new MessageTileEntityButton(buttonid, this.equipmentForge.getPos());
		LycanitesMobs.packetHandler.sendToServer(message);
	}
}
