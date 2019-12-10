package com.lycanitesmobs.client.gui.beastiary.list;

import com.lycanitesmobs.client.gui.beastiary.GuiBeastiary;
import com.lycanitesmobs.core.info.CreatureGroup;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiCreatureDescriptionList extends GuiScrollingList {
	protected GuiBeastiary parentGui;
	public CreatureKnowledge creatureKnowledge;

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiCreatureDescriptionList(GuiBeastiary parentGui, int width, int height, int top, int bottom, int x) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, x, 10800, width, height);
		this.parentGui = parentGui;
	}


	@Override
	protected int getSize() {
		return 1;
	}


	@Override
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
	}


	public String getContent() {
		if(this.creatureKnowledge == null) {
			return "";
		}
		CreatureInfo creatureInfo = this.creatureKnowledge.getCreatureInfo();
		if(creatureInfo == null) {
			return "";
		}
		String text = "";

		if(creatureInfo.creatureType != null) {
			// Taming:
			if(creatureInfo.isTameable() && creatureInfo.creatureType.getTreatItem() != null) {
				text += "\u00A7l" + LanguageManager.translate("gui.beastiary.tameable") + ": " + "\u00A7r" + LanguageManager.translate(creatureInfo.creatureType.getTreatItem().getUnlocalizedName() + ".name") + "\n\n";

				// Mounting:
				if(creatureInfo.isMountable()) {
					text += "\u00A7l" + LanguageManager.translate("gui.beastiary.mountable") + "\u00A7r\n\n";
				}
			}

			// Summoning:
			if(creatureInfo.isSummonable()) {
				text += "\u00A7l" + LanguageManager.translate("gui.beastiary.summonable") + "\u00A7r\n\n";
			}

			// Perching:
			if((creatureInfo.isTameable() || creatureInfo.isSummonable()) && creatureInfo.isPerchable()) {
				text += "\u00A7l" + LanguageManager.translate("gui.beastiary.perchable") + "\u00A7r\n\n";
			}
		}

		// Diet:
		text += "\u00A7l" + LanguageManager.translate("gui.beastiary.diet") + ": " + "\u00A7r" + creatureInfo.getDietNames() + "\n\n";

		/*/ Groups:
		text += "\u00A7l" + LanguageManager.translate("gui.beastiary.groups") + ": " + "\u00A7r";
		for (CreatureGroup creatureGroup : creatureInfo.getGroups()) {
			text += creatureGroup.getName() + " ";
		}
		text += "\n\n";*/

		// Summary:
		text += "\u00A7l" + LanguageManager.translate("gui.beastiary.summary") + ": " + "\u00A7r";
		text += "\n" + creatureInfo.getDescription();

		// Stats:
		text += "\n\n\u00A7l" + LanguageManager.translate("creature.stat.base") + ": " + "\u00A7r";
		if(this.creatureKnowledge.rank >= 2) {
			// Stats:
			String statPrefix = "\n" + LanguageManager.translate("creature.stat.base") + " ";

			text += "\n" + LanguageManager.translate("creature.stat.health") + ": " + creatureInfo.health;
			text += "\n" + LanguageManager.translate("creature.stat.defense") + ": " + creatureInfo.defense;

			text += "\n" + LanguageManager.translate("creature.stat.speed") + ": " + creatureInfo.speed;
			text += "\n" + LanguageManager.translate("creature.stat.damage") + ": " + creatureInfo.damage;

			text += "\n" + LanguageManager.translate("creature.stat.pierce") + ": " + creatureInfo.pierce;
			String effectText = creatureInfo.effectDuration + "s " + creatureInfo.effectAmplifier + "X";
			if(creatureInfo.effectDuration <= 0 || creatureInfo.effectAmplifier < 0)
				effectText = LanguageManager.translate("common.none");
			text += "\n" + LanguageManager.translate("creature.stat.effect") + ": " + effectText;
		}
		else {
			text += "\n" + LanguageManager.translate("gui.beastiary.unlockedat") + " " + LanguageManager.translate("creature.stat.knowledge") + " " + 2;
		}

		// Combat:
		text += "\n\n\u00A7l" + LanguageManager.translate("gui.beastiary.combat") + ": " + "\u00A7r";
		if(this.creatureKnowledge.rank >= 2)
			text += "\n" + creatureInfo.getCombatDescription();
		else
			text += "\n" + LanguageManager.translate("gui.beastiary.unlockedat") + " " + LanguageManager.translate("creature.stat.knowledge") + " " + 2;

		// Habitat:
		text += "\n\n\u00A7l" + LanguageManager.translate("gui.beastiary.habitat") + ": " + "\u00A7r";
		if(this.creatureKnowledge.rank >= 2)
			text += "\n" + creatureInfo.getHabitatDescription();
		else
			text += "\n" + LanguageManager.translate("gui.beastiary.unlockedat") + " " + LanguageManager.translate("creature.stat.knowledge") + " " + 2;

		return text;
	}

	/** Overridden to change the background gradient without copying over an entire function. **/
	@Override
	protected void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2) {
		color1 = 0x33101010;
		color2 = color1;
		GuiUtils.drawGradientRect(0, left, top, right, bottom, color1, color2);
	}
}
