package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.item.ItemStack;

public class SlotEquipmentFeature extends EquipmentFeature {
	/** The type of slot that this adds to the part. Can be: head, blade, axe, pike or jewel. Shouldn't be base else you can have infinitely large weapons! **/
	public String slotType;

	/** Loads this slot from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);
		this.slotType = json.get("slotType").getAsString();
	}

	@Override
	public String getDescription(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		return LanguageManager.translate("equipment.feature." + this.featureType) + " " + LanguageManager.translate("equipment.slot." + this.slotType);
	}

	@Override
	public String getSummary(ItemStack itemStack, int level) {
		return this.getDescription(itemStack, level);
	}
}
