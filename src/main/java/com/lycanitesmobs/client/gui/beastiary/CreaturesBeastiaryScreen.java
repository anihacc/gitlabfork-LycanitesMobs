package com.lycanitesmobs.client.gui.beastiary;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.client.gui.beastiary.lists.CreatureDescriptionList;
import com.lycanitesmobs.client.gui.beastiary.lists.CreatureList;
import com.lycanitesmobs.client.gui.beastiary.lists.CreatureTypeList;
import com.lycanitesmobs.client.gui.beastiary.lists.SubspeciesList;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import com.lycanitesmobs.core.info.Subspecies;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CreaturesBeastiaryScreen extends BeastiaryScreen {
	public CreatureTypeList creatureTypeList;
	public CreatureList creatureList;
	public SubspeciesList subspeciesList;
	public CreatureDescriptionList descriptionList;

	public CreaturesBeastiaryScreen(PlayerEntity player) {
		super(player);
	}

	@Override
	public void initWidgets() {
		super.initWidgets();

		this.creatureTypeList = new CreatureTypeList(this, this.colLeftWidth, this.colLeftHeight, this.colLeftY,this.colLeftY + this.colLeftHeight, this.colLeftX);
		this.children.add(this.creatureTypeList);

		int selectionListsWidth = this.getScaledX(240F / 1920F);

		int creatureListY = this.colRightY;
		int creatureListHeight = Math.round((float)this.colRightHeight * 0.6f);
		this.creatureList = new CreatureList(CreatureList.Type.KNOWLEDGE, this, this.creatureTypeList, selectionListsWidth, creatureListHeight, creatureListY,creatureListY + creatureListHeight, this.colRightX);
		this.children.add(this.creatureList);

		int subspeciesListY = creatureListY + 2 + creatureListHeight;
		int subspeciesListHeight = Math.round((float)this.colRightHeight * 0.4f) - 2;
		this.subspeciesList = new SubspeciesList(this, false, selectionListsWidth, subspeciesListHeight, subspeciesListY,subspeciesListY + subspeciesListHeight, this.colRightX);
		this.children.add(this.subspeciesList);

		int newLine = this.getFontRenderer().getWordWrappedHeight("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA", this.colRightWidth - selectionListsWidth) + 2;
		int descriptionListY = this.colRightY + (newLine * 3);
		this.descriptionList = new CreatureDescriptionList(this, this.colRightWidth - selectionListsWidth, this.colRightHeight, descriptionListY, this.colRightY + this.colRightHeight, this.colRightX + selectionListsWidth + 2);
		this.children.add(this.descriptionList);
	}


	@Override
	public void renderBackground(int mouseX, int mouseY, float partialTicks) {
		super.renderBackground(mouseX, mouseY, partialTicks);

		// Selected Creature Model:
		if(this.playerExt.selectedCreature != null) {
			int marginX = this.getScaledX(240F / 1920F) + 8;
			this.renderCreature(this.playerExt.selectedCreature, this.colRightX + (marginX / 2) + (this.colRightWidth / 2), this.colRightY + 100, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	protected void renderWidgets(int mouseX, int mouseY, float partialTicks) {
		super.renderWidgets(mouseX, mouseY, partialTicks);

		if(this.playerExt.getBeastiary().creatureKnowledgeList.isEmpty()) {
			return;
		}

		this.creatureTypeList.render(mouseX, mouseY, partialTicks);
		if(this.playerExt.selectedCreatureType != null) {
			this.creatureList.render(mouseX, mouseY, partialTicks);
			this.subspeciesList.render(mouseX, mouseY, partialTicks);
		}
		if(this.playerExt.selectedCreature != null) {
			this.descriptionList.creatureKnowledge = this.playerExt.beastiary.getCreatureKnowledge(this.playerExt.selectedCreature.getName());
			this.descriptionList.render(mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public void renderForeground(int mouseX, int mouseY, float partialTicks) {
		super.renderForeground(mouseX, mouseY, partialTicks);

		int marginX = this.getScaledX(240F / 1920F) + 8;
		int nextX = this.colRightX + marginX;
		int nextY = this.colRightY;
		int width = this.colRightWidth - marginX;

		// No Creatures:
		if(this.playerExt.getBeastiary().creatureKnowledgeList.isEmpty()) {
			nextY += this.getFontRenderer().getWordWrappedHeight("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA", this.colRightWidth) + 2;
			String text = new TranslationTextComponent("gui.beastiary.creatures.empty.info").getFormattedText();
			this.drawSplitString(text, this.colRightX, nextY, this.colRightWidth, 0xFFFFFF, true);
			return;
		}

		// Creature Display:
		if(this.playerExt.selectedCreature != null) {
			Subspecies subspecies = null;
			if(this.playerExt.selectedSubspecies != 0) {
				subspecies = this.playerExt.selectedCreature.getSubspecies(this.playerExt.selectedSubspecies);
			}

			// Model:
			CreatureInfo creatureInfo = this.playerExt.selectedCreature;
			CreatureKnowledge creatureKnowledge = this.playerExt.beastiary.getCreatureKnowledge(this.playerExt.selectedCreature.getName());

			// Element:
			String text = "\u00A7l" + new TranslationTextComponent("creature.stat.element").getFormattedText() + ": " + "\u00A7r";
			text += creatureInfo.elements != null ? creatureInfo.getElementNames(subspecies).getFormattedText() : "None";
			this.getFontRenderer().drawSplitString(text, nextX, nextY, width, 0xFFFFFF);

			// Level:
			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, width);
			text = "\u00A7l" + new TranslationTextComponent("creature.stat.cost").getFormattedText() + ": " + "\u00A7r";
			this.getFontRenderer().drawSplitString(text, nextX, nextY, width, 0xFFFFFF);
			this.drawLevel(creatureInfo, TextureManager.getTexture("GUIPetLevel"),nextX + this.getFontRenderer().getStringWidth(text), nextY);

			// Knowledge Rank:
			nextY += 2 + this.getFontRenderer().getWordWrappedHeight(text, width);
			text = "\u00A7l" + new TranslationTextComponent("creature.stat.knowledge").getFormattedText() + ": " + "\u00A7r";
			this.getFontRenderer().drawSplitString(text, nextX, nextY, width, 0xFFFFFF);
			this.drawBar(TextureManager.getTexture("GUIPetSpiritEmpty"), nextX + this.getFontRenderer().getStringWidth(text), nextY, 0, 9, 9, 3, 10);
			if(creatureKnowledge != null) {
				this.drawBar(TextureManager.getTexture("GUIPetSpiritUsed"), nextX + this.getFontRenderer().getStringWidth(text), nextY, 0, 9, 9, creatureKnowledge.rank, 10);
			}
		}

		// Creature Type Display:
		else if(this.playerExt.selectedCreatureType != null) {
			// Description:
			nextY += this.getFontRenderer().getWordWrappedHeight("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA", this.colRightWidth) + 2;
			String text = this.playerExt.selectedCreatureType.getTitle().getFormattedText();
			this.drawSplitString(text, nextX, nextY, width, 0xFFFFFF, true);

			// Descovered:
			nextY += 12 + this.getFontRenderer().getWordWrappedHeight(text, colRightWidth);
			text = new TranslationTextComponent("gui.beastiary.creatures.descovered").getFormattedText() + ": ";
			text += this.playerExt.getBeastiary().getCreaturesDescovered(this.playerExt.selectedCreatureType);
			text += "/" + this.playerExt.selectedCreatureType.creatures.size();
			this.getFontRenderer().drawStringWithShadow(text, nextX, nextY, 0xFFFFFF);
		}

		// Base Display:
		else {
			nextY += this.getFontRenderer().getWordWrappedHeight("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA", this.colRightWidth) + 2;
			String text = new TranslationTextComponent("gui.beastiary.creatures.select").getFormattedText();
			this.drawSplitString(text, this.colRightX, nextY, this.colRightWidth, 0xFFFFFF, true);
		}
	}

	@Override
	public ITextComponent getTitle() {
		if(this.creatureList != null && this.playerExt.selectedCreature != null) {
			return new StringTextComponent("");
			//return this.playerExt.selectedCreature.getTitle();
		}
		if(this.creatureTypeList != null && this.playerExt.selectedCreatureType != null) {
			return this.playerExt.selectedCreatureType.getTitle();
		}
		if(this.playerExt.getBeastiary().creatureKnowledgeList.isEmpty()) {
			return new TranslationTextComponent("gui.beastiary.creatures.empty.title");
		}
		return new TranslationTextComponent("gui.beastiary.creatures");
	}
}
