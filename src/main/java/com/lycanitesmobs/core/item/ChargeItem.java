package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChargeItem extends BaseItem {
    /** How much experience a Charge Item grants per element matched. **/
    public static int CHARGE_EXPERIENCE = 50;

    /** The projectile info that this projectile charge item belongs to. **/
    public ProjectileInfo projectileInfo;

    /**
     * Constructor
     * @param projectileInfo The projectile info to base this charge off.
     */
    public ChargeItem(Item.Properties properties, ProjectileInfo projectileInfo) {
        super(properties);
        this.projectileInfo = projectileInfo;
        this.modInfo = LycanitesMobs.modInfo;
        if(this.projectileInfo != null) {
            this.itemName = projectileInfo.chargeItemName;
            LycanitesMobs.logDebug("Projectile", "Created Charge Item: " + projectileInfo.chargeItemName);
            this.setup();
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack itemStack) {
        return this.getProjectileName().appendText(" ").appendSibling(new TranslationTextComponent("item.lycanitesmobs.charge"));
    }

    @Override
    public void addInformation(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {
        super.addInformation(itemStack, world, tooltip, tooltipFlag);
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        for(ITextComponent description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
            List<String> formattedDescriptionList = fontRenderer.listFormattedStringToWidth("-------------------\n" + description.getFormattedText(), DESCRIPTION_WIDTH + 100);
            for (String formattedDescription : formattedDescriptionList) {
                tooltip.add(new StringTextComponent(formattedDescription));
            }
        }
    }

    @Override
    public ITextComponent getDescription(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        return new TranslationTextComponent("item.lycanitesmobs.charge.description");
    }

    public List<ITextComponent> getAdditionalDescriptions(ItemStack itemStack, @Nullable World world, ITooltipFlag tooltipFlag) {
        List<ITextComponent> descriptions = new ArrayList<>();

        if(!this.getElements().isEmpty()) {
            ITextComponent elements = new TranslationTextComponent("item.lycanitesmobs.charge.elements")
                    .appendText(" ").appendSibling(this.getElementNames());
            descriptions.add(elements);
        }

        ITextComponent projectile = new TranslationTextComponent("item.lycanitesmobs.charge.projectile")
                .appendText(" ").appendSibling(this.getProjectileName());
        descriptions.add(projectile);

        return descriptions;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);

        if(!world.isRemote && player.isShiftKeyDown()) { // isSneaking()
            BaseProjectileEntity projectile = this.createProjectile(itemStack, world, player);
            if(projectile == null) {
                LycanitesMobs.logWarning("", "Failed to create projectile from Charge Item: " + this.itemName);
                return new ActionResult<>(ActionResultType.FAIL, itemStack);
            }
            world.addEntity(projectile);
            if(!player.abilities.isCreativeMode) {
                itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
            }
            this.playSound(world, player.getPosition(), projectile.getLaunchSound(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (player.getRNG().nextFloat() * 0.4F + 0.8F));
        }

        return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if(entity instanceof TameableCreatureEntity && ((TameableCreatureEntity)entity).getPlayerOwner() == player) {
            return true;
        }
        return super.itemInteractionForEntity(stack, player, entity, hand);
    }

    /**
     * Creates a projectile instance from this charge item.
     * @param itemStack The charge itemstack.
     * @param world The world to create the projectile in.
     * @param entityPlayer The player using the charge.
     * @return A projectile instance.
     */
    public BaseProjectileEntity createProjectile(ItemStack itemStack, World world, PlayerEntity entityPlayer) {
        if(this.projectileInfo != null) {
            return this.projectileInfo.createProjectile(world, entityPlayer);
        }
        return null;
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
    public ITextComponent getElementNames() {
        ITextComponent elementNames = new StringTextComponent("");
        boolean firstElement = true;
        for(ElementInfo element : this.getElements()) {
            if(!firstElement) {
                elementNames.appendText(", ");
            }
            firstElement = false;
            elementNames.appendSibling(element.getTitle());
        }
        return elementNames;
    }

    /**
     * Returns the display name of the projectile fired by this Charge.
     * @return The Projectile this Charge fires.
     */
    public ITextComponent getProjectileName() {
        if(this.projectileInfo != null) {
            return this.projectileInfo.getTitle();
        }
        return new TranslationTextComponent("item.lycanitesmobs.charge");
    }
}
