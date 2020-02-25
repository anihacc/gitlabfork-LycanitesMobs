package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

public class ProjectileBehaviourRandomEffect extends ProjectileBehaviour {
	/** A list of beneficial potion effects that this element can grant. **/
	public List<String> effects = new ArrayList<>();

	/** The duration (in ticks) of the random effect. **/
	public int duration = 100;

	/** The random effect amplifier. **/
	public int amplifier = 0;

	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("effects"))
			this.effects = JSONHelper.getJsonStrings(json.get("effects").getAsJsonArray());
	}

	@Override
	public void onProjectileDamage(BaseProjectileEntity projectile, World world, EntityLivingBase target, float damage) {
		if(projectile.getThrower() == null || damage <= 0) {
			return;
		}

		if(this.duration <= 0 || this.amplifier < 0) {
			return;
		}

		int randomIndex = world.rand.nextInt(this.effects.size());
		Potion effect = GameRegistry.findRegistry(Potion.class).getValue(new ResourceLocation(this.effects.get(randomIndex)));
		if(effect != null) {
			target.addPotionEffect(new PotionEffect(effect, this.duration, this.amplifier));
		}
	}
}
