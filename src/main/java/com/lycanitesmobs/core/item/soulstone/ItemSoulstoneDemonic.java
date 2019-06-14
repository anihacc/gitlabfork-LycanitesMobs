package com.lycanitesmobs.core.item.soulstone;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.creature.EntityCacodemon;
import com.lycanitesmobs.core.entity.creature.EntityPinky;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.special.ItemSoulstone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemSoulstoneDemonic extends ItemSoulstone {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulstoneDemonic(ModInfo group) {
        super(group, "demonic");
    }
    
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
    	ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
    	if(playerExt == null)
            return new ActionResult(ActionResultType.SUCCESS, itemStack);

        EntityCreatureTameable entity;
        if(player.getRNG().nextBoolean())
            entity = new EntityCacodemon(world);
        else
            entity = new EntityPinky(world);
        if(!player.getEntityWorld().isRemote) {
            entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            world.func_217376_c(entity);
            entity.setPlayerOwner(player);
        }

		super.onItemRightClickOnEntity(player, entity, itemStack);
        return new ActionResult(ActionResultType.SUCCESS, itemStack);
    }


    @Override
    public boolean onItemRightClickOnEntity(PlayerEntity player, Entity entity, ItemStack itemStack) {
        return false;
    }
}
