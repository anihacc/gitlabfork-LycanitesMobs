package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.network.MessageSummonSetSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

public class GuiMinionSelection extends GuiBaseScreen {
	public PlayerEntity player;
	public ExtendedPlayer playerExt;
	
	int centerX;
	int centerY;
	int windowWidth;
	int windowHeight;
	int windowX;
	int windowY;
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public static void openToPlayer(PlayerEntity player) {
		//if(player != null)
			//player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.getEntityWorld(), GuiHandler.PlayerGuiType.MINION_SELECTION.id, 0, 0);
	}
	
	public boolean doesGuiPauseGame() {
        return false;
    }
	
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GuiMinionSelection(PlayerEntity player) {
		super(new TranslationTextComponent("gui.minion.selection"));
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);
	}
	
	
	// ==================================================
  	//                       Init
  	// ==================================================
	@Override
	public void init() {
		super.init();
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
		this.windowWidth = 256;
        this.windowHeight = 256;
        this.windowX = this.centerX;
        this.windowY = this.centerY;
		this.drawControls();
	}
	
	
	// ==================================================
  	//                    Draw Screen
  	// ==================================================
	@Override
	public void render(int x, int y, float f) {
        this.drawGuiContainerBackgroundLayer();
        this.updateControls();
        this.drawGuiContainerForegroundLayer();

        super.render(x, y, f);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer() {}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	protected void drawGuiContainerBackgroundLayer() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(AssetManager.getTexture("GUIMinion"));
	}
	
	
	// ==================================================
  	//                    Controls
  	// ==================================================
	protected void drawControls() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonWidth = 32;
        int buttonHeight = 32;
        int buttonX = this.centerX - Math.round(buttonWidth / 2);
        int buttonY = this.centerY - Math.round(buttonHeight / 2);
        ButtonBase button;
        CreatureInfo creatureInfo;
        int offset = 32;
        
        creatureInfo = this.playerExt.getSummonSet(1).getCreatureInfo();
        button = new GuiButtonCreature(1, buttonX, buttonY - Math.round(offset * 2), buttonWidth, buttonHeight, "" + 1, creatureInfo, this);
    	this.buttons.add(button);
    	
        creatureInfo = this.playerExt.getSummonSet(2).getCreatureInfo();
        button = new GuiButtonCreature(2, buttonX + Math.round(offset * 2), buttonY - Math.round(offset * 0.5F), buttonWidth, buttonHeight, "" + 2, creatureInfo, this);
    	this.buttons.add(button);
    	
        creatureInfo = this.playerExt.getSummonSet(3).getCreatureInfo();
        button = new GuiButtonCreature(3, buttonX + Math.round(offset * 1), buttonY +  Math.round(offset * 1.75F), buttonWidth, buttonHeight, "" + 3, creatureInfo, this);
    	this.buttons.add(button);
    	
        creatureInfo = this.playerExt.getSummonSet(4).getCreatureInfo();
        button = new GuiButtonCreature(4, buttonX - Math.round(offset * 1), buttonY +  Math.round(offset * 1.75F), buttonWidth, buttonHeight, "" + 4, creatureInfo, this);
    	this.buttons.add(button);
    	
        creatureInfo = this.playerExt.getSummonSet(5).getCreatureInfo();
        button = new GuiButtonCreature(5, buttonX - Math.round(offset * 2), buttonY - Math.round(offset * 0.5F), buttonWidth, buttonHeight, "" + 5, creatureInfo, this);
    	this.buttons.add(button);
        
        /*for(int setID = 1; setID <= 5; setID++) {
        	float offset = ((float)setID / 6) - 0.5F;
        	offset = (float)Math.sin(offset);
        	LycanitesMobs.printDebug("", "" + offset);
        	int posX = this.centerX + Math.round(this.windowWidth * offset) - Math.round(buttonWidth / 2);
        	int posY = this.centerY + Math.round(this.windowHeight * offset) - Math.round(buttonHeight / 2);
        	GuiButton button = new GuiButton(setID, posX, posY, buttonWidth, buttonHeight, "" + setID);
        	this.buttonList.add(button);
        }*/
    }
	
	public void updateControls() {
        for(Object buttonObj : this.buttons) {
        	if(buttonObj instanceof ButtonBase) {
				ButtonBase button = (ButtonBase)buttonObj;
        		button.visible = this.playerExt.getSummonSet(button.buttonId).isUseable();
        		button.active = button.buttonId != this.playerExt.selectedSummonSet;
        	}
        }
	}
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	public void actionPerformed(byte buttonId) {
		this.playerExt.setSelectedSummonSet(buttonId);
		MessageSummonSetSelection message = new MessageSummonSetSelection(this.playerExt);
		LycanitesMobs.packetHandler.sendToServer(message);
		super.actionPerformed(buttonId);
	}
	
	
	// ==================================================
  	//                     Key Press
  	// ==================================================
	@Override
	public boolean keyPressed(int par1, int par2, int par3) {
		if(par2 == 1 || par2 == Minecraft.getInstance().gameSettings.keyBindInventory.getKey().getKeyCode())
        	 Minecraft.getInstance().player.closeScreen();
		return super.keyPressed(par1, par2, par3);
	}
}
