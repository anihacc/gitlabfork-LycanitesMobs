package com.lycanitesmobs.core.datagen;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.logging.LogManager;

public class BlockModelsGenerator extends BlockModelsProvider {

    public BlockModelsGenerator(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator, fileHelper);
    }

    @Override
    public String getName() {
        return "Lycanites Mobs Block States and Models";
    }

    @Override
    protected void registerStatesAndModels() {
        dungeonBlockStates("aberrant");
    }

    private void dungeonBlockStates(String name) {
        fullBlock("aberrantstone");
//        fullBlock(name + "stonebrick");
//        fullBlock(name + "stonetile");
//        fullBlock(name + "stonepolished");
//        fullBlock(name + "stonechiseled");
    }

    private void fullBlock(String name) {
        block(() -> ObjectManager.getBlock(name));
    }
}
