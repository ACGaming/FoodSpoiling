package mod.acgaming.foodspoiling.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import mod.acgaming.foodspoiling.FoodSpoiling;
import mod.acgaming.foodspoiling.config.FSConfig;
import mod.acgaming.foodspoiling.logic.FSLogic;

public class FSPlayerEvents
{
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            if (player.server.isDedicatedServer())
            {
                FSLogic.saveInventory(player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.PlayerTickEvent.Phase.END || event.player.world.getTotalWorldTime() % FSConfig.GENERAL.checkIntervalInTicks != 0) return;
        FSLogic.updateInventory(event.player);
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        if (FSConfig.GENERAL.debugContainerClass)
        {
            FoodSpoiling.LOGGER.info("Inventory class name: {}", event.getContainer().getClass().getName());
        }
        FSLogic.updateInventory(event.getEntityPlayer());
    }
}
