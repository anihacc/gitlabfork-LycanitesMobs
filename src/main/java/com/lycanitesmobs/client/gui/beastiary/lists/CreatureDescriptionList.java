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

		if(creatureInfo.creatureType != null) {
			// Taming:
			if(creatureInfo.isTameable() && creatureInfo.creatureType.getTreatItem() != null) {
				text.func_240702_b_("\u00A7l")
						.func_230529_a_(new TranslationTextComponent("gui.beastiary.tameable"))
						.func_240702_b_(": " + "\u00A7r")
						.func_230529_a_(creatureInfo.creatureType.getTreatItem().getName())
						.func_240702_b_("\n\n");

				// Mounting:
				if(creatureInfo.isMountable()) {
					text.func_240702_b_("\u00A7l")
							.func_230529_a_(new TranslationTextComponent("gui.beastiary.mountable"))
							.func_240702_b_("\u00A7r\n\n");
				}
			}

			// Summoning:
			if(creatureInfo.isSummonable()) {
				text.func_240702_b_("\u00A7l")
						.func_230529_a_(new TranslationTextComponent("gui.beastiary.summonable"))
						.func_240702_b_("\u00A7r\n\n");
			}

			// Perching:
			if((creatureInfo.isTameable() || creatureInfo.isSummonable()) && creatureInfo.isPerchable()) {
				text.func_240702_b_("\u00A7l")
						.func_230529_a_(new TranslationTextComponent("gui.beastiary.perchable"))
						.func_240702_b_("\u00A7r\n\n");
			}
		}

		// Diet:
		text.func_240702_b_("\n\n\u00A7l")
				.func_230529_a_(new TranslationTextComponent("gui.beastiary.diet"))
				.func_240702_b_(": " + "\u00A7r")
				.func_240702_b_("\n").func_230529_a_(creatureInfo.getDietNames());

		// Summary:
		text.func_240702_b_("\n\n\u00A7l")
				.func_230529_a_(new TranslationTextComponent("gui.beastiary.summary"))
				.func_240702_b_(": " + "\u00A7r")
				.func_240702_b_("\n")
				.func_230529_a_(creatureInfo.getDescription());

		// Stats:
		text.func_240702_b_("\n\n\u00A7l")
				.func_230529_a_(new TranslationTextComponent("creature.stat.base"))
				.func_240702_b_(": " + "\u00A7r");

		if(this.creatureKnowledge.rank >= 2) {
			// Stats:
			String statPrefix = "\n" + new TranslationTextComponent("creature.stat.base") + " ";

			text.func_240702_b_("\n")
					.func_230529_a_(new TranslationTextComponent("creature.stat.health"))
					.func_240702_b_(": " + creatureInfo.health);
			text.func_240702_b_("\n")
					.func_230529_a_(new TranslationTextComponent("creature.stat.defense"))
					.func_240702_b_(": " + creatureInfo.defense);

			text.func_240702_b_("\n")
					.func_230529_a_(new TranslationTextComponent("creature.stat.speed"))
					.func_240702_b_(": " + creatureInfo.speed);
			text.func_240702_b_("\n")
					.func_230529_a_(new TranslationTextComponent("creature.stat.damage"))
					.func_240702_b_(": " + creatureInfo.damage);

			text.func_240702_b_("\n")
					.func_230529_a_(new TranslationTextComponent("creature.stat.pierce"))
					.func_240702_b_(": " + creatureInfo.pierce);
			ITextComponent effectText = new StringTextComponent(creatureInfo.effectDuration + "s " + creatureInfo.effectAmplifier + "X");
			if(creatureInfo.effectDuration <= 0 || creatureInfo.effectAmplifier < 0)
				effectText = new TranslationTextComponent("common.none");
			text.func_240702_b_("\n")
					.func_230529_a_(new TranslationTextComponent("creature.stat.effect"))
					.func_240702_b_(": ")
					.func_230529_a_(effectText);
		}
		else {
			text.func_240702_b_("\n")
					.func_230529_a_(new TranslationTextComponent("gui.beastiary.unlockedat"))
					.func_240702_b_(" ")
					.func_230529_a_(new TranslationTextComponent("creature.stat.knowledge"))
					.func_240702_b_(" " + 2);
		}

		// Combat:
		text.func_240702_b_("\n\n\u00A7l")
				.func_230529_a_(new TranslationTextComponent("gui.beastiary.combat"))
				.func_240702_b_(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			text.func_240702_b_("\n").func_230529_a_(creatureInfo.getCombatDescription());
		}
		else {
			text.func_240702_b_("\n")
					.func_230529_a_(new TranslationTextComponent("gui.beastiary.unlockedat"))
					.func_240702_b_(" ")
					.func_230529_a_(new TranslationTextComponent("creature.stat.knowledge"))
					.func_240702_b_(" " + 2);
		}

		// Habitat:
		text.func_240702_b_("\n\n\u00A7l")
				.func_230529_a_(new TranslationTextComponent("gui.beastiary.habitat"))
				.func_240702_b_(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			text.func_240702_b_("\n")
					.func_230529_a_(creatureInfo.getHabitatDescription());
		}
		else {
			text.func_240702_b_("\n")
					.func_230529_a_(new TranslationTextComponent("gui.beastiary.unlockedat"))
					.func_240702_b_(" ")
					.func_230529_a_(new TranslationTextComponent("creature.stat.knowledge"))
					.func_240702_b_(" " + 2);
		}

		// Biomes:
		text.func_240702_b_("\n\n\u00A7l")
				.func_230529_a_(new TranslationTextComponent("gui.beastiary.biomes"))
				.func_240702_b_(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			text.func_240702_b_("\n")
					.func_230529_a_(creatureInfo.getBiomeNames());
		}
		else {
			text.func_240702_b_("\n")
					.func_230529_a_(new TranslationTextComponent("gui.beastiary.unlockedat"))
					.func_240702_b_(" ")
					.func_230529_a_(new TranslationTextComponent("creature.stat.knowledge"))
					.func_240702_b_(" " + 2);
		}

		// Drops:
		text.func_240702_b_("\n\n\u00A7l")
				.func_230529_a_(new TranslationTextComponent("gui.beastiary.drops"))
				.func_240702_b_(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			text.func_240702_b_("\n")
					.func_230529_a_(creatureInfo.getDropNames());
		}
		else {
			text.func_240702_b_("\n")
					.func_230529_a_(new TranslationTextComponent("gui.beastiary.unlockedat"))
					.func_240702_b_(" ")
					.func_230529_a_(new TranslationTextComponent("creature.stat.knowledge"))
					.func_240702_b_(" " + 2);
		}

		return text.getString();
	}
}
