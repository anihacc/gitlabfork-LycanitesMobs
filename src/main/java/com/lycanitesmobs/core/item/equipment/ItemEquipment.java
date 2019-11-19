package com.lycanitesmobs.core.item.equipment;

import com.google.common.collect.Multimap;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.features.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;

public class ItemEquipment extends ItemBase {
	/** I am sorry, I couldn't find another way. Set in getMetadata(ItemStack) as it's called just before rendering. **/
	public static ItemStack ITEMSTACK_TO_RENDER;

	/** The maximum amount of parts that can be added to an Equipment Piece. **/
	public static int PART_LIMIT = 20;


	/**
	 * Constructor
	 */
	public ItemEquipment() {
		super();
		this.itemName = "equipment";
		this.modInfo = LycanitesMobs.modInfo;
		this.setRegistryName(this.modInfo.modid, this.itemName);
		this.setUnlocalizedName(this.itemName);
		this.setMaxStackSize(1);
	}


	// ==================================================
	//                      Info
	// ==================================================
	@Override
	public void addInformation(ItemStack itemStack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
		super.addInformation(itemStack, world, tooltip, tooltipFlag);
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		for(String description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
			List formattedDescriptionList = fontRenderer.listFormattedStringToWidth("" + description, descriptionWidth);
			for (Object formattedDescription : formattedDescriptionList) {
				if (formattedDescription instanceof String)
					tooltip.add("\u00a73" + formattedDescription);
			}
		}
	}

	@Override
	public String getDescription(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		return LanguageManager.translate("item.equipment.description");
	}

	public List<String> getAdditionalDescriptions(ItemStack itemStack, @Nullable World world, ITooltipFlag tooltipFlag) {
		List<String> descriptions = new ArrayList<>();
		for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(itemStack)) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null)
				continue;
			int partLevel = equipmentPart.getLevel(equipmentPartStack);
			descriptions.add(equipmentPart.getItemStackDisplayName(itemStack) + " " + LanguageManager.translate("entity.level") + " " + partLevel);
		}
		//descriptions.add(LanguageManager.translate("common.holdshift"));
		return descriptions;
	}


	@Override
	public int getMetadata(ItemStack stack) {
		ITEMSTACK_TO_RENDER = stack; // Render hack.
		return super.getMetadata(stack);
	}


	/** Gets or creates an NBT Compound for the provided itemstack. **/
	public NBTTagCompound getTagCompound(ItemStack itemStack) {
		if(itemStack.hasTagCompound()) {
			return itemStack.getTagCompound();
		}
		return new NBTTagCompound();
	}

	@SideOnly(Side.CLIENT)
	@Nullable
	@Override
	public net.minecraft.client.gui.FontRenderer getFontRenderer(ItemStack stack) {
		return LycanitesMobs.proxy.getFontRenderer();
	}


	// ==================================================
	//                  Equipment Piece
	// ==================================================
	/**
	 * Returns a list of all Equipment Part ItemStacks for each slot.
	 * @param itemStack The Equipment ItemStack to get the Equipment Part ItemStacks from.
	 * @return The map of all Equipment Part ItemStacks for each slot.
	 */
	public NonNullList<ItemStack> getEquipmentPartStacks(ItemStack itemStack) {
		NonNullList<ItemStack> itemStacks = NonNullList.withSize(PART_LIMIT, ItemStack.EMPTY);
		NBTTagCompound nbt = this.getTagCompound(itemStack);
		if(nbt.hasKey("Items")) {
			ItemStackHelper.loadAllItems(nbt, itemStacks); // Reads ItemStacks into a List from "Items" tag.
		}
		return itemStacks;
	}


	/**
	 * Counts how many Equipment Parts this Piece is made out of.
	 * @param itemStack The Equipment ItemStack to count the Equipment Part ItemStacks from.
	 * @return How many Equipment Parts make this Equipment Piece.
	 */
	public int getEquipmentPartCount(ItemStack itemStack) {
		int partCount = 0;
		for(ItemStack partStack : this.getEquipmentPartStacks(itemStack)) {
			if(!partStack.isEmpty()) {
				partCount++;
			}
		}
		return partCount;
	}


	/**
	 * Returns the Equipment Part for the provided ItemStack or null in empty or a different item.
	 * @param itemStack The Equipment Part ItemStack to get the Equipment Part from.
	 * @return The Equipment Part or null if the stack is empty.
	 */
	public ItemEquipmentPart getEquipmentPart(ItemStack itemStack) {
		if(itemStack.isEmpty()) {
			return null;
		}
		if(!(itemStack.getItem() instanceof ItemEquipmentPart)) {
			return null;
		}
		return (ItemEquipmentPart)itemStack.getItem();
	}


	/**
	 * Adds an Equipment Part ItemStack to the provided Equipment ItemStack.
	 * @param equipmentStack The Equipment ItemStack to add a part to.
	 * @param equipmentPartStack The Equipment Part ItemStack to add.
	 * @param slotIndex The slot index to connect the part to.
	 */
	public void addEquipmentPart(ItemStack equipmentStack, ItemStack equipmentPartStack, int slotIndex) {
		if(slotIndex >= PART_LIMIT) {
			return;
		}
		NonNullList<ItemStack> itemStacks = this.getEquipmentPartStacks(equipmentStack);
		itemStacks.set(slotIndex, equipmentPartStack);
		NBTTagCompound nbt = this.getTagCompound(equipmentStack);
		ItemStackHelper.saveAllItems(nbt, itemStacks);
		equipmentStack.setTagCompound(nbt);
	}


	/**
	 * Searches for the provided active features by type and returns a list of them.
	 * @param equipmentStack The itemStack of the Equipment.
	 * @param featureType The type of feature to search for.
	 * @return A list of features.
	 */
	public List<EquipmentFeature> getFeaturesByType(ItemStack equipmentStack, String featureType) {
		List<EquipmentFeature> features = new ArrayList<>();
		for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null) {
				continue;
			}
			for (EquipmentFeature feature : equipmentPart.features) {
				if(feature.isActive(equipmentPartStack, equipmentPart.getLevel(equipmentPartStack)) && feature.featureType.equalsIgnoreCase(featureType)) {
					features.add(feature);
				}
			}
		}
		return features;
	}


	/**
	 * Cycles through each Equipment Part and lowers their level to the provided level cap, used by lower level Forges.
	 * @param equipmentStack The itemStack of the Equipment.
	 * @param levelCap The level cap to lower parts to.
	 */
	public void applyLevelCap(ItemStack equipmentStack, int levelCap) {
		for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null) {
				continue;
			}
			equipmentPart.setLevel(equipmentPartStack, Math.min(levelCap, equipmentPart.getLevel(equipmentPartStack)));
		}
	}


	/**
	 * Cycles through each Equipment Part returns the highest part level found.
	 * @param equipmentStack The itemStack of the Equipment.
	 * @return The highest part level found.
	 */
	public int getHighestLevel(ItemStack equipmentStack) {
		int highestLevel = 0;
		for(ItemStack equipmentPartStack : this.getEquipmentPartStacks(equipmentStack)) {
			ItemEquipmentPart equipmentPart = this.getEquipmentPart(equipmentPartStack);
			if(equipmentPart == null) {
				continue;
			}
			int partLevel = equipmentPart.getLevel(equipmentPartStack);
			if(partLevel > highestLevel) {
				highestLevel = partLevel;
			}
		}
		return highestLevel;
	}


	// ==================================================
	//                       Using
	// ==================================================
	@Override
	public EnumAction getItemUseAction(ItemStack itemStack) {
		return EnumAction.NONE;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 18000;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
		boolean active = false;

		// Projectiles:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
			ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
			if(projectileFeature.onUseSecondary(world, player, hand)) {
				active = true;
			}
		}

		if(active) {
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.PASS, itemStack);
		}
		return new ActionResult<>(EnumActionResult.FAIL, itemStack);
	}

	@Override
	public void onUsingTick(ItemStack itemStack, EntityLivingBase user, int count) {
		if(!user.isHandActive()) {
			return;
		}

		// Projectiles:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
			ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
			projectileFeature.onHoldSecondary(user, count);
		}
	}


	// ==================================================
	//                     Harvesting
	// ==================================================
	@Override
	public Set<String> getToolClasses(ItemStack itemStack) {
		Map<String, Boolean> toolTypes = new HashMap<>();
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			String toolType = harvestFeature.getToolType();
			if(toolType != null) {
				toolTypes.put(toolType, true);
			}
		}
		return toolTypes.keySet();
	}

	@Override
	public int getHarvestLevel(ItemStack itemStack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
		int harvestLevel = -1;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			if(harvestLevel < harvestFeature.harvestLevel && toolClass.equals(harvestFeature.getToolType())) {
				harvestLevel = harvestFeature.harvestLevel;
			}
		}
		return harvestLevel;
	}

	@Override
	public boolean canHarvestBlock(IBlockState blockState, ItemStack itemStack) {
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			if(harvestFeature.canHarvestBlock(blockState)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public float getDestroySpeed(ItemStack itemStack, IBlockState blockState) {
		float speed = 1;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			speed += harvestFeature.getHarvestSpeed(blockState);
		}
		return speed;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemStack, World worldIn, IBlockState blockState, BlockPos pos, EntityLivingBase entityLiving)
	{
		if(worldIn.isRemote) {
			return super.onBlockDestroyed(itemStack, worldIn, blockState, pos, entityLiving);
		}
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "harvest")) {
			HarvestEquipmentFeature harvestFeature = (HarvestEquipmentFeature)equipmentFeature;
			harvestFeature.onBlockDestroyed(worldIn, blockState, pos, entityLiving);
		}
		return super.onBlockDestroyed(itemStack, worldIn, blockState, pos, entityLiving);
	}


	// ==================================================
	//                      Damaging
	// ==================================================
	/**
	 * Called when an entity is hit with this item.
	 * @param itemStack The ItemStack being hit with.
	 * @param target The target entity being hit.
	 * @param attacker The entity using this item to hit.
	 * @return True on successful hit.
	 */
	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase attacker) {
		// Knockback:
		double knockback = this.getDamageKnockback(itemStack);
		if(knockback != 0 && attacker != null && target != null) {
			double xDist = attacker.posX - target.posX;
			double zDist = attacker.posZ - target.posZ;
			double xzDist = MathHelper.sqrt(xDist * xDist + zDist * zDist);
			double motionCap = 10;
			if(target.motionX < motionCap && target.motionX > -motionCap && target.motionZ < motionCap && target.motionZ > -motionCap) {
				target.addVelocity(
						-(xDist / xzDist * knockback + target.motionX * knockback),
						0,
						-(zDist / xzDist * knockback + target.motionZ * knockback)
				);
			}
		}

		// Effects:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "effect")) {
			EffectEquipmentFeature effectFeature = (EffectEquipmentFeature)equipmentFeature;
			effectFeature.onHitEntity(itemStack, target, attacker);
		}

		// Summons:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "summon")) {
			SummonEquipmentFeature summonFeature = (SummonEquipmentFeature)equipmentFeature;
			summonFeature.onHitEntity(itemStack, target, attacker);
		}

		// Projectiles:
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "projectile")) {
			ProjectileEquipmentFeature projectileFeature = (ProjectileEquipmentFeature)equipmentFeature;
			projectileFeature.onHitEntity(itemStack, target, attacker);
		}

		return true;
	}

	/**
	 * Adds attribute modifiers provided by this equipment.
	 * @param slot The slot that this equipment is in.
	 * @return The Attribute Modifier multimap with changes applied to it.
	 */
	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack itemStack) {
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
		if (slot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.getDamageAmount(itemStack), 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -this.getDamageCooldown(itemStack), 0));
		}

		return multimap;
	}

	/**
	 * Returns how much damage this equipment will do.
	 * @param itemStack The equipment ItemStack.
	 * @return The amount of base damage.
	 */
	public double getDamageAmount(ItemStack itemStack) {
		double damage = 0;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "damage")) {
			DamageEquipmentFeature damageFeature = (DamageEquipmentFeature)equipmentFeature;
			damage += damageFeature.damageAmount;
		}
		return damage;
	}

	/**
	 * Returns the attack cooldown of this equipment.
	 * @param itemStack The equipment ItemStack.
	 * @return The amount of cooldown in ticks.
	 */
	public double getDamageCooldown(ItemStack itemStack) {
		double cooldown = 0;
		double i = 0;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "damage")) {
			DamageEquipmentFeature damageFeature = (DamageEquipmentFeature)equipmentFeature;
			cooldown += damageFeature.damageCooldown;
			i++;
		}

		if(i == 0) {
			return 2.4D;
		}

		cooldown = 2.4D * (cooldown / i);
		return cooldown;
	}

	/**
	 * Returns the attack knockback of this equipment.
	 * @param itemStack The equipment ItemStack.
	 * @return The amount of knockback.
	 */
	public double getDamageKnockback(ItemStack itemStack) {
		double knockback = 0;
		for(EquipmentFeature equipmentFeature : this.getFeaturesByType(itemStack, "damage")) {
			DamageEquipmentFeature damageFeature = (DamageEquipmentFeature)equipmentFeature;
			knockback += damageFeature.damageKnockback;
		}
		return knockback;
	}


	// ==================================================
	//                      Visuals
	// ==================================================
	@Override
	public ModelResourceLocation getModelResourceLocation() {
		return new ModelResourceLocation(new ResourceLocation(LycanitesMobs.modid, "equipmentpart"), "inventory");
	}

	/** Returns the texture to use for the provided ItemStack. **/
	public ResourceLocation getTexture(ItemStack itemStack) {
		return AssetManager.getTexture(this.itemName);
	}
}
