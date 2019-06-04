package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.pets.PetEntry;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraftforge.fml.client.GuiScrollingList;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GUIBaseManager extends GuiBaseScreen {
	public EntityPlayer player;
	public ExtendedPlayer playerExt;
	public String type;
	public PetEntry selectedPet;
	public SummonSet summonSet;

	public GuiScrollingList list;

	public int centerX;
	public int centerY;
	public int windowWidth;
	public int windowHeight;
	public int halfX;
	public int halfY;
	public int windowX;
	public int windowY;

    public static int tabButtonID = 55555;

    // Minion GUI:
    public int editSet;

	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIBaseManager(EntityPlayer player, String type) {
		super();
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);
		this.type = type;
	}
	
	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
        return false;
    }
	
	
	// ==================================================
  	//                       Init
  	// ==================================================
	@Override
	public void initGui() {
		super.initGui();
		
        this.buttonList.clear();
		this.windowWidth = 256;
        this.windowHeight = 172;
        this.halfX = this.windowWidth / 2;
        this.halfY = this.windowHeight / 2;
        this.windowX = (this.width / 2) - (this.windowWidth / 2);
        this.windowY = (this.height / 2) - (this.windowHeight / 2);
        this.centerX = this.windowX + (this.windowWidth / 2);
        this.centerY = this.windowY + (this.windowHeight / 2);
		this.drawControls();
        
		this.initList();
	}

    public void initList() {
        // Default Selection:
        if(this.hasPets()) {
            this.selectPet(this.playerExt.petManager.getEntry(this.type, 0));
        }

        int buttonSpacing = 2;
        int listWidth = (this.windowWidth / 2) - (buttonSpacing * 4);
        int listHeight = this.windowHeight - (39 + buttonSpacing) - 16; // 39 = Title Height + Spirit Height, 24 = Excess
        int listTop = this.windowY + 39 + buttonSpacing; // 39 = Title Height + Spirit Height
        int listBottom = listTop + listHeight;
        int listX = this.windowX + (buttonSpacing * 2);

        this.list = new GUIPetList(this, this.playerExt, listWidth, listHeight, listTop, listBottom, listX);
        this.list.registerScrollButtons(this.buttonList, 51, 52);
    }
	
	
	// ==================================================
  	//                    Draw Screen
  	// ==================================================
	@Override
	public void drawScreen(int x, int y, float f) {
        this.drawGuiContainerBackgroundLayer(x, y, f);
		this.updateControls();
        this.drawGuiContainerForegroundLayer(x, y, f);
        
        // Pet List:
		if(this.hasPets())
			this.list.drawScreen(x, y, f);

        super.drawScreen(x, y, f);
	}
	
	
	// ==================================================
  	//                    Foreground
  	// ==================================================
	protected void drawGuiContainerForegroundLayer(int x, int y, float f) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(this.getTexture());

		// No Pets:
		if (!this.hasPets()) {
			this.getFontRenderer().drawString(LanguageManager.translate("gui.beastiary.summoning.empty.title"), this.centerX - 96, this.windowY + 6, 0xFFFFFF);
			this.getFontRenderer().drawSplitString(LanguageManager.translate("gui.beastiary.summoning.empty.info"), this.windowX + 16, this.windowY + 30, this.windowWidth - 32, 0xFFFFFF);
			return;
		}

		// Title:
		this.getFontRenderer().drawString(this.getTitle(), this.centerX - 24, this.windowY + 6, 0xFFFFFF);

		// Spirit Title:
		this.getFontRenderer().drawString(this.getEnergyTitle(), this.windowX + 16, this.windowY + 20, 0xFFFFFF);

		// Removal Confirmation:
		if (this.hasSelectedPet()) {
			if ((this.type.equalsIgnoreCase("pet") || this.type.equalsIgnoreCase("mount")) && this.selectedPet.releaseEntity)
				this.getFontRenderer().drawSplitString(LanguageManager.translate("gui.pet.release.confirm"), this.centerX + 2, this.windowY + 41, (this.windowWidth / 2) - 2, 0xFFFFFF);
		}
	}

    public String getTitle() {
        return LanguageManager.translate("gui." + this.type + "manager.name");
    }

    public String getEnergyTitle() {
        return LanguageManager.translate("stat.spirit.name");
    }
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	protected void drawGuiContainerBackgroundLayer(int x, int y, float f) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.getTexture());

        this.drawTexturedModalRect(this.windowX, this.windowY, 0, 0, this.windowWidth, this.windowHeight);

		if(!this.hasPets()) {
			int recipeWidth = 108;
			int recipeHeight = 54;
			this.drawTexturedModalRect(this.centerX - (recipeWidth / 2), this.windowY + this.windowHeight - recipeHeight - 16, 0, 256 - recipeHeight, recipeWidth, recipeHeight);
			return;
		}

		this.drawEnergyBar();
		if(this.hasSelectedPet()) {
            this.drawHealthBar();
        }
	}

    public void drawEnergyBar() {
        // Spirit Bar:
        int energyBarWidth = 9;
        int energyBarHeight = 9;
        int energyBarX = this.windowX + 16;
        int energyBarY = this.windowY + 40 - energyBarHeight;
        int energyBarU = 256 - energyBarWidth;
        int energyBarV = 256 - energyBarHeight;

        for(int energyBarEnergyN = 1; energyBarEnergyN <= 10; energyBarEnergyN++) {
            // Empty:
            this.drawTexturedModalRect(energyBarX - energyBarWidth + (energyBarWidth * energyBarEnergyN), energyBarY, energyBarU, energyBarV, energyBarWidth, energyBarHeight);
            // Full:
            if(this.playerExt.spirit >= energyBarEnergyN * this.playerExt.spiritCharge) {
                this.drawTexturedModalRect(energyBarX - energyBarWidth + (energyBarWidth * energyBarEnergyN), energyBarY, energyBarU - energyBarWidth, energyBarV, energyBarWidth, energyBarHeight);
            }
            // Partial:
            else if(this.playerExt.spirit + this.playerExt.spiritCharge > energyBarEnergyN * this.playerExt.spiritCharge) {
                float spiritChargeScale = (float)(this.playerExt.spirit % this.playerExt.spiritCharge) / (float)this.playerExt.spiritCharge;
                this.drawTexturedModalRect(energyBarX - energyBarWidth + (energyBarWidth * energyBarEnergyN), energyBarY, energyBarU - energyBarWidth, energyBarV, Math.round((float)energyBarWidth * spiritChargeScale), energyBarHeight);
            }
        }
        // Reserved Spirit:
        energyBarU -= energyBarWidth * 2;
        for(int spiritBarReservedN = 1; spiritBarReservedN * this.playerExt.spiritCharge <= this.playerExt.spiritReserved; spiritBarReservedN++) {
            this.drawTexturedModalRect(energyBarX + (energyBarWidth * 10) - (energyBarWidth * spiritBarReservedN), energyBarY, energyBarU, energyBarV, energyBarWidth, energyBarHeight);
        }
    }

    public void drawHealthBar() {
        // Health and Respawn Bar:
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));

        int barWidth = 80;
        int barHeight = 11;
        int barX = this.centerX + 2;
        int barY = this.windowY + 26;
        int barU = 144;
        int barV = 256 - (barHeight * 2);
        this.drawTexturedModalRect(barX, barY, barU, barV, barWidth, barHeight);

        if(!this.selectedPet.isRespawning) {
            barWidth = Math.round(barWidth * (this.selectedPet.getHealth() / this.selectedPet.getMaxHealth()));
            barV = barV + barHeight;
        }
        else {
            barWidth = barWidth - Math.round(barWidth * ((float)this.selectedPet.respawnTime / (float) this.selectedPet.respawnTimeMax));
            barV = barV - barHeight;
        }
        this.drawTexturedModalRect(barX, barY, barU, barV, barWidth, barHeight);
    }
	
	
	// ==================================================
  	//                    Controls
  	// ==================================================
	protected void drawControls() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = (this.windowWidth / 4) - (buttonSpacing * 2);
        int buttonHeight = 20;
        int buttonX = this.windowX + 6;
        int buttonY = this.windowY;

		this.buttonList.add(new GuiTabMain(this.tabButtonID, buttonX, buttonY - 24));

		buttonX = this.centerX + buttonSpacing;
		int buttonXRight = buttonX + buttonWidth + buttonSpacing;
		buttonY = this.windowY + 39 + buttonSpacing;

		// Spawning and Teleport:
		this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND.SPAWNING.id, buttonX, buttonY, buttonWidth, buttonHeight, "..."));
		this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND.TELEPORT.id, buttonXRight, buttonY, buttonWidth, buttonHeight, "..."));

		// Sitting and Following:
		buttonY += buttonHeight + (buttonSpacing * 2);
		this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND.SITTING.id, buttonX, buttonY, buttonWidth, buttonHeight, "..."));
		this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND.FOLLOWING.id, buttonXRight, buttonY, buttonWidth, buttonHeight, "..."));

		// Passive and Stance:
		buttonY += buttonHeight + (buttonSpacing * 2);
		this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND.PASSIVE.id, buttonX, buttonY, buttonWidth, buttonHeight, "..."));
		this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND.STANCE.id, buttonXRight, buttonY, buttonWidth, buttonHeight, "..."));

		// PVP:
		buttonY += buttonHeight + (buttonSpacing * 2);
		this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND.PVP.id, buttonX, buttonY, buttonWidth, buttonHeight, "..."));
		this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND.RELEASE.id, buttonXRight, buttonY, buttonWidth, buttonHeight, "..."));

		// Removal Confirmation:
		this.buttonList.add(new GuiButton(101, buttonX, buttonY, buttonWidth, buttonHeight, LanguageManager.translate("common.yes")));
		this.buttonList.add(new GuiButton(102, buttonXRight, buttonY, buttonWidth, buttonHeight, LanguageManager.translate("common.no")));
	}

	public void updateControls() {
		for(Object buttonObj : this.buttonList) {
			if(buttonObj instanceof GuiButton) {
				GuiButton button = (GuiButton)buttonObj;

				// Tab:
				if(button instanceof GuiTabMain) {
					button.enabled = true;
					button.visible = true;
					continue;
				}

				// Inactive:
				if(!this.hasSelectedPet()) {
					button.enabled = false;
					button.visible = false;
					continue;
				}

                this.updateButtons(button);
			}
		}
	}

    public void updateButtons(GuiButton button) {
        // Action Buttons:
        if(button.id == EntityCreatureBase.GUI_COMMAND.SPAWNING.id)
            button.displayString = LanguageManager.translate("gui.pet.active") + ": " + (this.selectedPet.spawningActive ? LanguageManager.translate("common.yes") : LanguageManager.translate("common.no"));

        if(button.id == EntityCreatureBase.GUI_COMMAND.TELEPORT.id)
            button.displayString = LanguageManager.translate("gui.pet.teleport");

        // Behaviour Buttons:
        if (button.id == EntityCreatureBase.GUI_COMMAND.SITTING.id)
            button.displayString = LanguageManager.translate("gui.pet.sit") + ": " + (this.summonSet.getSitting() ? LanguageManager.translate("common.yes") : LanguageManager.translate("common.no"));

        if (button.id == EntityCreatureBase.GUI_COMMAND.FOLLOWING.id)
            button.displayString = (this.summonSet.getFollowing() ? LanguageManager.translate("gui.pet.follow") : LanguageManager.translate("gui.pet.wander"));

        if (button.id == EntityCreatureBase.GUI_COMMAND.PASSIVE.id)
            button.displayString = LanguageManager.translate("gui.pet.passive") + ": " + (this.summonSet.getPassive() ? LanguageManager.translate("common.yes") : LanguageManager.translate("common.no"));

        if (button.id == EntityCreatureBase.GUI_COMMAND.STANCE.id)
            button.displayString = (this.summonSet.getAggressive() ? LanguageManager.translate("gui.pet.aggressive") : LanguageManager.translate("gui.pet.defensive"));

        if (button.id == EntityCreatureBase.GUI_COMMAND.PVP.id)
            button.displayString = LanguageManager.translate("gui.pet.pvp") + ": " + (this.summonSet.getPVP() ? LanguageManager.translate("common.yes") : LanguageManager.translate("common.no"));

        // Remove:
        if(button.id == EntityCreatureBase.GUI_COMMAND.RELEASE.id)
            button.displayString = LanguageManager.translate("gui.pet.release");

        // Removal Confirmation:
        if(!this.selectedPet.releaseEntity) {
            if(button.id < 100) {
                button.enabled = true;
                button.visible = true;
            }
            else if(button.id == 101 || button.id == 102) {
                button.enabled = false;
                button.visible = false;
            }
        }
        else {
            if(button.id < 100) {
                button.enabled = false;
                button.visible = false;
            }
            else if(button.id == 101 || button.id == 102) {
                button.enabled = true;
                button.visible = true;
            }
        }

        // Hidden Mount Buttons:
        if("mount".equals(this.type)) {
            if(button.id >= EntityCreatureBase.GUI_COMMAND.SITTING.id && button.id <= EntityCreatureBase.GUI_COMMAND.PVP.id) {
                button.enabled = false;
                button.visible = false;
            }
        }
    }
	
	
	// ==================================================
  	//                     Actions
  	// ==================================================
	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		// Inactive:
		if(!this.hasSelectedPet()) {
			super.actionPerformed(guiButton);
			return;
		}

		// Behaviour Button:
		if(guiButton.id == EntityCreatureBase.GUI_COMMAND.SITTING.id)
			this.summonSet.sitting = !this.summonSet.sitting;
		if(guiButton.id == EntityCreatureBase.GUI_COMMAND.FOLLOWING.id)
			this.summonSet.following = !this.summonSet.following;
		if(guiButton.id == EntityCreatureBase.GUI_COMMAND.PASSIVE.id)
			this.summonSet.passive = !this.summonSet.passive;
		if(guiButton.id == EntityCreatureBase.GUI_COMMAND.STANCE.id)
			this.summonSet.aggressive = !this.summonSet.aggressive;
		if(guiButton.id == EntityCreatureBase.GUI_COMMAND.PVP.id)
			this.summonSet.pvp = !this.summonSet.pvp;

		// Action Button:
		if(guiButton.id == EntityCreatureBase.GUI_COMMAND.TELEPORT.id)
			this.selectedPet.teleportEntity = true;
		if(guiButton.id == EntityCreatureBase.GUI_COMMAND.SPAWNING.id)
			this.selectedPet.spawningActive = !this.selectedPet.spawningActive;

		// Release and Confirmation:
		if(guiButton.id == EntityCreatureBase.GUI_COMMAND.RELEASE.id) {
			if(!this.selectedPet.releaseEntity)
				this.selectedPet.releaseEntity = true;
		}

		if(guiButton.id == 101 && this.selectedPet.releaseEntity) { // Yes
			this.playerExt.sendPetEntryRemoveRequest(this.selectedPet);
		}

		if (guiButton.id == 102) { // No
			this.selectPet(this.selectedPet);
		}

		if(guiButton.id < 100) {
			this.sendCommandsToServer();
		}

		super.actionPerformed(guiButton);
	}

    public void sendCommandsToServer() {
        this.playerExt.sendPetEntryToServer(this.selectedPet);
    }


	// ==================================================
	//                   Pet Selection
	// ==================================================
	public void selectPet(PetEntry petSelection) {
		this.selectedPet = petSelection;
		this.summonSet = this.selectedPet.summonSet;
		this.selectedPet.releaseEntity = false;
	}

	public PetEntry getSelectedPet() {
		return this.selectedPet;
	}

    public void selectMinion(String minionName) {
        this.summonSet.setSummonType(minionName);
        this.playerExt.sendSummonSetToServer((byte)this.editSet);
        for(Object buttonObj : this.buttonList) {
            GuiButton button = (GuiButton)buttonObj;
            if(button instanceof GuiButtonCreature && button.id == this.editSet + this.tabButtonID) {
                CreatureInfo creatureInfo = this.playerExt.getSummonSet(this.editSet).getCreatureInfo();
                ((GuiButtonCreature)button).creatureInfo = creatureInfo;
            }
        }
    }

    public String getSelectedMinion() {
		if(this.summonSet == null)
			return null;
        return this.summonSet.summonType;
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


	// ==================================================
	//                     Has Pets
	// ==================================================
	public boolean hasPets() {
		return this.playerExt.petManager.getEntryList(this.type) != null && this.playerExt.petManager.getEntryList(this.type).size() > 0;
	}

	public boolean hasSelectedPet() {
		return this.hasPets() && this.selectedPet != null;
	}


	// ==================================================
	//                     Get Texture
	// ==================================================
	protected ResourceLocation getTexture() {
		return AssetManager.getTexture("GUIPet");
	}
}
