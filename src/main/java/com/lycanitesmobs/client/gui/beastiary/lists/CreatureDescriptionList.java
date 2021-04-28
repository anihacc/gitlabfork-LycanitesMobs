package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.client.gui.widgets.BaseList;
import com.lycanitesmobs.client.gui.widgets.BaseListEntry;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import com.mojang.blaze3d.matrix.MatrixStack;
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
		public void render(MatrixStack matrixStack, int index, int top, int left, int bottom, int right, int mouseX, int mouseY, boolean focus, float partialTicks) {
			if(index == 0) {
				this.drawSplitString(matrixStack, this.parentList.getContent(), left + 6, top, this.parentList.getWidth() - 20, 0xFFFFFF, true);
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
				new TranslationTextComponent("\u00A7l")
						.append(new TranslationTextComponent("gui.beastiary.tameable"))
						.appendString(": " + "\u00A7r")
						.append(creatureInfo.creatureType.getTreatItem().getName())
						.appendString("\n\n");

				// Mounting:
				if(creatureInfo.isMountable()) {
					new TranslationTextComponent("\u00A7l")
							.append(new TranslationTextComponent("gui.beastiary.mountable"))
							.appendString("\u00A7r\n\n");
				}
			}

			// Summoning:
			if(creatureInfo.isSummonable()) {
				new TranslationTextComponent("\u00A7l")
						.append(new TranslationTextComponent("gui.beastiary.summonable"))
						.appendString("\u00A7r\n\n");
			}

			// Perching:
			if((creatureInfo.isTameable() || creatureInfo.isSummonable()) && creatureInfo.isPerchable()) {
				new TranslationTextComponent("\u00A7l")
						.append(new TranslationTextComponent("gui.beastiary.perchable"))
						.appendString("\u00A7r\n\n");
			}
		}

		// Diet:
		new TranslationTextComponent("\n\n\u00A7l")
				.append(new TranslationTextComponent("gui.beastiary.diet"))
				.appendString(": " + "\u00A7r")
				.appendString("\n").append(creatureInfo.getDietNames());

		// Summary:
		new TranslationTextComponent("\n\n\u00A7l")
				.append(new TranslationTextComponent("gui.beastiary.summary"))
				.appendString(": " + "\u00A7r")
				.appendString("\n")
				.append(creatureInfo.getDescription());

		// Stats:
		new TranslationTextComponent("\n\n\u00A7l")
				.append(new TranslationTextComponent("creature.stat.base"))
				.appendString(": " + "\u00A7r");

		if(this.creatureKnowledge.rank >= 2) {
			// Stats:
			String statPrefix = "\n" + new TranslationTextComponent("creature.stat.base") + " ";

			new TranslationTextComponent("\n")
					.append(new TranslationTextComponent("creature.stat.health"))
					.appendString(": " + creatureInfo.health);
			new TranslationTextComponent("\n")
					.append(new TranslationTextComponent("creature.stat.defense"))
					.appendString(": " + creatureInfo.defense);

			new TranslationTextComponent("\n")
					.append(new TranslationTextComponent("creature.stat.speed"))
					.appendString(": " + creatureInfo.speed);
			new TranslationTextComponent("\n")
					.append(new TranslationTextComponent("creature.stat.damage"))
					.appendString(": " + creatureInfo.damage);

			new TranslationTextComponent("\n")
					.append(new TranslationTextComponent("creature.stat.pierce"))
					.appendString(": " + creatureInfo.pierce);
			ITextComponent effectText = new StringTextComponent(creatureInfo.effectDuration + "s " + creatureInfo.effectAmplifier + "X");
			if(creatureInfo.effectDuration <= 0 || creatureInfo.effectAmplifier < 0)
				effectText = new TranslationTextComponent("common.none");
			new TranslationTextComponent("\n")
					.append(new TranslationTextComponent("creature.stat.effect"))
					.appendString(": ")
					.append(effectText);
		}
		else {
			new TranslationTextComponent("\n")
					.append(new TranslationTextComponent("gui.beastiary.unlockedat"))
					.appendString(" ")
					.append(new TranslationTextComponent("creature.stat.knowledge"))
					.appendString(" " + 2);
		}

		// Combat:
		new TranslationTextComponent("\n\n\u00A7l")
				.append(new TranslationTextComponent("gui.beastiary.combat"))
				.appendString(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			new TranslationTextComponent("\n").append(creatureInfo.getCombatDescription());
		}
		else {
			new TranslationTextComponent("\n")
					.append(new TranslationTextComponent("gui.beastiary.unlockedat"))
					.appendString(" ")
					.append(new TranslationTextComponent("creature.stat.knowledge"))
					.appendString(" " + 2);
		}

		// Habitat:
		new TranslationTextComponent("\n\n\u00A7l")
				.append(new TranslationTextComponent("gui.beastiary.habitat"))
				.appendString(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			new TranslationTextComponent("\n")
					.append(creatureInfo.getHabitatDescription());
		}
		else {
			new TranslationTextComponent("\n")
					.append(new TranslationTextComponent("gui.beastiary.unlockedat"))
					.appendString(" ")
					.append(new TranslationTextComponent("creature.stat.knowledge"))
					.appendString(" " + 2);
		}

		// Biomes:
		new TranslationTextComponent("\n\n\u00A7l")
				.append(new TranslationTextComponent("gui.beastiary.biomes"))
				.appendString(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			new TranslationTextComponent("\n")
					.append(creatureInfo.getBiomeNames());
		}
		else {
			new TranslationTextComponent("\n")
					.append(new TranslationTextComponent("gui.beastiary.unlockedat"))
					.appendString(" ")
					.append(new TranslationTextComponent("creature.stat.knowledge"))
					.appendString(" " + 2);
		}

		// Drops:
		new TranslationTextComponent("\n\n\u00A7l")
				.append(new TranslationTextComponent("gui.beastiary.drops"))
				.appendString(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			new TranslationTextComponent("\n")
					.append(creatureInfo.getDropNames());
		}
		else {
			new TranslationTextComponent("\n")
					.append(new TranslationTextComponent("gui.beastiary.unlockedat"))
					.appendString(" ")
					.append(new TranslationTextComponent("creature.stat.knowledge"))
					.appendString(" " + 2);
		}

		return text.getString();
	}
}
