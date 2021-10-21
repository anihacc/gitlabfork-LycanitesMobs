package com.lycanitesmobs.core.block;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import net.minecraftforge.fluids.ForgeFlowingFluid.Properties;

abstract public class CustomFluid extends ForgeFlowingFluid {

	public CustomFluid(Properties properties, String name) {
		super(properties);
		this.setRegistryName(name);
	}

	public static class Flowing extends CustomFluid {
		public Flowing(Properties properties, String name) {
			super(properties, name);
		}

		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> fluidStateDefinitionBuilder) {
			super.createFluidStateDefinition(fluidStateDefinitionBuilder);
			fluidStateDefinitionBuilder.add(LEVEL);
		}

		public int getAmount(FluidState fluidState) {
			return fluidState.getValue(LEVEL);
		}

		public boolean isSource(FluidState fluidState) {
			return false;
		}
	}

	public static class Still extends CustomFluid {
		public Still(Properties properties, String name) {
			super(properties, name);
		}

		public int getAmount(FluidState fluidState) {
			return 8;
		}

		public boolean isSource(FluidState fluidState) {
			return true;
		}
	}
}
