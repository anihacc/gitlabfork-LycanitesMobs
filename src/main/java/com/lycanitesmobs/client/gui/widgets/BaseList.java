package com.lycanitesmobs.client.gui.widgets;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.DrawHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.util.Mth;
import org.lwjgl.opengl.GL11;

public abstract class BaseList<S> extends ContainerObjectSelectionList<BaseListEntry> {
	public DrawHelper drawHelper;
	public S screen;

	public BaseList(S screen, int width, int height, int top, int bottom, int left, int slotHeight) {
		super(Minecraft.getInstance(), width, height, top, bottom, slotHeight);
		Minecraft minecraft = Minecraft.getInstance();
		this.drawHelper = new DrawHelper(minecraft, minecraft.font);
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
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);

		// Scissor Start:
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		double scaleFactor = Minecraft.getInstance().getWindow().getGuiScale();
		int scissorX = (int)((float)this.getLeft() * scaleFactor);
		int scissorTop = Minecraft.getInstance().getWindow().getScreenHeight() - (int)((float)this.getTop() * scaleFactor);
		int scissorBottom = Minecraft.getInstance().getWindow().getScreenHeight() - (int)((float)this.getBottom() * scaleFactor);
		int scissorWidth = (int)((float)this.getWidth() * scaleFactor);
		int scissorHeight = scissorTop - scissorBottom;
		GL11.glScissor(scissorX, scissorBottom, scissorWidth, scissorHeight); // Scissor starts at bottom right.

		RenderSystem.disableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		Matrix4f matrix = matrixStack.last().pose();

		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferbuilder.vertex(matrix, (float)this.x0, (float)this.y1, 0.0F).color(0, 0, 0, 64).endVertex();
		bufferbuilder.vertex(matrix, (float)this.x1, (float)this.y1, 0.0F).color(0, 0, 0, 64).endVertex();
		bufferbuilder.vertex(matrix, (float)this.x1, (float)this.y0, 0.0F).color(0, 0, 0, 64).endVertex();
		bufferbuilder.vertex(matrix, (float)this.x0, (float)this.y0, 0.0F).color(0, 0, 0, 64).endVertex();
		tessellator.end();

		// Render Entries:
		RenderSystem.enableTexture();
		int listLeft = this.getRowLeft();
		int listTop = this.y0 + 4 - (int)this.getScrollAmount();
		if (this.isFocused()) { // was: this.renderHeader; may cause problems in the future
			this.renderHeader(matrixStack, listLeft, listTop, tessellator);
		}

		try {
			this.renderList(matrixStack, listLeft, listTop, mouseX, mouseY, partialTicks);
		}
		catch (Exception e) {
			LycanitesMobs.logError("MStack: " + matrixStack);
		}

		// Draw Gradients:
		RenderSystem.disableTexture();

		// Draw Scrollbar:
		int maxScroll = this.getMaxScroll();
		if (maxScroll > 0) {
			int contentMax = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
			contentMax = Mth.clamp(contentMax, 32, this.y1 - this.y0 - 8);
			int scrollY = (int)this.getScrollAmount() * (this.y1 - this.y0 - contentMax) / maxScroll + this.y0;
			if (scrollY < this.y0) {
				scrollY = this.y0;
			}
			int scrollbarLeft = this.getScrollbarPosition();
			int scrollbarRight = scrollbarLeft + this.getScrollbarWidth();

			bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			bufferbuilder.vertex(matrix, (float)scrollbarLeft, (float)this.y1, 0.0F).uv(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(matrix, (float)scrollbarRight, (float)this.y1, 0.0F).uv(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(matrix, (float)scrollbarRight, (float)this.y0, 0.0F).uv(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(matrix, (float)scrollbarLeft, (float)this.y0, 0.0F).uv(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
			tessellator.end();

			bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			bufferbuilder.vertex(matrix, (float)scrollbarLeft, (float)(scrollY + contentMax), 0.0F).uv(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
			bufferbuilder.vertex(matrix, (float)scrollbarRight, (float)(scrollY + contentMax), 0.0F).uv(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
			bufferbuilder.vertex(matrix, (float)scrollbarRight, (float)scrollY, 0.0F).uv(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
			bufferbuilder.vertex(matrix, (float)scrollbarLeft, (float)scrollY, 0.0F).uv(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
			tessellator.end();

			bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			bufferbuilder.vertex(matrix, (float)scrollbarLeft, (float)(scrollY + contentMax - 1), 0.0F).uv(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
			bufferbuilder.vertex(matrix, (float)(scrollbarRight - 1), (float)(scrollY + contentMax - 1), 0.0F).uv(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
			bufferbuilder.vertex(matrix, (float)(scrollbarRight - 1), (float)scrollY, 0.0F).uv(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
			bufferbuilder.vertex(matrix, (float)scrollbarLeft, (float)scrollY, 0.0F).uv(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
			tessellator.end();
		}

		RenderSystem.enableTexture();
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
