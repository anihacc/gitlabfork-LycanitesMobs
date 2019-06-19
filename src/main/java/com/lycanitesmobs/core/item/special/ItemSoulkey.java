package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.AltarInfo;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemSoulkey extends ItemBase {
    public int rank = 0; // 0 = Standard, 1 = Diamond, 2 = Emerald

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulkey(Item.Properties properties, String itemName, int rank) {
        super(properties);
        this.itemName = itemName;
        this.rank = rank;
        this.setup();
    }
    
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getItem();

        if(!AltarInfo.checkAltarsEnabled() && !player.getEntityWorld().isRemote) {
            String message = LanguageManager.translate("message.soulkey.disabled");
            player.sendMessage(new TranslationTextComponent(message));
            return ActionResultType.FAIL;
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
            String message = LanguageManager.translate("message.soulkey.none");
            player.sendMessage(new TranslationTextComponent(message));
            return ActionResultType.FAIL;
        }

        // Activate First Valid Altar:
        for(AltarInfo altarInfo : possibleAltars) {
            if(altarInfo.fullCheck(player, world, pos)) {

                // Valid Altar:
                if(!player.getEntityWorld().isRemote) {
                    if (!player.abilities.isCreativeMode)
                        itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
                    if (itemStack.getCount() <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                    if(!altarInfo.activate(player, world, pos, this.rank + 1)) {
                        String message = LanguageManager.translate("message.soulkey.badlocation");
                        player.sendMessage(new TranslationTextComponent(message));
                        return ActionResultType.FAIL;
                    }
                    String message = LanguageManager.translate("message.soulkey.active");
                    player.sendMessage(new TranslationTextComponent(message));
                }
                return ActionResultType.SUCCESS;
            }
        }
        String message = LanguageManager.translate("message.soulkey.invalid");
        player.sendMessage(new TranslationTextComponent(message));

        return ActionResultType.FAIL;
    }
}
