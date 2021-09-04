package com.leviathanstudio.craftstudio.test.common.block;

import com.leviathanstudio.craftstudio.test.common.RegistryHandler;
import com.leviathanstudio.craftstudio.test.common.tileEntity.TileEntityTest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockTest extends BaseEntityBlock
{

    public BlockTest(final Block.Properties props) {
        super(props);
    }

    @Override
    public RenderShape getRenderShape(final BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new TileEntityTest(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final Level level, final BlockState state, final BlockEntityType<T> type) {
        return createTicker(level, type, RegistryHandler.tile_test);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(final Level level, final BlockEntityType<T> type, final BlockEntityType<? extends TileEntityTest> expectedType) {
        return level.isClientSide ? null : createTickerHelper(type, expectedType, TileEntityTest::tick);
    }

}
