package com.leviathanstudio.craftstudio.test.common.block;

import com.leviathanstudio.craftstudio.test.common.tileEntity.TileEntityTest;
import net.minecraft.block.BlockRenderType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockTest extends Block
{

    public BlockTest(Block.Properties props) {
        super(props);

    }



    @Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}



	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}


	@Override
	public BlockEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityTest();
	}




}
