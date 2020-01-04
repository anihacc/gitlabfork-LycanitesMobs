package com.lycanitesmobs.client.gui.overlays;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.BaseScreen;
import com.lycanitesmobs.client.gui.buttons.CreatureButton;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.network.MessageSummonSetSelection;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class MinionSelectionOverlay extends BaseScreen {
	public EntityPlayer player;
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
	public static void openToPlayer(EntityPlayer player) {
		if(player != null && player.getEntityWorld() != null)
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.getEntityWorld(), GuiHandler.PlayerGuiType.MINION_SELECTION.id, 0, 0);
	}
	
	public boolean doesGuiPauseGame() {
        return false;
    }
	
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public MinionSelectionOverlay(EntityPlayer player) {
		super();
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);
	}
	
	
	// ==================================================
  	//                       Init
  	// ==================================================
	@Override
	public void initGui() {
		super.initGui();
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
	public void drawScreen(int x, int y, float f) {
        this.drawGuiContainerBackgroundLayer();
        this.updateControls();
        this.drawGuiContainerForegroundLayer();
        
        // Creature List:
        super.drawScreen(x, y, f);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer() {}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	protected void drawGuiContainerBackgroundLayer() {

	}
	
	
	// ==================================================
  	//                    Controls
  	// ==================================================
	protected void drawControls() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = 32;
        int buttonHeight = 32;
        int buttonX = this.centerX - Math.round(buttonWidth / 2);
        int buttonY = this.centerY - Math.round(buttonHeight / 2);
        GuiButton button;
        CreatureInfo creatureInfo;
        int offset = 32;
        
        creatureInfo = this.playerExt.getSummonSet(1).getCreatureInfo();
        button = new CreatureButton(1, buttonX, buttonY - Math.round(offset * 2), buttonWidth, buttonHeight, "" + 1, creatureInfo);
    	this.buttonList.add(button);
    	
        creatureInfo = this.playerExt.getSummonSet(2).getCreatureInfo();
        button = new CreatureButton(2, buttonX + Math.round(offset * 2), buttonY - Math.round(offset * 0.5F), buttonWidth, buttonHeight, "" + 2, creatureInfo);
    	this.buttonList.add(button);
    	
        creatureInfo = this.playerExt.getSummonSet(3).getCreatureInfo();
        button = new CreatureButton(3, buttonX + Math.round(offset * 1), buttonY +  Math.round(offset * 1.75F), buttonWidth, buttonHeight, "" + 3, creatureInfo);
    	this.buttonList.add(button);
    	
        creatureInfo = this.playerExt.getSummonSet(4).getCreatureInfo();
        button = new CreatureButton(4, buttonX - Math.round(offset * 1), buttonY +  Math.round(offset * 1.75F), buttonWidth, buttonHeight, "" + 4, creatureInfo);
    	this.buttonList.add(button);
    	
        creatureInfo = this.playerExt.getSummonSet(5).getCreatureInfo();
        button = new CreatureButton(5, buttonX - Math.round(offset * 2), buttonY - Math.round(offset * 0.5F), buttonWidth, buttonHeight, "" + 5, creatureInfo);
    	this.buttonList.add(button);
        
        /*for(int setID = 1; setID <= 5; setID++) {
        	float offset = ((float)setID / 6) - 0.5F;
        	offset = (float)Math.sin(offset);
        	LycanitesMobs.logDebug("", "" + offset);
        	int posX = this.centerX + Math.round(this.windowWidth * offset) - Math.round(buttonWidth / 2);
        	int posY = this.centerY + Math.round(this.windowHeight * offset) - Math.round(buttonHeight / 2);
        	GuiButton button = new GuiButton(setID, posX, posY, buttonWidth, buttonHeight, "" + setID);
        	this.buttonList.add(button);
        }*/
    }
	
	public void updateControls() {
        for(Object buttonObj : this.buttonList) {
        	if(buttonObj instanceof GuiButton) {
        		GuiButton button = (GuiButton)buttonObj;
        		button.visible = this.playerExt.getSummonSet(button.id).isUseable();
        		button.enabled = button.id != this.playerExt.selectedSummonSet;
        	}
        }
	}
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		if(guiButton != null) {
			this.playerExt.setSelectedSummonSet(guiButton.id);
			MessageSummonSetSelection message = new MessageSummonSetSelection(this.playerExt);
			LycanitesMobs.packetHandler.sendToServer(message);
		}
		super.actionPerformed(guiButton);
	}
	
	
	// ==================================================
  	//                     Key Press
  	// ==================================================
	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
		if(par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode())
        	 this.mc.player.closeScreen();
		super.keyTyped(par1, par2);
	}
}
