package com.lycanitesmobs.client;

import com.lycanitesmobs.client.gui.buttons.TabManager;
import com.lycanitesmobs.client.gui.overlays.MinionSelectionOverlay;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.beastiary.CreaturesBeastiaryScreen;
import com.lycanitesmobs.client.gui.beastiary.IndexBeastiaryScreen;
import com.lycanitesmobs.client.gui.beastiary.PetsBeastiaryScreen;
import com.lycanitesmobs.client.gui.beastiary.SummoningBeastiaryScreen;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.network.MessagePlayerAttack;
import com.lycanitesmobs.core.network.MessagePlayerControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyHandler {
	public static KeyHandler instance;
	public Minecraft mc;
	
	public boolean inventoryOpen = false;
	
	public KeyBinding dismount = new KeyBinding("key.mount.dismount", Keyboard.KEY_C, "Lycanites Mobs");
	public KeyBinding descend = new KeyBinding("key.mount.descend", Keyboard.KEY_LSHIFT, "Lycanites Mobs");
	public KeyBinding mountAbility = new KeyBinding("key.mount.ability", Keyboard.KEY_X, "Lycanites Mobs");
	public KeyBinding mountInventory = new KeyBinding("key.mount.inventory", Keyboard.KEY_NONE, "Lycanites Mobs");

	public KeyBinding beastiary = new KeyBinding("key.beastiary", Keyboard.KEY_NONE, "Lycanites Mobs");
	public KeyBinding index = new KeyBinding("key.index", Keyboard.KEY_G, "Lycanites Mobs");
	public KeyBinding pets = new KeyBinding("key.pets", Keyboard.KEY_NONE, "Lycanites Mobs");
	public KeyBinding summoning = new KeyBinding("key.summoning", Keyboard.KEY_NONE, "Lycanites Mobs");
	public KeyBinding minionSelection = new KeyBinding("key.minions", Keyboard.KEY_R, "Lycanites Mobs");
	
	// ==================================================
    //                     Constructor
    // ==================================================
	public KeyHandler(Minecraft mc) {
		this.mc = mc;
		instance = this;
		
		// Register Keys:
		ClientRegistry.registerKeyBinding(this.dismount);
		ClientRegistry.registerKeyBinding(this.descend);
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

		// Right (Fix Hold Releasing):
		if (Mouse.isButtonDown(1)) {
			controlStates += ExtendedPlayer.CONTROL_ID.RIGHT_MOUSE.id;
		}
		
		// ========== GUI Keys ==========
		// Player Inventory: Adds extra buttons to the GUI.
		if(!this.inventoryOpen && mc.currentScreen != null && mc.currentScreen.getClass() == GuiInventory.class) {
			TabManager.addTabsToInventory(mc.currentScreen);
			this.inventoryOpen = true;
		}
		if(this.inventoryOpen && (mc.currentScreen == null || mc.currentScreen.getClass() != GuiInventory.class)) {
			this.inventoryOpen = false;
		}
		
		// Mount Inventory: Adds to control states.
		if(this.mountInventory.isPressed()) {
			controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_INVENTORY.id;
		}

		// LM Main Menu:
		if(this.index.isPressed()) {
			IndexBeastiaryScreen.openToPlayer(this.mc.player);
		}

		// Beastiary:
		if(this.beastiary.isPressed()) {
			CreaturesBeastiaryScreen.openToPlayer(this.mc.player);
		}

		// Pet Manager:
		if(this.pets.isPressed()) {
			PetsBeastiaryScreen.openToPlayer(this.mc.player);
		}

		// Minion Manager:
		if(this.summoning.isPressed()) {
			SummoningBeastiaryScreen.openToPlayer(this.mc.player);
		}
		
		// Minion Selection: Closes If Not Holding:
		try {
			if ((!Keyboard.isKeyDown(this.minionSelection.getKeyCode()) && !Mouse.isButtonDown(this.minionSelection.getKeyCode())) && this.mc.currentScreen instanceof MinionSelectionOverlay) {
				this.mc.player.closeScreen();
			}
		}
		catch(Exception e) {}
		
		
		if(this.mc.inGameHasFocus) {
			// ========== HUD Controls ==========
			// Minion Selection:
			if(this.minionSelection.isPressed()) {
				MinionSelectionOverlay.openToPlayer(this.mc.player);
			}
			
			
			// ========== Action Controls ==========
			// Vanilla Jump: Adds to control states.
			if(this.mc.gameSettings.keyBindJump.isKeyDown())
				controlStates += ExtendedPlayer.CONTROL_ID.JUMP.id;

			// Descend: Adds to control states.
			if(this.descend.isKeyDown())
				controlStates += ExtendedPlayer.CONTROL_ID.DESCEND.id;
			
			// Mount Ability: Adds to control states.
			if(this.dismount.isKeyDown())
				controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_DISMOUNT.id;

			// Mount Ability: Adds to control states.
			if(this.mountAbility.isKeyDown())
				controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_ABILITY.id;

            // Attack Key Pressed:
            if(Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown()) {
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
    //                   Item Use Events
    // ==================================================
    /** Player 'mouse' events, these are actually events based on attack or item use actions and are still triggered if the key binding is no longer a mouse click. **/
    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.mc.player);
        if(this.mc.player == null || playerExt == null || this.mc.objectMouseOver == null)
            return;

        // Left (Attack):
        if(event.getButton() == 0) {
            // Disable attack for large entity reach override:
            if(!this.mc.player.isSpectator() && !this.mc.player.isRowingBoat() && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                Entity entityHit = this.mc.objectMouseOver.entityHit;
                if(playerExt.canMeleeBigEntity(entityHit)) {
                    MessagePlayerAttack message = new MessagePlayerAttack(entityHit);
                    LycanitesMobs.packetHandler.sendToServer(message);
                    //event.setCanceled(true);
                }
            }
        }
    }
}
