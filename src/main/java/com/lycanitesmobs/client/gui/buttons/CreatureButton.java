package com.lycanitesmobs.client.gui.buttons;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.lwjgl.opengl.GL11;

public class CreatureButton extends ButtonBase {
	public CreatureInfo creatureInfo;
	public int summonSetId = 0;
	
	public CreatureButton(int buttonID, int x, int y, int w, int h, Component text, int summonSetId, CreatureInfo creatureInfo, Button.OnPress pressable) {
        super(buttonID, x, y, w, h, text, pressable);
        this.summonSetId = summonSetId;
        this.creatureInfo = creatureInfo;
    }
	

	@Override
	public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(!this.visible) {
			return;
		}
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

		int buttonX = this.x;
		int buttonY = this.y;
		Minecraft.getInstance().getTextureManager().bind(TextureManager.getTexture("GUIInventoryCreature"));
		int stateVOffset = 0;
		if(this.isHovered()) {
			stateVOffset = 64;
		}
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(Minecraft.getInstance().player);
		if(playerExt != null && playerExt.selectedSummonSet == this.summonSetId) {
			stateVOffset = 32;
		}
		this.drawHelper.drawTexturedModalRect(matrixStack, buttonX, buttonY, 193, 187 -stateVOffset, this.width, this.height);
		if(this.creatureInfo != null) {
			Minecraft.getInstance().getTextureManager().bind(creatureInfo.getIcon());
			this.drawHelper.drawTexturedModalRect(matrixStack, buttonX + 8, buttonY + 8, 0, 0, 16, 16, 16);
		}

		//this.mouseDragged(Minecraft.getInstance(), mouseX, mouseY);
		int textColor = 14737632;

		if(!this.active) {
			textColor = -6250336;
		}
		else if(this.isHovered()) {
			textColor = 16777120;
		}

		this.drawCenteredString(matrixStack, this.drawHelper.getFontRenderer(), this.getMessage(), buttonX + 5, buttonY + 2, textColor);
    }
}
