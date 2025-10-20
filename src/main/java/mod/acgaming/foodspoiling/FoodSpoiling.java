package mod.acgaming.foodspoiling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import mod.acgaming.foodspoiling.compat.toughasnails.TANDrinkHandler;
import mod.acgaming.foodspoiling.config.FSConfig;
import mod.acgaming.foodspoiling.event.FSClientEvents;
import mod.acgaming.foodspoiling.event.FSPlayerEvents;
import mod.acgaming.foodspoiling.event.FSWorldEvents;
import mod.acgaming.foodspoiling.logic.FSMaps;

@Mod(modid = FoodSpoiling.MOD_ID, name = FoodSpoiling.NAME, version = FoodSpoiling.VERSION, dependencies = "after:toughasnails")
public class FoodSpoiling
{
    public static final String MOD_ID = Tags.MOD_ID;
    public static final String NAME = Tags.NAME;
    public static final String VERSION = Tags.VERSION;
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(FSPlayerEvents.class);
        if (FSConfig.ROTTING.affectItemEntities) MinecraftForge.EVENT_BUS.register(FSWorldEvents.class);

        if (Loader.isModLoaded("toughasnails"))
        {
            LOGGER.info("Tough As Nails detected, enabling integration...");
            MinecraftForge.EVENT_BUS.register(new TANDrinkHandler());
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        FSMaps.initializeFoodMaps();
        FSMaps.initializeContainerConditions();
        if (FMLLaunchHandler.side().isClient())
        {
            FSClientEvents.registerColorHandlerItems();
        }
    }
}
