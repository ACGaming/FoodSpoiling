package mod.acgaming.foodspoiling.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

import mod.acgaming.foodspoiling.FoodSpoiling;
import mod.acgaming.foodspoiling.config.FSConfig;
import mod.acgaming.foodspoiling.logic.FSData;
import mod.acgaming.foodspoiling.logic.FSLogic;
import mod.acgaming.foodspoiling.logic.FSMaps;

@Mod.EventBusSubscriber(modid = FoodSpoiling.MOD_ID, value = Side.CLIENT)
public class FSClientEvents
{
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        if (!FSConfig.TOOLTIPS.showFoodTooltip || event.getEntityPlayer() == null) return;

        Container container = event.getEntityPlayer().openContainer;
        String containerClass = container.getClass().getName();
        ItemStack stack = event.getItemStack();

        if (!FSLogic.canRot(stack)) return;

        long creationTime = FSData.hasCreationTime(stack) ? FSData.getCreationTime(stack) : event.getEntityPlayer().world.getTotalWorldTime();
        int maxSpoilTicks = FSLogic.getTicksToRot(event.getEntityPlayer(), stack);

        if (FSLogic.hasCustomContainerConditions(event.getEntityPlayer(), stack) && !FSConfig.ROTTING.rotInPlayerInvOnly)
        {
            event.getToolTip().add(I18n.format("tooltip.foodspoiling.stored_in_container"));
        }

        if (maxSpoilTicks < 0)
        {
            event.getToolTip().add(I18n.format("tooltip.foodspoiling.does_not_rot"));
        }
        else if (!FSData.hasRemainingLifetime(stack))
        {
            long elapsedTime = event.getEntityPlayer().world.getTotalWorldTime() - creationTime;
            int daysRemaining = (int) ((maxSpoilTicks - elapsedTime) / FSConfig.GENERAL.dayLengthInTicks);
            int percentageRemaining = Math.max(0, Math.min(100, 100 - (int) ((elapsedTime * 100) / maxSpoilTicks)));

            StringBuilder tooltipBuilder = new StringBuilder();

            if (FSConfig.TOOLTIPS.tooltipFoodDays)
            {
                if (daysRemaining > 0)
                {
                    tooltipBuilder.append(I18n.format("tooltip.foodspoiling.good_for_days", daysRemaining));
                }
                else
                {
                    tooltipBuilder.append(I18n.format("tooltip.foodspoiling.good_for_less_than_day"));
                }

                if (FSConfig.TOOLTIPS.tooltipFoodPercent)
                {
                    tooltipBuilder.append(" (").append(percentageRemaining).append("%)");
                }
            }
            else if (FSConfig.TOOLTIPS.tooltipFoodPercent)
            {
                tooltipBuilder.append(percentageRemaining).append("%");
            }

            if (tooltipBuilder.length() > 0)
            {
                event.getToolTip().add(tooltipBuilder.toString());
            }

            if (FSConfig.TOOLTIPS.tooltipFoodDays && FSLogic.hasCustomContainerConditions(event.getEntityPlayer(), stack))
            {
                double lifetimeFactor = FSLogic.getCustomContainerConditions(event.getEntityPlayer(), stack);
                if (lifetimeFactor > 0 && lifetimeFactor != 1)
                {
                    String bonusTooltip = I18n.format("tooltip.foodspoiling.lifetime_factor", lifetimeFactor);
                    if (FSConfig.TOOLTIPS.tooltipFoodPercent)
                    {
                        int percentageBonus = (int) ((lifetimeFactor - 1.0) * 100);
                        bonusTooltip += " (" + (percentageBonus >= 0 ? "+" : "") + percentageBonus + "%)";
                    }
                    event.getToolTip().add(bonusTooltip);
                }
            }
        }

        if (event.getFlags().isAdvanced())
        {
            if (FSData.hasID(stack))
            {
                event.getToolTip().add("§8" + "ID: " + FSData.getID(stack));
            }
            if (FSData.hasCreationTime(stack))
            {
                event.getToolTip().add("§8" + "CreationTime: " + creationTime);
            }
            if (FSData.hasRemainingLifetime(stack))
            {
                event.getToolTip().add("§8" + "RemainingLifetime: " + FSData.getRemainingLifetime(stack));
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterColorHandlerItems(ColorHandlerEvent.Item event)
    {
        if (!FSConfig.ROTTING.renderRottenState) return;

        ItemColors itemColors = event.getItemColors();

        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player == null || (player.isCreative() && !FSConfig.ROTTING.rotInCreative) || !FSLogic.canRot(stack))
            {
                return 0xFFFFFF;
            }

            long currentTime = Minecraft.getMinecraft().world.getTotalWorldTime();
            int maxSpoilTicks = FSLogic.getTicksToRot(player, stack);

            if (maxSpoilTicks < 0)
            {
                return FSMaps.FOOD_TINTS.getOrDefault(FSData.getID(stack), 0xFFFFFF);
            }

            float spoilPercentage;
            if (FSData.hasRemainingLifetime(stack))
            {
                int remainingLifetime = FSData.getRemainingLifetime(stack);
                spoilPercentage = 1.0F - (float) remainingLifetime / maxSpoilTicks;
            }
            else if (FSData.hasCreationTime(stack))
            {
                long creationTime = FSData.getCreationTime(stack);
                long elapsedTime = currentTime - creationTime;
                spoilPercentage = Math.min(1.0F, (float) elapsedTime / maxSpoilTicks);
            }
            else
            {
                return 0xFFFFFF;
            }

            int color = getColor(spoilPercentage);

            FSMaps.FOOD_TINTS.put(FSData.getID(stack), color);

            return color;
        }, ForgeRegistries.ITEMS.getValuesCollection().stream().filter(ItemFood.class::isInstance).toArray(Item[]::new));
    }

    private static int getColor(float spoilPercentage)
    {
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
    }
}
