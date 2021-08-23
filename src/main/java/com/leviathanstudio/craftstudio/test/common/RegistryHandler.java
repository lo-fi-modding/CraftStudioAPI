package com.leviathanstudio.craftstudio.test.common;

import com.leviathanstudio.craftstudio.test.common.block.BlockTest;
import com.leviathanstudio.craftstudio.test.common.entity.EntityTest;
import com.leviathanstudio.craftstudio.test.common.entity.EntityTest2;
import com.leviathanstudio.craftstudio.test.common.entity.EntityTest3;
import com.leviathanstudio.craftstudio.test.common.entity.EntityTest4;
import com.leviathanstudio.craftstudio.test.common.item.ItemTest;
import com.leviathanstudio.craftstudio.test.common.tileEntity.TileEntityTest;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import net.minecraft.SharedConstants;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = ModTest.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegistryHandler
{
    public static final Block block_test = new BlockTest(Block.Properties.of(Material.STONE));
    public static final BlockEntityType<?> tile_test = register("tileTest", BlockEntityType.Builder.of(TileEntityTest::new, block_test));

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(block_test.setRegistryName("block_test"));
    }

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<BlockEntityType<?>> event) {
    	event.getRegistry().register(tile_test.setRegistryName("tile_test"));
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(block_test, (new Item.Properties())).setRegistryName("block_test"));
        event.getRegistry().register(new ItemTest(new Item.Properties()).setRegistryName("item_test"));
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
    	EntityType<EntityTest> entityTest = EntityType.Builder.of(EntityTest::new, MobCategory.MISC).build("entityTest");
    	EntityType<EntityTest2> entityTest2 = EntityType.Builder.of(EntityTest2::new, MobCategory.MISC).build("entityTest2");
    	EntityType<EntityTest3> entityTest3 = EntityType.Builder.of(EntityTest3::new, MobCategory.MISC).build("entityTest3");
    	EntityType<EntityTest4> entityTest4 = EntityType.Builder.of(EntityTest4::new, MobCategory.MISC).build("entityTest4");


    	event.getRegistry().register(entityTest.setRegistryName("test_1"));
    	event.getRegistry().register(entityTest2.setRegistryName("test_2"));
    	event.getRegistry().register(entityTest3.setRegistryName("test_3"));
    	event.getRegistry().register(entityTest4.setRegistryName("test_4"));

        ModTest.PROXY.registerEntityRender();

    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        Type<?> type = null;

        try {
            type = DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(1631)).getChoiceType(References.BLOCK_ENTITY, ModTest.MODID + ":" + id);
        }
        catch(IllegalArgumentException e) {
            if(SharedConstants.IS_RUNNING_IN_IDE) {
                throw e;
            }
        }

        BlockEntityType<T> tileEntityType = builder.build(type);

        return tileEntityType;
    }

}
