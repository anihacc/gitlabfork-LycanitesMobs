package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.client.gui.widgets.BaseList;
import com.lycanitesmobs.client.gui.widgets.BaseListEntry;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CreatureDescriptionList extends BaseList {
	public CreatureKnowledge creatureKnowledge;

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public CreatureDescriptionList(BeastiaryScreen parentGui, int width, int height, int top, int bottom, int x) {
		super(parentGui, width, height, top, bottom, x, 500);
	}

	@Override
	public void createEntries() {
		this.addEntry(new Entry(this));
	}

	/**
	 * List Entry
	 */
	public static class Entry extends BaseListEntry {
		private CreatureDescriptionList parentList;

		public Entry(CreatureDescriptionList parentList) {
			this.parentList = parentList;
		}

		@Override
		public void render(int index, int top, int left, int bottom, int right, int mouseX, int mouseY, boolean focus, float partialTicks) {
			if(index == 0) {
				this.drawSplitString(this.parentList.getContent(), left + 6, top, this.parentList.getWidth() - 20, 0xFFFFFF, true);
			}
		}

		@Override
		protected void onClicked() {}
	}

	public String getContent() {
		if(this.creatureKnowledge == null) {
			return "";
		}
		CreatureInfo creatureInfo = this.creatureKnowledge.getCreatureInfo();
		if(creatureInfo == null) {
			return "";
		}
		ITextComponent text = new StringTextComponent("");

		// Taming:
		if(creatureInfo.creatureType != null && creatureInfo.isTameable() && creatureInfo.creatureType.getTreatItem() != null) {
			text.appendText("\u00A7l")
					.appendSibling(new TranslationTextComponent("gui.beastiary.tameable"))
					.appendText(": " + "\u00A7r")
					.appendSibling(new TranslationTextComponent(creatureInfo.creatureType.getTreatItem().getTranslationKey() + ".name"))
					.appendText("\n\n");
		}

		// Summoning:
		if(creatureInfo.creatureType != null && creatureInfo.isSummonable() && creatureInfo.creatureType.getTreatItem() != null) {
			text.appendText("\u00A7l")
					.appendSibling(new TranslationTextComponent("gui.beastiary.summonable"))
					.appendText("\u00A7r\n\n");
		}

		// Summary:
		text.appendText("\u00A7l")
				.appendSibling(new TranslationTextComponent("gui.beastiary.summary"))
				.appendText(": " + "\u00A7r")
				.appendText("\n")
				.appendSibling(creatureInfo.getDescription());

		// Stats:
		text.appendText("\n\n\u00A7l")
				.appendSibling(new TranslationTextComponent("creature.stat.base"))
				.appendText(": " + "\u00A7r");

		if(this.creatureKnowledge.rank >= 2) {
			// Stats:
			String statPrefix = "\n" + new TranslationTextComponent("creature.stat.base") + " ";

			text.appendText("\n")
					.appendSibling(new TranslationTextComponent("creature.stat.health"))
					.appendText(": " + creatureInfo.health);
			text.appendText("\n")
					.appendSibling(new TranslationTextComponent("creature.stat.defense"))
					.appendText(": " + creatureInfo.defense);

			text.appendText("\n")
					.appendSibling(new TranslationTextComponent("creature.stat.speed"))
					.appendText(": " + creatureInfo.speed);
			text.appendText("\n")
					.appendSibling(new TranslationTextComponent("creature.stat.damage"))
					.appendText(": " + creatureInfo.damage);

			text.appendText("\n")
					.appendSibling(new TranslationTextComponent("creature.stat.pierce"))
					.appendText(": " + creatureInfo.pierce);
			ITextComponent effectText = new StringTextComponent(creatureInfo.effectDuration + "s " + creatureInfo.effectAmplifier + "X");
			if(creatureInfo.effectDuration <= 0 || creatureInfo.effectAmplifier < 0)
				effectText = new TranslationTextComponent("common.none");
			text.appendText("\n")
					.appendSibling(new TranslationTextComponent("creature.stat.effect"))
					.appendText(": ")
					.appendSibling(effectText);
		}
		else {
			text.appendText("\n")
					.appendSibling(new TranslationTextComponent("gui.beastiary.unlockedat"))
					.appendText(" ")
					.appendSibling(new TranslationTextComponent("creature.stat.knowledge"))
					.appendText(" " + 2);
		}

		// Combat:
		text.appendText("\n\n\u00A7l")
				.appendSibling(new TranslationTextComponent("gui.beastiary.combat"))
				.appendText(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			text.appendText("\n").appendSibling(creatureInfo.getCombatDescription());
		}
		else {
			text.appendText("\n")
					.appendSibling(new TranslationTextComponent("gui.beastiary.unlockedat"))
					.appendText(" ")
					.appendSibling(new TranslationTextComponent("creature.stat.knowledge"))
					.appendText(" " + 2);
		}

		// Habitat:
		text.appendText("\n\n\u00A7l")
				.appendSibling(new TranslationTextComponent("gui.beastiary.habitat"))
				.appendText(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			text.appendText("\n")
					.appendSibling(creatureInfo.getHabitatDescription());
		}
		else {
			text.appendText("\n")
					.appendSibling(new TranslationTextComponent("gui.beastiary.unlockedat"))
					.appendText(" ")
					.appendSibling(new TranslationTextComponent("creature.stat.knowledge"))
					.appendText(" " + 2);
		}

		return text.getFormattedText();
	}
}
