package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.client.gui.buttons.ButtonBase;
import com.lycanitesmobs.client.gui.buttons.MainTab;
import com.lycanitesmobs.client.gui.widgets.SummoningPedestalList;
import com.lycanitesmobs.core.container.SummoningPedestalContainer;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.pets.SummonSet;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

public class SummoningPedestalScreen extends BaseContainerScreen<SummoningPedestalContainer> {
    public PlayerEntity player;
    public ExtendedPlayer playerExt;
    public TileEntitySummoningPedestal summoningPedestal;
    public SummonSet summonSet;

    public ExtendedList list;

    public int centerX;
    public int centerY;
    public int windowWidth;
    public int windowHeight;
    public int halfX;
    public int halfY;
    public int windowX;
    public int windowY;

    public static int TAB_BUTTON_ID = 55555;

    public SummoningPedestalScreen(SummoningPedestalContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
        this.summoningPedestal = container.summoningPedestal;
        this.player = playerInventory.player;
        this.playerExt = ExtendedPlayer.getForPlayer(this.player);
        this.summonSet = this.summoningPedestal.summonSet;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init() {
        super.init();

        this.buttons.clear();
        this.windowWidth = 256;
        this.windowHeight = 166;
        this.halfX = this.windowWidth / 2;
        this.halfY = this.windowHeight / 2;
        this.windowX = (this.width / 2) - (this.windowWidth / 2);
        this.windowY = (this.height / 2) - (this.windowHeight / 2);
        this.centerX = this.windowX + (this.windowWidth / 2);
        this.centerY = this.windowY + (this.windowHeight / 2);
    }

    @Override
    protected void initWidgets() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = (this.windowWidth / 4) - (buttonSpacing * 2);
        int buttonHeight = 20;
        int buttonX = this.windowX + 6;
        int buttonY = this.windowY;

        //this.addButton(new MainTab(TAB_BUTTON_ID, buttonX, buttonY - 24, this)); TODO New Tabs

        buttonX = this.centerX + buttonSpacing;
        int buttonXRight = buttonX + buttonWidth + buttonSpacing;
        buttonY = this.windowY + 39 + buttonSpacing;

        // Sitting and Following:
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.addButton(new ButtonBase(BaseCreatureEntity.GUI_COMMAND.SITTING.id, buttonX, buttonY, buttonWidth * 2, buttonHeight, "...", this));

        // Passive and Stance:
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.addButton(new ButtonBase(BaseCreatureEntity.GUI_COMMAND.PASSIVE.id, buttonX, buttonY, buttonWidth, buttonHeight, "...", this));
        this.addButton(new ButtonBase(BaseCreatureEntity.GUI_COMMAND.STANCE.id, buttonXRight, buttonY, buttonWidth, buttonHeight, "...", this));

        // PVP:
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.addButton(new ButtonBase(BaseCreatureEntity.GUI_COMMAND.PVP.id, buttonX, buttonY, buttonWidth * 2, buttonHeight, "...", this));

        // List:
        if(this.hasPets() && this.summoningPedestal.summonSet != null) {
            this.selectMinion(this.summoningPedestal.summonSet.summonType);
        }
        int listWidth = (this.windowWidth / 2) - (buttonSpacing * 4);
        int listHeight = this.windowHeight - (39 + buttonSpacing) - 16; // 39 = Title Height + Spirit Height, 24 = Excess
        int listTop = this.windowY + 39 + buttonSpacing; // 39 = Title Height + Spirit Height
        int listBottom = listTop + listHeight;
        int listX = this.windowX + (buttonSpacing * 2);
        this.list = new SummoningPedestalList(this, this.playerExt, listWidth, listHeight, listTop, listBottom, listX);
        this.children.add(this.list);
    }

    @Override
    protected void renderBackground(int mouseX, int mouseY, float partialTicks) {
        GL11.glColor4f(255, 255, 255, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(this.getTexture());

        this.drawTexturedModalRect(this.windowX, this.windowY, 0, 0, this.windowWidth, this.windowHeight);
        this.drawTexturedModalRect(this.windowX + 40, this.windowY + this.windowHeight, 40, 224, this.windowWidth - 80, 29);

        if(!this.hasPets()) {
            return;
        }

        this.drawFuel();
        this.drawCapacityBar();
        this.drawProgressBar();
    }

    @Override
    public void renderWidgets(int mouseX, int mouseY, float partialTicks) {
        for(Object buttonObj : this.buttons) {
            if(buttonObj instanceof ButtonBase) {
                ButtonBase button = (ButtonBase)buttonObj;

                // Tab:
                if(button instanceof MainTab) {
                    button.active = true;
                    button.visible = true;
                    continue;
                }

                // Inactive:
                if(!this.hasSelectedPet()) {
                    button.active = false;
                    button.visible = false;
                    continue;
                }

                // Behaviour Buttons:
                if (button.buttonId == BaseCreatureEntity.GUI_COMMAND.SITTING.id)
                    button.setMessage(new TranslationTextComponent("gui.pet.sit").getFormattedText() + ": " + (this.summonSet.getSitting() ? new TranslationTextComponent("common.yes").getFormattedText() : new TranslationTextComponent("common.no").getFormattedText()));

                if (button.buttonId == BaseCreatureEntity.GUI_COMMAND.PASSIVE.id)
                    button.setMessage(new TranslationTextComponent("gui.pet.passive").getFormattedText() + ": " + (this.summonSet.getPassive() ? new TranslationTextComponent("common.yes").getFormattedText() : new TranslationTextComponent("common.no").getFormattedText()));

                if (button.buttonId == BaseCreatureEntity.GUI_COMMAND.STANCE.id)
                    button.setMessage((this.summonSet.getAggressive() ? new TranslationTextComponent("gui.pet.aggressive").getFormattedText() : new TranslationTextComponent("gui.pet.defensive").getFormattedText()));

                if (button.buttonId == BaseCreatureEntity.GUI_COMMAND.PVP.id)
                    button.setMessage(new TranslationTextComponent("gui.pet.pvp").getFormattedText() + ": " + (this.summonSet.getPVP() ? new TranslationTextComponent("common.yes").getFormattedText() : new TranslationTextComponent("common.no").getFormattedText()));
            }
        }

        // Pet List:
        if(this.hasPets()) {
            this.list.render(mouseX, mouseY, partialTicks);
        }

        super.renderWidgets(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderForeground(int mouseX, int mouseY, float partialTicks) {
        GL11.glColor4f(255, 255, 255, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(this.getTexture());

        // No Pets:
        if (!this.hasPets()) {
            this.getFontRenderer().drawString(new TranslationTextComponent("gui.beastiary.summoning.empty.title").getFormattedText(), this.centerX - 96, this.windowY + 6, 0xFFFFFF);
            this.getFontRenderer().drawSplitString(new TranslationTextComponent("gui.beastiary.summoning.empty.info").getFormattedText(), this.windowX + 16, this.windowY + 30, this.windowWidth - 32, 0xFFFFFF);
            return;
        }

        // Title:
        this.getFontRenderer().drawString(this.getTitle().getFormattedText(), this.centerX - 24, this.windowY + 6, 0xFFFFFF);

        // Spirit Title:
        this.getFontRenderer().drawString(this.getEnergyTitle().getFormattedText(), this.windowX + 16, this.windowY + 20, 0xFFFFFF);
    }

    @Override
    public void actionPerformed(int buttonId) {
        // Inactive:
        if(!this.hasSelectedPet()) {
            return;
        }

        // Behaviour Button:
        if(buttonId == BaseCreatureEntity.GUI_COMMAND.SITTING.id)
            this.summonSet.sitting = !this.summonSet.sitting;
        if(buttonId == BaseCreatureEntity.GUI_COMMAND.FOLLOWING.id)
            this.summonSet.following = !this.summonSet.following;
        if(buttonId == BaseCreatureEntity.GUI_COMMAND.PASSIVE.id)
            this.summonSet.passive = !this.summonSet.passive;
        if(buttonId == BaseCreatureEntity.GUI_COMMAND.STANCE.id)
            this.summonSet.aggressive = !this.summonSet.aggressive;
        if(buttonId == BaseCreatureEntity.GUI_COMMAND.PVP.id)
            this.summonSet.pvp = !this.summonSet.pvp;

        if(buttonId < 100) {
            this.sendCommandsToServer();
        }
    }

    /*@Override
    protected void keyPressed(char par1, int par2) {
        if(par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode())
            this.mc.player.closeScreen();
        super.keyTyped(par1, par2);
    }*/

    public ITextComponent getTitle() {
        return new TranslationTextComponent("gui." + "summoningpedestal");
    }

    public ITextComponent getEnergyTitle() {
        return new TranslationTextComponent("stat.portal");
    }

    public void drawFuel() {
        int fuelX = this.windowX + 132;
        int fuelY = this.windowY + 42;
        this.drawTexturedModalRect(fuelX, fuelY, 47, 170, 18, 18);

        int barWidth = 38;
        int barHeight = 11;
        int barX = fuelX + 22;
        int barY = fuelY + 3;
        int barU = 218;
        int barV = 225;
        this.drawTexturedModalRect(barX, barY, barU, barV + barHeight, barWidth, barHeight);

        barWidth = Math.round((float)barWidth * ((float)this.summoningPedestal.summoningFuel / this.summoningPedestal.summoningFuelMax));
        this.drawTexturedModalRect(barX, barY, barU, barV, barWidth, barHeight);
    }

    public void drawCapacityBar() {
        int energyBarWidth = 9;
        int energyBarHeight = 9;
        int energyBarX = this.windowX + 16;
        int energyBarY = this.windowY + 40 - energyBarHeight;
        this.drawBar(TextureManager.getTexture("GUIPetSpiritEmpty"), energyBarX, energyBarY, 0, energyBarWidth, energyBarHeight, 10, 10);
        this.drawBar(TextureManager.getTexture("GUIPetSpirit"), energyBarX, energyBarY, 0, energyBarWidth, energyBarHeight, this.summoningPedestal.capacity / this.summoningPedestal.capacityCharge, 10);
    }

    public void drawProgressBar() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int barWidth = (256 / 4) + 16;
        int barHeight = (32 / 4) + 2;
        int barX = this.centerX + 2;
        int barY = this.windowY + 26;
        this.drawTexture(TextureManager.getTexture("GUIPetBarEmpty"), barX, barY, 0, 1, 1, barWidth, barHeight);

        float respawnNormal = ((float)this.summoningPedestal.summonProgress / this.summoningPedestal.summonProgressMax);
        this.drawTexture(TextureManager.getTexture("GUIPetBarRespawn"), barX, barY, 0, respawnNormal, 1, barWidth * respawnNormal, barHeight);
    }

    public void sendCommandsToServer() {
        this.summoningPedestal.sendSummonSetToServer(this.summonSet);
    }

    public void selectMinion(String minionName) {
        if(this.summonSet == null) {
            if(this.summoningPedestal.summonSet == null) {
				this.summoningPedestal.summonSet = new SummonSet(this.playerExt);
				this.summoningPedestal.sendSummonSetToServer(this.summoningPedestal.summonSet);
            }
            this.summonSet = this.summoningPedestal.summonSet;
        }
        this.summonSet.setSummonType(minionName);
        this.sendCommandsToServer();
    }

    public String getSelectedMinionName() {
        if(this.summonSet == null)
            return null;
        return this.summonSet.summonType;
    }

    public boolean hasPets() {
        return this.playerExt.getBeastiary().getSummonableList().size() > 0;
    }

    public boolean hasSelectedPet() {
        return this.hasPets() && this.summonSet != null && !this.summonSet.summonType.equals("");
    }

    protected ResourceLocation getTexture() {
        return TextureManager.getTexture("GUISummoningPedestal");
    }
}
