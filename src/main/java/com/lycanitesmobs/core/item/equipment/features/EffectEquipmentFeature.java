package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.common.registry.GameRegistry;

public class EffectEquipmentFeature extends EquipmentFeature {
	/** The type of effect to apply. Can be fire or a potion effect name. **/
	public String effectType;

	/** Controls what this effect targets, Can be: self or target. **/
	public String effectTarget;

	/** The time (in ticks) that this feature adds to the weapon attack cooldown on use. **/
	public int effectDuration = 0;

	/** The strength of the effect, 1 = amplifier 0, 2 = amplifier 1, etc. **/
	public int effectStrength = 0;


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);

		this.effectType = json.get("effectType").getAsString();

		this.effectTarget = json.get("effectTarget").getAsString();

		if(json.has("effectDuration"))
			this.effectDuration = json.get("effectDuration").getAsInt();

		if(json.has("effectStrength"))
			this.effectStrength = json.get("effectStrength").getAsInt();
	}

	@Override
	public Component getDescription(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}

		BaseComponent description = (BaseComponent) new TranslatableComponent("equipment.feature." + this.featureType).append(" ").append(
				this.getEffectTypeName());
		if(this.effectStrength > 0) {
			description.append(" ").append(new TranslatableComponent("entity.level")).append(" " + this.effectStrength);
		}
		if(!"self".equals(this.effectTarget) && this.effectDuration > 0) {
			description.append(" " + ((float)this.effectDuration / 20) + "s");
		}

		description.append(" (" + this.effectTarget + ")");

		return description;
	}

	@Override
	public Component getSummary(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}

		BaseComponent summary = this.getEffectTypeName();
		if(this.effectStrength > 0) {
			summary.append(" ").append(new TranslatableComponent("entity.level")).append(" " + this.effectStrength);
		}
		if(!"self".equals(this.effectTarget) && this.effectDuration > 0) {
			summary.append(" " + ((float)this.effectDuration / 20) + "s");
		}

		summary.append(" (" + this.effectTarget + ")");

		return summary;
	}

	public BaseComponent getEffectTypeName() {
		if("burning".equals(this.effectType)) {
			return new TranslatableComponent("effect.burning");
		}
		MobEffect effect = GameRegistry.findRegistry(MobEffect.class).getValue(new ResourceLocation(this.effectType));
		if(effect == null) {
			return new TextComponent(this.effectType);
		}
		return (BaseComponent) effect.getDisplayName();
	}

	/**
	 * Called when an entity is hit by equipment with this feature.
	 * @param itemStack The ItemStack being hit with.
	 * @param target The target entity being hit.
	 * @param attacker The entity using this item to hit.
	 */
	public void onHitEntity(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
		if(target == null || attacker == null) {
			return;
		}

		LivingEntity effectTarget = target;
		if("self".equalsIgnoreCase(this.effectTarget)) {
			effectTarget = attacker;
		}

		// Burning:
		if("burning".equalsIgnoreCase(this.effectType)) {
			effectTarget.setSecondsOnFire(Math.round(((float)this.effectDuration) / 20));
			return;
		}

		// Potion Effects:
		MobEffect effect = GameRegistry.findRegistry(MobEffect.class).getValue(new ResourceLocation(this.effectType));
		if(effect != null && this.effectStrength > 0) {
			effectTarget.addEffect(new MobEffectInstance(effect, this.effectDuration, this.effectStrength - 1));
		}
	}
}
