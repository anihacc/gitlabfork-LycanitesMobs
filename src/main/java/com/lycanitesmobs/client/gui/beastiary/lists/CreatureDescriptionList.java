package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.client.gui.widgets.BaseList;
import com.lycanitesmobs.client.gui.widgets.BaseListEntry;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public class CreatureDescriptionList extends BaseList {
	protected CreatureKnowledge creatureKnowledge;

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

	public void setCreatureKnowledge(CreatureKnowledge creatureKnowledge) {
		this.creatureKnowledge = creatureKnowledge;
	}

	@Override
	protected int getMaxPosition() {
		return this.drawHelper.getWordWrappedHeight(this.getContent(), (this.width / 2)) + this.headerHeight;
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
		public void render(PoseStack matrixStack, int index, int top, int left, int bottom, int right, int mouseX, int mouseY, boolean focus, float partialTicks) {
			if(index == 0) {
				this.parentList.drawHelper.drawStringWrapped(matrixStack, this.parentList.getContent(), left + 6, top, this.parentList.getWidth() - 20, 0xFFFFFF, true);
			}
		}

		@Override
		protected void onClicked() {}

		@Override
		public List<? extends GuiEventListener> children() {
			return null;
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return null;
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
		TextComponent text = new TextComponent("");

		if(creatureInfo.creatureType != null) {
			// Taming:
			if(creatureInfo.isTameable() && creatureInfo.creatureType.getTreatItem() != null) {
				text.append("\u00A7l")
						.append(new TranslatableComponent("gui.beastiary.tameable"))
						.append(": " + "\u00A7r")
						.append(creatureInfo.creatureType.getTreatItem().getDescription())
						.append("\n");

				// Mounting:
				if(creatureInfo.isMountable()) {
					text.append("\n\u00A7l")
							.append(new TranslatableComponent("gui.beastiary.mountable"))
							.append("\u00A7r\n");
				}
			}

			// Summoning:
			if(creatureInfo.isSummonable()) {
				text.append("\u00A7l")
						.append(new TranslatableComponent("gui.beastiary.summonable"))
						.append("\u00A7r\n");
			}

			// Perching:
			if((creatureInfo.isTameable() || creatureInfo.isSummonable()) && creatureInfo.isPerchable()) {
				text.append("\u00A7l")
						.append(new TranslatableComponent("gui.beastiary.perchable"))
						.append("\u00A7r\n");
			}
		}

		if (this.drawHelper.getStringWidth(text.getString()) > 0) {
			text.append("\n");
		}

		// Diet:
		text.append("\u00A7l")
				.append(new TranslatableComponent("gui.beastiary.diet"))
				.append(": " + "\u00A7r")
				.append("\n").append(creatureInfo.getDietNames());

		// Summary:
		text.append("\n\n\u00A7l")
				.append(new TranslatableComponent("gui.beastiary.summary"))
				.append(": " + "\u00A7r")
				.append("\n")
				.append(creatureInfo.getDescription());

		// Stats:
		text.append("\n\n\u00A7l")
				.append(new TranslatableComponent("creature.stat.base"))
				.append(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			text.append("\n")
					.append(new TranslatableComponent("creature.stat.health"))
					.append(": " + creatureInfo.health);
			text.append("\n")
					.append(new TranslatableComponent("creature.stat.defense"))
					.append(": " + creatureInfo.defense);

			text.append("\n")
					.append(new TranslatableComponent("creature.stat.speed"))
					.append(": " + creatureInfo.speed);
			text.append("\n")
					.append(new TranslatableComponent("creature.stat.damage"))
					.append(": " + creatureInfo.damage);

			text.append("\n")
					.append(new TranslatableComponent("creature.stat.pierce"))
					.append(": " + creatureInfo.pierce);
			Component effectText = new TextComponent(creatureInfo.effectDuration + "s " + creatureInfo.effectAmplifier + "X");
			if(creatureInfo.effectDuration <= 0 || creatureInfo.effectAmplifier < 0) {
				effectText = new TranslatableComponent("common.none");
			}
			text.append("\n")
					.append(new TranslatableComponent("creature.stat.effect"))
					.append(": ")
					.append(effectText);
		}
		else {
			text.append("\n")
					.append(new TranslatableComponent("gui.beastiary.unlockedat"))
					.append(" ")
					.append(new TranslatableComponent("creature.stat.knowledge"))
					.append(" " + 2);
		}

		// Combat:
		text.append("\n\n\u00A7l")
				.append(new TranslatableComponent("gui.beastiary.combat"))
				.append(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			text.append("\n").append(creatureInfo.getCombatDescription());
		}
		else {
			text.append("\n")
					.append(new TranslatableComponent("gui.beastiary.unlockedat"))
					.append(" ")
					.append(new TranslatableComponent("creature.stat.knowledge"))
					.append(" " + 2);
		}

		// Habitat:
		text.append("\n\n\u00A7l")
				.append(new TranslatableComponent("gui.beastiary.habitat"))
				.append(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			text.append("\n")
					.append(creatureInfo.getHabitatDescription());
		}
		else {
			text.append("\n")
					.append(new TranslatableComponent("gui.beastiary.unlockedat"))
					.append(" ")
					.append(new TranslatableComponent("creature.stat.knowledge"))
					.append(" " + 2);
		}

		// Biomes:
		text.append("\n\n\u00A7l")
				.append(new TranslatableComponent("gui.beastiary.biomes"))
				.append(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			text.append("\n")
					.append(creatureInfo.getBiomeNames());
		}
		else {
			text.append("\n")
					.append(new TranslatableComponent("gui.beastiary.unlockedat"))
					.append(" ")
					.append(new TranslatableComponent("creature.stat.knowledge"))
					.append(" " + 2);
		}

		// Drops:
		text.append("\n\n\u00A7l")
				.append(new TranslatableComponent("gui.beastiary.drops"))
				.append(": " + "\u00A7r");
		if(this.creatureKnowledge.rank >= 2) {
			text.append("\n")
					.append(creatureInfo.getDropNames());
		}
		else {
			text.append("\n")
					.append(new TranslatableComponent("gui.beastiary.unlockedat"))
					.append(" ")
					.append(new TranslatableComponent("creature.stat.knowledge"))
					.append(" " + 2);
		}

		return text.getString();
	}
}
