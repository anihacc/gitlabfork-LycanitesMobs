package lycanite.lycanitesmobs.demonmobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupDemon;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.entity.EntityProjectileLaser;
import lycanite.lycanitesmobs.api.entity.ai.*;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityAsmodeus extends EntityCreatureBase implements IMob, IGroupDemon {

    public List<EntityPlayer> playerTargets = new ArrayList<EntityPlayer>();
    public List<EntityTrite> triteMinions = new ArrayList<EntityTrite>();
    public List<EntityAstaroth> astarothMinions = new ArrayList<EntityAstaroth>();
    public List<EntityCacodemon> cacodemonMinions = new ArrayList<EntityCacodemon>();

    // Second Phase:
    public int hellshieldAstarothRespawnTime = 0;
    public int hellshieldAstarothRespawnTimeMax = 15;

    // Third Phase:
    public int rebuildAstarothRespawnTime = 0;
    public int rebuildAstarothRespawnTimeMax = 15;

    public float damageTakenThisSec = 0;
    public float healthLastTick = -1;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAsmodeus(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.defense = 2;
        this.experience = 1000;
        this.hasAttackSound = false;
        this.justAttackedTime = 100;
        
        this.setWidth = 30F;
        this.setHeight = 42F;
        this.solidCollision = false;
        this.entityCollisionReduction = 1.0F;
        this.setupMob();
        this.hitAreaScale = 2F;

        // Boss:
        this.boss = true;
        this.damageMax = 25;
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackRanged(this).setSpeed(1.0D).setRate(5).setStaminaTime(200).setRange(100.0F).setChaseTime(0).setCheckSight(false));
        //this.tasks.addTask(6, new EntityAIWander(this).setSpeed(1.0D));
        this.tasks.addTask(7, new EntityAIStayByHome(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityTrite.class));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityAstaroth.class));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityCacodemon.class));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityNetherSoul.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 5000D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 1D);
		baseAttributes.put("followRange", 40D);
		baseAttributes.put("attackDamage", 18D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.REDSTONE), 1F).setMinAmount(20).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(Items.IRON_INGOT), 1F).setMinAmount(20).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(Items.GOLD_INGOT), 1F).setMinAmount(10).setMaxAmount(20));
        this.drops.add(new DropRate(new ItemStack(Items.DIAMOND), 1F).setMinAmount(10).setMaxAmount(20));
        this.drops.add(new DropRate(new ItemStack(Items.NETHER_STAR), 1F).setMinAmount(1).setMaxAmount(8));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("devilstarcharge")), 1F).setMinAmount(10).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("demoniclightningcharge")), 1F).setMinAmount(10).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("soulstonedemonic")), 1F).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("demonstone")), 1F).setMinAmount(64).setMaxAmount(128));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("demonstonebrick")), 1F).setMinAmount(64).setMaxAmount(128));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("demonstonetile")), 1F).setMinAmount(64).setMaxAmount(128));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("demoncrystal")), 1F).setMinAmount(64).setMaxAmount(128));
	}

    // ========== Rendering Distance ==========
    /** Returns a larger bounding box for rendering this large entity. **/
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getEntityBoundingBox().expand(10, 50, 10).offset(0, 25, 0);
    }


    // ==================================================
    //                      Updates
    // ==================================================
    // ========== Living Update ==========
    @Override
    public void onLivingUpdate() {
        // Enforce Damage Limit:
        if (this.healthLastTick < 0)
            this.healthLastTick = this.getHealth();
        if (this.healthLastTick - this.getHealth() > 50)
            this.setHealth(this.healthLastTick);
        this.healthLastTick = this.getHealth();
        if (!this.worldObj.isRemote && this.updateTick % 20 == 0) {
            this.damageTakenThisSec = 0;
        }

        super.onLivingUpdate();

        // Force Home Point:
        if(!this.worldObj.isRemote && this.hasHome()) {
            if(this.worldObj.isAirBlock(this.getHomePosition()))
                this.posY = this.getHomePosition().getY();

            double range = this.getHomeDistanceMax();

            if(this.getHomePosition().getX() - this.posX > range)
                this.posX = this.getHomePosition().getX() + range;
            else if(this.getHomePosition().getX() - this.posX < -range)
                this.posX = this.getHomePosition().getX() - range;

            if(this.getHomePosition().getZ() - this.posZ > range)
                this.posZ = this.getHomePosition().getZ() + range;
            else if(this.getHomePosition().getZ() - this.posZ < -range)
                this.posZ = this.getHomePosition().getZ() - range;
        }

        // Update Phases:
        if(!this.worldObj.isRemote)
            this.updatePhases();

        // Player Targets and No Player Healing:
        if(!this.worldObj.isRemote && this.updateTick % 200 == 0) {
            this.playerTargets = this.getNearbyEntities(EntityPlayer.class, 64);
        }
        if(!this.worldObj.isRemote && this.updateTick % 20 == 0) {
            if (this.playerTargets.size() == 0)
                this.heal(50);
        }

        // Passive Attacks:
        if(!this.worldObj.isRemote && this.updateTick % 20 == 0) {
            // Player Checks
            for(EntityPlayer target : this.playerTargets) {
                if(target.capabilities.isCreativeMode)
                    continue;
                this.rangedAttack(target, 1F);
                if(target.posY > this.posY + this.height + 5) {
                    for(int i = 0; i < 3; i++) {
                        EntityNetherSoul minion = new EntityNetherSoul(this.worldObj);
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                    }
                }
            }
        }
    }

    // ========== Phases Update ==========
    public void updatePhases() {
        int playerCount = Math.max(this.playerTargets.size(), 1);

        // ===== First Phase - Devilstar Stream =====
        if(this.getBattlePhase() == 0) {
            // Devilstars:
            this.attackHellLaser(20F);
            this.attackHellLaser(-20F);
            this.attackHellLaser(50F);
            this.attackHellLaser(-50F);
            this.attackHellLaser(90F);
            this.attackHellLaser(-90F);
            this.attackHellLaser(130F);
            this.attackHellLaser(-130F);
            this.attackHellLaser(160F);
            this.attackHellLaser(-160F);
            this.attackHellLaser(-180F);

            // Summon Trites:
            if(this.triteMinions.size() < playerCount * 20 && this.updateTick % 10 * 20 == 0) {
                for (int i = 0; i < 5 * playerCount; i++) {
                    EntityTrite minion = new EntityTrite(this.worldObj);
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 20);
                    this.triteMinions.add(minion);
                }
            }
        }


        // ===== Second Phase - Hellshield =====
        else if(this.getBattlePhase() == 1 && this.updateTick % 20 == 0) {
            // Summon Astaroth:
            if(this.astarothMinions.isEmpty() && this.hellshieldAstarothRespawnTime-- <= 0) {
                for (int i = 0; i < 2 * playerCount; i++) {
                    EntityAstaroth minion = new EntityAstaroth(this.worldObj);
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 10);
                    this.astarothMinions.add(minion);
                }
                this.hellshieldAstarothRespawnTime = this.hellshieldAstarothRespawnTimeMax;
            }
        }


        // ===== Third Phase - Rebuild =====
        else if(this.updateTick % 20 == 0) {
            if(this.astarothMinions.size() < playerCount * 6) {
                // Summon Astaroth:
                if (this.rebuildAstarothRespawnTime-- <= 0) {
                    for (int i = 0; i < playerCount * 2; i++) {
                        EntityAstaroth minion = new EntityAstaroth(this.worldObj);
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 10);
                        this.astarothMinions.add(minion);
                    }
                    this.rebuildAstarothRespawnTime = this.rebuildAstarothRespawnTimeMax;
                }
            }

            // Summon Cacodemon:
            if(this.cacodemonMinions.size() < playerCount * 6 && this.updateTick % 10 * 20 == 0) {
                for (int i = 0; i < 5 * playerCount; i++) {
                    EntityCacodemon minion = new EntityCacodemon(this.worldObj);
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 10);
                    minion.posY += 10 + this.getRNG().nextInt(20);
                    this.cacodemonMinions.add(minion);
                }
            }

            // Heal:
            if(!this.astarothMinions.isEmpty()) {
                float healAmount = this.astarothMinions.size() * Math.min(Math.max(this.worldObj.getDifficulty().getDifficultyId(), 1), 3);
                if (((this.getHealth() + healAmount) / this.getMaxHealth()) <= 0.2D)
                    this.heal(healAmount);
            }
        }
    }

    // ========== Minion Death ==========
    @Override
    public void onMinionDeath(EntityLivingBase minion) {
        if(minion instanceof EntityTrite && this.triteMinions.contains(minion)) {
            this.triteMinions.remove(minion);
            return;
        }
        if(minion instanceof EntityAstaroth && this.astarothMinions.contains(minion)) {
            this.astarothMinions.remove(minion);
            return;
        }
        if(minion instanceof EntityCacodemon && this.cacodemonMinions.contains(minion)) {
            this.cacodemonMinions.remove(minion);
            return;
        }
    }


    // ==================================================
    //                  Battle Phases
    // ==================================================
    @Override
    public void updateBattlePhase() {
        double healthNormal = this.getHealth() / this.getMaxHealth();
        if(healthNormal <= 0.2D) {
            this.setBattlePhase(2);
            return;
        }
        if(healthNormal <= 0.6D) {
            this.setBattlePhase(1);
            return;
        }
        this.setBattlePhase(0);
    }
    
    
	// ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(EntityTrite.class) || targetClass.isAssignableFrom(EntityCacodemon.class) ||  targetClass.isAssignableFrom(EntityAstaroth.class))
    		return false;
        return super.canAttackClass(targetClass);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityProjectileBase projectile = new EntityDemonicBlast(this.worldObj, this);
        projectile.setProjectileScale(4f);
    	
    	// Y Offset:
    	projectile.posY -= this.height * 0.35D;
    	
    	// Set Velocities:
        double d0 = target.posX - this.posX;
        double d1 = target.posY - (target.height * 0.25D) - projectile.posY;
        double d2 = target.posZ - this.posZ;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.1F;
        float velocity = 1.2F;
        projectile.setThrowableHeading(d0, d1 + (double) f1, d2, velocity, 0.0F);
        
        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(projectile);

        super.rangedAttack(target, range);
    }

    // ========== Devilstar Stream ==========
    private Map<Float, EntityProjectileLaser> lasers = new HashMap<Float, EntityProjectileLaser>();
    public void attackHellLaser(float angle) {
        EntityProjectileLaser laser;
        if(!lasers.containsKey(angle)) {
            laser = new EntityHellLaser(this.worldObj, this, 20, 10);
            laser.useEntityAttackTarget = false;
        }
        else
            laser = this.lasers.get(angle);

        // Update Laser:
        if(laser.isEntityAlive()) {
            laser.setTime(20);
            BlockPos targetPosition = this.getFacingPosition(this, angle, 100);
            laser.setTarget(targetPosition.getX(), targetPosition.getY() + Math.sin(this.updateTick * 4), targetPosition.getZ());
        }
    }
	
	
	// ==================================================
   	//                      Death
   	// ==================================================
	@Override
	public void onDeath(DamageSource damageSource) {
        if(!this.worldObj.isRemote && MobInfo.getFromName("trite").mobEnabled) {
            int j = 6 + this.rand.nextInt(20) + (worldObj.getDifficulty().getDifficultyId() * 4);
            for(int k = 0; k < j; ++k) {
                float f = ((float)(k % 2) - 0.5F) * this.width / 4.0F;
                float f1 = ((float)(k / 2) - 0.5F) * this.width / 4.0F;
                EntityTrite trite = new EntityTrite(this.worldObj);
                trite.setLocationAndAngles(this.posX + (double)f, this.posY + 0.5D, this.posZ + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
                trite.setMinion(true);
                trite.setSubspecies(this.getSubspeciesIndex(), true);
                this.worldObj.spawnEntityInWorld(trite);
                if(this.getAttackTarget() != null)
                	trite.setRevengeTarget(this.getAttackTarget());
            }
        }
        super.onDeath(damageSource);
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.WITHER) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }

    // ========== Blocking ==========
    @Override
    public boolean isBlocking() {
        if(this.worldObj.isRemote)
            return super.isBlocking();
        return this.getBattlePhase() == 1 && !this.astarothMinions.isEmpty();
    }

    @Override
    public int getBlockingMultiplier() {
        return 1000;
    }
}
