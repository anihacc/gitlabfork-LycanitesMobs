package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.container.ContainerSummoningPedestal;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.localisation.LanguageManager;
import com.lycanitesmobs.core.pets.SummonSet;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

public class GuiSummoningPedestal extends GuiBaseContainer<ContainerSummoningPedestal> {
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


    /**
     * Constructor
     */
    public GuiSummoningPedestal(ContainerSummoningPedestal container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
        this.summoningPedestal = container.summoningPedestal;
        this.player = playerInventory.player;
        this.playerExt = ExtendedPlayer.getForPlayer(this.player);
        this.summonSet = this.summoningPedestal.summonSet;
    }

    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
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
        this.initControls();

        this.initList();
    }

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
    }

    protected void initControls() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int buttonSpacing = 2;
        int buttonWidth = (this.windowWidth / 4) - (buttonSpacing * 2);
        int buttonHeight = 20;
        int buttonX = this.windowX + 6;
        int buttonY = this.windowY;

        this.buttons.add(new GuiTabMain(TAB_BUTTON_ID, buttonX, buttonY - 24, this));

        buttonX = this.centerX + buttonSpacing;
        int buttonXRight = buttonX + buttonWidth + buttonSpacing;
        buttonY = this.windowY + 39 + buttonSpacing;

        // Sitting and Following:
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttons.add(new ButtonBase(EntityCreatureBase.GUI_COMMAND.SITTING.id, buttonX, buttonY, buttonWidth * 2, buttonHeight, "...", this));

        // Passive and Stance:
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttons.add(new ButtonBase(EntityCreatureBase.GUI_COMMAND.PASSIVE.id, buttonX, buttonY, buttonWidth, buttonHeight, "...", this));
        this.buttons.add(new ButtonBase(EntityCreatureBase.GUI_COMMAND.STANCE.id, buttonXRight, buttonY, buttonWidth, buttonHeight, "...", this));

        // PVP:
        buttonY += buttonHeight + (buttonSpacing * 2);
        this.buttons.add(new ButtonBase(EntityCreatureBase.GUI_COMMAND.PVP.id, buttonX, buttonY, buttonWidth * 2, buttonHeight, "...", this));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.updateControls();
        this.drawGuiContainerForegroundLayer(partialTicks, mouseX, mouseY);

        // Pet List:
        if(this.hasPets())
            this.list.render(mouseX, mouseY, partialTicks);
    }

    protected void drawGuiContainerForegroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(this.getTexture());

        // No Pets:
        if (!this.hasPets()) {
            this.getFontRenderer().drawString(LanguageManager.translate("gui.beastiary.summoning.empty.title"), this.centerX - 96, this.windowY + 6, 0xFFFFFF);
            this.getFontRenderer().drawSplitString(LanguageManager.translate("gui.beastiary.summoning.empty.info"), this.windowX + 16, this.windowY + 30, this.windowWidth - 32, 0xFFFFFF);
            return;
        }

        // Title:
        this.getFontRenderer().drawString(this.getTitle().toString(), this.centerX - 24, this.windowY + 6, 0xFFFFFF);

        // Spirit Title:
        this.getFontRenderer().drawString(this.getEnergyTitle(), this.windowX + 16, this.windowY + 20, 0xFFFFFF);
    }

    public ITextComponent getTitle() {
        return new TranslationTextComponent(LanguageManager.translate("gui." + "summoningpedestal.name"));
    }

    public String getEnergyTitle() {
        return LanguageManager.translate("stat.portal.name");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(this.getTexture());

        this.drawTexturedModalRect(this.windowX, this.windowY, 0, 0, this.windowWidth, this.windowHeight);
        this.drawTexturedModalRect(this.windowX + 40, this.windowY + this.windowHeight, 40, 224, this.windowWidth - 80, 29);

        if(!this.hasPets()) {
            int recipeWidth = 108;
            int recipeHeight = 54;
            this.drawTexturedModalRect(this.centerX - (recipeWidth / 2), this.windowY + this.windowHeight - recipeHeight - 16, 0, 256 - recipeHeight, recipeWidth, recipeHeight);
            return;
        }

        this.drawFuel();
        this.drawCapacityBar();
        this.drawProgressBar();
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

    public void drawProgressBar() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));

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

    public void updateControls() {
        for(Object buttonObj : this.buttons) {
            if(buttonObj instanceof ButtonBase) {
                ButtonBase button = (ButtonBase)buttonObj;

                // Tab:
                if(button instanceof GuiTabMain) {
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

                this.updateButtons(button);
            }
        }
    }

    public void updateButtons(ButtonBase button) {
        // Behaviour Buttons:
        if (button.buttonId == EntityCreatureBase.GUI_COMMAND.SITTING.id)
            button.setMessage(LanguageManager.translate("gui.pet.sit") + ": " + (this.summonSet.getSitting() ? LanguageManager.translate("common.yes") : LanguageManager.translate("common.no")));

        if (button.buttonId == EntityCreatureBase.GUI_COMMAND.PASSIVE.id)
            button.setMessage(LanguageManager.translate("gui.pet.passive") + ": " + (this.summonSet.getPassive() ? LanguageManager.translate("common.yes") : LanguageManager.translate("common.no")));

        if (button.buttonId == EntityCreatureBase.GUI_COMMAND.STANCE.id)
            button.setMessage((this.summonSet.getAggressive() ? LanguageManager.translate("gui.pet.aggressive") : LanguageManager.translate("gui.pet.defensive")));

        if (button.buttonId == EntityCreatureBase.GUI_COMMAND.PVP.id)
            button.setMessage(LanguageManager.translate("gui.pet.pvp") + ": " + (this.summonSet.getPVP() ? LanguageManager.translate("common.yes") : LanguageManager.translate("common.no")));
    }

    public void sendCommandsToServer() {
        this.summoningPedestal.sendSummonSetToServer(this.summonSet);
    }

    @Override
    public void actionPerformed(byte buttonId) {
        // Inactive:
        if(!this.hasSelectedPet()) {
            super.actionPerformed(buttonId);
            return;
        }

        // Behaviour Button:
        if(buttonId == EntityCreatureBase.GUI_COMMAND.SITTING.id)
            this.summonSet.sitting = !this.summonSet.sitting;
        if(buttonId == EntityCreatureBase.GUI_COMMAND.FOLLOWING.id)
            this.summonSet.following = !this.summonSet.following;
        if(buttonId == EntityCreatureBase.GUI_COMMAND.PASSIVE.id)
            this.summonSet.passive = !this.summonSet.passive;
        if(buttonId == EntityCreatureBase.GUI_COMMAND.STANCE.id)
            this.summonSet.aggressive = !this.summonSet.aggressive;
        if(buttonId == EntityCreatureBase.GUI_COMMAND.PVP.id)
            this.summonSet.pvp = !this.summonSet.pvp;

        if(buttonId < 100) {
            this.sendCommandsToServer();
        }

        super.actionPerformed(buttonId);
    }

    /*@Override
    protected void keyTyped(char par1, int par2) {
        if(par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode())
            this.mc.player.closeScreen();
        super.keyTyped(par1, par2);
    }*/

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

    public String getSelectedMinion() {
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
        return AssetManager.getTexture("GUIMinionLg");
    }
}
