package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class Arisaur extends AgeableCreatureEntity implements IGroupHeavy {

    public Arisaur(EntityType<? extends Arisaur> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = false;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        this.fleeHealthPercent = 1.0F;
        this.isAggressiveByDefault = false;
        //this.solidCollision = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
		this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new TemptGoal(this).setIncludeDiet(true));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
    }

	@Override
	public float getBlockPathWeight(int x, int y, int z) {
		if(this.getCommandSenderWorld().getBlockState(new BlockPos(x, y - 1, z)).getBlock() != Blocks.AIR) {
			BlockState blocState = this.getCommandSenderWorld().getBlockState(new BlockPos(x, y - 1, z));
			if(blocState.getMaterial() == Material.GRASS)
				return 10F;
			if(blocState.getMaterial() == Material.DIRT)
				return 7F;
		}
        return super.getBlockPathWeight(x, y, z);
    }

    @Override
    public int getNoBagSize() { return 0; }

    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean canBeLeashed(Player player) {
	    return true;
    }

	@Override
	public ResourceLocation getTexture() {
		if(!this.hasCustomName() || !"Flowersaur".equals(this.getCustomName().getString()))
			return super.getTexture();

		String textureName = this.getTextureName() + "_flowersaur";
		if(TextureManager.getTexture(textureName) == null)
			TextureManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
		return TextureManager.getTexture(textureName);
	}
}
