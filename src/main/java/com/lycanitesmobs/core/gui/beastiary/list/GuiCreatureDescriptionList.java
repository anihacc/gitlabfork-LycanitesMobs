package com.lycanitesmobs.core.gui.beastiary.list;

import com.lycanitesmobs.core.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.core.gui.widgets.BaseList;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiCreatureDescriptionList extends BaseList {
	public CreatureKnowledge creatureKnowledge;

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiCreatureDescriptionList(BeastiaryScreen parentGui, int width, int height, int top, int bottom, int x) {
		super(parentGui, width, height, top, bottom, x, 10800);
	}

	@Override
	public void createEntries() {

	}


	@Override
	protected int getItemCount() {
		return 1;
	}


	/*@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.selectedIndex = index;
	}


	@Override
	protected boolean isSelected(int index) {
		return false;
	}


	@Override
	protected void drawBackground() {}


	@Override
	protected int getContentHeight() {
		return this.parentGui.getFontRenderer().getWordWrappedHeight(this.getContent(), this.listWidth) + 10;
	}


	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		if(index == 0 && this.creatureKnowledge != null) {
			this.parentGui.drawSplitString(this.getContent(), this.left + 6, boxTop, this.listWidth - 20, 0xFFFFFF, true);
		}
	}*/


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
					.appendText(": " + effectText);
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

	/** Overridden to change the background gradient without copying over an entire function. **/
	protected void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2) {
		color1 = 0x33101010;
		color2 = color1;
		GuiUtils.drawGradientRect(0, left, top, right, bottom, color1, color2);
	}
}
