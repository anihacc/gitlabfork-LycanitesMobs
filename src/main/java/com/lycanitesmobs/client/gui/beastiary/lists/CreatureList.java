package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.client.gui.widgets.BaseList;
import com.lycanitesmobs.client.gui.widgets.BaseListEntry;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.network.MessageSummonSetSelection;
import com.lycanitesmobs.core.pets.PetEntry;

import javax.annotation.Nullable;
import java.text.Collator;
import java.util.*;

public class CreatureList extends BaseList<BeastiaryScreen> {
	public enum Type {
		KNOWLEDGE((byte)0), SUMMONABLE((byte)1), PET((byte)2), MOUNT((byte)3), FAMILIAR((byte)4);
		public byte id;
		Type(byte i) { id = i; }
	}

	private Type listType;
	private CreatureFilterList filterList;
	private boolean releaseRefresh;

	/**
	 * Constructor
	 * @param listType The type of contents to show in this list.
	 * @param screen The Beastiary GUI using this list.
	 * @param filterList A creature filter list to restrict this list by, if null every creature is listed.
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public CreatureList(Type listType, BeastiaryScreen screen, CreatureFilterList filterList, int width, int height, int top, int bottom, int x) {
		super(screen, width, height, top, bottom, x, 24);
		this.listType = listType;
		this.filterList = filterList;
		if(this.filterList != null) {
			this.filterList.addFilteredList(this);
		}
		this.refreshList();
	}

	/**
	 * Reloads all items in this list.
	 */
	public void refreshList() {
		// Clear:
		this.replaceEntries(new ArrayList<>());
		int creatureIndex = 0;

		// Creature Knowledge List:
		if(this.listType == Type.KNOWLEDGE || this.listType == Type.SUMMONABLE) {
			List<String> creatures = new ArrayList<>(this.screen.playerExt.getBeastiary().creatureKnowledgeList.keySet());
			creatures.sort(Collator.getInstance(new Locale("US")));
			for(String creatureName : creatures) {
				CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(creatureName.toLowerCase());
				if(this.listType == Type.SUMMONABLE && !creatureInfo.isSummonable()) {
					continue;
				}
				if (creatureInfo != null && (this.filterList == null || this.filterList.canListCreature(creatureInfo, this.listType))) {
					this.addEntry(new Entry(this, creatureIndex++, creatureInfo));
				}
			}
			return;
		}

		// Pet List:
		if(this.listType == Type.PET || this.listType == Type.MOUNT || this.listType == Type.FAMILIAR) {
			String petType = "pet";
			if(this.listType == Type.MOUNT) {
				petType = "mount";
			}
			else if(this.listType == Type.FAMILIAR) {
				petType = "familiar";
			}
			List<PetEntry> creatures = new ArrayList<>();
			creatures.addAll(this.screen.playerExt.petManager.getEntryList(petType));
			creatures.sort(Comparator.comparing(PetEntry::getName));
			for(PetEntry petEntry : creatures) {
				CreatureInfo creatureInfo = petEntry.getCreatureInfo();
				if (creatureInfo != null && (this.filterList == null || this.filterList.canListCreature(creatureInfo, this.listType))) {
					this.addEntry(new Entry(this, creatureIndex++, petEntry));
				}
			}
		}
	}

	/**
	 * Changes the type of creatures that this list should display. Also refreshes this list.
	 * @param listType The new list type to use.
	 */
	public void changeType(Type listType) {
		this.listType = listType;
		this.refreshList();
	}

	@Override
	public void setSelected(@Nullable BaseListEntry entry) {
		super.setSelected(entry);
		if(!(entry instanceof Entry)) {
			return;
		}

		Entry creatureEntry = (Entry)entry;
		if(this.listType == Type.KNOWLEDGE) {
			this.screen.playerExt.selectedCreature = creatureEntry.creatureInfo;
			this.screen.playerExt.selectedSubspecies = 0;
		}
		else if(this.listType == Type.SUMMONABLE) {
			this.screen.playerExt.getSelectedSummonSet().setSummonType(creatureEntry.creatureInfo.getName());
			this.screen.playerExt.getSelectedSummonSet().setSubspecies(this.screen.playerExt.getSelectedSummonSet().getSubspecies());
			this.screen.playerExt.sendSummonSetToServer((byte)this.screen.playerExt.selectedSummonSet);
			MessageSummonSetSelection message = new MessageSummonSetSelection(this.screen.playerExt);
			LycanitesMobs.packetHandler.sendToServer(message);
		}
		else if(this.listType == Type.PET || this.listType == Type.MOUNT || this.listType == Type.FAMILIAR) {
			this.screen.playerExt.selectedPet = creatureEntry.petEntry;
		}
	}

	@Override
	protected boolean isSelectedItem(int index) {
		if(!(this.getEntry(index) instanceof Entry)) {
			return false;
		}

		Entry creatureEntry = (Entry)this.getEntry(index);
		if(this.listType == Type.KNOWLEDGE) {
			return this.screen.playerExt.selectedCreature != null && this.screen.playerExt.selectedCreature.equals(creatureEntry.creatureInfo);
		}
		else if(this.listType == Type.SUMMONABLE) {
			return this.screen.playerExt.getSelectedSummonSet().getCreatureInfo() != null && this.screen.playerExt.getSelectedSummonSet().getCreatureInfo().equals(creatureEntry.creatureInfo);
		}
		else if(this.listType == Type.PET || this.listType == Type.MOUNT || this.listType == Type.FAMILIAR) {
			return this.screen.playerExt.selectedPet != null && this.screen.playerExt.selectedPet.equals(creatureEntry.petEntry);
		}
		return false;
	}

	@Override
	protected void renderBackground() {
		// Automatically Refresh:
		if(this.listType == Type.PET || this.listType == Type.MOUNT ||this.listType == Type.FAMILIAR) {
			if(this.screen.playerExt.selectedPet != null && this.releaseRefresh != this.screen.playerExt.selectedPet.releaseEntity) {
				this.releaseRefresh = this.screen.playerExt.selectedPet.releaseEntity;
				this.refreshList();
			}
		}
	}

	/**
	 * List Entry
	 */
	public static class Entry extends BaseListEntry {
		private CreatureList parentList;
		public CreatureInfo creatureInfo;
		public PetEntry petEntry;

		public Entry(CreatureList parentList, int index, CreatureInfo creatureInfo) {
			this.parentList = parentList;
			this.index = index;
			this.creatureInfo = creatureInfo;
		}

		public Entry(CreatureList parentList, int index, PetEntry petEntry) {
			this.parentList = parentList;
			this.index = index;
			this.petEntry = petEntry;
		}

		@Override
		public void render(int index, int top, int left, int bottom, int right, int mouseX, int mouseY, boolean focus, float partialTicks) {
			// Knowledge Slot:
			if(this.parentList.listType == Type.KNOWLEDGE || this.parentList.listType == Type.SUMMONABLE) {
				if (this.creatureInfo == null) {
					return;
				}

				// Name:
				int nameY = top + 6;
				if (this.parentList.listType == Type.SUMMONABLE) {
					nameY = top + 2;
				}
				this.parentList.screen.getFontRenderer().drawString(this.creatureInfo.getTitle().getFormattedText(), left + 20, nameY, 0xFFFFFF);

				// Level:
				if (this.parentList.listType == Type.SUMMONABLE) {
					this.parentList.screen.drawLevel(this.creatureInfo, TextureManager.getTexture("GUIPetLevel"), left + 18, top + 10);
				}

				// Icon:
				if (this.creatureInfo.getIcon() != null) {
					this.parentList.screen.drawTexture(this.creatureInfo.getIcon(), left + 2, top + 2, 0, 1, 1, 16, 16);
				}
			}

			// Pet Slot:
			else {
				if (this.petEntry == null) {
					return;
				}

				// Name:
				int nameY = top + 6;
				if (this.parentList.listType == Type.PET || this.parentList.listType == Type.MOUNT) {
					nameY = top + 2;
				}
				this.parentList.screen.getFontRenderer().drawString(this.petEntry.getDisplayName().getFormattedText(), left + 20, nameY, 0xFFFFFF);

				// Level:
				if (this.parentList.listType == Type.PET || this.parentList.listType == Type.MOUNT) {
					this.parentList.screen.drawLevel(this.petEntry.getCreatureInfo(), TextureManager.getTexture("GUIPetLevel"), left + 18, top + 10);
				}

				// Icon:
				if (this.petEntry.getCreatureInfo().getIcon() != null) {
					this.parentList.screen.drawTexture(this.petEntry.getCreatureInfo().getIcon(), left + 2, top + 2, 0, 1, 1, 16, 16);
				}
			}
		}

		@Override
		protected void onClicked() {
			this.parentList.setSelected(this);
		}
	}
}