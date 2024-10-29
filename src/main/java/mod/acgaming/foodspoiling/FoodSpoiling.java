package mod.acgaming.foodspoiling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import mod.acgaming.foodspoiling.event.FSClientEvents;
import mod.acgaming.foodspoiling.logic.FSMaps;

@Mod(modid = FoodSpoiling.MOD_ID, name = FoodSpoiling.NAME, version = FoodSpoiling.VERSION, acceptedMinecraftVersions = FoodSpoiling.ACCEPTED_VERSIONS)
public class FoodSpoiling
{
    public static final String MOD_ID = Tags.MOD_ID;
    public static final String NAME = Tags.NAME;
    public static final String VERSION = Tags.VERSION;
    public static final String ACCEPTED_VERSIONS = "[1.12.2]";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

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
