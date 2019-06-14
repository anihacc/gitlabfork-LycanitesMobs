package com.lycanitesmobs.core.config;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.ModInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class ConfigSpawning extends ConfigBase {
	
	// ========== Config Collections ==========
	// Get Config:
     public static ConfigSpawning getConfig(ModInfo group, String configName) {
		 String configFileName = configName.toLowerCase();
		 if(!"lycanitesmobs".equalsIgnoreCase(group.filename)) {
			 configFileName = group.filename + "-" + configFileName;
		 }
         if(!configs.containsKey(configFileName))
             registerConfig(new ConfigSpawning(group, configName, configFileName));
         ConfigBase config = ConfigBase.configs.get(configFileName);
         if(config instanceof ConfigSpawning)
         	return (ConfigSpawning)config;
     	LycanitesMobs.printWarning("", "[Config] Tried to access the Base Config: " + configName + " as a Spawning Config from group: " + group.name + "!");
     	return null;
     }
    
    
	// ========================================
	//		       Spawn Dimensions
	// ========================================
	public class SpawnDimensionSet {
		public int[] dimensionIDs;
		public String[] dimensionTypes;
		
		public SpawnDimensionSet(int[] dimensionIDs, String[] dimensionTypes) {
			this.dimensionIDs = dimensionIDs;
			this.dimensionTypes = dimensionTypes;
		}
	}
	
	
	// ========================================
	//				 Constructor
	// ========================================
    public ConfigSpawning(ModInfo group, String name, String filename) {
        super(group, name, filename);
    }
	
	
	// ========================================
	//			 Get Dimensions List
	// ========================================
	// Still used by Lake Worldgen Features
	public SpawnDimensionSet getDimensions(String category, String key, String defaultValue) {
		return this.getDimensions(category, key, defaultValue, null);
	}
	
	public SpawnDimensionSet getDimensions(String category, String key, String defaultValue, String comment) {
		String dimensionEntries = this.getString(category, key, defaultValue);
        dimensionEntries = dimensionEntries.replace(" ", "");

        List<Integer> dimensionIDList = new ArrayList<>();
        List<String> dimensionTypeList = new ArrayList<>();
        for(String dimensionEntry : dimensionEntries.split(",")) {
            if(NumberUtils.isCreatable(dimensionEntry))
                dimensionIDList.add(Integer.parseInt(dimensionEntry.replace("+", "")));
            else
                dimensionTypeList.add(dimensionEntry.replace("+", ""));
        }
        
		int[] dimensionIDs = ArrayUtils.toPrimitive(dimensionIDList.toArray(new Integer[dimensionIDList.size()]));
		String[] dimensionTypes = dimensionTypeList.toArray(new String[dimensionTypeList.size()]);
		return new SpawnDimensionSet(dimensionIDs, dimensionTypes);
	}
}