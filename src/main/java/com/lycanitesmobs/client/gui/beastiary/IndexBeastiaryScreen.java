package com.lycanitesmobs.client.gui.beastiary;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.VersionChecker;
import com.lycanitesmobs.client.gui.beastiary.lists.BeastiaryIndexList;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class IndexBeastiaryScreen extends BeastiaryScreen {
	public BeastiaryIndexList indexList;

	/**
	 * Opens this GUI up to the provided player.
	 * @param player The player to open the GUI to.
	 */
	public static void openToPlayer(EntityPlayer player) {
		if(player != null) {
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.BEASTIARY.id, player.getEntityWorld(), GuiHandler.Beastiary.INDEX.id, 0, 0);
		}
	}


	@Override
	public String getTitle() {
		return LanguageManager.translate("gui.beastiary.index.title");
	}


	public IndexBeastiaryScreen(EntityPlayer player) {
		super(player);
	}


	@Override
	protected void initControls() {
		super.initControls();

		int menuWidth = this.colRightWidth;

		int buttonCount = 5;
		int buttonPadding = 2;
		int buttonWidth = Math.round((float)(menuWidth / buttonCount)) - buttonPadding;
		int buttonWidthPadded = buttonWidth + buttonPadding;
		int buttonHeight = 20;
		int buttonX = this.colRightX + buttonPadding;
		int buttonY = this.colRightY + this.colRightHeight - buttonHeight;
		GuiButton button;

		// Links:
		button = new GuiButton(100, buttonX, buttonY, buttonWidth, buttonHeight, "Website");
		this.buttonList.add(button);
		button = new GuiButton(101, buttonX + buttonWidthPadded, buttonY, buttonWidth, buttonHeight, "Twitter");
		this.buttonList.add(button);
		button = new GuiButton(102, buttonX + (buttonWidthPadded * 2), buttonY, buttonWidth, buttonHeight, "Patreon");
		this.buttonList.add(button);
		button = new GuiButton(103, buttonX + (buttonWidthPadded * 3), buttonY, buttonWidth, buttonHeight, "Discord");
		this.buttonList.add(button);
		button = new GuiButton(104, buttonX + (buttonWidthPadded * 4), buttonY, buttonWidth, buttonHeight, "Card Game");
		this.buttonList.add(button);

		// Lists:
		this.indexList = new BeastiaryIndexList(this, this.colRightWidth, this.colRightHeight, this.colRightY + 93, buttonY - buttonPadding, this.colRightX + 2);
	}


	@Override
	public void drawBackground(int mouseX, int mouseY, float partialTicks) {
		super.drawBackground(mouseX, mouseY, partialTicks);
	}


	@Override
	protected void updateControls(int mouseX, int mouseY, float partialTicks) {
		super.updateControls(mouseX, mouseY, partialTicks);
	}


	@Override
	public void drawForeground(int mouseX, int mouseY, float partialTicks) {
		super.drawForeground(mouseX, mouseY, partialTicks);

		int yOffset = this.colRightY + 13;
		String info = LanguageManager.translate("gui.beastiary.index.description");
		this.drawSplitString(info, this.colRightX + 1, yOffset, this.colRightWidth, 0xFFFFFF, true);
		yOffset += this.getFontRenderer().getWordWrappedHeight(info, this.colRightWidth);

		// Get Latest Version:
		VersionChecker.VersionInfo latestVersion = VersionChecker.getLatestVersion(false);
		if(latestVersion == null) {
			return;
		}

		// Check Mod Version:
		String version = "\n\u00A7l" + LanguageManager.translate("gui.beastiary.index.version") + ": \u00A7r";
		if(latestVersion.isNewer) {
			version += "\u00A74";
		}
		version += LycanitesMobs.versionNumber + "\u00A7r";
		if(latestVersion.isNewer) {
			version += " \u00A7l" + LanguageManager.translate("gui.beastiary.index.version.newer") + ": \u00A7r\u00A72" + latestVersion.versionNumber + "\u00A7r";
		}
		this.drawSplitString(version, this.colRightX + 1, yOffset, this.colRightWidth, 0xFFFFFF, true);
		yOffset += this.getFontRenderer().getWordWrappedHeight(version, this.colRightWidth);

		// Latest Changes:
		this.indexList.versionInfo = latestVersion;
		this.indexList.drawScreen(mouseX, mouseY, partialTicks);
	}


	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		if(guiButton != null) {
			if(guiButton.id == 100) {
				try {
					this.openURI(new URI(LycanitesMobs.website));
				} catch (URISyntaxException e) {}
			}
			if(guiButton.id == 101) {
				try {
					this.openURI(new URI(LycanitesMobs.twitter));
				} catch (URISyntaxException e) {}
			}
			if(guiButton.id == 102) {
				try {
					this.openURI(new URI(LycanitesMobs.patreon));
				} catch (URISyntaxException e) {}
			}
			if(guiButton.id == 103) {
				try {
					this.openURI(new URI(LycanitesMobs.discord));
				} catch (URISyntaxException e) {}
			}
			if(guiButton.id == 104) {
				try {
					this.openURI(new URI("https://shardtcg.com"));
				} catch (URISyntaxException e) {}
			}
			super.actionPerformed(guiButton);
		}

		super.actionPerformed(guiButton);
	}
}
