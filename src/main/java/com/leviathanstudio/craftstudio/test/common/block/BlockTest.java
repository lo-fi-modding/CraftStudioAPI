package com.leviathanstudio.craftstudio.test.common.block;

import com.leviathanstudio.craftstudio.test.common.tileEntity.TileEntityTest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

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
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityTest();
	}




}
