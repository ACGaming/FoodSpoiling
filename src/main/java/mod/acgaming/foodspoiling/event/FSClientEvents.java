package mod.acgaming.foodspoiling.event;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import mod.acgaming.foodspoiling.FoodSpoiling;
import mod.acgaming.foodspoiling.config.FSConfig;
import mod.acgaming.foodspoiling.logic.FSLogic;

@Mod.EventBusSubscriber(modid = FoodSpoiling.MOD_ID, value = Side.CLIENT)
public class FSClientEvents
{
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        if (!FSConfig.TOOLTIPS.showFoodTooltip || event.getEntityPlayer() == null || (event.getEntityPlayer().isCreative() && !FSConfig.ROTTING.rotInCreative)) return;

        ItemStack stack = event.getItemStack();
        if (!FSLogic.canRot(stack)) return;

        NBTTagCompound tag = stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);
        if (!tag.hasKey(FSLogic.TAG_CREATION_TIME)) return;

        long creationTime = tag.getLong(FSLogic.TAG_CREATION_TIME);
        long currentTime = event.getEntityPlayer().world.getTotalWorldTime();
        int daysToRot = FSLogic.getDaysToRot(stack);
        int maxSpoilTicks = daysToRot * FSConfig.GENERAL.dayLengthInTicks;

        if (daysToRot < 0)
        {
            event.getToolTip().add(I18n.format("tooltip.foodspoiling.does_not_rot"));
        }
        else
        {
            long elapsedTime = currentTime - creationTime;
            String tooltip = "";
            int daysRemaining = (int) ((maxSpoilTicks - elapsedTime) / FSConfig.GENERAL.dayLengthInTicks);
            int percentageRemaining = 100 - (int) ((elapsedTime * 100) / maxSpoilTicks);
            if (FSConfig.TOOLTIPS.tooltipFoodDays)
            {
                tooltip = daysRemaining > 0 ? I18n.format("tooltip.foodspoiling.good_for_days", daysRemaining) : I18n.format("tooltip.foodspoiling.good_for_less_than_day");

                if (FSConfig.TOOLTIPS.tooltipFoodPercent)
                {
                    tooltip = tooltip + " (" + I18n.format("tooltip.foodspoiling.good_for_days_percentage", percentageRemaining) + ")";
                }
            }
            else if (FSConfig.TOOLTIPS.tooltipFoodPercent)
            {
                tooltip = I18n.format("tooltip.foodspoiling.good_for_days_percentage", percentageRemaining);
            }
            if (!tooltip.isEmpty())
            {
                event.getToolTip().add(tooltip);
            }
        }
    }
}
