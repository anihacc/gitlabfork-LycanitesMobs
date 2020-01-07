package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChargeItem extends ItemBase {
    /** How much experience a Charge Item grants per element matched. **/
    public static int CHARGE_EXPERIENCE = 50;

    /** The projectile info that this projectile charge item belongs to. **/
    public ProjectileInfo projectileInfo;

    /**
     * Constructor
     * @param projectileInfo The projectile info to base this charge off.
     */
    public ChargeItem(ProjectileInfo projectileInfo) {
        super();
        this.projectileInfo = projectileInfo;
        this.modInfo = LycanitesMobs.modInfo;
        if(this.projectileInfo != null) {
            this.itemName = projectileInfo.chargeItemName;
            LycanitesMobs.logDebug("Projectile", "Created Charge Item: " + projectileInfo.chargeItemName);
            this.setup();
        }
    }

    @Override
    public void setup() {
        this.setRegistryName(this.modInfo.modid, this.itemName);
        this.setUnlocalizedName(this.itemName);
        this.setCreativeTab(LycanitesMobs.chargesTab);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return this.getProjectileName() + " " + LanguageManager.translate("item.lycanitesmobs.charge");
    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
        super.addInformation(itemStack, world, tooltip, tooltipFlag);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        for(String description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
            List formattedDescriptionList = fontRenderer.listFormattedStringToWidth("-------------------\n" + description, DESCRIPTION_WIDTH);
            for (Object formattedDescription : formattedDescriptionList) {
                if (formattedDescription instanceof String)
                    tooltip.add("\u00a73" + formattedDescription);
            }
        }
    }

    public String getDescription(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        return LanguageManager.translate("item.lycanitesmobs.charge.description");
    }

    public List<String> getAdditionalDescriptions(ItemStack itemStack, @Nullable World world, ITooltipFlag tooltipFlag) {
        List<String> descriptions = new ArrayList<>();

        if(!this.getElements().isEmpty()) {
            String elements = LanguageManager.translate("item.lycanitesmobs.charge.elements")
                    + " " + this.getElementNames();
            descriptions.add(elements);
        }

        String projectile = LanguageManager.translate("item.lycanitesmobs.charge.projectile")
                + " " + this.getProjectileName();
        descriptions.add(projectile);

        return descriptions;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);

        if(!world.isRemote && player.isSneaking()) {
            BaseProjectileEntity projectile = this.createProjectile(itemStack, world, player);
            if(projectile == null) {
                LycanitesMobs.logWarning("", "Failed to create projectile from Charge Item: " + this.itemName);
                return new ActionResult<>(EnumActionResult.FAIL, itemStack);
            }
            world.spawnEntity(projectile);
            if(!player.capabilities.isCreativeMode) {
                itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
            }
            this.playSound(world, player.getPosition(), projectile.getLaunchSound(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

    @Override
    public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
        if(entity instanceof TameableCreatureEntity && ((TameableCreatureEntity)entity).getPlayerOwner() == player) {
            return false;
        }
        return true;
    }

    /**
     * Creates a projectile instance from this charge item.
     * @param itemStack The charge itemstack.
     * @param world The world to create the projectile in.
     * @param entityPlayer The player using the charge.
     * @return A projectile instance.
     */
    public BaseProjectileEntity createProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if(this.projectileInfo == null) {
            return null;
        }
        return this.projectileInfo.createProjectile(world, entityPlayer);
    }

    /**
     * Gets the Elements of this Charge.
     * @return A list of Elements that this Charge contains.
     */
    public List<ElementInfo> getElements() {
        if(this.projectileInfo == null) {
            return new ArrayList<>();
        }
        return this.projectileInfo.elements;
    }

    /**
     * Returns a comma separated list of Elements this Charge contains.
     * @return The Elements this Charge contains.
     */
    public String getElementNames() {
        String elementNames = "";
        boolean firstElement = true;
        for(ElementInfo element : this.getElements()) {
            if(!firstElement) {
                elementNames += ", ";
            }
            firstElement = false;
            elementNames += element.getTitle();
        }
        return elementNames;
    }

    /**
     * Returns the display name of the projectile fired by this Charge.
     * @return The Projectile this Charge fires.
     */
    public String getProjectileName() {
        if(this.projectileInfo != null) {
            return this.projectileInfo.getTitle();
        }
        return LanguageManager.translate("item.lycanitesmobs.charge");
    }
}
