package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TabManager {
	public static ArrayList<GuiTab> tabList = new ArrayList<GuiTab>();

    public static void registerTab (GuiTab tab) {
        tabList.add(tab);
    }

    public static ArrayList<GuiTab> getTabList () {
        return tabList;
    }
    
    public static void addTabsToInventory (Screen gui) {
    	if(LycanitesMobs.config.getBool("GUI", "Show Inventory Tabs", true, "Set to false to disable the GUI tabs.") && gui.getClass() == InventoryScreen.class) {
        	GuiInventorySnooper guiInventorySnooper = new GuiInventorySnooper(mc.player);
        	try {
            	Field field = Screen.class.getDeclaredField(guiInventorySnooper.getButtonListFieldName());
	            field.setAccessible(true);
	            List buttonList = (List)field.get(gui);
	            addTabsToList(buttonList);
	            field.set(gui, buttonList);
			}
        	catch(Exception e) {
				LycanitesMobs.printWarning("", "A problem occured when adding custom inventory tabs:");
				e.printStackTrace();
			}
        }
    }

    private static Minecraft mc = Minecraft.getInstance();

    public static void openInventoryGui () {
		InventoryScreen inventory = new InventoryScreen(mc.player);
        mc.displayGuiScreen(inventory);
        TabManager.addTabsToInventory(inventory);
    }

    public static void updateTabValues (int cornerX, int cornerY, Class<?> selectedButton) {
        int count = 2;
        for(int i = 0; i < tabList.size(); i++) {
        	GuiTab t = tabList.get(i);

            if(t.shouldAddToList()) {
                t.buttonId = (byte)count;
                t.x = cornerX + (count - 2) * 28;
                t.y = cornerY - 28;
                t.active = !t.getClass().equals(selectedButton);
                count++;
            }
        }
    }

    public static void addTabsToList (List buttonList) {
        for(GuiTab tab : tabList) {
            if(tab.shouldAddToList()) {
                buttonList.add(tab);
            }
        }
    }
}
