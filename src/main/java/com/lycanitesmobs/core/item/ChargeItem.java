package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

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
    public Component getName(ItemStack itemStack) {
        return this.getProjectileName().append(" ").append(new TranslatableComponent("item.lycanitesmobs.charge"));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, world, tooltip, tooltipFlag);
        Font fontRenderer = Minecraft.getInstance().font;
        for(Component description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
            tooltip.add(description);
        }
    }

    @Override
    public Component getDescription(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        return new TranslatableComponent("item.lycanitesmobs.charge.description").withStyle(ChatFormatting.GREEN);
    }

    public List<Component> getAdditionalDescriptions(ItemStack itemStack, @Nullable Level world, TooltipFlag tooltipFlag) {
        List<Component> descriptions = new ArrayList<>();

        descriptions.add(new TranslatableComponent("item.lycanitesmobs.charge.projectile").withStyle(ChatFormatting.GOLD)
                .append(" ").append(this.getProjectileName()));

        if(!this.getElements().isEmpty()) {
            descriptions.add(new TranslatableComponent("item.lycanitesmobs.charge.elements").withStyle(ChatFormatting.DARK_AQUA)
                    .append(" ").append(this.getElementNames()));
        }

        return descriptions;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if(!world.isClientSide && player.isShiftKeyDown()) { // isSneaking()
            BaseProjectileEntity projectile = this.createProjectile(itemStack, world, player);
            if(projectile == null) {
                LycanitesMobs.logWarning("", "Failed to create projectile from Charge Item: " + this.itemName);
                return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
            }
            world.addFreshEntity(projectile);
            if(!player.getAbilities().instabuild) {
                itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
            }
            this.playSound(world, player.blockPosition(), projectile.getLaunchSound(), SoundSource.NEUTRAL, 0.5F, 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if(entity instanceof TameableCreatureEntity && ((TameableCreatureEntity)entity).getPlayerOwner() == player) {
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    /**
     * Creates a projectile instance from this charge item.
     * @param itemStack The charge itemstack.
     * @param world The world to create the projectile in.
     * @param entityPlayer The player using the charge.
     * @return A projectile instance.
     */
    public BaseProjectileEntity createProjectile(ItemStack itemStack, Level world, Player entityPlayer) {
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
    public Component getElementNames() {
        BaseComponent elementNames = new TextComponent("");
        boolean firstElement = true;
        for(ElementInfo element : this.getElements()) {
            if(!firstElement) {
                elementNames.append(", ");
            }
            firstElement = false;
            elementNames.append(element.getTitle());
        }
        return elementNames;
    }

    /**
     * Returns the display name of the projectile fired by this Charge.
     * @return The Projectile this Charge fires.
     */
    public BaseComponent getProjectileName() {
        if(this.projectileInfo != null) {
            return this.projectileInfo.getTitle();
        }
        return new TranslatableComponent("item.lycanitesmobs.charge");
    }
}
