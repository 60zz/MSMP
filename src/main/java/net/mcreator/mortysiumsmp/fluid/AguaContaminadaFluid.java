package net.mcreator.mortysiumsmp.fluid;

import net.minecraftforge.fluids.ForgeFlowingFluid;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleOptions;

import net.mcreator.mortysiumsmp.init.MortysiumsmpModItems;
import net.mcreator.mortysiumsmp.init.MortysiumsmpModFluids;
import net.mcreator.mortysiumsmp.init.MortysiumsmpModFluidTypes;
import net.mcreator.mortysiumsmp.init.MortysiumsmpModBlocks;

public abstract class AguaContaminadaFluid extends ForgeFlowingFluid {
	public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(() -> MortysiumsmpModFluidTypes.AGUA_CONTAMINADA_TYPE.get(), () -> MortysiumsmpModFluids.AGUA_CONTAMINADA.get(),
			() -> MortysiumsmpModFluids.FLOWING_AGUA_CONTAMINADA.get()).explosionResistance(100f).bucket(() -> MortysiumsmpModItems.AGUA_CONTAMINADA_BUCKET.get()).block(() -> (LiquidBlock) MortysiumsmpModBlocks.AGUA_CONTAMINADA.get());

	private AguaContaminadaFluid() {
		super(PROPERTIES);
	}

	@Override
	public ParticleOptions getDripParticle() {
		return ParticleTypes.DRIPPING_WATER;
	}

	public static class Source extends AguaContaminadaFluid {
		public int getAmount(FluidState state) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Flowing extends AguaContaminadaFluid {
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		public boolean isSource(FluidState state) {
			return false;
		}
	}
}