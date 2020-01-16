package com.lycanitesmobs.core.item.soulstone;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.special.ItemSoulstone;
import com.lycanitesmobs.core.entity.creature.EntityIoray;
import com.lycanitesmobs.core.entity.creature.EntityStrider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemSoulstoneFreshwater extends ItemSoulstone {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulstoneFreshwater(ModInfo group) {
        super(group, "freshwater");
    }
    
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
    	ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
    	if(playerExt == null)
            return new ActionResult(EnumActionResult.SUCCESS, itemStack);

        TameableCreatureEntity entity;
        if(player.getRNG().nextBoolean())
            entity = new EntityIoray(world);
        else
            entity = new EntityStrider(world);
        if(!player.getEntityWorld().isRemote) {
            entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            world.spawnEntity(entity);
            entity.setPlayerOwner(player);
        }

		super.onItemRightClickOnEntity(player, entity, itemStack);
        return new ActionResult(EnumActionResult.SUCCESS, itemStack);
    }


    @Override
    public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
        return false;
    }
}