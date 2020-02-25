package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.gui.beastiary.ElementsBeastiaryScreen;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.ElementManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.*;

public class ElementList extends GuiScrollingList {
	private ElementsBeastiaryScreen parentGui;
	private Map<Integer, String> elementNames = new HashMap<>();

	/**
	 * Constructor
	 * @param parentGui The Beastiary GUI using this list.
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public ElementList(ElementsBeastiaryScreen parentGui, int width, int height, int top, int bottom, int x) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, x, 24, width, height);
		this.parentGui = parentGui;

		int i = 0;
		List<ElementInfo> elements = new ArrayList<>();
		elements.addAll(ElementManager.getInstance().elements.values());
		elements.sort(Comparator.comparing(ElementInfo::getTitle));
		for(ElementInfo elementInfo : elements) {
			this.elementNames.put(i++, elementInfo.name);
		}
	}


	@Override
	protected int getSize() {
		return this.elementNames.size();
	}


	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.selectedIndex = index;
		this.parentGui.elementInfo = ElementManager.getInstance().getElement(this.elementNames.get(index));
	}


	@Override
	protected boolean isSelected(int index) {
		return this.selectedIndex == index;
	}
	

	@Override
	protected void drawBackground() {

	}


    @Override
    protected int getContentHeight() {
        return this.getSize() * this.slotHeight;
    }


	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		ElementInfo elementInfo = ElementManager.getInstance().getElement(this.elementNames.get(index));
		if(elementInfo == null) {
			return;
		}
		this.parentGui.getFontRenderer().drawString(elementInfo.getTitle(), this.left + 4, boxTop + 4, 0xFFFFFF);

		/*/ Icon:
		if (elementInfo.getIcon() != null) {
			this.parentGui.drawTexture(elementInfo.getIcon(), this.left + 2, boxTop + 2, 0, 1, 1, 16, 16);
		}*/
	}
}
