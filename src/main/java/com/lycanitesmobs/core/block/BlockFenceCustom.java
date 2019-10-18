package com.lycanitesmobs.core.block;

import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFenceCustom extends BlockFence {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFenceCustom(BlockBase block) {
		super(block.getDefaultState().getMaterial(), MapColor.STONE);
        this.setRegistryName(new ResourceLocation(block.group.modid, block.blockName + "_fence"));
        this.setUnlocalizedName(block.blockName + "_fence");
        block.copyAttributesTo(this);
        this.setSoundType(block.getSoundType());
	}


	// ==================================================
	//                      Info
	// ==================================================
	@Override
	public String getLocalizedName() {
		return LanguageManager.translate(this.getUnlocalizedName() + ".name");
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
		tooltip.add(this.getDescription(stack, world));
	}

	public String getDescription(ItemStack itemStack, @Nullable World world) {
		return LanguageManager.translate(this.getUnlocalizedName() + ".description");
	}
}
