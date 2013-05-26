package com.jayserp.minesia;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HeadSet{

        ItemStack headSet(String whosHead){
                ItemStack tempHead;//create a CraftItemStack
                tempHead = new ItemStack(Material.SKULL_ITEM,1,(short)3);//set the CraftItemStack "head" to the Skull item
                SkullMeta meta = (SkullMeta) tempHead.getItemMeta();
                meta.setOwner(whosHead);
                tempHead.setItemMeta(meta);
                return tempHead;
        }
}
