package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.CreatureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import org.lwjgl.opengl.GL11;

public class GuiButtonCreature extends ButtonBase {
	public CreatureInfo creatureInfo;

	public GuiButtonCreature(int buttonID, int x, int y, String text, CreatureInfo creatureInfo, Button.IPressable pressable) {
        super(buttonID, x, y, 32, 32, text, pressable);
        this.creatureInfo = creatureInfo;
    }
	
	public GuiButtonCreature(int buttonID, int x, int y, int w, int h, String text, CreatureInfo creatureInfo, Button.IPressable pressable) {
        super(buttonID, x, y, w, h, text, pressable);
        this.creatureInfo = creatureInfo;
    }
	

	@Override
	public void renderButton(int mouseX, int mouseY, float partialTicks) {
        if(!this.visible) {
			return;
		}
        super.renderButton(mouseX, mouseY, partialTicks);

		/*GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

		int buttonW = this.width;
		int buttonH = this.height;
		int buttonX = this.x;
		int buttonY = this.y;
		Minecraft.getInstance().getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
		this.drawTexturedModalRect(buttonX, buttonY, 193, 187 - (hoverState * 32), this.width, this.height);
		if(this.creatureInfo != null) {
			Minecraft.getInstance().getTextureManager().bindTexture(creatureInfo.getIcon());
			this.drawTexturedModalRect(buttonX + 8, buttonY + 8, 0, 0, 16, 16, 16);
		}

		//this.mouseDragged(Minecraft.getInstance(), mouseX, mouseY);
		int textColor = 14737632;

		if(!this.active) {
			textColor = -6250336;
		}
		else if(this.isHovered()) {
			textColor = 16777120;
		}

		this.drawCenteredString(this.fontRenderer, this.displayString, buttonX + 5, buttonY + 2, textColor);*/
    }
}
