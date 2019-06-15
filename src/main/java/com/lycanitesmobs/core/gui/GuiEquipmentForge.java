package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.container.ContainerBase;
import com.lycanitesmobs.core.container.ContainerEquipmentForge;
import com.lycanitesmobs.core.container.SlotEquipment;
import com.lycanitesmobs.core.localisation.LanguageManager;
import com.lycanitesmobs.core.network.MessageTileEntityButton;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiEquipmentForge extends GuiBaseContainer {
	public TileEntityEquipmentForge equipmentForge;
	public String currentMode = "empty";
	public boolean confirmation = false;

	/**
	 * Constructor
	 * @param equipmentForge
	 * @param playerInventory
	 */
	public GuiEquipmentForge(TileEntityEquipmentForge equipmentForge, PlayerInventory playerInventory) {
		super(new ContainerEquipmentForge(equipmentForge, playerInventory), playerInventory, new TranslationTextComponent(equipmentForge.getName()));
		this.playerInventory = playerInventory;
		this.equipmentForge = equipmentForge;
	}


	/**
	 * Initializes this Forge GUI.
	 */
	@Override
	public void init() {
		super.init();
		this.xSize = 176;
        this.ySize = 166;
        int backX = (this.width - this.xSize) / 2;
        int backY = (this.height - this.ySize) / 2;
		//this.initControls(backX, backY);
	}


	/**
	 * Draws the foreground GUI layer.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawString(this.equipmentForge.getName(), 8, 6, 4210752);
        this.fontRenderer.drawString(LanguageManager.translate(this.playerInventory.getName().toString()), 8, this.ySize - 96 + 2, 4210752);
    }


	/**
	 * Draws the background GUI layer.
	 * @param partialTicks Ticks used for animation.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(AssetManager.getTexture("GUIEquipmentForge"));
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
        this.getMinecraft().getTextureManager().bindTexture(AssetManager.getTexture("GUIEquipmentForge"));
        
		ContainerBase container = this.getContainer();
		List<Slot> forgeSlots = container.inventorySlots.subList(container.inventoryStart, container.inventoryFinish);
		int slotWidth = 18;
		int slotHeight = 18;
		int slotU = 238;
		int slotVBase = 0;
		for(Slot forgeSlot : forgeSlots) {
			int slotX = backX + forgeSlot.xPos - 1;
			int slotY = backY + forgeSlot.yPos - 1;
			int slotV = slotVBase;

			if(forgeSlot instanceof SlotEquipment) {
				SlotEquipment slotEquipment =(SlotEquipment)forgeSlot;
				if("base".equals(slotEquipment.type)) {
					slotV += slotHeight;
				}
				else if("head".equals(slotEquipment.type)) {
					slotV += slotHeight * 2;
				}
				else if("blade".equals(slotEquipment.type)) {
					slotV += slotHeight * 3;
				}
				else if("axe".equals(slotEquipment.type)) {
					slotV += slotHeight * 4;
				}
				else if("pike".equals(slotEquipment.type)) {
					slotV += slotHeight * 5;
				}
				else if("pommel".equals(slotEquipment.type)) {
					slotV += slotHeight * 6;
				}
				else if("jewel".equals(slotEquipment.type)) {
					slotV += slotHeight * 7;
				}
				else if("aura".equals(slotEquipment.type)) {
					slotV += slotHeight * 8;
				}
			}

			this.drawTexturedModalRect(slotX, slotY, slotU, slotV, slotWidth, slotHeight);
		}
	}


	/**
	 * Creates and initially positions controls such as buttons.
	 * @param backX
	 * @param backY
	 */
	protected void initControls(int backX, int backY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = 128;
        int buttonHeight = 20;
        int buttonX = backX + this.xSize;
        int buttonY = backY;
        
        String buttonText = "";
        if("construct".equals(this.currentMode)) {
			buttonText = LanguageManager.translate("gui.equipmentforge.forge");
		}
		else if("deconstruct".equals(this.currentMode)) {
			buttonText = LanguageManager.translate("gui.equipmentforge.deconstruct");
		}
        buttonY += buttonSpacing;
        this.buttons.add(new ButtonBase(1, buttonX + buttonSpacing, buttonY, buttonWidth, buttonHeight, buttonText, this));
    }
    
	@Override
	public void actionPerformed(byte buttonid) {
		MessageTileEntityButton message = new MessageTileEntityButton(buttonid, this.equipmentForge.getPos());
		LycanitesMobs.packetHandler.sendToServer(message);
	}
}
