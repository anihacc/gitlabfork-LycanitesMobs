package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.AltarInfo;
import com.lycanitesmobs.core.item.BaseItem;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ItemSoulkey extends BaseItem {
    public int variant = 0; // 0 = Standard, 1 = Diamond, 2 = Emerald

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulkey(Item.Properties properties, String itemName, int variant) {
        super(properties);
        this.itemName = itemName;
        this.variant = variant;
        this.setup();
    }
    
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();

        if(!AltarInfo.checkAltarsEnabled() && !player.getCommandSenderWorld().isClientSide) {
            Component message = new TranslatableComponent("message.soulkey.disabled");
            player.sendMessage(message, Util.NIL_UUID);
            return InteractionResult.FAIL;
        }

        // Get Possible Altars:
        List<AltarInfo> possibleAltars = new ArrayList<>();
        if(AltarInfo.altars.isEmpty())
            LycanitesMobs.logWarning("", "No altars have been registered, Soulkeys will not work at all.");
        for(AltarInfo altarInfo : AltarInfo.altars.values()) {
            if(altarInfo.checkBlockEvent(player, world, pos) && altarInfo.quickCheck(player, world, pos)) {
                possibleAltars.add(altarInfo);
            }
        }
        if(possibleAltars.isEmpty()) {
            Component message = new TranslatableComponent("message.soulkey.none");
            player.sendMessage(message, Util.NIL_UUID);
            return InteractionResult.FAIL;
        }

        // Activate First Valid Altar:
        for(AltarInfo altarInfo : possibleAltars) {
            if(altarInfo.fullCheck(player, world, pos)) {

                // Valid Altar:
                if(!player.getCommandSenderWorld().isClientSide) {
                    if(!altarInfo.activate(player, world, pos, this.variant)) {
                        Component message = new TranslatableComponent("message.soulkey.badlocation");
                        player.sendMessage(message, Util.NIL_UUID);
                        return InteractionResult.FAIL;
                    }
                    if (!player.getAbilities().instabuild)
                        itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
                    if (itemStack.getCount() <= 0)
                        player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);
                    Component message = new TranslatableComponent("message.soulkey.active");
                    player.sendMessage(message, Util.NIL_UUID);
                }
                return InteractionResult.SUCCESS;
            }
        }
        if(!player.getCommandSenderWorld().isClientSide) {
            Component message = new TranslatableComponent("message.soulkey.invalid");
            player.sendMessage(message, Util.NIL_UUID);
        }

        return InteractionResult.FAIL;
    }
}
