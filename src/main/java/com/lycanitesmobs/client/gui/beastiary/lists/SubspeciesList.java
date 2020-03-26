package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.info.Variant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.HashMap;
import java.util.Map;

public class SubspeciesList extends GuiScrollingList {
	private BeastiaryScreen parentGui;
	private CreatureInfo creature;
	private Map<Integer, Entry> entryList = new HashMap<>();
	private boolean summoning;

	/**
	 * Constructor
	 * @param parentGui The Beastiary GUI using this list.
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public SubspeciesList(BeastiaryScreen parentGui, boolean summoning, int width, int height, int top, int bottom, int x) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, x, 24, width, height);
		this.parentGui = parentGui;
		this.summoning = summoning;
		this.refreshList();
	}


	/**
	 * Reloads all items in this list.
	 */
	public void refreshList() {
		// Clear:
		this.entryList.clear();

		if(!this.summoning) {
			this.creature = this.parentGui.playerExt.selectedCreature;
		}
		else {
			this.creature = this.parentGui.playerExt.getSelectedSummonSet().getCreatureInfo();
		}
		if(this.creature == null) {
			return;
		}

		int index = 0;
		for(Subspecies subspecies : this.creature.subspecies.values()) {
			this.entryList.put(index++, new Entry(subspecies.index, 0));
			for (int variantIndex : subspecies.variants.keySet()) {
				if (!this.parentGui.playerExt.getBeastiary().hasKnowledgeRank(this.creature.getName(), 2)) {
					continue;
				}
				Variant variant = subspecies.getVariant(variantIndex);
				if(variant == null) {
					continue;
				}
				if (this.summoning && "rare".equals(variant.rarity)) {
					continue;
				}
				this.entryList.put(index++, new Entry(subspecies.index, variant.index));
			}
		}
	}


	@Override
	protected int getSize() {
		return this.entryList.size();
	}


	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.selectedIndex = index;
		if(!this.summoning) {
			this.parentGui.playerExt.selectedSubspecies = this.entryList.get(index).subspeciesIndex;
			this.parentGui.playerExt.selectedVariant = this.entryList.get(index).variantIndex;
		}
		else {
			this.parentGui.playerExt.getSelectedSummonSet().setSubspecies(this.entryList.get(index).subspeciesIndex);
			this.parentGui.playerExt.getSelectedSummonSet().setVariant(this.entryList.get(index).variantIndex);
			this.parentGui.playerExt.sendSummonSetToServer((byte)this.parentGui.playerExt.selectedSummonSet);
		}
	}


	@Override
	protected boolean isSelected(int index) {
		if(!this.summoning) {
			return this.parentGui.playerExt.selectedSubspecies == this.entryList.get(index).subspeciesIndex &&
					this.parentGui.playerExt.selectedVariant == this.entryList.get(index).variantIndex;
		}
		else {
			return this.parentGui.playerExt.getSelectedSummonSet().getSubspecies() == this.entryList.get(index).subspeciesIndex &&
					this.parentGui.playerExt.getSelectedSummonSet().getVariant() == this.entryList.get(index).variantIndex;
		}
	}
	

	@Override
	protected void drawBackground() {
		if(!this.summoning) {
			if(this.creature != this.parentGui.playerExt.selectedCreature) {
				this.refreshList();
			}
		}
		else {
			if(this.creature != this.parentGui.playerExt.getSelectedSummonSet().getCreatureInfo()) {
				this.refreshList();
			}
		}

	}


    @Override
    protected int getContentHeight() {
        return this.getSize() * this.slotHeight;
    }


	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		int subspeciesId = this.entryList.get(index).subspeciesIndex;
		Subspecies subspecies = this.creature.getSubspecies(subspeciesId);
		String subspeciesName = "";
		if(subspecies.name != null) {
			subspeciesName = " " + subspecies.getTitle();
		}

		int variantId = this.entryList.get(index).variantIndex;
		Variant variant = subspecies.getVariant(variantId);
		String variantName = "Normal";
		if(variant != null) {
			variantName = variant.getTitle();
		}

		int nameY = boxTop + 6;
		this.parentGui.getFontRenderer().drawString(variantName + subspeciesName, this.left + 10, nameY, 0xFFFFFF);
	}

	/**
	 * List Entry
	 */
	public static class Entry {
		int subspeciesIndex;
		int variantIndex;

		public Entry(int subspeciesIndex, int variantIndex) {
			this.subspeciesIndex = subspeciesIndex;
			this.variantIndex = variantIndex;
		}
	}
}
