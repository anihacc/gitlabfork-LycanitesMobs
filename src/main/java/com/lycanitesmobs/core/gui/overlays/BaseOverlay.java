package com.lycanitesmobs.core.gui.overlays;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.gui.BaseGui;
import com.lycanitesmobs.core.item.summoningstaff.ItemStaffSummoning;
import com.lycanitesmobs.core.mobevent.MobEventPlayerClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class BaseOverlay extends BaseGui {
	public Minecraft mc;
	
	private int mountMessageTimeMax = 10 * 20;
	private int mountMessageTime = 0;
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public BaseOverlay(Minecraft minecraft) {
		super(new TranslationTextComponent("gui.overlay"));
		this.mc = Minecraft.getInstance();
	}
	
	
    // ==================================================
    //                  Draw Game Overlay
    // ==================================================
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
        if(ClientManager.getInstance().getClientPlayer() == null)
            return;
        PlayerEntity player = ClientManager.getInstance().getClientPlayer();

		if(event.isCancelable() || event.getType() != ElementType.EXPERIENCE)
	      return;

		GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		int sWidth = Minecraft.getInstance().mainWindow.getScaledWidth();
        int sHeight = Minecraft.getInstance().mainWindow.getScaledHeight();

        // ========== Mob/World Events Title ==========
        ExtendedWorld worldExt = ExtendedWorld.getForWorld(player.getEntityWorld());
        if(worldExt != null) {
            for(MobEventPlayerClient mobEventPlayerClient : worldExt.clientMobEventPlayers.values()) {
				GlStateManager.pushMatrix();
				mobEventPlayerClient.onGUIUpdate(this, sWidth, sHeight);
				GlStateManager.popMatrix();
			}
            if(worldExt.clientWorldEventPlayer != null) {
				GlStateManager.pushMatrix();
				worldExt.clientWorldEventPlayer.onGUIUpdate(this, sWidth, sHeight);
				GL11.glPopMatrix();
			}
        }
		
		// ========== Summoning Focus Bar ==========
        ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt != null && !this.mc.player.abilities.isCreativeMode && (
                (this.mc.player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof ItemStaffSummoning)
                || (this.mc.player.getHeldItem(Hand.OFF_HAND).getItem() instanceof ItemStaffSummoning)
                )) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
			
			int barYSpace = 10;
			int barXSpace = -1;
			
            int summonBarWidth = 9;
            int summonBarHeight = 9;
            int summonBarX = (sWidth / 2) + 10;
            int summonBarY = sHeight - 30 - summonBarHeight;
            int summonBarU = 256 - summonBarWidth;
            int summonBarV = 256 - summonBarHeight;
            
            summonBarY -= barYSpace;
            if(this.mc.player.areEyesInFluid(FluidTags.WATER))
            	summonBarY -= barYSpace;
            
            for(int summonBarEnergyN = 0; summonBarEnergyN < 10; summonBarEnergyN++) {
            	this.drawTexturedModalRect(summonBarX + ((summonBarWidth + barXSpace) * summonBarEnergyN), summonBarY, summonBarU, summonBarV, summonBarWidth, summonBarHeight);
            	if(playerExt.summonFocus >= playerExt.summonFocusMax - (summonBarEnergyN * playerExt.summonFocusCharge)) {
                	this.drawTexturedModalRect(summonBarX + ((summonBarWidth + barXSpace) * summonBarEnergyN), summonBarY, summonBarU - summonBarWidth, summonBarV, summonBarWidth, summonBarHeight);
            	}
                else if(playerExt.summonFocus + playerExt.summonFocusCharge > playerExt.summonFocusMax - (summonBarEnergyN * playerExt.summonFocusCharge)) {
            		float summonChargeScale = (float)(playerExt.summonFocus % playerExt.summonFocusCharge) / (float)playerExt.summonFocusCharge;
            		this.drawTexturedModalRect((summonBarX + ((summonBarWidth + barXSpace) * summonBarEnergyN)) + (summonBarWidth - Math.round((float)summonBarWidth * summonChargeScale)), summonBarY, summonBarU - Math.round((float)summonBarWidth * summonChargeScale), summonBarV, Math.round((float)summonBarWidth * summonChargeScale), summonBarHeight);
            	}
            }
		}
		
		// ========== Mount Stamina Bar ==========
		if(this.mc.player.getRidingEntity() != null && this.mc.player.getRidingEntity() instanceof RideableCreatureEntity) {
			RideableCreatureEntity mount = (RideableCreatureEntity)this.mc.player.getRidingEntity();
            float mountStamina = mount.getStaminaPercent();
            
            // Mount Controls Message:
            if(this.mountMessageTime > 0) {
            	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            	if(this.mountMessageTime < 60)
            		GL11.glColor4f(1.0F, 1.0F, 1.0F, (float)this.mountMessageTime / (float)60);
            	String mountMessage = new TranslationTextComponent("gui.mount.controls").getFormattedText();
            	//mountMessage = mountMessage.replace("%control%", this.mc.gameSettings.(KeyHandler.instance.mountAbility.getKey().getKeyCode()));
            	int stringWidth = this.mc.fontRenderer.getStringWidth(mountMessage);
            	this.mc.fontRenderer.drawString(mountMessage, (sWidth / 2) - (stringWidth / 2), sHeight - 64, 0xFFFFFF);
            }
            
            // Mount Ability Stamina Bar:
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
            int staminaBarWidth = 182;
            int staminaBarHeight = 5;
            int staminaEnergyWidth = (int)((float)(staminaBarWidth + 1) * mountStamina);
            int staminaBarX = (sWidth / 2) - (staminaBarWidth / 2);
            int staminaBarY = sHeight - 32 + 3;
            int staminaTextureY = 84;
            if("toggle".equals(mount.getStaminaType()))
            	staminaTextureY -= staminaBarHeight * 2;
            int staminaEnergyY = staminaTextureY + staminaBarHeight;
            
            this.drawTexturedModalRect(staminaBarX, staminaBarY, 0, staminaTextureY, staminaBarWidth, staminaBarHeight);
            if(staminaEnergyWidth > 0)
                this.drawTexturedModalRect(staminaBarX, staminaBarY, 0, staminaEnergyY, staminaEnergyWidth, staminaBarHeight);
            
            if(this.mountMessageTime > 0)
            	this.mountMessageTime--;
		}
		else
			this.mountMessageTime = this.mountMessageTimeMax;

		GlStateManager.popMatrix();
		this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
	}
}
