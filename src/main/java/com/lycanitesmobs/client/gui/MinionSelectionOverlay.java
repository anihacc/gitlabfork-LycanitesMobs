package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.buttons.ButtonBase;
import com.lycanitesmobs.client.gui.buttons.CreatureButton;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.network.MessageSummonSetSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

public class MinionSelectionOverlay extends BaseScreen {
	public PlayerEntity player;
	public ExtendedPlayer playerExt;
	
	int centerX;
	int centerY;
	int windowWidth;
	int windowHeight;
	int windowX;
	int windowY;

	/**
	 * Constructor
	 * @param player The player opening this Screen.
	 */
    public MinionSelectionOverlay(PlayerEntity player) {
		super(new TranslationTextComponent("gui.minion.selection"));
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
	
	@Override
	public void init() {
		super.init();
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
		this.windowWidth = 256;
        this.windowHeight = 256;
        this.windowX = this.centerX;
        this.windowY = this.centerY;
	}

	@Override
	protected void initWidgets() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int buttonWidth = 32;
		int buttonHeight = 32;
		int buttonX = this.centerX - Math.round(buttonWidth / 2);
		int buttonY = this.centerY - Math.round(buttonHeight / 2);
		ButtonBase button;
		CreatureInfo creatureInfo;
		int offset = 32;

		creatureInfo = this.playerExt.getSummonSet(1).getCreatureInfo();
		button = new CreatureButton(1, buttonX, buttonY - Math.round(offset * 2), buttonWidth, buttonHeight, "" + 1, creatureInfo, this);
		this.addButton(button);

		creatureInfo = this.playerExt.getSummonSet(2).getCreatureInfo();
		button = new CreatureButton(2, buttonX + Math.round(offset * 2), buttonY - Math.round(offset * 0.5F), buttonWidth, buttonHeight, "" + 2, creatureInfo, this);
		this.addButton(button);

		creatureInfo = this.playerExt.getSummonSet(3).getCreatureInfo();
		button = new CreatureButton(3, buttonX + Math.round(offset * 1), buttonY +  Math.round(offset * 1.75F), buttonWidth, buttonHeight, "" + 3, creatureInfo, this);
		this.addButton(button);

		creatureInfo = this.playerExt.getSummonSet(4).getCreatureInfo();
		button = new CreatureButton(4, buttonX - Math.round(offset * 1), buttonY +  Math.round(offset * 1.75F), buttonWidth, buttonHeight, "" + 4, creatureInfo, this);
		this.addButton(button);

		creatureInfo = this.playerExt.getSummonSet(5).getCreatureInfo();
		button = new CreatureButton(5, buttonX - Math.round(offset * 2), buttonY - Math.round(offset * 0.5F), buttonWidth, buttonHeight, "" + 5, creatureInfo, this);
		this.addButton(button);
	}

	@Override
	protected void renderForeground(int mouseX, int mouseY, float partialTicks) {}

	@Override
	protected void renderWidgets(int mouseX, int mouseY, float partialTicks) {
		for(Object buttonObj : this.buttons) {
			if(buttonObj instanceof ButtonBase) {
				ButtonBase button = (ButtonBase)buttonObj;
				button.visible = this.playerExt.getSummonSet(button.buttonId).isUseable();
				button.active = button.buttonId != this.playerExt.selectedSummonSet;
			}
		}
	}

	@Override
	protected void renderBackground(int mouseX, int mouseY, float partialTicks) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(TextureManager.getTexture("GUIMinion"));
	}
	
	@Override
	public void actionPerformed(int buttonId) {
		this.playerExt.setSelectedSummonSet(buttonId);
		MessageSummonSetSelection message = new MessageSummonSetSelection(this.playerExt);
		LycanitesMobs.packetHandler.sendToServer(message);
	}
	
	@Override
	public boolean keyPressed(int par1, int par2, int par3) {
		if(par2 == 1 || par2 == Minecraft.getInstance().gameSettings.keyBindInventory.getKey().getKeyCode())
        	 Minecraft.getInstance().player.closeScreen();
		return super.keyPressed(par1, par2, par3);
	}
}
