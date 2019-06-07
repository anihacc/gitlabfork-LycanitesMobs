package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemCharge extends ItemBase {
    /** The projectile info that this projectile charge item belongs to. **/
    public ProjectileInfo projectileInfo;

    /**
     * Constructor
     * @param projectileInfo The projectile info to base this charge off.
     */
    public ItemCharge(ProjectileInfo projectileInfo) {
        super();
        this.projectileInfo = projectileInfo;
        this.modInfo = LycanitesMobs.modInfo;
        if(this.projectileInfo != null) {
            this.itemName = projectileInfo.chargeItemName;
            LycanitesMobs.printDebug("Projectile", "Created Charge Item: " + projectileInfo.chargeItemName);
            this.setup();
        }
    }

    /**
     * Older constructor for hard coded projectiles.
     */
    public ItemCharge() {
        super();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if(!player.capabilities.isCreativeMode) {
            itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
        }

        if(!world.isRemote) {
            EntityProjectileBase projectile = this.createProjectile(itemStack, world, player);
            if(projectile == null)
                return new ActionResult<>(EnumActionResult.FAIL, itemStack);
            world.spawnEntity(projectile);
            this.playSound(world, player.getPosition(), projectile.getLaunchSound(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

    /**
     * Creates a projectile instance from this charge item.
     * @param itemStack The charge itemstack.
     * @param world The world to create the projectile in.
     * @param entityPlayer The player using the charge.
     * @return A projectile instance.
     */
    public EntityProjectileBase createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if(this.projectileInfo == null) {
            return null;
        }
        return this.projectileInfo.createProjectile(world, entityPlayer);
    }
}
