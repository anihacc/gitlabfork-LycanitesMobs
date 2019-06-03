package com.lycanitesmobs.core.gui.beastiary.list;

import com.lycanitesmobs.core.gui.beastiary.GuiBeastiary;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.client.renderer.Tessellator;

import java.util.*;

public class GuiGroupList extends GuiCreatureFilterList {
	private Map<Integer, ModInfo> groupList = new HashMap<>();

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiGroupList(GuiBeastiary parentGui, int width, int height, int top, int bottom, int x) {
		super(parentGui, width, height, top, bottom, x);
		this.refreshList();
	}


	@Override
	public void refreshList() {
		this.groupList.clear();

		int groupIndex = 0;
		List<ModInfo> groups = new ArrayList<>();
		groups.addAll(CreatureManager.getInstance().loadedGroups);
		groups.sort(Comparator.comparing(ModInfo::getTitle));
		for(ModInfo group : groups) {
			if(this.parentGui.playerExt.beastiary.getCreaturesDescovered(group) > 0) {
				this.groupList.put(groupIndex++, group);
			}
		}
	}


	@Override
	protected int getSize() {
		return this.groupList.size();
	}


	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.parentGui.playerExt.selectedGroup = this.groupList.get(index);
		super.elementClicked(index, doubleClick);
	}


	@Override
	protected boolean isSelected(int index) {
		return this.parentGui.playerExt.selectedGroup != null && this.parentGui.playerExt.selectedGroup.equals(this.groupList.get(index));
	}
	

	@Override
	protected void drawBackground() {}


    @Override
    protected int getContentHeight() {
        return this.getSize() * 24;
    }


	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		ModInfo group = this.groupList.get(index);
		if(group == null) {
			return;
		}
		this.parentGui.getFontRenderer().drawString(group.getTitle(), this.left + 2 , boxTop + 4, 0xFFFFFF, true);
	}


	@Override
	public boolean canListCreature(CreatureInfo creatureInfo, GuiCreatureList.Type listType) {
		if(this.parentGui.playerExt.selectedGroup == null || creatureInfo == null) {
			return false;
		}
		return creatureInfo.modInfo.equals(this.parentGui.playerExt.selectedGroup);
	}
}
