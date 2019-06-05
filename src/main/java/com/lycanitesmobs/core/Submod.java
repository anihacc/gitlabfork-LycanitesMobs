package com.lycanitesmobs.core;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.equipment.EquipmentPartManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

public abstract class Submod {

	/** The primary group info used by this mod. **/
	public ModInfo group;


	public void preInit(FMLPreInitializationEvent event) {
		this.initialSetup();
		ObjectManager.setCurrentGroup(this.group);
		this.createBlocks();
		this.createItems();
		this.createEntities();
		try {
			CreatureManager.getInstance().loadCreaturesFromJSON(this.group);
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "No Creatures loaded for: " + this.group.name);
		}
		try {
			EquipmentPartManager.getInstance().loadAllFromJSON(this.group);
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "No Equipment Parts loaded for: " + this.group.name);
		}
		this.registerModels();
	}

	public void init(FMLInitializationEvent event) {
		ObjectManager.setCurrentGroup(this.group);
		CreatureManager.getInstance().registerAll(this.group);
		this.registerOres();
		this.addRecipes();
	}

	public void postInit(FMLPostInitializationEvent event) {
		ObjectManager.setCurrentGroup(this.group);
		if(CreatureManager.getInstance().spawnConfig.controlVanillaSpawns) {
			this.editVanillaSpawns();
		}
	}


	/** Everything here is loaded before anything else. The GroupInfo should be established here, etc. **/
	public abstract void initialSetup();

	/** All items belonging to this submod are created here. **/
	public abstract void createItems();

	/** All blocks belonging to this submod are created here. **/
	public abstract void createBlocks();

	/** All entities belonging to this submod are created here. **/
	public abstract void createEntities();

	/** Registers entities, must be called by submods themselves. **/
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		ObjectManager.registerEntities(event, this.group);
	}

	/** Registers all models. **/
	public abstract void registerModels();

	/** Registers ores. **/
	public abstract void registerOres();

	/** Adds smelting recipes, etc. **/
	public abstract void addRecipes();

	/** Edit vanilla spawns **/
	public abstract void editVanillaSpawns();
}
