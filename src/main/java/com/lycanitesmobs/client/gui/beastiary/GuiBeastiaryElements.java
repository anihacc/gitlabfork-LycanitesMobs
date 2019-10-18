package com.lycanitesmobs.client.gui.beastiary;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.beastiary.list.GuiElementDescriptionList;
import com.lycanitesmobs.client.gui.beastiary.list.GuiElementList;
import com.lycanitesmobs.core.info.ElementInfo;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import com.lycanitesmobs.client.localisation.LanguageManager;

import java.io.IOException;

public class GuiBeastiaryElements extends GuiBeastiary {
	public ElementInfo elementInfo;
	protected GuiElementList elementList;
	protected GuiElementDescriptionList descriptionList;

	/**
	 * Opens this GUI up to the provided player.
	 * @param player The player to open the GUI to.
	 */
	public static void openToPlayer(EntityPlayer player) {
		if(player != null) {
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.BEASTIARY.id, player.getEntityWorld(), GuiHandler.Beastiary.ELEMENTS.id, 0, 0);
		}
	}


	public GuiBeastiaryElements(EntityPlayer player) {
		super(player);
	}


	@Override
	public String getTitle() {
		if(this.elementInfo != null) {
			return "";
		}
		return LanguageManager.translate("gui.beastiary.elements");
	}


	@Override
	public void initControls() {
		super.initControls();

		this.elementList = new GuiElementList(this, this.colLeftWidth, this.colLeftHeight, this.colLeftY,this.colLeftY + this.colLeftHeight, this.colLeftX);

		int descriptionListY = this.colRightY;
		this.descriptionList = new GuiElementDescriptionList(this, this.colRightWidth, this.colRightHeight, descriptionListY, this.colRightY + this.colRightHeight, this.colRightX);
	}


	@Override
	public void drawBackground(int x, int y, float partialTicks) {
		super.drawBackground(x, y, partialTicks);
	}


	@Override
	protected void updateControls(int x, int y, float partialTicks) {
		this.elementList.drawScreen(x, y, partialTicks);

		if(this.elementInfo != null) {
			this.descriptionList.elementInfo = this.elementInfo;
			this.descriptionList.drawScreen(x, y, partialTicks);
		}
	}


	@Override
	public void drawForeground(int x, int y, float partialTicks) {
		super.drawForeground(x, y, partialTicks);

		if(this.elementInfo == null) {
			String info = LanguageManager.translate("gui.beastiary.elements.about");
			this.drawSplitString(info, colRightX + 1, colRightY + 12 + 1, colRightWidth, 0xFFFFFF, true);
		}
	}


	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		super.actionPerformed(guiButton);
	}
}
