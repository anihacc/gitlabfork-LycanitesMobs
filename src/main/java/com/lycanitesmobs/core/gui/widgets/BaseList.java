package com.lycanitesmobs.core.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class BaseList<S> extends ExtendedList<BaseList.Entry> {
	public S screen;

	public BaseList(S screen, int width, int height, int top, int bottom, int left, int slotHeight) {
		super(Minecraft.getInstance(), width, height, top, bottom, 28);
		this.setLeftPos(left);
		this.screen = screen;
		this.createEntries();
	}

	public BaseList(S screen, int width, int height, int top, int bottom, int left) {
		this(screen, width, height, top, bottom, left, 28);
	}

	/**
	 * Creates all List Entries for this List Widget.
	 */
	public abstract void createEntries();


	// ==================================================
	//                     Entries
	// ==================================================
	@OnlyIn(Dist.CLIENT)
	public abstract static class Entry extends AbstractOptionList.Entry<BaseList.Entry> {
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
			return true; // TODO May have to check if mouse click is within bounds of entry.
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
			this.elementClicked();
			return true; // TODO May have to check if mouse click is within bounds of entry.
		}

		protected void elementClicked() {}

		protected boolean isSelected() {
			return false;
		}
	}


}
