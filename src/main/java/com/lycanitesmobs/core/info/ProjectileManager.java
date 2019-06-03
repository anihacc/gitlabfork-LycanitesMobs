package com.lycanitesmobs.core.info;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.dispenser.projectile.*;
import com.lycanitesmobs.core.entity.projectile.*;
import net.minecraft.init.Items;

public class ProjectileManager {
	public static ProjectileManager INSTANCE;

	/** Returns the main Projectile Manager instance or creates it and returns it. **/
	public static ProjectileManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ProjectileManager();
		}
		return INSTANCE;
	}


	/** Called during early start up, loads all items. **/
	public void loadProjectiles() {
		ObjectManager.addProjectile("frostweb", EntityFrostweb.class, ObjectManager.getItem("frostwebcharge"), new DispenserBehaviorFrostweb());
		ObjectManager.addProjectile("tundra", EntityTundra.class, ObjectManager.getItem("tundracharge"), new DispenserBehaviorTundra());
		ObjectManager.addProjectile("icefireball", EntityIcefireball.class, ObjectManager.getItem("icefirecharge"), new DispenserBehaviorIcefire());
		ObjectManager.addProjectile("blizzard", EntityBlizzard.class, ObjectManager.getItem("blizzardcharge"), new DispenserBehaviorBlizzard());
		ObjectManager.addProjectile("hellfireball", EntityHellfireball.class, ObjectManager.getItem("hellfirecharge"), new DispenserBehaviorHellfireball());
		ObjectManager.addProjectile("doomfireball", EntityDoomfireball.class, ObjectManager.getItem("doomfirecharge"), new DispenserBehaviorDoomfireball());
		ObjectManager.addProjectile("devilstar", EntityDevilstar.class, ObjectManager.getItem("devilstarcharge"), new DispenserBehaviorDevilstar());
		ObjectManager.addProjectile("demonicspark", EntityDemonicSpark.class, false);
		ObjectManager.addProjectile("demonicblast", EntityDemonicBlast.class, ObjectManager.getItem("demoniclightningcharge"), new DispenserBehaviorDemonicLightning());
		ObjectManager.addProjectile("hellfirewall", EntityHellfireWall.class, false);
		ObjectManager.addProjectile("hellfireorb", EntityHellfireOrb.class, false);
		ObjectManager.addProjectile("hellfirewave", EntityHellfireWave.class, false);
		ObjectManager.addProjectile("hellfirewavepart", EntityHellfireWavePart.class, false);
		ObjectManager.addProjectile("hellfirebarrier", EntityHellfireBarrier.class, false);
		ObjectManager.addProjectile("hellfirebarrierpart", EntityHellfireBarrierPart.class, false);
		ObjectManager.addProjectile("devilgatling", EntityDevilGatling.class, false);
		ObjectManager.addProjectile("hellshield", EntityHellShield.class, false);
		ObjectManager.addProjectile("helllaser", EntityHellLaser.class, false);
		ObjectManager.addProjectile("helllaserend", EntityHellLaserEnd.class, false);
		ObjectManager.addProjectile("throwingscythe", EntityThrowingScythe.class, ObjectManager.getItem("throwingscythe"), new DispenserBehaviorThrowingScythe());
		ObjectManager.addProjectile("mudshot", EntityMudshot.class, ObjectManager.getItem("mudshotcharge"), new DispenserBehaviorMudshot());
		ObjectManager.addProjectile("ember", EntityEmber.class, ObjectManager.getItem("embercharge"), new DispenserBehaviorEmber());
		ObjectManager.addProjectile("aquapulse", EntityAquaPulse.class, ObjectManager.getItem("aquapulsecharge"), new DispenserBehaviorAquaPulse());
		ObjectManager.addProjectile("whirlwind", EntityWhirlwind.class, ObjectManager.getItem("whirlwindcharge"), new DispenserBehaviorWhirlwind());
		ObjectManager.addProjectile("chaosorb", EntityChaosOrb.class, ObjectManager.getItem("chaosorbcharge"), new DispenserBehaviorAetherwave(), true);
		ObjectManager.addProjectile("acidsplash", EntityAcidSplash.class, ObjectManager.getItem("acidsplashcharge"), new DispenserBehaviorAcidSplash(), true);
		ObjectManager.addProjectile("lightball", EntityLightBall.class, ObjectManager.getItem("lightball"), new DispenserBehaviorLightBall());
		ObjectManager.addProjectile("lifedrain", EntityLifeDrain.class, ObjectManager.getItem("lifedraincharge"), new DispenserBehaviorLifeDrain());
		ObjectManager.addProjectile("lifedrainend", EntityLifeDrainEnd.class, false);
		ObjectManager.addProjectile("crystalshard", EntityCrystalShard.class, ObjectManager.getItem("crystalshard"), new DispenserBehaviorCrystalShard());
		ObjectManager.addProjectile("frostbolt", EntityFrostbolt.class, ObjectManager.getItem("frostboltcharge"), new DispenserBehaviorFrostbolt());
		ObjectManager.addProjectile("faebolt", EntityFaeBolt.class, ObjectManager.getItem("faeboltcharge"), new DispenserBehaviorFaebolt());
		ObjectManager.addProjectile("aetherwave", EntityAetherwave.class, ObjectManager.getItem("aetherwavecharge"), new DispenserBehaviorAetherwave());
		ObjectManager.addProjectile("waterjet", EntityWaterJet.class, ObjectManager.getItem("waterjetcharge"), new DispenserBehaviorWaterJet());
		ObjectManager.addProjectile("waterjetend", EntityWaterJetEnd.class, false);
		ObjectManager.addProjectile("magma", EntityMagma.class, ObjectManager.getItem("magmacharge"), new DispenserBehaviorMagma());
		ObjectManager.addProjectile("scorchfireball", EntityScorchfireball.class, ObjectManager.getItem("scorchfirecharge"), new DispenserBehaviorScorchfire());
		ObjectManager.addProjectile("poop", EntityPoop.class, ObjectManager.getItem("poopcharge"), new DispenserBehaviorPoop());
		ObjectManager.addProjectile("boulderblast", EntityBoulderBlast.class, ObjectManager.getItem("boulderblastcharge"), new DispenserBehaviorBoulderBlast());
		ObjectManager.addProjectile("arcanelaserstorm", EntityArcaneLaserStorm.class, ObjectManager.getItem("arcanelaserstormcharge"), new DispenserBehaviorArcaneLaserStorm());
		ObjectManager.addProjectile("arcanelaser", EntityArcaneLaser.class, false);
		ObjectManager.addProjectile("arcanelaserend", EntityArcaneLaserEnd.class, false);
		ObjectManager.addProjectile("quill", EntityQuill.class, ObjectManager.getItem("quill"), new DispenserBehaviorQuill());
		ObjectManager.addProjectile("spectralbolt", EntitySpectralbolt.class, ObjectManager.getItem("spectralboltcharge"), new DispenserBehaviorSpectralbolt());
		ObjectManager.addProjectile("bloodleech", EntityBloodleech.class, ObjectManager.getItem("bloodleechcharge"), new DispenserBehaviorBloodleech());
		ObjectManager.addProjectile("poisonray", EntityPoisonRay.class, Items.FERMENTED_SPIDER_EYE, new DispenserBehaviorPoisonRay());
		ObjectManager.addProjectile("poisonrayend", EntityPoisonRayEnd.class, false);
		ObjectManager.addProjectile("venomshot", EntityVenomShot.class, ObjectManager.getItem("poisongland"), new DispenserBehaviorVenomShot());
	}
}
