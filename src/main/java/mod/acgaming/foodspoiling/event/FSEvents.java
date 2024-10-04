package mod.acgaming.foodspoiling.event;

import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import mod.acgaming.foodspoiling.FoodSpoiling;
import mod.acgaming.foodspoiling.config.FSConfig;
import mod.acgaming.foodspoiling.logic.FSLogic;

@Mod.EventBusSubscriber(modid = FoodSpoiling.MOD_ID)
public class FSEvents
{
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.PlayerTickEvent.Phase.END || event.player.world.getTotalWorldTime() % FSConfig.GENERAL.checkIntervalInTicks != 0) return;
        FSLogic.updateInventory(event.player);
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        FSLogic.updateInventory(event.getEntityPlayer());
    }
}
