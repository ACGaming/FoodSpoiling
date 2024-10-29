package mod.acgaming.foodspoiling.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
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

        ItemStack stack = event.getItemStack();

        if (!FSLogic.canRot(stack)) return;

        long creationTime = FSData.hasCreationTime(stack) ? FSData.getCreationTime(stack) : event.getEntityPlayer().world.getTotalWorldTime();
        long elapsedTime = event.getEntityPlayer().world.getTotalWorldTime() - creationTime;
        int maxSpoilTicks = FSLogic.getTicksToRot(event.getEntityPlayer(), stack);
        int daysRemaining = (int) ((maxSpoilTicks - elapsedTime) / FSConfig.GENERAL.dayLengthInTicks);
        int percentageRemaining = Math.max(0, Math.min(100, 100 - (int) ((elapsedTime * 100) / maxSpoilTicks)));

        if (FSData.hasRemainingLifetime(stack))
        {
            maxSpoilTicks = FSData.getRemainingLifetime(stack);
            daysRemaining = maxSpoilTicks / FSConfig.GENERAL.dayLengthInTicks;
            percentageRemaining = (maxSpoilTicks * 100) / (FSMaps.FOOD_EXPIRATION_DAYS.get(stack.getItem()) * FSConfig.GENERAL.dayLengthInTicks);
        }

        if (FSLogic.hasCustomContainerConditions(event.getEntityPlayer(), stack) && !FSConfig.ROTTING.rotInPlayerInvOnly)
        {
            event.getToolTip().add(I18n.format("tooltip.foodspoiling.stored_in_container"));
        }

        if (maxSpoilTicks < 0)
        {
            event.getToolTip().add(I18n.format("tooltip.foodspoiling.does_not_rot"));

            maxSpoilTicks = FSMaps.FOOD_EXPIRATION_DAYS.get(stack.getItem()) * FSConfig.GENERAL.dayLengthInTicks;
            daysRemaining = (int) ((maxSpoilTicks - elapsedTime) / FSConfig.GENERAL.dayLengthInTicks);

            if (daysRemaining >= 0)
            {
                String regularTooltip = displayRegularTooltip(daysRemaining, percentageRemaining);
                if (!regularTooltip.isEmpty()) event.getToolTip().add(regularTooltip);
            }
        }
        else
        {
            String regularTooltip = displayRegularTooltip(daysRemaining, percentageRemaining);
            if (!regularTooltip.isEmpty()) event.getToolTip().add(regularTooltip);

            String conditionsTooltip = displayConditionsTooltip(event.getEntityPlayer(), stack);
            if (!conditionsTooltip.isEmpty()) event.getToolTip().add(conditionsTooltip);
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

    public static void registerColorHandlerItems()
    {
        if (!FSConfig.ROTTING.renderRottenState) return;

        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player == null || (player.isCreative() && !FSConfig.ROTTING.rotInCreative) || (FSConfig.ROTTING.renderRottenStateFoodOnly && !(stack.getItem() instanceof ItemFood)))
            {
                return 0xFFFFFF;
            }

            long currentTime = Minecraft.getMinecraft().world.getTotalWorldTime();
            int maxSpoilTicks = FSLogic.getTicksToRot(player, stack);

            float spoilPercentage;
            if (maxSpoilTicks > 0 && FSData.hasCreationTime(stack))
            {
                long creationTime = FSData.getCreationTime(stack);
                long elapsedTime = currentTime - creationTime;
                spoilPercentage = Math.min(1.0F, (float) elapsedTime / maxSpoilTicks);
            }
            else if (FSData.hasRemainingLifetime(stack))
            {
                int remainingLifetime = FSData.getRemainingLifetime(stack);
                spoilPercentage = 1.0F - (float) remainingLifetime / (FSMaps.FOOD_EXPIRATION_DAYS.get(stack.getItem()) * FSConfig.GENERAL.dayLengthInTicks);
            }
            else
            {
                return FSMaps.FOOD_TINTS.getOrDefault(FSData.getID(stack), 0xFFFFFF);
            }

            int color = getColor(spoilPercentage);

            FSMaps.FOOD_TINTS.put(FSData.getID(stack), color);

            return color;
        }, ForgeRegistries.ITEMS.getValuesCollection().stream().filter(FSMaps.FOOD_EXPIRATION_DAYS::containsKey).toArray(Item[]::new));
    }

    private static String displayRegularTooltip(int daysRemaining, int percentageRemaining)
    {
        StringBuilder tooltipBuilder = new StringBuilder();
        if (FSConfig.TOOLTIPS.tooltipFoodDays)
        {
            if (daysRemaining >= 1)
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
        return tooltipBuilder.toString();
    }

    private static String displayConditionsTooltip(EntityPlayer player, ItemStack stack)
    {
        StringBuilder tooltipBuilder = new StringBuilder();
        if (FSConfig.TOOLTIPS.tooltipFoodDays && FSLogic.hasCustomContainerConditions(player, stack))
        {
            double lifetimeFactor = FSLogic.getCustomContainerConditions(player, stack);
            if (lifetimeFactor > 0 && lifetimeFactor != 1)
            {
                tooltipBuilder.append(I18n.format("tooltip.foodspoiling.lifetime_factor", lifetimeFactor));
                if (FSConfig.TOOLTIPS.tooltipFoodPercent)
                {
                    int percentageBonus = (int) ((lifetimeFactor - 1.0) * 100);
                    tooltipBuilder.append(" (").append(percentageBonus >= 0 ? "+" : "").append(percentageBonus).append("%)");
                }
            }
        }
        return tooltipBuilder.toString();
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
