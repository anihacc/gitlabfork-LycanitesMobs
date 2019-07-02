package com.lycanitesmobs.core.gui.beastiary.lists;

import com.lycanitesmobs.core.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.core.gui.widgets.BaseList;
import com.lycanitesmobs.core.gui.widgets.BaseListEntry;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.Subspecies;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class SubspeciesList extends BaseList<BeastiaryScreen> {
	private CreatureInfo creature;
	private boolean summoning;

	/**
	 * Constructor
	 * @param screen The Beastiary GUI using this list.
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public SubspeciesList(BeastiaryScreen screen, boolean summoning, int width, int height, int top, int bottom, int x) {
		super(screen, width, height, top, bottom, x, 24);
		this.summoning = summoning;
		this.refreshList();
	}

	/**
	 * Reloads all items in this list.
	 */
	public void refreshList() {
		this.replaceEntries(new ArrayList<>());

		if(!this.summoning) {
			this.creature = this.screen.playerExt.selectedCreature;
		}
		else {
			this.creature = this.screen.playerExt.getSelectedSummonSet().getCreatureInfo();
		}
		if(this.creature == null) {
			return;
		}

		int index = 1;
		this.addEntry(new Entry(this, index++, 0));
		for(int subspeciesIndex : this.creature.subspecies.keySet()) {
			if(!this.screen.playerExt.getBeastiary().hasKnowledgeRank(this.creature.getName(), 2)) {
				continue;
			}
			Subspecies subspecies = this.creature.subspecies.get(subspeciesIndex);
			if (subspecies != null && "rare".equals(subspecies.rarity)) {
				continue;
			}
			this.addEntry(new Entry(this, index++, subspeciesIndex));
		}
	}

	@Override
	public void setSelected(@Nullable BaseListEntry entry) {
		super.setSelected(entry);
		if(!this.summoning && entry instanceof Entry) {
			this.screen.playerExt.selectedSubspecies = ((Entry)entry).subspeciesIndex;
		}
		else {
			this.screen.playerExt.getSelectedSummonSet().setSubspecies(this.getSelectedIndex());
			this.screen.playerExt.sendSummonSetToServer((byte)this.screen.playerExt.selectedSummonSet);
		}
	}

	@Override
	protected boolean isSelectedItem(int index) {
		if(!(this.getEntry(index) instanceof Entry))
			return false;
		if(!this.summoning) {
			return this.screen.playerExt.selectedSubspecies == ((Entry)this.getEntry(index)).subspeciesIndex;
		}
		else {
			return this.screen.playerExt.getSelectedSummonSet().getSubspecies() == index;
		}
	}

	@Override
	protected void renderBackground() {
		if(!this.summoning) {
			if(this.creature != this.screen.playerExt.selectedCreature) {
				this.refreshList();
			}
		}
		else {
			if(this.creature != this.screen.playerExt.getSelectedSummonSet().getCreatureInfo()) {
				this.refreshList();
			}
		}

	}

	/**
	 * List Entry
	 */
	public static class Entry extends BaseListEntry {
		private SubspeciesList parentList;
		public int subspeciesIndex;

		public Entry(SubspeciesList parentList, int index, int subspeciesIndex) {
			this.parentList = parentList;
			this.index = index;
			this.subspeciesIndex = subspeciesIndex;
		}

		@Override
		public void render(int index, int top, int left, int bottom, int right, int mouseX, int mouseY, boolean focus, float partialTicks) {
			Subspecies subspecies = this.parentList.creature.getSubspecies(this.subspeciesIndex);

			// Name:
			int nameY = top + 6;
			if(subspecies == null) {
				this.parentList.screen.getFontRenderer().drawString("Normal", left + 20, nameY, 0xFFFFFF);
				return;
			}
			this.parentList.screen.getFontRenderer().drawString(subspecies.getTitle().getFormattedText(), left + 20, nameY, 0xFFFFFF);
		}

		@Override
		protected void onClicked() {
			this.parentList.setSelected(this);
		}
	}
}
