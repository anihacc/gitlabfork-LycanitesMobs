package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;

public class GuiSummoningPedestalList extends GuiListBase<GuiSummoningPedestal> {
	GuiSummoningPedestal parentGUI;
	Map<Integer, String> minionList;

	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GuiSummoningPedestalList(GuiSummoningPedestal parentGUI, ExtendedPlayer playerExt, int width, int height, int top, int bottom, int left) {
		super(parentGUI, width, height, top, bottom, 28);
		this.setLeftPos(left);
		this.parentGUI = parentGUI;
		this.minionList = playerExt.getBeastiary().getSummonableList();
	}

	@Override
	public void createEntries() {
		for(String minionName : this.minionList.values()) {
			this.addEntry(new SummoningPedestalEntry(this, minionName));
		}
	}
	
	
	// ==================================================
  	//                    List Info
  	// ==================================================
	@Override
	protected int getItemCount() {
		return minionList.size();
	}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	@Override
	protected void renderBackground() {
		super.renderBackground();
	}


	// ==================================================
	//                     Entries
	// ==================================================
	@OnlyIn(Dist.CLIENT)
	public class SummoningPedestalEntry extends GuiListBase.Entry {
		GuiSummoningPedestalList parentGUI;
		String minionName;

		public SummoningPedestalEntry(GuiSummoningPedestalList list, String minionName) {
			this.parentGUI = list;
			this.minionName = minionName;
		}

		@Override
		public void render(int index, int p_render_2_, int boxRight, int p_render_4_, int boxTop, int boxBottom, int p_render_7_, boolean p_render_8_, float p_render_9_) {
			CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(this.minionName);

			int boxLeft = this.parentGUI.getLeft();
			int levelBarWidth = 9;
			int levelBarHeight = 9;
			int levelBarX = boxLeft + 20;
			int levelBarY = boxTop + boxBottom - levelBarHeight - 4;
			int levelBarU = 256 - (levelBarWidth * 2);
			int levelBarV = 256 - levelBarHeight;
			int level = creatureInfo.summonCost;

			// Summon Level:
			Minecraft.getInstance().getTextureManager().bindTexture(AssetManager.getTexture("GUIBeastiary"));
			for(int currentLevel = 0; currentLevel < level; currentLevel++) {
				this.parentGUI.screen.drawTexturedModalRect(levelBarX + (levelBarWidth * currentLevel), levelBarY, levelBarU, levelBarV, levelBarWidth, levelBarHeight);
			}

			this.parentGUI.screen.getFontRenderer().drawString(creatureInfo.getTitle(), this.parentGUI.getLeft() + 20 , boxTop + 4, 0xFFFFFF);
			Minecraft.getInstance().getTextureManager().bindTexture(creatureInfo.getIcon());
			this.parentGUI.screen.drawTexturedModalRect(this.parentGUI.getLeft() + 2, boxTop + 4, 0, 0, 16, 16, 16);
		}

		@Override
		public List<? extends IGuiEventListener> children() {
			return null;
		}

		@Override
		protected void elementClicked() {
			this.parentGUI.screen.selectMinion(this.minionName);
		}

		@Override
		protected boolean isSelected() {
			return false;
			//return this.parentGUI.getSelectedMinion() != null && this.parentGUI.getSelectedMinion().equals(this.minionList.get(index));
		}
	}
}
