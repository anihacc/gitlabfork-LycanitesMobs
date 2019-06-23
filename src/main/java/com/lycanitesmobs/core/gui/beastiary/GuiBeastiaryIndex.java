package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.VersionChecker;
import com.lycanitesmobs.core.gui.ButtonBase;
import com.lycanitesmobs.core.gui.beastiary.list.GuiIndexList;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.net.URI;
import java.net.URISyntaxException;

public class GuiBeastiaryIndex extends GuiBeastiary {
	public GuiIndexList indexList;

	@Override
	public ITextComponent getTitle() {
		return new TranslationTextComponent(LanguageManager.translate("gui.beastiary.index.title"));
	}


	public GuiBeastiaryIndex(PlayerEntity player) {
		super(player);
	}


	@Override
	protected void initControls() {
		super.initControls();

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
		this.buttons.add(button);
		button = new ButtonBase(101, buttonX + buttonWidthPadded, buttonY, buttonWidth, buttonHeight, "Patreon", this);
		this.buttons.add(button);

		// Lists:
		this.indexList = new GuiIndexList(this, this.colRightWidth, this.colRightHeight, this.colRightY + 93, buttonY - buttonPadding, this.colRightX + 2);
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
		String version = "\n§l" + LanguageManager.translate("gui.beastiary.index.version") + ": §r";
		if(latestVersion.isNewer) {
			version += "§4";
		}
		version += LycanitesMobs.versionNumber + "§r";
		if(latestVersion.isNewer) {
			version += " §l" + LanguageManager.translate("gui.beastiary.index.version.newer") + ": §r§2" + latestVersion.versionNumber + "§r";
		}
		this.drawSplitString(version, this.colRightX + 1, yOffset, this.colRightWidth, 0xFFFFFF, true);
		yOffset += this.getFontRenderer().getWordWrappedHeight(version, this.colRightWidth);

		// Latest Changes:
		this.indexList.versionInfo = latestVersion;
		this.indexList.render(mouseX, mouseY, partialTicks);
	}


	@Override
	public void actionPerformed(byte buttonId) {
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
}
