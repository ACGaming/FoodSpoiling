package mod.acgaming.foodspoiling.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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

    @SubscribeEvent
    public static void onRegisterColorHandlerItems(ColorHandlerEvent.Item event)
    {
        ItemColors itemColors = event.getItemColors();

        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            if (!FSLogic.canRot(stack))
            {
                return 0xFFFFFF;
            }

            NBTTagCompound tag = stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);
            if (!tag.hasKey(FSLogic.TAG_CREATION_TIME))
            {
                return 0xFFFFFF;
            }

            long spoilTime = tag.getLong(FSLogic.TAG_CREATION_TIME);
            long currentTime = Minecraft.getMinecraft().world.getTotalWorldTime();
            int daysToRot = FSLogic.getDaysToRot(stack);
            int maxSpoilTicks = daysToRot * FSConfig.GENERAL.dayLengthInTicks;

            long elapsedTime = currentTime - spoilTime;
            float spoilPercentage = Math.min(1.0F, (float) elapsedTime / maxSpoilTicks);

            int startRed = 255;
            int startGreen = 255;
            int startBlue = 255;

            int targetRed = 136;
            int targetGreen = 204;
            int targetBlue = 51;

            int red = (int) (startRed + spoilPercentage * (targetRed - startRed));
            int green = (int) (startGreen + spoilPercentage * (targetGreen - startGreen));
            int blue = (int) (startBlue + spoilPercentage * (targetBlue - startBlue));

            return (red << 16) | (green << 8) | blue;

        }, ForgeRegistries.ITEMS.getValuesCollection().stream().filter(ItemFood.class::isInstance).toArray(Item[]::new));
    }
}
