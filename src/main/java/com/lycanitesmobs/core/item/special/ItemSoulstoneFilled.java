package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityCacodemon;
import com.lycanitesmobs.core.entity.creature.EntityPinky;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureType;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemSoulstoneFilled extends ItemSoulstone {
    public CreatureType creatureType;

	public ItemSoulstoneFilled(ModInfo group, CreatureType creatureType) {
        super(group);
        this.creatureType = creatureType;
        this.itemName = creatureType.getSoulstoneName();
        this.setRegistryName(this.modInfo.modid, this.itemName);
        this.setUnlocalizedName(this.itemName);
        this.setCreativeTab(LycanitesMobs.itemsTab);
    }

    @Override
    public void setup() {}
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
    	ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
    	if(playerExt == null) {
            return new ActionResult(EnumActionResult.SUCCESS, itemStack);
        }
        if(this.creatureType.tameableCreatures.isEmpty()) {
            LycanitesMobs.logInfo("", "Tried to use a " + this.creatureType.getSoulstoneName() + " but there are no tameable creatures for this type yet.");
            return super.onItemRightClick(world, player, hand);
        }

        int creatureIndex = 0;
        if(this.creatureType.tameableCreatures.size() > 1) {
            player.getRNG().nextInt(this.creatureType.tameableCreatures.size());
        }
        TameableCreatureEntity entity = (TameableCreatureEntity)this.creatureType.tameableCreatures.get(creatureIndex).createEntity(world);
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
