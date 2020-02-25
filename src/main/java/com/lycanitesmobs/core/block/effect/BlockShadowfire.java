package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.PotionBase;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockFireBase;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.info.ElementManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockShadowfire extends BlockFireBase {
    public boolean blindness;
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockShadowfire() {
		super(Material.FIRE, LycanitesMobs.modInfo, "shadowfire");
		
		// Stats:
		this.tickRate = 30;
        this.dieInRain = false;
        this.triggerTNT = false;
        this.agingRate = 3;
        this.spreadChance = 0;
        this.removeOnTick = !ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Shadowfire", true);
        this.removeOnNoFireTick = ConfigBase.getConfig(this.group, "general").getBool("Features", "Remove Shadowfire on No Fire Tick", false);
		this.blindness = !ConfigBase.getConfig(this.group, "general").getBool("Features", "Shadowfire Blindness", true);
	}


	// ==================================================
	//                       Fire
	// ==================================================
    protected boolean canNeighborCatchFire(World worldIn, BlockPos pos) {
        return false;
    }

    protected int getNeighborEncouragement(World worldIn, BlockPos pos) {
        return 0;
    }

    public boolean canCatchFire(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }

    public boolean isBlockFireSource(Block block, World world, BlockPos pos, EnumFacing side) {
        return block == Blocks.OBSIDIAN;
    }

    protected boolean canDie(World world, BlockPos pos) {
        return false;
    }
    

	// ==================================================
	//                       Break
	// ==================================================
	@Override
	public Item getItemDropped(IBlockState state, Random random, int zero) {
		return ObjectManager.getItem("spectralboltcharge");
	}
    
    
	// ==================================================
    //                Collision Effects
    // ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollidedWithBlock(world, pos, state, entity);

        if(entity instanceof EntityItem)
            return;

        if(entity instanceof EntityLivingBase) {
			EntityLivingBase entityLiving = (EntityLivingBase)entity;

			PotionBase decay = ObjectManager.getEffect("decay");
			if(decay != null) {
				PotionEffect effectDecay = new PotionEffect(decay, 5 * 20, 0);
				if(entityLiving.isPotionApplicable(effectDecay))
					entityLiving.addPotionEffect(effectDecay);
			}

			PotionEffect effectBlindness = new PotionEffect(MobEffects.BLINDNESS, 5 * 20, 0);
            if(this.blindness && entityLiving.isPotionApplicable(effectBlindness)) {
				entityLiving.addPotionEffect(effectBlindness);
			}
        }

        if(entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).hasElement(ElementManager.getInstance().getElement("shadow")))
        	return;

        entity.attackEntityFrom(DamageSource.WITHER, 1);
    }
    
    
	// ==================================================
	//                      Particles
	// ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if(random.nextInt(24) == 0)
            world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("shadowfire"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

        for(int particleCount = 0; particleCount < 12; ++particleCount) {
            float particleX = (float)x + random.nextFloat();
            float particleY = (float)y + random.nextFloat() * 0.5F;
            float particleZ = (float)z + random.nextFloat();
            world.spawnParticle(EnumParticleTypes.SPELL_WITCH, (double)particleX, (double)particleY, (double)particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }
}