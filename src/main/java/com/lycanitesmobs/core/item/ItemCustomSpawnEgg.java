package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCustomSpawnEgg extends ItemBase implements IItemColor {
	public CreatureInfo creatureInfo;
    
	// ==================================================
	//                    Constructor
	// ==================================================
    public ItemCustomSpawnEgg(Item.Properties properties, String name, CreatureInfo creatureInfo) {
        super(properties);

        this.itemName = name;
        this.creatureInfo = creatureInfo;
        this.setRegistryName(this.modInfo.modid, this.itemName);

        DispenserBlock.registerDispenseBehavior(this, new DispenserBehaviorMobEggCustom());

		LycanitesMobs.printDebug("Creature Type", "Created Creature Type Spawn Egg: " + this.itemName);
    }
    
	// ==================================================
	//                  Get Display Name
	// ==================================================
    @Override
    public ITextComponent getDisplayName(ItemStack itemStack) {
		String displayName = LanguageManager.translate("creaturetype.spawn") + " " + this.creatureInfo.creatureType.getTitle() + ": ";
		displayName += this.creatureInfo.getTitle();
        return new TranslationTextComponent(displayName);
    }
    
    
    // ==================================================
	//                      Info
	// ==================================================
    @Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag) {
        String description = this.getDescription(stack, worldIn, tooltip, flag);
        if(!"".equalsIgnoreCase(description) && !("item." + this.itemName + ".description").equals(description)) {
            FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
            List formattedDescriptionList = fontRenderer.listFormattedStringToWidth(description, ItemBase.descriptionWidth);
            for(Object formattedDescription : formattedDescriptionList) {
                if(formattedDescription instanceof String)
                    tooltip.add(new TranslationTextComponent((String)formattedDescription));
            }
        }
    }

    @Override
	public String getDescription(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        return this.creatureInfo.getDescription();
    }
    
    
	// ==================================================
	//                     Item Use
	// ==================================================
    @Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		PlayerEntity player = context.getPlayer();
		ItemStack itemStack = context.getItem();

        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (world.isRemote) {
            return ActionResultType.SUCCESS;
        }
        if (!player.canPlayerEdit(pos.offset(context.getFace()), context.getFace(), itemStack)) {
            return ActionResultType.FAIL;
        }
        
        // Edit Spawner:
        if(block == Blocks.SPAWNER) {
            TileEntity tileEntity = world.getTileEntity(pos);
			AbstractSpawner mobspawnerbaselogic = ((MobSpawnerTileEntity)tileEntity).getSpawnerBaseLogic();
            mobspawnerbaselogic.setEntityType(this.creatureInfo.getEntityType());
            tileEntity.markDirty();
            world.notifyBlockUpdate(pos, blockState, blockState, 3);
            if (!player.abilities.isCreativeMode) {
                itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
            }

            return ActionResultType.SUCCESS;
        }
        
        // Spawn Mob:
		pos = pos.offset(context.getFace());
		double d0 = 0.0D;
		if (context.getFace() == Direction.UP && blockState.getBlock() instanceof FenceBlock) {
			d0 = 0.5D;
		}

		LivingEntity entity = this.spawnCreature(world, itemStack, (double)pos.getX() + 0.5D, (double)pos.getY() + d0, (double)pos.getZ() + 0.5D);
		if(entity != null) {
			if(itemStack.hasDisplayName()) {
				entity.setCustomName(itemStack.getDisplayName());
			}
			if(!player.abilities.isCreativeMode) {
				itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
			}
		}

        return ActionResultType.SUCCESS;
    }
    
    
	// ==================================================
	//                   On Right Click
	// ==================================================
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if(world.isRemote)
            return new ActionResult(ActionResultType.PASS, itemStack);
        else {
            RayTraceResult rayTraceResult = this.rayTrace(world, player, RayTraceContext.FluidMode.ANY);

            if(rayTraceResult.getType() != RayTraceResult.Type.BLOCK)
                return new ActionResult(ActionResultType.PASS, itemStack);

            BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)rayTraceResult;
			BlockPos pos = blockRayTraceResult.getPos();

			if (!world.canMineBlockBody(player, pos)) {
				return new ActionResult(ActionResultType.FAIL, itemStack);
			}

			if (!player.canPlayerEdit(pos, blockRayTraceResult.getFace(), itemStack)) {
				return new ActionResult(ActionResultType.PASS, itemStack);
			}

			if (world.getBlockState(pos).getMaterial() == Material.WATER) {
				LivingEntity entity = spawnCreature(world, itemStack, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
				if (entity != null)
					if (itemStack.hasDisplayName()) {
						entity.setCustomName(itemStack.getDisplayName());
					}
				if (!player.abilities.isCreativeMode) {
					itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
				}
			}

			return new ActionResult(ActionResultType.SUCCESS, itemStack);
        }
    }
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    // ========== Use Colors ==========
    @Override
    public boolean useItemColors() {
        return true;
    }

    // ========== Get Color from ItemStack ==========
    @Override
    public int getColor(ItemStack itemStack, int tintIndex) {
		return this.creatureInfo != null ? (tintIndex == 0 ? this.creatureInfo.eggBackColor : this.creatureInfo.eggForeColor) : 16777215;
    }


	// ==================================================
	//                     Spawning
	// ==================================================
	/**
	 * Spawn Creature
	 * @param world The world to spawn in.
	 * @param itemStack The spawn egg itemstack to spawn from.
	 * @param x X spawn coordinate.
	 * @param y Y spawn coordinate.
	 * @param z Z spawn coordinate.
	 * @return The spawned entity instance.
	 */
	public LivingEntity spawnCreature(World world, ItemStack itemStack, double x, double y, double z) {
		LivingEntity entity = this.creatureInfo.createEntity(world);
		if(entity != null) {
			entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
			entity.rotationYawHead = entity.rotationYaw;
			entity.renderYawOffset = entity.rotationYaw;

			if(itemStack.hasDisplayName()) {
				entity.setCustomName(itemStack.getDisplayName());
			}

			if(entity instanceof MobEntity) {
				MobEntity mobEntity = (MobEntity)entity;
				mobEntity.onInitialSpawn(world, world.getDifficultyForLocation(new BlockPos(mobEntity)), SpawnReason.SPAWN_EGG, null, null);
				mobEntity.playAmbientSound();
			}

			world.addEntity(entity);
		}
		return entity;
	}
}