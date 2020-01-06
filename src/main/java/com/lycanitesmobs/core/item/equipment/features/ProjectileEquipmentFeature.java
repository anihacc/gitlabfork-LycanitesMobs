package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ProjectileEquipmentFeature extends EquipmentFeature {
	/** The name of the projectile to spawn. **/
	public String projectileName;

	/** How this feature spawns projectiles. Can be: 'hit' (when damaging an entity), 'primary' (left click) or 'secondary' (right click). **/
	public String projectileTrigger = "secondary";

	/** The pattern to fire projectiles in. Can be 'simple', 'spread' or 'ring'. **/
	public String projectilePattern = "simple";

	/** The chance of firing a projectile for the hit trigger. **/
	public double hitChance = 0.05;

	/** The cooldown (in ticks) for primary and secondary triggers. **/
	public int cooldown = 2;

	/** How many projectiles to fire per round. **/
	public int count = 1;

	/** The x spread for the spread projectile pattern. **/
	public double spreadX = 0;

	/** The y spread for the spread projectile pattern. **/
	public double spreadY = 0;

	/** The range in degrees for the ring pattern. **/
	public double ringRange = 0;

	/** Additional damage added to the projectile. **/
	public int bonusDamage = 0;


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);

		this.projectileName = json.get("projectileName").getAsString();

		if(json.has("projectileTrigger"))
			this.projectileTrigger = json.get("projectileTrigger").getAsString();

		if(json.has("projectilePattern"))
			this.projectilePattern = json.get("projectilePattern").getAsString();

		if(json.has("hitChance"))
			this.hitChance = json.get("hitChance").getAsDouble();

		if(json.has("cooldown"))
			this.cooldown = json.get("cooldown").getAsInt();

		if(json.has("count"))
			this.count = json.get("count").getAsInt();

		if(json.has("spreadX"))
			this.spreadX = json.get("spreadX").getAsDouble();

		if(json.has("spreadY"))
			this.spreadY = json.get("spreadY").getAsDouble();

		if(json.has("ringRange"))
			this.ringRange = json.get("ringRange").getAsDouble();

		if(json.has("bonusDamage"))
			this.bonusDamage = json.get("bonusDamage").getAsInt();
	}

	@Override
	public boolean isActive(ItemStack itemStack, int level) {
		if(!super.isActive(itemStack, level)) {
			return false;
		}
		return ProjectileManager.getInstance().getProjectile(this.projectileName) != null;
	}

	@Override
	public String getDescription(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}

		ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.projectileName);
		String description = LanguageManager.translate("equipment.feature." + this.featureType) + " "
				+ projectileInfo.getTitle();

		if(!"simple".equals(this.projectilePattern)) {
			description += " " + LanguageManager.translate("equipment.feature.projectile.pattern." + this.projectilePattern);
		}

		description += " " + LanguageManager.translate("equipment.feature.projectile.trigger." + this.projectileTrigger);
		if("hit".equals(this.projectileTrigger)) {
			description += " " + String.format("%.0f", this.hitChance * 100) + "%";
		}
		else {
			description += " " + String.format("%.1f", (float)this.cooldown / 20) + "s";
		}

		return description;
	}

	@Override
	public String getSummary(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.projectileName);
		String summary = projectileInfo.getTitle();
		if(this.bonusDamage != 0) {
			summary += " +" + this.bonusDamage;
		}
		return summary;
	}

	/**
	 * Called when a player left clicks to use their equipment.
	 * @param world The world the player is in.
	 * @param shooter The player using the equipment.
	 * @param hand The hand the player is holding the equipment in.
	 */
	public void onUsePrimary(World world, EntityPlayer shooter, EnumHand hand) {
		if(!"primary".equalsIgnoreCase(this.projectileTrigger)) {
			return;
		}
		ExtendedEntity shooterExt = ExtendedEntity.getForEntity(shooter);
		if(shooterExt == null) {
			return;
		}
		if(shooterExt.getProjectileCooldown(1, this.projectileName) > 0) {
			return;
		}
		shooterExt.setProjectileCooldown(1, this.projectileName, this.cooldown);
		this.fireProjectile(shooter);
	}

	/**
	 * Called when a player right click begins to use their equipment.
	 * @param world The world the player is in.
	 * @param shooter The player using the equipment.
	 * @param hand The hand the player is holding the equipment in.
	 * @return True so that the item becomes active.
	 */
	public boolean onUseSecondary(World world, EntityPlayer shooter, EnumHand hand) {
		return "secondary".equalsIgnoreCase(this.projectileTrigger);
	}

	/**
	 * Called when an entity using their equipment.
	 * @param shooter The entity using the equipment.
	 * @param count How long (in ticks) the equipment has been used for.
	 */
	public void onHoldSecondary(EntityLivingBase shooter, int count) {
		if(!"secondary".equalsIgnoreCase(this.projectileTrigger)) {
			return;
		}
		ExtendedEntity shooterExt = ExtendedEntity.getForEntity(shooter);
		if(shooterExt == null) {
			return;
		}
		if(shooterExt.getProjectileCooldown(2, this.projectileName) > 0) {
			return;
		}
		shooterExt.setProjectileCooldown(2, this.projectileName, this.cooldown);
		this.fireProjectile(shooter);
	}

	/**
	 * Called when an entity is hit by equipment with this feature.
	 * @param itemStack The ItemStack being hit with.
	 * @param target The target entity being hit.
	 * @param attacker The entity using this item to hit.
	 */
	public void onHitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase attacker) {
		if(target == null || attacker == null || attacker.getEntityWorld().isRemote || attacker.isSneaking() || !"hit".equals(this.projectileTrigger)) {
			return;
		}

		// Fire Projectile:
		if(attacker.getRNG().nextDouble() <= this.hitChance) {
			this.fireProjectile(attacker);
		}
	}

	/**
	 * Fires a projectile from this feature.
	 * @param shooter The entity firing the projectile.
	 */
	public void fireProjectile(EntityLivingBase shooter) {
		if(shooter == null || shooter.getEntityWorld().isRemote || this.count <= 0) {
			return;
		}

		World world = shooter.getEntityWorld();
		BaseProjectileEntity mainProjectile = null;
		Vec3d firePos = new Vec3d(shooter.posX, shooter.posY + (shooter.height * 0.65), shooter.posZ);
		double offsetX = 0;
		/*if(shooter.isHandActive()) {
			offsetX = 0.75D;
			if(shooter.getActiveHand() == EnumHand.OFF_HAND) {
				offsetX = -offsetX;
			}
			Vec3d playerFirePos = this.getFacingPosition(shooter, offsetX, shooter.rotationYaw + 90);
			firePos = new Vec3d(playerFirePos.x, firePos.y, playerFirePos.z);
		}*/

		// Patterns:
		if("spread".equals(this.projectilePattern)) {
			this.count = 10;
			this.spreadX = 45;
			this.spreadY = 10;
			for(int i = 0; i < this.count; i++) {
				double yaw = shooter.rotationYaw + (this.spreadX * shooter.getRNG().nextDouble()) - (this.spreadX / 2);
				double pitch = shooter.rotationPitch + (this.spreadY * shooter.getRNG().nextDouble()) - (this.spreadY / 2);
				ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.projectileName);
				BaseProjectileEntity projectile = projectileInfo.createProjectile(world, shooter);
				projectile.setPosition(firePos.x, firePos.y, firePos.z);
				projectile.shoot(shooter, (float)pitch, (float)yaw - (float)offsetX, 0, (float)projectileInfo.velocity, 0);
				projectile.setBonusDamage(this.bonusDamage);
				world.spawnEntity(projectile);
				mainProjectile = projectile;
			}
		}
		else if("ring".equals(this.projectilePattern)) {
			double angle = this.ringRange / this.count;
			for(int i = 0; i < this.count; i++) {
				double yaw = shooter.rotationYaw + (angle * i) - (this.ringRange / 2);
				ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.projectileName);
				BaseProjectileEntity projectile = projectileInfo.createProjectile(world, shooter);
				projectile.setPosition(firePos.x, firePos.y, firePos.z);
				projectile.setBonusDamage(this.bonusDamage);
				world.spawnEntity(projectile);
				projectile.shoot(shooter, shooter.rotationPitch, (float)yaw - (float)offsetX, 0, (float)projectileInfo.velocity, 0);
				mainProjectile = projectile;
			}
		}
		else {
			ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.projectileName);
			mainProjectile = projectileInfo.createProjectile(world, shooter);
			mainProjectile.setPosition(firePos.x, firePos.y, firePos.z);
			mainProjectile.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw - (float)offsetX, 0, (float)projectileInfo.velocity, 0);
			mainProjectile.setBonusDamage(this.bonusDamage);
			world.spawnEntity(mainProjectile);
		}

		if(shooter instanceof EntityPlayer && mainProjectile != null) {
			world.playSound(null, shooter.getPosition(), mainProjectile.getLaunchSound(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (shooter.getRNG().nextFloat() * 0.4F + 0.8F));
		}
	}

	/** Returns the Vec3f in front or behind the provided entity's position coords with the given distance and angle (in degrees), use a negative distance for behind. **/
	public Vec3d getFacingPosition(EntityLivingBase entity, double distance, double angle) {
		angle = Math.toRadians(angle);
		double xAmount = -Math.sin(angle);
		double zAmount = Math.cos(angle);
		return new Vec3d(entity.posX + (distance * xAmount), entity.posY, entity.posZ + (distance * zAmount));
	}
}
