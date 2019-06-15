package com.lycanitesmobs;

import com.lycanitesmobs.core.gui.*;
import com.lycanitesmobs.core.gui.beastiary.*;
import com.lycanitesmobs.core.tileentity.TileEntityBase;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.container.ContainerCreature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public static GuiHandler instance;
	
	// GUI IDs:
    public enum GuiType {
		TILEENTITY((byte)0), ENTITY((byte)1), ITEM((byte)2), BEASTIARY((byte)3), PLAYER((byte)4);
		public byte id;
		GuiType(byte i) { id = i; }
	}

	public enum Beastiary {
		INDEX((byte)0), CREATURES((byte)1), PETS((byte)2), SUMMONING((byte)3), ELEMENTS((byte)4);
		public byte id;
		Beastiary(byte i) { id = i; }
	}

	public enum PlayerGuiType {
		MINION_SELECTION((byte)0);
		public byte id;
		PlayerGuiType(byte i) { id = i; }
	}

    
    // ==================================================
    //                     Constructor
    // ==================================================
    public GuiHandler() {
    	instance = this;
    }
    
    
    // ==================================================
    //                      Server
    // ==================================================
	@Override
	public Object getServerGuiElement(int id, PlayerEntity player, World world, int x, int y, int z) {
		// ========== Tile Entity ==========
		if(id == GuiType.TILEENTITY.id) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if(tileEntity instanceof TileEntityBase)
                return ((TileEntityBase)tileEntity).getGUI(player);

		}
		
		// ========== Entity ==========
		else if(id == GuiType.ENTITY.id) {
			Entity entity = world.getEntityByID(x);
			if(entity instanceof EntityCreatureBase && player instanceof ServerPlayerEntity)
				return new ContainerCreature(((ServerPlayerEntity)player).getNextWindowId();, (EntityCreatureBase)entity, player.inventory);
		}
		
		// ========== Item ==========
		else if(id == GuiType.ITEM.id) {
			// No item GUIs just yet.
		}
		
		return null;
	}
    
    
    // ==================================================
    //                      Client
    // ==================================================
	@Override
	public Object getClientGuiElement(int id, PlayerEntity player, World world, int x, int y, int z) {
		// ========== Tile Entity ==========
		if(id == GuiType.TILEENTITY.id) {
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
            if(tileEntity instanceof TileEntityBase)
                return ((TileEntityBase)tileEntity).getGUI(player);
		}
		
		// ========== Entity ==========
		else if(id == GuiType.ENTITY.id) {
			Entity entity = world.getEntityByID(x);
			if(entity instanceof EntityCreatureBase)
				return new GuiCreature((EntityCreatureBase)entity, player.inventory);
		}
		
		// ========== Item ==========
		else if(id == GuiType.ITEM.id) {
			// No item GUIs just yet.
		}

		// ========== Beastiary ==========
		else if(id == GuiType.BEASTIARY.id) {
			if(x == Beastiary.INDEX.id) {
				return new GuiBeastiaryIndex(player);
			}
			if(x == Beastiary.CREATURES.id) {
				return new GuiBeastiaryCreatures(player);
			}
			if(x == Beastiary.PETS.id) {
				return new GuiBeastiaryPets(player);
			}
			if(x == Beastiary.SUMMONING.id) {
				return new GuiBeastiarySummoning(player);
			}
			if(x == Beastiary.ELEMENTS.id) {
				return new GuiBeastiaryElements(player);
			}
		}

		// ========== Player ==========
		else if(id == GuiType.PLAYER.id) {
			if(x == PlayerGuiType.MINION_SELECTION.id) {
				return new GuiMinionSelection(player);
			}
		}
		
		return null;
	}
	
}
