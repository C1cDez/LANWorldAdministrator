package com.cicdez.lanwa;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = LanwaMod.MODID, name = LanwaMod.NAME, version = LanwaMod.VERSION)
public class LanwaMod {
    public static final String MODID = "lanwa";
    public static final String NAME = "LAN World Administrator";
    public static final String VERSION = "0.0.1";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("Initializing Mod");
    }
    
    @EventHandler
    public void serverInit(FMLServerStartingEvent event) {
        event.registerServerCommand(new LanwaCommand());
    }
}
