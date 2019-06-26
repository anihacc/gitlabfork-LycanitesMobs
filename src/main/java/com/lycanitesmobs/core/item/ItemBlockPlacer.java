package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockPlacer extends BaseItem {
	public String placedBlockName;

	public ItemBlockPlacer(Item.Properties properties, String itemName, String placedBlockName) {
		super(properties);
		this.modInfo = LycanitesMobs.modInfo;
		this.itemName = itemName;
		this.placedBlockName = placedBlockName;
		this.setup();
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		PlayerEntity player = context.getPlayer();
		ItemStack itemStack = context.getItem();

		if (world.isRemote) {
			return ActionResultType.SUCCESS;
		}
		else {
			pos = pos.offset(context.getFace());
			if(player.canPlayerEdit(pos, context.getFace(), itemStack)) {
				try {
					BlockState blockState = world.getBlockState(pos);
					if(blockState.isAir(world, pos)) {
						world.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, ObjectManager.getSound(this.placedBlockName), SoundCategory.PLAYERS, 1.0F, player.getRNG().nextFloat() * 0.4F + 0.8F, false);
						world.setBlockState(pos, ObjectManager.getBlock(this.placedBlockName).getDefaultState());
					}
					if(!player.abilities.isCreativeMode)
						itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
					return ActionResultType.SUCCESS;
				}
				catch(Exception e) {}
			}
		}
		return ActionResultType.FAIL;
	}
}
