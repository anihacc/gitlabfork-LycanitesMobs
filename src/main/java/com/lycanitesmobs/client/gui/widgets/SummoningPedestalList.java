package com.lycanitesmobs.client.gui.widgets;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.client.gui.SummoningPedestalScreen;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class SummoningPedestalList extends BaseList<SummoningPedestalScreen> {
	public ExtendedPlayer playerExt;

	public SummoningPedestalList(SummoningPedestalScreen parentGui, ExtendedPlayer playerExt, int width, int height, int top, int bottom, int left) {
		super(parentGui, width, height, top, bottom, left, 28);
		this.playerExt = playerExt;
		this.createEntries(); // Called again here for playerExt.
	}

	@Override
	public void createEntries() {
		if(this.playerExt == null)
			return;
		for(String minionName : this.playerExt.getBeastiary().getSummonableList().values()) {
			this.addEntry(new SummoningPedestalEntry(this, minionName));
		}
	}

	@Override
	protected boolean isSelectedItem(int index) {
		if(!(this.getEntry(index) instanceof SummoningPedestalEntry))
			return false;
		return this.screen.getSelectedMinionName() != null && this.screen.getSelectedMinionName().equals(((SummoningPedestalEntry)this.getEntry(index)).minionName);
	}

	@OnlyIn(Dist.CLIENT)
	public class SummoningPedestalEntry extends BaseListEntry {
		SummoningPedestalList parentGUI;
		String minionName;

		public SummoningPedestalEntry(SummoningPedestalList list, String minionName) {
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
			Minecraft.getInstance().getTextureManager().bindTexture(TextureManager.getTexture("GUIBeastiary"));
			for(int currentLevel = 0; currentLevel < level; currentLevel++) {
				this.parentGUI.screen.drawTexturedModalRect(levelBarX + (levelBarWidth * currentLevel), levelBarY, levelBarU, levelBarV, levelBarWidth, levelBarHeight);
			}

			this.parentGUI.screen.getFontRenderer().drawString(creatureInfo.getTitle().getFormattedText(), this.parentGUI.getLeft() + 20 , boxTop + 4, 0xFFFFFF);
			Minecraft.getInstance().getTextureManager().bindTexture(creatureInfo.getIcon());
			this.parentGUI.screen.drawTexturedModalRect(this.parentGUI.getLeft() + 2, boxTop + 4, 0, 0, 16, 16, 16);
		}

		@Override
		public List<? extends IGuiEventListener> children() {
			return null;
		}

		@Override
		protected void onClicked() {
			this.parentGUI.screen.selectMinion(this.minionName);
		}
	}
}
