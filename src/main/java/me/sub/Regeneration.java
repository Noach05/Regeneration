package me.sub;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.sub.common.capability.CapabilityRegeneration;
import me.sub.common.command.CommandDebug;
import me.sub.network.NetworkHandler;
import me.sub.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Regeneration.MODID, name = Regeneration.NAME, version = Regeneration.VERSION)
public class Regeneration {
    public static final String MODID = "regeneration";
    public static final String NAME = "Regeneration";
    public static final String VERSION = "1.0";

    @Mod.Instance(MODID)
    public static Regeneration INSTANCE;

    @SidedProxy(clientSide = "me.sub.proxy.ClientProxy", serverSide = "me.sub.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static Logger LOG = LogManager.getLogger(NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
        CapabilityRegeneration.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        NetworkHandler.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit();
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandDebug());
    }
}