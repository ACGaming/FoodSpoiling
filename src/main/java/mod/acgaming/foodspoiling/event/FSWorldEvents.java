package mod.acgaming.foodspoiling.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import mod.acgaming.foodspoiling.config.FSConfig;
import mod.acgaming.foodspoiling.logic.FSLogic;

public class FSWorldEvents
{
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (FSConfig.ROTTING.rotInPlayerInvOnly || event.phase != TickEvent.WorldTickEvent.Phase.END || event.world.getTotalWorldTime() % FSConfig.GENERAL.checkIntervalInTicks != 0) return;
        for (Entity entity : event.world.loadedEntityList)
        {
            if (entity instanceof EntityItem)
            {
                EntityItem itemEntity = (EntityItem) entity;
                ItemStack stack = itemEntity.getItem();
                FSLogic.updateItemEntity(itemEntity, stack);
            }
        }
    }
}
