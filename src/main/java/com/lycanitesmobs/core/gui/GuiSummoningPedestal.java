package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.pets.SummonSet;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import com.lycanitesmobs.core.localisation.LanguageManager;
import org.lwjgl.opengl.GL11;

public class GuiSummoningPedestal extends GUIBaseManager {
    public TileEntitySummoningPedestal summoningPedestal;

    // ==================================================
    //                    Constructor
    // ==================================================
    public GuiSummoningPedestal(EntityPlayer player, TileEntitySummoningPedestal summoningPedestal) {
        super(player, "minion");
        this.summoningPedestal = summoningPedestal;
        this.summonSet = this.summoningPedestal.summonSet;
    }


    // ==================================================
    //                       Init
    // ==================================================
    @Override
    public void initList() {
        if(this.hasPets() && this.summoningPedestal.summonSet != null) {
            this.selectMinion(this.summoningPedestal.summonSet.summonType);
        }

        int buttonSpacing = 2;
        int listWidth = (this.windowWidth / 2) - (buttonSpacing * 4);
        int listHeight = this.windowHeight - (39 + buttonSpacing) - 16; // 39 = Title Height + Spirit Height, 24 = Excess
        int listTop = this.windowY + 39 + buttonSpacing; // 39 = Title Height + Spirit Height
        int listBottom = listTop + listHeight;
        int listX = this.windowX + (buttonSpacing * 2);

        this.list = new GuiSummoningPedestalList(this, this.playerExt, listWidth, listHeight, listTop, listBottom, listX);
        this.list.registerScrollButtons(this.buttonList, 51, 52);
    }


    // ==================================================
    //                    Foreground
    // ==================================================
    @Override
    public String getTitle() {
        return LanguageManager.translate("gui." + "summoningpedestal.name");
    }

    @Override
    public String getEnergyTitle() {
        return LanguageManager.translate("stat.portal.name");
    }


    // ==================================================
    //                    Background
    // ==================================================
    @Override
    public void drawEnergyBar() {
        // Portal Energy Bar:
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
            if(this.summoningPedestal.capacity >= energyBarEnergyN * this.summoningPedestal.capacityCharge) {
                this.drawTexturedModalRect(energyBarX - energyBarWidth + (energyBarWidth * energyBarEnergyN), energyBarY, energyBarU - (energyBarWidth * 2), energyBarV, energyBarWidth, energyBarHeight);
            }
            // Partial:
            else if(this.summoningPedestal.capacity + this.summoningPedestal.capacityCharge > energyBarEnergyN * this.summoningPedestal.capacityCharge) {
                float spiritChargeScale = (float)(this.summoningPedestal.capacity % this.summoningPedestal.capacityCharge) / (float)this.summoningPedestal.capacityCharge;
                this.drawTexturedModalRect(energyBarX - energyBarWidth + (energyBarWidth * energyBarEnergyN), energyBarY, energyBarU - (energyBarWidth * 2), energyBarV, Math.round((float)energyBarWidth * spiritChargeScale), energyBarHeight);
            }
        }
    }

    @Override
    public void drawHealthBar() {
        // Summoning Progress Bar:
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));

        int barWidth = 80;
        int barHeight = 11;
        int barX = this.centerX + 2;
        int barY = this.windowY + 26;
        int barU = 144;
        int barV = 256 - (barHeight * 2);
        this.drawTexturedModalRect(barX, barY, barU, barV, barWidth, barHeight);

        barWidth = Math.round((float)barWidth * ((float)this.summoningPedestal.summonProgress / this.summoningPedestal.summonProgressMax));
        barV = barV + barHeight;

        this.drawTexturedModalRect(barX, barY, barU, barV, barWidth, barHeight);
    }


    // ==================================================
    //                     Controls
    // ==================================================
    @Override
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

        // Sitting and Following:
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND.SITTING.id, buttonX, buttonY, buttonWidth * 2, buttonHeight, "..."));

        // Passive and Stance:
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND.PASSIVE.id, buttonX, buttonY, buttonWidth, buttonHeight, "..."));
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND.STANCE.id, buttonXRight, buttonY, buttonWidth, buttonHeight, "..."));

        // PVP:
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttonList.add(new GuiButton(EntityCreatureBase.GUI_COMMAND.PVP.id, buttonX, buttonY, buttonWidth * 2, buttonHeight, "..."));
    }

    @Override
    public void updateButtons(GuiButton button) {
        // Behaviour Buttons:
        if (button.id == EntityCreatureBase.GUI_COMMAND.SITTING.id)
            button.displayString = LanguageManager.translate("gui.pet.sit") + ": " + (this.summonSet.getSitting() ? LanguageManager.translate("common.yes") : LanguageManager.translate("common.no"));

        if (button.id == EntityCreatureBase.GUI_COMMAND.PASSIVE.id)
            button.displayString = LanguageManager.translate("gui.pet.passive") + ": " + (this.summonSet.getPassive() ? LanguageManager.translate("common.yes") : LanguageManager.translate("common.no"));

        if (button.id == EntityCreatureBase.GUI_COMMAND.STANCE.id)
            button.displayString = (this.summonSet.getAggressive() ? LanguageManager.translate("gui.pet.aggressive") : LanguageManager.translate("gui.pet.defensive"));

        if (button.id == EntityCreatureBase.GUI_COMMAND.PVP.id)
            button.displayString = LanguageManager.translate("gui.pet.pvp") + ": " + (this.summonSet.getPVP() ? LanguageManager.translate("common.yes") : LanguageManager.translate("common.no"));

        // Hidden Mount Buttons:
        if("mount".equals(this.type)) {
            if(button.id >= EntityCreatureBase.GUI_COMMAND.SITTING.id && button.id <= EntityCreatureBase.GUI_COMMAND.PVP.id) {
                button.enabled = false;
                button.visible = false;
            }
        }
    }


    // ==================================================
    //                      Actions
    // ==================================================
    @Override
    public void sendCommandsToServer() {
        this.summoningPedestal.sendSummonSetToServer(this.summonSet);
    }


    // ==================================================
    //                    Pet Selection
    // ==================================================
    @Override
    public void selectMinion(String minionName) {
        if(this.summonSet == null) {
            if(this.summoningPedestal == null || this.summoningPedestal.summonSet == null) {
				this.summoningPedestal.summonSet = new SummonSet(this.playerExt);
				this.summoningPedestal.sendSummonSetToServer(this.summoningPedestal.summonSet);
            }
            this.summonSet = this.summoningPedestal.summonSet;
        }
        this.summonSet.setSummonType(minionName);
        this.sendCommandsToServer();
    }


    // ==================================================
    //                     Has Pets
    // ==================================================
    @Override
    public boolean hasPets() {
        return this.playerExt.getBeastiary().getSummonableList().size() > 0;
    }

    @Override
    public boolean hasSelectedPet() {
        return this.hasPets() && this.summonSet != null && !this.summonSet.summonType.equals("");
    }


    // ==================================================
    //                     Get Texture
    // ==================================================
    @Override
    protected ResourceLocation getTexture() {
        return AssetManager.getTexture("GUIMinionLg");
    }
}
