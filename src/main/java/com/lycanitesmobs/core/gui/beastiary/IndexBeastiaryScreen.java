package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.VersionChecker;
import com.lycanitesmobs.core.gui.buttons.ButtonBase;
import com.lycanitesmobs.core.gui.beastiary.list.GuiIndexList;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.net.URI;
import java.net.URISyntaxException;

public class IndexBeastiaryScreen extends BeastiaryScreen {
	public GuiIndexList indexList;

	public IndexBeastiaryScreen(PlayerEntity player) {
		super(player);
	}

	@Override
	protected void initWidgets() {
		super.initWidgets();

		int menuWidth = this.colRightWidth;

		int buttonCount = 2;
		int buttonPadding = 2;
		int buttonWidth = Math.round((float)(menuWidth / buttonCount)) - buttonPadding;
		int buttonWidthPadded = buttonWidth + buttonPadding;
		int buttonHeight = 20;
		int buttonX = this.colRightX + buttonPadding;
		int buttonY = this.colRightY + this.colRightHeight - buttonHeight;
		ButtonBase button;

		// Links:
		button = new ButtonBase(100, buttonX, buttonY, buttonWidth, buttonHeight, "Website", this);
		this.addButton(button);
		button = new ButtonBase(101, buttonX + buttonWidthPadded, buttonY, buttonWidth, buttonHeight, "Patreon", this);
		this.addButton(button);

		// Lists:
		this.indexList = new GuiIndexList(this, this.colRightWidth, this.colRightHeight, this.colRightY + 93, buttonY - buttonPadding, this.colRightX + 2);
	}

	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		super.renderBackground(mouseX, mouseY, partialTicks);
	}

	@Override
	public void renderForeground(int mouseX, int mouseY, float partialTicks) {
		super.renderForeground(mouseX, mouseY, partialTicks);

		int yOffset = this.colRightY + 13;
		String info = new TranslationTextComponent("gui.beastiary.index.description").getFormattedText();
		this.drawSplitString(info, this.colRightX + 1, yOffset, this.colRightWidth, 0xFFFFFF, true);
		yOffset += this.getFontRenderer().getWordWrappedHeight(info, this.colRightWidth);

		// Get Latest Version:
		VersionChecker.VersionInfo latestVersion = VersionChecker.getLatestVersion(false);
		if(latestVersion == null) {
			return;
		}

		// Check Mod Version:
		String version = "\n\u00A7l" + new TranslationTextComponent("gui.beastiary.index.version").getFormattedText() + ": \u00A7r";
		if(latestVersion.isNewer) {
			version += "\u00A74";
		}
		version += LycanitesMobs.versionNumber + "\u00A7r";
		if(latestVersion.isNewer) {
			version += " \u00A7l" + new TranslationTextComponent("gui.beastiary.index.version.newer").getFormattedText() + ": \u00A7r\u00A72" + latestVersion.versionNumber + "\u00A7r";
		}
		this.drawSplitString(version, this.colRightX + 1, yOffset, this.colRightWidth, 0xFFFFFF, true);

		// Latest Changes:
		this.indexList.versionInfo = latestVersion;
		//this.indexList.render(mouseX, mouseY, partialTicks); TODO Lists
	}

	@Override
	public void actionPerformed(byte buttonId) {
		LycanitesMobs.logDebug("", "Button pressed: " + buttonId);
		if(buttonId == 100) {
			try {
				this.openURI(new URI(LycanitesMobs.website));
			} catch (URISyntaxException e) {}
		}
		if(buttonId == 101) {
			try {
				this.openURI(new URI(LycanitesMobs.websitePatreon));
			} catch (URISyntaxException e) {}
		}
		super.actionPerformed(buttonId);
	}

	@Override
	public ITextComponent getTitle() {
		return new TranslationTextComponent("gui.beastiary.index.title");
	}
}
