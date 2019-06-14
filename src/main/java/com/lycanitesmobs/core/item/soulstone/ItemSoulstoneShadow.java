package com.lycanitesmobs.core.item.soulstone;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.creature.EntityChupacabra;
import com.lycanitesmobs.core.entity.creature.EntityShade;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.special.ItemSoulstone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
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
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
    	ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
    	if(playerExt == null)
            return new ActionResult(ActionResultType.SUCCESS, itemStack);

        EntityCreatureTameable entity;
        if(player.getRNG().nextBoolean())
            entity = new EntityChupacabra(world);
        else
            entity = new EntityShade(world);
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
