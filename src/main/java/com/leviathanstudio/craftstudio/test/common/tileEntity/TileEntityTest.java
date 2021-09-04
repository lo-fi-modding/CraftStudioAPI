package com.leviathanstudio.craftstudio.test.common.tileEntity;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.simpleImpl.AnimatedTileEntity;
import com.leviathanstudio.craftstudio.test.common.ModTest;
import com.leviathanstudio.craftstudio.test.common.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityTest extends AnimatedTileEntity
{
    protected static AnimationHandler<TileEntityTest> animHandler = CraftStudioApi.getNewAnimationHandler(TileEntityTest.class);

    static {
        TileEntityTest.animHandler.addAnim(ModTest.MODID, "position", "craftstudio_api_test", true);
    }

    public TileEntityTest(BlockEntityType<?> tileEntityTypeIn, final BlockPos pos, final BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public TileEntityTest(final BlockPos pos, final BlockState state) {
    	super(RegistryHandler.tile_test, pos, state);
    }

    public TileEntityTest(Level worldIn, final BlockPos pos, final BlockState state) {
        super(RegistryHandler.tile_test, pos, state);
        this.level = worldIn;
    }

    @SuppressWarnings("unchecked")
	  @Override
    public AnimationHandler<TileEntityTest> getAnimationHandler() {
        return TileEntityTest.animHandler;
    }

    public static void tick(final Level level, final BlockPos pos, final BlockState state, final TileEntityTest te) {
        AnimatedTileEntity.tick(level, pos, state, te);

        if (te.isWorldRemote() && !te.getAnimationHandler().isAnimationActive(ModTest.MODID, "position", te))
            te.getAnimationHandler().startAnimation(ModTest.MODID, "position", te);

    }
}
