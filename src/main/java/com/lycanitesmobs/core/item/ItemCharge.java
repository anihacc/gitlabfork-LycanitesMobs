package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemCharge extends ItemBase {
    /** The projectile info that this projectile charge item belongs to. **/
    public ProjectileInfo projectileInfo;

    /** Used for created charges of old projectiles, this is the class that is instantiated when spawning the projectile. **/
    public Class<? extends EntityProjectileBase> oldProjectileClass;

    /**
     * Constructor
     * @param projectileInfo The projectile info to base this charge off.
     */
    public ItemCharge(Item.Properties properties, ProjectileInfo projectileInfo) {
        super(properties);
        this.projectileInfo = projectileInfo;
        this.modInfo = LycanitesMobs.modInfo;
        properties.group(LycanitesMobs.itemsTab);
        if(this.projectileInfo != null) {
            this.itemName = projectileInfo.chargeItemName;
            LycanitesMobs.printDebug("Projectile", "Created Charge Item: " + projectileInfo.chargeItemName);
            this.setup();
        }
    }

    /**
     * Older constructor for hard coded projectiles.
     */
    public ItemCharge(Item.Properties properties, String itemName, Class<? extends EntityProjectileBase> projectileClass) {
        super(properties);
        this.oldProjectileClass = projectileClass;
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = itemName;
        properties.group(LycanitesMobs.itemsTab);
        this.setup();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if(!player.playerAbilities.isCreativeMode) {
            itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
        }

        if(!world.isRemote) {
            EntityProjectileBase projectile = this.createProjectile(itemStack, world, player);
            if(projectile == null)
                return new ActionResult<>(ActionResultType.FAIL, itemStack);
            world.func_217376_c(projectile);
            this.playSound(world, player.getPosition(), projectile.getLaunchSound(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (player.getRNG().nextFloat() * 0.4F + 0.8F));
        }

        return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
    }

    /**
     * Creates a projectile instance from this charge item.
     * @param itemStack The charge itemstack.
     * @param world The world to create the projectile in.
     * @param entityPlayer The player using the charge.
     * @return A projectile instance.
     */
    public EntityProjectileBase createProjectile(ItemStack itemStack, World world, PlayerEntity entityPlayer) {
        if(this.projectileInfo != null) {
            return this.projectileInfo.createProjectile(world, entityPlayer);
        }
        if(this.oldProjectileClass != null) {
            try {
                return this.oldProjectileClass.getConstructor(World.class, PlayerEntity.class).newInstance(world, entityPlayer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
