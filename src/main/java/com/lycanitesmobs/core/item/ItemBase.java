package com.lycanitesmobs.core.item;

import com.google.common.collect.Multimap;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.compatibility.Thaumcraft;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBase extends Item {
	public static int descriptionWidth = 200;
	
	public String itemName = "unamed_item";
	public ModInfo modInfo = LycanitesMobs.modInfo;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBase(Properties properties) {
    	super(properties);
        //this.setMaxStackSize(64);
    }

    public void setup() {
        this.setRegistryName(this.modInfo.filename, this.itemName);
        //this.setCreativeTab(LycanitesMobs.itemsTab);
    }
    
    
	// ==================================================
	//                      Info
	// ==================================================
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return LanguageManager.translate(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
	}

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    	String description = this.getDescription(stack, worldIn, tooltip, flagIn);
    	if(!"".equalsIgnoreCase(description)) {
    		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    		List formattedDescriptionList = fontRenderer.listFormattedStringToWidth(description, descriptionWidth);
    		for(Object formattedDescription : formattedDescriptionList) {
    			if(formattedDescription instanceof String)
                    tooltip.add("\u00a7a" + formattedDescription);
    		}
    	}
    	super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public String getDescription(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    	return LanguageManager.translate(this.getUnlocalizedName() + ".description");
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return super.getAttributeModifiers(slot, stack);
    }
	
    
	// ==================================================
	//                      Update
	// ==================================================
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
		super.onUpdate(itemStack, world, entity, par4, par5);
	}
    
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Use ==========
    @Override
    public EnumActionResult onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	return super.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
    
    // ========== Start ==========
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, EnumHand hand) {
        return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public void onItemLeftClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, EnumHand hand) {
        return;
    }

    // ========== Using ==========
    @Override
    public void onUsingTick(ItemStack itemStack, EntityLivingBase entity, int useRemaining) {
    	super.onUsingTick(itemStack, entity, useRemaining);
    }
    
    // ========== Stop ==========
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
    	super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
    }

    // ========== Animation ==========
    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        return super.getItemUseAction(itemStack);
    }
    
    // ========== Entity Interaction ==========
    public boolean onItemRightClickOnEntity(PlayerEntity player, Entity entity, ItemStack itemStack) {
    	return false;
    }

	
	// ==================================================
	//                     Enchanting
	// ==================================================
    @Override
    public int getItemEnchantability() {
        return 0;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        return super.getIsRepairable(itemStack, repairStack);
    }


    // ==================================================
    //                      Sound
    // ==================================================
    public void playSound(World world, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        world.playSound(null, x, y, z, sound, category, volume, pitch);
    }

    public void playSound(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        world.playSound(null, pos, sound, category, volume, pitch);
    }
    
	
	// ==================================================
	//                     Visuals
	// ==================================================
    // ========== Holding Angle ==========
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return super.isFull3D();
    }

    // ========== Get Model Resource Location ==========
    public ModelResourceLocation getModelResourceLocation() {
        return new ModelResourceLocation(this.getRegistryName(), "inventory");
    }

    // ========== Use Colors ==========
    public boolean useItemColors() {
        return false;
    }

    // ========== Get Color from ItemStack ==========
    public int getColorFromItemstack(ItemStack itemStack, int tintIndex) {
        return 16777215;
    }
}
