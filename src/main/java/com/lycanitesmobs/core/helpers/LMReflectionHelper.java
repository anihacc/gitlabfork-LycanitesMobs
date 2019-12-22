package com.lycanitesmobs.core.helpers;

import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class LMReflectionHelper {
	/**
	 * Removes final from a field.
	 * @param classToAccess
	 * @param instance
	 * @param fieldName
	 * @return
	 */
	public static <T> Field removeFinal(Class <? super T > classToAccess, T instance, String fieldName) {
    	Field field = ObfuscationReflectionHelper.findField(classToAccess, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, fieldName));
    	
    	try {
    		Field modifiersField = Field.class.getDeclaredField("modifiers");
    		modifiersField.setAccessible(true);
    		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return field;
	}
	
	
	/**
	 * Sets the value of a private final field.
	 * @param classToAccess
	 * @param instance
	 * @param value
	 * @param fieldName
	 */
    public static <T, E> void setPrivateFinalValue(Class <? super T > classToAccess, T instance, E value, String fieldName) {
		try {
			ObfuscationReflectionHelper.setPrivateValue(classToAccess, instance, value, fieldName);

    		Field field = ObfuscationReflectionHelper.findField(classToAccess, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, fieldName));
    		Field modifiersField = Field.class.getDeclaredField("modifiers");
    		modifiersField.setAccessible(true);
    		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

    		field.set(instance, value);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}
