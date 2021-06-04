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

    /**
     https://github.com/reetamb/GatorLab/blob/main/src/main/java/com/reetam/gatorlab/datagen/provider/LabBlockProvider.java
     Resource for functioning block model generation
     */
    //TODO: FIX BLOCK DATAGEN

    private void dungeonBlockStates(String name) {
        fullBlock(name + "stone");
        fullBlock(name + "stonebrick");
        fullBlock(name + "stonetile");
        fullBlock(name + "stonepolished");
        fullBlock(name + "stonechiseled");
    }

    private void fullBlock(String name) {
        block(() -> ObjectManager.getBlock(name));
    }
}
