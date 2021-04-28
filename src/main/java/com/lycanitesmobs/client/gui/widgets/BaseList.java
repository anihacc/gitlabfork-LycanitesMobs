package com.lycanitesmobs.client.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public abstract class BaseList<S> extends ExtendedList<BaseListEntry> {
	public S screen;

	public BaseList(S screen, int width, int height, int top, int bottom, int left, int slotHeight) {
		super(Minecraft.getInstance(), width, height, top, bottom, slotHeight);
		this.setLeftPos(left);
		this.screen = screen;
		this.createEntries();
	}

	public BaseList(S screen, int width, int height, int top, int bottom, int left) {
		this(screen, width, height, top, bottom, left, 28);
	}

	@Override
	public int getRowWidth() {
		return this.getWidth();
	}

	protected int getScrollbarWidth() {
		return 6;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.getRight() - this.getScrollbarWidth();
	}

	/**
	 * Creates all List Entries for this List Widget.
	 */
	public void createEntries() {}

	/**
	 * Returns the index of the selected entry.
	 * @return The selected entry index, defaults to 0 if none are selected.
	 */
	public int getSelectedIndex() {
		if(this.getSelected() != null)
			return this.getSelected().index;
		return 0;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);

		// Scissor Start:
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		double scaleFactor = Minecraft.getInstance().getMainWindow().getGuiScaleFactor();
		int scissorX = (int)((double)this.getLeft() * scaleFactor);
		int scissorTop = Minecraft.getInstance().getMainWindow().getHeight() - (int)((double)this.getTop() * scaleFactor);
		int scissorBottom = Minecraft.getInstance().getMainWindow().getHeight() - (int)((double)this.getBottom() * scaleFactor);
		int scissorWidth = (int)((double)this.getWidth() * scaleFactor);
		int scissorHeight = scissorTop - scissorBottom;
		GL11.glScissor(scissorX, scissorBottom, scissorWidth, scissorHeight); // Scissor starts at bottom right.

		RenderSystem.disableLighting();
		RenderSystem.disableFog();
		RenderSystem.disableDepthTest();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
		RenderSystem.shadeModel(7425);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos((double)this.x0, (double)this.y1, 0.0D).color(0, 0, 0, 64).endVertex();
		bufferbuilder.pos((double)this.x1, (double)this.y1, 0.0D).color(0, 0, 0, 64).endVertex();
		bufferbuilder.pos((double)this.x1, (double)this.y0, 0.0D).color(0, 0, 0, 64).endVertex();
		bufferbuilder.pos((double)this.x0, (double)this.y0, 0.0D).color(0, 0, 0, 64).endVertex();
		tessellator.draw();

		// Test Box:
		/*bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(0, 10000, 0.0D).color(32, 255, 32, 128).endVertex();
		bufferbuilder.pos(10000, 10000, 0.0D).color(32, 255, 32, 128).endVertex();
		bufferbuilder.pos(10000, 0, 0.0D).color(32, 255, 32, 128).endVertex();
		bufferbuilder.pos(0, 0, 0.0D).color(32, 255, 32, 128).endVertex();
		tessellator.draw();*/

		// Render Entries:
		RenderSystem.enableTexture();
		int listLeft = this.getRowLeft();
		int listTop = this.y0 + 4 - (int)this.getScrollAmount();
		if (this.renderHeader) {
			this.renderHeader(matrixStack, listLeft, listTop, tessellator);
		}
		this.renderList(matrixStack, listLeft, listTop, mouseX, mouseY, partialTicks);

		// Draw Gradients:
		RenderSystem.disableTexture();

		/*if(this.getScrollAmount() > 0) {
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos((double) this.x0, (double) (this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
			bufferbuilder.pos((double) this.x1, (double) (this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
			bufferbuilder.pos((double) this.x1, (double) this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos((double) this.x0, (double) this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
			tessellator.draw();
		}

		if(this.getScrollAmount() < this.getMaxScroll()) {
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos((double) this.x0, (double) this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos((double) this.x1, (double) this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos((double) this.x1, (double) (this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
			bufferbuilder.pos((double) this.x0, (double) (this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
			tessellator.draw();
		}*/

		// Draw Scrollbar:
		int maxScroll = this.getMaxScroll();
		if (maxScroll > 0) {
			int contentMax = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
			contentMax = MathHelper.clamp(contentMax, 32, this.y1 - this.y0 - 8);
			int scrollY = (int)this.getScrollAmount() * (this.y1 - this.y0 - contentMax) / maxScroll + this.y0;
			if (scrollY < this.y0) {
				scrollY = this.y0;
			}
			int scrollbarLeft = this.getScrollbarPosition();
			int scrollbarRight = scrollbarLeft + this.getScrollbarWidth();

			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferbuilder.pos((double)scrollbarLeft, (double)this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos((double)scrollbarRight, (double)this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos((double)scrollbarRight, (double)this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos((double)scrollbarLeft, (double)this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
			tessellator.draw();

			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferbuilder.pos((double)scrollbarLeft, (double)(scrollY + contentMax), 0.0D).tex(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
			bufferbuilder.pos((double)scrollbarRight, (double)(scrollY + contentMax), 0.0D).tex(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
			bufferbuilder.pos((double)scrollbarRight, (double)scrollY, 0.0D).tex(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
			bufferbuilder.pos((double)scrollbarLeft, (double)scrollY, 0.0D).tex(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
			tessellator.draw();

			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferbuilder.pos((double)scrollbarLeft, (double)(scrollY + contentMax - 1), 0.0D).tex(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
			bufferbuilder.pos((double)(scrollbarRight - 1), (double)(scrollY + contentMax - 1), 0.0D).tex(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
			bufferbuilder.pos((double)(scrollbarRight - 1), (double)scrollY, 0.0D).tex(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
			bufferbuilder.pos((double)scrollbarLeft, (double)scrollY, 0.0D).tex(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
			tessellator.draw();
		}

		RenderSystem.enableTexture();
		RenderSystem.shadeModel(7424);
		RenderSystem.enableAlphaTest();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();

		// Scissor Stop:
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		this.renderDecorations(matrixStack, mouseX, mouseY);
	}

	public int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
	}
}
