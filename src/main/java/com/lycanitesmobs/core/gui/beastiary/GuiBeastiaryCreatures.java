package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.gui.beastiary.list.GuiCreatureList;
import com.lycanitesmobs.core.gui.beastiary.list.GuiCreatureTypeList;
import com.lycanitesmobs.core.gui.beastiary.list.GuiSubspeciesList;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import com.lycanitesmobs.core.info.Subspecies;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import com.lycanitesmobs.core.localisation.LanguageManager;

import java.io.IOException;

public class GuiBeastiaryCreatures extends GuiBeastiary {
	public GuiCreatureTypeList creatureTypeList;
	public GuiCreatureList creatureList;
	public GuiSubspeciesList subspeciesList;

	/**
	 * Opens this GUI up to the provided player.
	 * @param player The player to open the GUI to.
	 */
	public static void openToPlayer(EntityPlayer player) {
		if(player != null) {
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.BEASTIARY.id, player.getEntityWorld(), GuiHandler.Beastiary.CREATURES.id, 0, 0);
		}
	}


	public GuiBeastiaryCreatures(EntityPlayer player) {
		super(player);
	}


	@Override
	public String getTitle() {
		if(this.creatureList != null && this.playerExt.selectedCreature != null) {
			return this.playerExt.selectedCreature.getTitle();
		}
		if(this.creatureTypeList != null && this.playerExt.selectedCreatureType != null) {
			return this.playerExt.selectedCreatureType.getTitle();
		}
		if(this.playerExt.getBeastiary().creatureKnowledgeList.isEmpty()) {
			LanguageManager.translate("gui.beastiary.creatures.empty.title");
		}
		return LanguageManager.translate("gui.beastiary.creatures");
	}


	@Override
	public void initControls() {
		super.initControls();

		this.creatureTypeList = new GuiCreatureTypeList(this, this.colLeftWidth, this.colLeftHeight, this.colLeftY,this.colLeftY + this.colLeftHeight, this.colLeftX);

		int creatureListHeight = Math.round((float)this.colRightHeight * 0.6f);
		int creatureListY = this.colRightY + 20;
		this.creatureList = new GuiCreatureList(GuiCreatureList.Type.KNOWLEDGE, this, this.creatureTypeList, this.getScaledX(240F / 1920F), creatureListHeight, creatureListY,creatureListY + creatureListHeight, this.colRightX);

		int subspeciesListHeight = Math.round((float)this.colRightHeight * 0.3f);
		int subspeciesListY = creatureListY + 8 + creatureListHeight;
		this.subspeciesList = new GuiSubspeciesList(this, false, this.getScaledX(240F / 1920F), subspeciesListHeight, subspeciesListY,subspeciesListY + subspeciesListHeight, this.colRightX);
	}


	@Override
	public void drawBackground(int mouseX, int mouseY, float partialTicks) {
		super.drawBackground(mouseX, mouseY, partialTicks);
	}


	@Override
	protected void updateControls(int mouseX, int mouseY, float partialTicks) {
		super.updateControls(mouseX, mouseY, partialTicks);

		if(this.playerExt.getBeastiary().creatureKnowledgeList.isEmpty()) {
			return;
		}

		this.creatureTypeList.drawScreen(mouseX, mouseY, partialTicks);
		if(this.playerExt.selectedCreatureType != null) {
			this.creatureList.drawScreen(mouseX, mouseY, partialTicks);
			this.subspeciesList.drawScreen(mouseX, mouseY, partialTicks);
		}
	}


	@Override
	public void drawForeground(int mouseX, int mouseY, float partialTicks) {
		super.drawForeground(mouseX, mouseY, partialTicks);

		int marginX = this.getScaledX(240F / 1920F) + 8;
		int nextX = this.colRightX + marginX;
		int nextY = this.colRightY + 20;
		int width = this.colRightWidth - marginX;

		if(this.playerExt.getBeastiary().creatureKnowledgeList.isEmpty()) {
			String text = LanguageManager.translate("gui.beastiary.creatures.empty.info");
			this.drawSplitString(text, this.colRightX, nextY, this.colRightWidth, 0xFFFFFF, true);
			return;
		}

		// Creature Display:
		if(this.playerExt.selectedCreature != null) {
			CreatureKnowledge creatureKnowledge = this.playerExt.beastiary.getCreatureKnowledge(this.playerExt.selectedCreature.getName());

			// Model:
			this.renderCreature(this.playerExt.selectedCreature, this.colRightX + (marginX / 2) + (this.colRightWidth / 2), this.colRightY + 100, mouseX, mouseY, partialTicks);

			// Element:
			String text = "\u00A7l" + LanguageManager.translate("creature.stat.element") + ": " + "\u00A7r";
			text += this.playerExt.selectedCreature.elements != null ? this.playerExt.selectedCreature.getElementNames() : "None";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);

			// Subspecies:
			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = "\u00A7l" + LanguageManager.translate("creature.stat.subspecies") + ": " + "\u00A7r";
			boolean firstSubspecies = true;
			for(Subspecies subspecies : this.playerExt.selectedCreature.subspecies.values()) {
				if(!firstSubspecies) {
					text += ", ";
				}
				firstSubspecies = false;
				text += subspecies.getTitle();
			}
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);

			// Level:
			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = "\u00A7l" + LanguageManager.translate("creature.stat.cost") + ": " + "\u00A7r";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);
			this.drawLevel(this.playerExt.selectedCreature, AssetManager.getTexture("GUIPetLevel"),nextX + this.getFontRenderer().getStringWidth(text), nextY);

			// Knowledge Rank:
			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = "\u00A7l" + LanguageManager.translate("creature.stat.knowledge") + ": " + "\u00A7r";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);
			this.drawBar(AssetManager.getTexture("GUIPetSpiritEmpty"), nextX + this.getFontRenderer().getStringWidth(text), nextY, 0, 9, 9, 3, 10);
			this.drawBar(AssetManager.getTexture("GUIPetSpiritUsed"), nextX + this.getFontRenderer().getStringWidth(text), nextY, 0, 9, 9, creatureKnowledge.rank, 10);

			// Summary:
			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = "\u00A7l" + LanguageManager.translate("gui.beastiary.summary") + ": " + "\u00A7r";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);

			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = this.playerExt.selectedCreature.getDescription();
			this.drawSplitString(text, nextX, nextY, width, 0xFFFFFF, true);

			// Habitat:
			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = "\u00A7l" + LanguageManager.translate("gui.beastiary.habitat") + ": " + "\u00A7r";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);

			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			if(creatureKnowledge.rank >= 2) {
				text = this.playerExt.selectedCreature.getHabitatDescription();
			}
			else {
				text = LanguageManager.translate("gui.beastiary.unlockedat") + " " + LanguageManager.translate("creature.stat.knowledge") + " " + 2;
			}
			this.drawSplitString(text, nextX, nextY, width, 0xFFFFFF, true);

			// Combat:
			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = "\u00A7l" + LanguageManager.translate("gui.beastiary.combat") + ": " + "\u00A7r";
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);

			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			if(creatureKnowledge.rank >= 2) {
				text = this.playerExt.selectedCreature.getCombatDescription();
			}
			else {
				text = LanguageManager.translate("gui.beastiary.unlockedat") + " " + LanguageManager.translate("creature.stat.knowledge") + " " + 2;
			}
			this.drawSplitString(text, nextX, nextY, width, 0xFFFFFF, true);
		}

		// Creature Type Display:
		else if(this.playerExt.selectedCreatureType != null) {
			// Description:
			String text = this.playerExt.selectedCreatureType.getTitle();
			this.drawSplitString(text, nextX, nextY, width, 0xFFFFFF, true);

			// Descovered:
			nextY += 12 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = LanguageManager.translate("gui.beastiary.creatures.descovered") + ": ";
			text += this.playerExt.getBeastiary().getCreaturesDescovered(this.playerExt.selectedCreatureType);
			text += "/" + this.playerExt.selectedCreatureType.creatures.size();
			this.getFontRenderer().drawString(text, nextX, nextY, 0xFFFFFF, true);
		}

		// Base Display:
		else {
			String text = LanguageManager.translate("gui.beastiary.creatures.select");
			this.drawSplitString(text, this.colRightX, nextY, this.colRightWidth, 0xFFFFFF, true);
		}
	}


	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		super.actionPerformed(guiButton);
	}
}
