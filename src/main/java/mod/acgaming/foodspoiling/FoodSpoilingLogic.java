package mod.acgaming.foodspoiling;

import java.util.Map;
import java.util.Random;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@Mod.EventBusSubscriber(modid = FoodSpoiling.MOD_ID)
public class FoodSpoilingLogic
{
    private static final String TAG_CREATION_TIME = "CreationTime";
    private static final Map<Item, Item> FOOD_CONVERSIONS = new Object2ObjectOpenHashMap<>();
    private static final Map<Item, Integer> FOOD_EXPIRATION_DAYS = new Object2IntOpenHashMap<>();
    private static final Map<EntityPlayer, Long> WARNING_TIMES = new Object2LongOpenHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.PlayerTickEvent.Phase.END || event.player.world.getTotalWorldTime() % FoodSpoilingConfig.GENERAL.checkIntervalInTicks != 0) return;
        updateInventory(event.player);
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        updateInventory(event.getEntityPlayer());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        if (!FoodSpoilingConfig.TOOLTIPS.showFoodTooltip || event.getEntityPlayer() == null) return;

        ItemStack stack = event.getItemStack();
        if (!canRot(stack)) return;

        NBTTagCompound tag = stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);
        if (!tag.hasKey(TAG_CREATION_TIME)) return;

        long creationTime = tag.getLong(TAG_CREATION_TIME);
        long currentTime = event.getEntityPlayer().world.getTotalWorldTime();
        int daysToRot = getDaysToRot(stack);
        int maxSpoilTicks = daysToRot * FoodSpoilingConfig.GENERAL.dayLengthInTicks;

        if (daysToRot < 0)
        {
            event.getToolTip().add(I18n.format("tooltip.foodspoiling.does_not_rot"));
        }
        else
        {
            long elapsedTime = currentTime - creationTime;
            String tooltip = "";
            int daysRemaining = (int) ((maxSpoilTicks - elapsedTime) / FoodSpoilingConfig.GENERAL.dayLengthInTicks);
            int percentageRemaining = 100 - (int) ((elapsedTime * 100) / maxSpoilTicks);
            if (FoodSpoilingConfig.TOOLTIPS.tooltipFoodDays)
            {
                tooltip = daysRemaining > 0 ? I18n.format("tooltip.foodspoiling.good_for_days", daysRemaining) : I18n.format("tooltip.foodspoiling.good_for_less_than_day");

                if (FoodSpoilingConfig.TOOLTIPS.tooltipFoodPercent)
                {
                    tooltip = tooltip + " (" + I18n.format("tooltip.foodspoiling.good_for_days_percentage", percentageRemaining) + ")";
                }
            }
            else if (FoodSpoilingConfig.TOOLTIPS.tooltipFoodPercent)
            {
                tooltip = I18n.format("tooltip.foodspoiling.good_for_days_percentage", percentageRemaining);
            }
            if (!tooltip.isEmpty())
            {
                event.getToolTip().add(tooltip);
            }
        }
    }

    public static void initializeFoodMaps()
    {
        for (String entry : FoodSpoilingConfig.ROTTING.daysToRot)
        {
            String[] parts = entry.split(",");
            if (parts.length >= 2)
            {
                String itemIdentifier = parts[0];
                String rotDaysString = parts[parts.length - 1];
                try
                {
                    int rotDays = Integer.parseInt(rotDaysString.trim());
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemIdentifier));
                    if (item != null) FOOD_EXPIRATION_DAYS.put(item, rotDays);
                    if (parts.length > 2)
                    {
                        String itemReplacementIdentifier = parts[1];
                        Item itemReplacement = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemReplacementIdentifier));
                        if (item != null && itemReplacement != null) FOOD_CONVERSIONS.put(item, itemReplacement);
                    }
                }
                catch (Exception e)
                {
                    FoodSpoiling.LOGGER.error("Invalid entry for rot days in config: {}", entry);
                }
            }
            else FoodSpoiling.LOGGER.error("Invalid entry for rot days in config: {}", entry);
        }
    }

    private static boolean canRot(ItemStack stack)
    {
        return FOOD_EXPIRATION_DAYS.containsKey(stack.getItem());
    }

    private static void updateInventory(EntityPlayer player)
    {
        if (player.world.isRemote || (player.isCreative() && !FoodSpoilingConfig.ROTTING.rotInCreative)) return;

        long currentWorldTime = player.world.getTotalWorldTime();

        for (int i = 0; i < player.openContainer.inventorySlots.size(); i++)
        {
            Slot slot = player.openContainer.inventorySlots.get(i);
            ItemStack stack = slot.getStack();
            if (!canRot(stack)) continue;

            updateRot(player, stack, i, currentWorldTime);

            if (shouldWarnPlayer(player, stack, currentWorldTime))
            {
                sendWarningMessage(player);
            }
        }
    }

    private static void updateRot(EntityPlayer player, ItemStack stack, int inventorySlot, long currentWorldTime)
    {
        NBTTagCompound tag = stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);

        if (!tag.hasKey(TAG_CREATION_TIME))
        {
            tag.setLong(TAG_CREATION_TIME, currentWorldTime);
            return;
        }

        long creationTime = tag.getLong(TAG_CREATION_TIME);
        int daysToRot = getDaysToRot(stack);

        if (daysToRot > 0)
        {
            int maxSpoilTicks = daysToRot * FoodSpoilingConfig.GENERAL.dayLengthInTicks;

            long elapsedTime = currentWorldTime - creationTime;

            if (elapsedTime >= maxSpoilTicks && FOOD_CONVERSIONS.containsKey(stack.getItem()))
            {
                Item itemReplacement = FOOD_CONVERSIONS.get(stack.getItem());

                if (itemReplacement != null)
                {
                    ItemStack rottenStack = new ItemStack(itemReplacement, stack.getCount());
                    player.openContainer.inventorySlots.get(inventorySlot).putStack(rottenStack);
                    player.openContainer.detectAndSendChanges();
                }
            }
        }
    }

    private static int getDaysToRot(ItemStack stack)
    {
        if (stack != null)
        {
            Item item = stack.getItem();
            if (FOOD_EXPIRATION_DAYS.containsKey(item))
            {
                return FOOD_EXPIRATION_DAYS.get(item);
            }
        }
        return -1;
    }

    private static boolean shouldWarnPlayer(EntityPlayer player, ItemStack stack, long currentWorldTime)
    {
        if (!FoodSpoilingConfig.WARNING_MESSAGE.sendMessages) return false;

        NBTTagCompound tag = stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);
        if (!tag.hasKey(TAG_CREATION_TIME)) return false;

        long spoilTime = tag.getLong(TAG_CREATION_TIME);
        int daysToRot = getDaysToRot(stack);
        int maxSpoilTicks = daysToRot * FoodSpoilingConfig.GENERAL.dayLengthInTicks;

        if (daysToRot < 0) return false;

        long elapsedTime = currentWorldTime - spoilTime;
        int spoilPercentage = 100 - (int) ((elapsedTime * 100) / maxSpoilTicks);

        if (spoilPercentage <= FoodSpoilingConfig.WARNING_MESSAGE.messagePercentage)
        {
            long currentTime = System.currentTimeMillis();
            Long lastWarned = WARNING_TIMES.getOrDefault(player, 0L);
            if (currentTime - lastWarned >= FoodSpoilingConfig.WARNING_MESSAGE.messageCooldownMinutes * 60000L)
            {
                WARNING_TIMES.put(player, currentTime);
                return true;
            }
        }
        return false;
    }

    private static void sendWarningMessage(EntityPlayer player)
    {
        String message = getRandomWarningMessage(player.world.rand);
        if (FoodSpoilingConfig.WARNING_MESSAGE.sendMessagesActionBar)
        {
            player.sendStatusMessage(new TextComponentString(message), true);
        }
        else
        {
            player.sendMessage(new TextComponentString(message));
        }
    }

    private static String getRandomWarningMessage(Random rand)
    {
        String[] messages = FoodSpoilingConfig.WARNING_MESSAGE.randomMessages;
        return messages[rand.nextInt(messages.length)];
    }
}
