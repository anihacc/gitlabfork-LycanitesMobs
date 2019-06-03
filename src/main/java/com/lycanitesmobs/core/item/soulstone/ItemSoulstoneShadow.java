package com.lycanitesmobs.core.item.soulstone;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.item.special.ItemSoulstone;
import com.lycanitesmobs.core.entity.creature.EntityShade;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.entity.creature.EntityChupacabra;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemSoulstoneShadow extends ItemSoulstone {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulstoneShadow(ModInfo group) {
        super(group, "shadow");
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

        EntityCreatureTameable entity;
        if(player.getRNG().nextBoolean())
            entity = new EntityChupacabra(world);
        else
            entity = new EntityShade(world);
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
