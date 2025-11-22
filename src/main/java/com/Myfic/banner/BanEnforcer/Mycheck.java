package com.Myfic.banner.BanEnforcer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import com.Myfic.banner.MyConfig.config;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;


public class Mycheck {

    public static Set<ResourceLocation> BAN_ITEMSCACHE = new HashSet<>();

    public static void ReloadCache(){
        Set<ResourceLocation> newcachers = new HashSet<>();
        config.ITEMS.get().forEach(item -> {
            try{

                ResourceLocation cachers = ResourceLocation.parse(item);
                if(BuiltInRegistries.ITEM.containsKey(cachers)) {
                    newcachers.add(cachers);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
        BAN_ITEMSCACHE = newcachers;
    }


    public static Boolean Check(ItemStack itemStack){
        if(itemStack.isEmpty()) return false;
        Item item = itemStack.getItem();
        ResourceLocation itemLocation = BuiltInRegistries.ITEM.getKey(item);
        return BAN_ITEMSCACHE.contains(itemLocation);
    }
}
