package com.Myfic.banner;

import com.Myfic.banner.BanEnforcer.Mycheck;

import net.neoforged.fml.event.config.ModConfigEvent;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import com.Myfic.banner.MyConfig.config;




@Mod(Banner.MODID)
public class Banner {

    public static final String MODID = "banner";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Banner(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        //start
        modContainer.registerConfig(ModConfig.Type.COMMON, config.ModConfigSpec);
        modEventBus.addListener(this::onConfigReloading);

    }



    public void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == config.ModConfigSpec) {
            Mycheck.ReloadCache();
        }
    }
    private void commonSetup(FMLCommonSetupEvent event) {
        Mycheck.ReloadCache();
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
