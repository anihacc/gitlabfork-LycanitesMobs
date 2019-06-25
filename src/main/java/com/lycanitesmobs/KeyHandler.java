package com.lycanitesmobs;

import com.lycanitesmobs.core.gui.MinionSelectionOverlay;
import com.lycanitesmobs.core.gui.buttons.TabManager;
import com.lycanitesmobs.core.gui.beastiary.CreaturesBeastiaryScreen;
import com.lycanitesmobs.core.gui.beastiary.IndexBeastiaryScreen;
import com.lycanitesmobs.core.gui.beastiary.PetsBeastiaryScreen;
import com.lycanitesmobs.core.gui.beastiary.SummoningBeastiaryScreen;
import com.lycanitesmobs.core.network.MessagePlayerAttack;
import com.lycanitesmobs.core.network.MessagePlayerControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
	public static KeyHandler instance;
	public Minecraft mc;
	
	public boolean inventoryOpen = false;
	
	public KeyBinding mountAbility = new KeyBinding("key.mount.ability", GLFW.GLFW_KEY_X, "Lycanites Mobs");
	public KeyBinding mountInventory = new KeyBinding("key.mount.inventory", GLFW.GLFW_KEY_UNKNOWN, "Lycanites Mobs");

	public KeyBinding beastiary = new KeyBinding("key.beastiary", GLFW.GLFW_KEY_UNKNOWN, "Lycanites Mobs");
	public KeyBinding index = new KeyBinding("key.index", GLFW.GLFW_KEY_G, "Lycanites Mobs");
	public KeyBinding pets = new KeyBinding("key.pets", GLFW.GLFW_KEY_UNKNOWN, "Lycanites Mobs");
	public KeyBinding summoning = new KeyBinding("key.summoning", GLFW.GLFW_KEY_UNKNOWN, "Lycanites Mobs");
	public KeyBinding minionSelection = new KeyBinding("key.minions", GLFW.GLFW_KEY_R, "Lycanites Mobs");
	
	// ==================================================
    //                     Constructor
    // ==================================================
	public KeyHandler(Minecraft mc) {
		this.mc = mc;
		instance = this;
		
		// Register Keys:
		ClientRegistry.registerKeyBinding(this.mountAbility);
		ClientRegistry.registerKeyBinding(this.mountInventory);
		ClientRegistry.registerKeyBinding(this.index);
		ClientRegistry.registerKeyBinding(this.beastiary);
		ClientRegistry.registerKeyBinding(this.pets);
		ClientRegistry.registerKeyBinding(this.summoning);
		ClientRegistry.registerKeyBinding(this.minionSelection);
	}
	
	
	// ==================================================
    //                    Handle Keys
    // ==================================================
	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent event) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.mc.player);
		if(playerExt == null)
			return;
		byte controlStates = 0;
		
		// ========== GUI Keys ==========
		// Player Inventory: Adds extra buttons to the GUI.
		if(!this.inventoryOpen && mc.currentScreen != null && mc.currentScreen.getClass() == InventoryScreen.class) {
			TabManager.addTabsToInventory(mc.currentScreen);
			this.inventoryOpen = true;
		}
		if(this.inventoryOpen && (mc.currentScreen == null || mc.currentScreen.getClass() != InventoryScreen.class)) {
			this.inventoryOpen = false;
		}
		
		// Mount Inventory: Adds to control states.
		if(this.mountInventory.isPressed()) {
			controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_INVENTORY.id;
		}

		// Beastiary Index:
		if(this.index.isPressed()) {
			this.mc.displayGuiScreen(new IndexBeastiaryScreen(this.mc.player));
		}

		// Beastiary Creatures:
		if(this.beastiary.isPressed()) {
			this.mc.displayGuiScreen(new CreaturesBeastiaryScreen(this.mc.player));
		}

		// Beastiary Pets:
		if(this.pets.isPressed()) {
			this.mc.displayGuiScreen(new PetsBeastiaryScreen(this.mc.player));
		}

		// Beastiary Summoning:
		if(this.summoning.isPressed()) {
			this.mc.displayGuiScreen(new SummoningBeastiaryScreen(this.mc.player));
		}
		
		// Minion Selection: Closes If Not Holding:
		try {
			if (!this.minionSelection.isPressed() && this.mc.currentScreen instanceof MinionSelectionOverlay) {
				this.mc.player.closeScreen();
			}
		}
		catch(Exception e) {}
		
		
		if(this.mc.isGameFocused()) {
			// ========== HUD Controls ==========
			// Minion Selection:
			if(this.minionSelection.isPressed()) {
				this.mc.displayGuiScreen(new MinionSelectionOverlay(this.mc.player));
			}
			
			
			// ========== Action Controls ==========
			// Vanilla Jump: Adds to control states.
			if(this.mc.gameSettings.keyBindJump.isKeyDown())
				controlStates += ExtendedPlayer.CONTROL_ID.JUMP.id;
			
			// Mount Ability: Adds to control states.
			if(this.mountAbility.isKeyDown())
				controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_ABILITY.id;

            // Attack Key Pressed:
            if(Minecraft.getInstance().gameSettings.keyBindAttack.isKeyDown()) {
                controlStates += ExtendedPlayer.CONTROL_ID.ATTACK.id;
            }
		}
		
		
		// ========== Sync Controls To Server ==========
		if(controlStates == playerExt.controlStates)
			return;
		MessagePlayerControl message = new MessagePlayerControl(controlStates);
		LycanitesMobs.packetHandler.sendToServer(message);
		playerExt.controlStates = controlStates;
	}


    // ==================================================
    //                    Mouse Events
    // ==================================================
    /** Player 'mouse' events, these are actually events based on attack or item use actions and are still triggered if the key binding is no longer a mouse click. **/
    @SubscribeEvent
    public void onMouseEvent(InputEvent.MouseInputEvent event) {
        ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.mc.player);
        if(this.mc.player == null || playerExt == null || this.mc.objectMouseOver == null)
            return;

        // Left (Attack):
        if(event.getButton() == 0) {
            // Disable attack for large entity reach override:
            if(!this.mc.player.isSpectator() && !this.mc.player.isRowingBoat() && this.mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY) {
                Entity entityHit = ((EntityRayTraceResult)this.mc.objectMouseOver).getEntity();
                if(playerExt.canMeleeBigEntity(entityHit)) {
                    MessagePlayerAttack message = new MessagePlayerAttack(entityHit);
                    LycanitesMobs.packetHandler.sendToServer(message);
                    //event.setCanceled(true);
                }
            }
        }
    }
}
