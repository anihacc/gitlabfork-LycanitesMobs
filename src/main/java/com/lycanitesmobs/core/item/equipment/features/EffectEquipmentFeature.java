package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EffectEquipmentFeature extends EquipmentFeature {
	/** The type of effect to apply. Can be fire or a potion effect name. **/
	public String effectType;

	/** Controls what this effect targets, Can be: self or target. **/
	public String effectTarget;

	/** The time (in ticks) that this feature adds to the weapon attack cooldown on use. **/
	public int effectDuration = 0;

	/** The range (in blocks) that this feature adds to the weapon attack. **/
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
	public ITextComponent getDescription(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		ITextComponent description = new TranslationTextComponent("equipment.feature." + this.featureType)
				.appendText(" ")
				.appendText(this.effectType + " (" + this.effectTarget + ")");

		if(!"self".equals(this.effectTarget) && this.effectDuration > 0) {
			description.appendText("\n")
				.appendSibling(new TranslationTextComponent("equipment.feature.effect.duration"))
				.appendText(" " + ((float)this.effectDuration / 20));
		}

		if(this.effectStrength > 0) {
			description.appendText("\n")
					.appendSibling(new TranslationTextComponent("equipment.feature.effect.strength"))
					.appendText(" " + this.effectStrength);
		}

		return description;
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

		// Fire:
		if("fire".equalsIgnoreCase(this.effectType)) {
			effectTarget.setFire(Math.round(((float)this.effectDuration) / 20));
			return;
		}

		// Potion Effects:
		Effect effect = GameRegistry.findRegistry(Effect.class).getValue(new ResourceLocation(this.effectType));
		if(effect != null) {
			effectTarget.addPotionEffect(new EffectInstance(effect, this.effectDuration, this.effectStrength));
		}
	}
}
