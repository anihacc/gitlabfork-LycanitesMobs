package com.lycanitesmobs.client.gui.widgets;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.gui.SummoningPedestalScreen;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.Map;

public class SummoningPedestalList extends GuiScrollingList {
	SummoningPedestalScreen parentGUI;
	Map<Integer, String> minionList;

	// ==================================================
  	//                    Constructor
  	// ==================================================
	public SummoningPedestalList(SummoningPedestalScreen parentGUI, ExtendedPlayer playerExt, int width, int height, int top, int bottom, int left) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, left, 28, width, height);
		this.parentGUI = parentGUI;
		this.minionList = playerExt.getBeastiary().getSummonableList();
	}
	
	
	// ==================================================
  	//                    List Info
  	// ==================================================
	@Override
	protected int getSize() {
		return minionList.size();
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.parentGUI.selectMinion(this.minionList.get(index));
	}

	@Override
	protected boolean isSelected(int index) {
		return this.parentGUI.getSelectedMinion() != null && this.parentGUI.getSelectedMinion().equals(this.minionList.get(index));
	}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	@Override
	protected void drawBackground() {}

	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		String mobName = this.minionList.get(index);
		CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(mobName);

		// Summon Level:
		int levelBarWidth = 9;
		int levelBarHeight = 9;
		int levelBarX = this.left + 20;
		int levelBarY = boxTop + boxBottom - levelBarHeight - 4;
		int level = creatureInfo.summonCost;
		if(level <= 10) {
			this.parentGUI.drawBar(AssetManager.getTexture("GUIPetLevel"), levelBarX, levelBarY, 0, levelBarWidth, levelBarHeight, level, 10);
		}

		this.parentGUI.getFontRenderer().drawString(creatureInfo.getTitle(), this.left + 20 , boxTop + 4, 0xFFFFFF);
		Minecraft.getMinecraft().getTextureManager().bindTexture(creatureInfo.getIcon());
		this.parentGUI.drawTexturedModalRect(this.left + 2, boxTop + 4, 0, 0, 16, 16, 16);
	}
}
