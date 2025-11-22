package com.Myfic.banner.MyConfig;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;

public class config {


    public static final config Config;//配置实例，用于控制配置项
    public static final ModConfigSpec ModConfigSpec ;//配置装载器


    public static ModConfigSpec.ConfigValue<List<String>> ITEMS ;
    public static ModConfigSpec.ConfigValue<Integer> FULLBODYEXCEPTION;
    public static ModConfigSpec.ConfigValue<Integer> CONTAINERINSPEXCEPTION;
    public static ModConfigSpec.ConfigValue<Boolean> CONTAINERINSPECTIONSTATE;
    public static ModConfigSpec.ConfigValue<Boolean> FULLBODYINSPECTIONSTATE;
    public static ModConfigSpec.ConfigValue<List<String>> ENCHANT;

    private config(ModConfigSpec.Builder builder){
        builder.push("items_group");
        ITEMS = builder.comment("There push items").defineList("items", Collections.EMPTY_LIST, (obj)->obj instanceof String);
        FULLBODYEXCEPTION = builder.comment("set FullBody Check ticks").defineInRange("FullBodyCheckTicks", 50, 1, Integer.MAX_VALUE);
        CONTAINERINSPEXCEPTION = builder.comment("set Container Inventory Check ticks").defineInRange("ContainerCheckTicks", 30, 1, Integer.MAX_VALUE);
        CONTAINERINSPECTIONSTATE = builder.comment("set Container Inventory Check State, true start,false close").define("ContainerInspectionState", true);
        FULLBODYINSPECTIONSTATE = builder.comment("set FullBody Check State, true start,false close").define("FullBodyInspectionState", true);
        builder.pop();

    }

    static {
        Pair<config, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(config::new);
        Config = pair.getLeft();
        ModConfigSpec = pair.getRight();
    }

}
