package com.lycanitesmobs.client.gui.overlays;

import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.client.KeyHandler;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.client.gui.BaseGui;
import com.lycanitesmobs.client.gui.DrawHelper;
import com.lycanitesmobs.core.config.ConfigDebug;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.item.summoningstaff.ItemStaffSummoning;
import com.lycanitesmobs.client.mobevent.MobEventPlayerClient;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class BaseOverlay extends BaseGui {
	public DrawHelper drawHelper;
	public Minecraft minecraft;
	
	private int mountMessageTimeMax = 10 * 20;
	private int mountMessageTime = 0;
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public BaseOverlay(Minecraft minecraft) {
		super(new TranslationTextComponent("gui.overlay"));
		this.minecraft = Minecraft.getInstance();
		this.drawHelper = new DrawHelper(minecraft, minecraft.fontRenderer);
	}
	
	
    // ==================================================
    //                  Draw Game Overlay
    // ==================================================
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
        if(ClientManager.getInstance().getClientPlayer() == null)
            return;
        PlayerEntity player = ClientManager.getInstance().getClientPlayer();

		if(event.isCancelable() || event.getType() != ElementType.EXPERIENCE) {
			return;
		}
		MatrixStack matrixStack = event.getMatrixStack();

		matrixStack.push();
		RenderSystem.disableLighting();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		int sWidth = Minecraft.getInstance().getMainWindow().getScaledWidth(); // getMainWindow()
        int sHeight = Minecraft.getInstance().getMainWindow().getScaledHeight();

        // ========== Mob/World Events Title ==========
        ExtendedWorld worldExt = ExtendedWorld.getForWorld(player.getEntityWorld());
        if(worldExt != null) {
            for(MobEventPlayerClient mobEventPlayerClient : worldExt.clientMobEventPlayers.values()) {
				matrixStack.push();
				mobEventPlayerClient.onGUIUpdate(matrixStack, this, sWidth, sHeight);
				matrixStack.pop();
			}
            if(worldExt.clientWorldEventPlayer != null) {
				matrixStack.push();
				worldExt.clientWorldEventPlayer.onGUIUpdate(matrixStack, this, sWidth, sHeight);
				matrixStack.pop();
			}
        }
		
		// ========== Summoning Focus Bar ==========
        ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt != null && !this.minecraft.player.abilities.isCreativeMode && (
                (this.minecraft.player.getHeldItem(Hand.MAIN_HAND).getItem() instanceof ItemStaffSummoning)
                || (this.minecraft.player.getHeldItem(Hand.OFF_HAND).getItem() instanceof ItemStaffSummoning)
                )) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(TextureManager.getTexture("GUIInventoryCreature"));
			
			int barYSpace = 10;
			int barXSpace = -1;
			
            int summonBarWidth = 9;
            int summonBarHeight = 9;
            int summonBarX = (sWidth / 2) + 10;
            int summonBarY = sHeight - 30 - summonBarHeight;
            int summonBarU = 256 - summonBarWidth;
            int summonBarV = 256 - summonBarHeight;
            
            summonBarY -= barYSpace;
            if(this.minecraft.player.areEyesInFluid(FluidTags.WATER))
            	summonBarY -= barYSpace;
            
            for(int summonBarEnergyN = 0; summonBarEnergyN < 10; summonBarEnergyN++) {
            	this.drawHelper.drawTexturedModalRect(matrixStack, summonBarX + ((summonBarWidth + barXSpace) * summonBarEnergyN), summonBarY, summonBarU, summonBarV, summonBarWidth, summonBarHeight);
            	if(playerExt.summonFocus >= playerExt.summonFocusMax - (summonBarEnergyN * playerExt.summonFocusCharge)) {
                	this.drawHelper.drawTexturedModalRect(matrixStack, summonBarX + ((summonBarWidth + barXSpace) * summonBarEnergyN), summonBarY, summonBarU - summonBarWidth, summonBarV, summonBarWidth, summonBarHeight);
            	}
                else if(playerExt.summonFocus + playerExt.summonFocusCharge > playerExt.summonFocusMax - (summonBarEnergyN * playerExt.summonFocusCharge)) {
            		float summonChargeScale = (float)(playerExt.summonFocus % playerExt.summonFocusCharge) / (float)playerExt.summonFocusCharge;
            		this.drawHelper.drawTexturedModalRect(matrixStack, (summonBarX + ((summonBarWidth + barXSpace) * summonBarEnergyN)) + (summonBarWidth - Math.round((float)summonBarWidth * summonChargeScale)), summonBarY, summonBarU - Math.round((float)summonBarWidth * summonChargeScale), summonBarV, Math.round((float)summonBarWidth * summonChargeScale), summonBarHeight);
            	}
            }
		}
		
		// ========== Mount Stamina Bar ==========
		if(this.minecraft.player.getRidingEntity() != null && this.minecraft.player.getRidingEntity() instanceof RideableCreatureEntity) {
			RideableCreatureEntity mount = (RideableCreatureEntity)this.minecraft.player.getRidingEntity();
            float mountStamina = mount.getStaminaPercent();
            
            // Mount Controls Message:
            if(this.mountMessageTime > 0) {
            	ITextComponent mountMessage = new TranslationTextComponent("gui.mount.controls.prefix");
				mountMessage = mountMessage.copyRaw().appendString(" ").copyRaw().appendString(KeyHandler.instance.mountAbility.getKeyDescription());
				mountMessage = mountMessage.copyRaw().append(new TranslationTextComponent("gui.mount.controls.ability"));
				mountMessage = mountMessage.copyRaw().appendString(" ").copyRaw().appendString(KeyHandler.instance.dismount.getKeyDescription());
				mountMessage = mountMessage.copyRaw().append(new TranslationTextComponent("gui.mount.controls.dismount"));
				this.minecraft.ingameGUI.setOverlayMessage(mountMessage, false);
            }
            
            // Mount Ability Stamina Bar:
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
            int staminaBarWidth = 182;
            int staminaBarHeight = 5;
            int staminaEnergyWidth = (int)((float)(staminaBarWidth + 1) * mountStamina);
            int staminaBarX = (sWidth / 2) - (staminaBarWidth / 2);
            int staminaBarY = sHeight - 32 + 3;
            int staminaTextureY = 84;
            if("toggle".equals(mount.getStaminaType()))
            	staminaTextureY -= staminaBarHeight * 2;
            int staminaEnergyY = staminaTextureY + staminaBarHeight;
            
            this.drawHelper.drawTexturedModalRect(matrixStack, staminaBarX, staminaBarY, 0, staminaTextureY, staminaBarWidth, staminaBarHeight);
            if(staminaEnergyWidth > 0)
                this.drawHelper.drawTexturedModalRect(matrixStack, staminaBarX, staminaBarY, 0, staminaEnergyY, staminaEnergyWidth, staminaBarHeight);
            
            if(this.mountMessageTime > 0)
            	this.mountMessageTime--;
		}
		else
			this.mountMessageTime = this.mountMessageTimeMax;

		matrixStack.pop();
		this.minecraft.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
	}


	// ==================================================
	//                 Debug Overlay
	// ==================================================
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onGameOverlay(RenderGameOverlayEvent.Text event) {
		if(!ConfigDebug.INSTANCE.creatureOverlay.get()) {
			return;
		}

		// Entity:
		RayTraceResult mouseOver = Minecraft.getInstance().objectMouseOver;
		if(mouseOver instanceof EntityRayTraceResult) {
			Entity mouseOverEntity = ((EntityRayTraceResult)mouseOver).getEntity();
			if(mouseOverEntity instanceof BaseCreatureEntity) {
				BaseCreatureEntity mouseOverCreature = (BaseCreatureEntity)mouseOverEntity;
				event.getLeft().add("");
				event.getLeft().add("Target Creature: " + mouseOverCreature.getName().getString());
				event.getLeft().add("Distance To player: " + mouseOverCreature.getDistance(Minecraft.getInstance().player));
				event.getLeft().add("Elements: " + mouseOverCreature.creatureInfo.getElementNames(mouseOverCreature.getSubspecies()).getString());
				event.getLeft().add("Subspecies: " + mouseOverCreature.getSubspeciesIndex());
				event.getLeft().add("Variant: " + mouseOverCreature.getVariantIndex());
				event.getLeft().add("Level: " + mouseOverCreature.getLevel());
				event.getLeft().add("Experience: " + mouseOverCreature.getExperience() + "/" + mouseOverCreature.creatureStats.getExperienceForNextLevel());
				event.getLeft().add("Size: " + mouseOverCreature.sizeScale);
				event.getLeft().add("");
				event.getLeft().add("Health: " + mouseOverCreature.getHealth() + "/" + mouseOverCreature.getMaxHealth() + " Fresh: " + mouseOverCreature.creatureStats.getHealth());
				event.getLeft().add("Speed: " + mouseOverCreature.getAttribute(Attributes.MOVEMENT_SPEED).getValue() + "/" + mouseOverCreature.creatureStats.getSpeed());
				event.getLeft().add("");
				event.getLeft().add("Defense: " + mouseOverCreature.creatureStats.getDefense());
				event.getLeft().add("Armor: " + mouseOverCreature.getTotalArmorValue());
				event.getLeft().add("");
				event.getLeft().add("Damage: " + mouseOverCreature.creatureStats.getDamage());
				event.getLeft().add("Melee Speed: " + mouseOverCreature.creatureStats.getAttackSpeed());
				event.getLeft().add("Melee Range: " + mouseOverCreature.getPhysicalRange());
				event.getLeft().add("Ranged Speed: " + mouseOverCreature.creatureStats.getRangedSpeed());
				event.getLeft().add("Pierce: " + mouseOverCreature.creatureStats.getPierce());
				event.getLeft().add("");
				event.getLeft().add("Effect Duration: " + mouseOverCreature.creatureStats.getEffect() + " Base Seconds");
				event.getLeft().add("Effect Amplifier: x" + mouseOverCreature.creatureStats.getAmplifier());
				event.getLeft().add("");
				event.getLeft().add("Has Attack Target: " + mouseOverCreature.hasAttackTarget());
				event.getLeft().add("Has Avoid Target: " + mouseOverCreature.hasAvoidTarget());
				event.getLeft().add("Has Master Target: " + mouseOverCreature.hasMaster());
				event.getLeft().add("Has Parent Target: " + mouseOverCreature.hasParent());
				if(mouseOverEntity instanceof TameableCreatureEntity) {
					TameableCreatureEntity mouseOverTameable = (TameableCreatureEntity)mouseOverCreature;
					event.getLeft().add("");
					event.getLeft().add("Owner ID: " + (mouseOverTameable.getOwnerId() != null ? mouseOverTameable.getOwnerId().toString() : "None"));
					event.getLeft().add("Owner Name: " + mouseOverTameable.getOwnerName().getString());
				}
			}
		}
	}
}
