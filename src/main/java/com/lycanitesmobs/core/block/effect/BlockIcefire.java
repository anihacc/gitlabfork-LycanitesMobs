package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.config.ConfigBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockIcefire extends BlockFrostfire {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockIcefire(Block.Properties properties) {
        super(properties);
        this.blockName = "icefire";

        // Stats:
        this.removeOnTick = !ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Icefire", true);
        this.removeOnNoFireTick = ConfigBase.getConfig(this.group, "general").getBool("Features", "Remove Frostfire on No Fire Tick", false);
	}


    // ==================================================
    //                       Break
    // ==================================================
   /*@Override
    public Item getItemDropped(BlockState state, Random random, int zero) {
        return ObjectManager.getItem("icefirecharge");
    }*/


	// ==================================================
	//                      Particles
	// ==================================================
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		if(random.nextInt(24) == 0)
			world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("icefire"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

		if (random.nextInt(100) == 0) {
			x = pos.getX() + random.nextFloat();
			z = pos.getZ() + random.nextFloat();
			world.addParticle(ParticleTypes.ITEM_SNOWBALL, x, y, z, 0.0D, 0.0D, 0.0D);
		}
		super.animateTick(state, world, pos, random);
	}
}