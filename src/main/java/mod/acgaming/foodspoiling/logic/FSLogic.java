package mod.acgaming.foodspoiling.logic;

import java.util.Map;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mod.acgaming.foodspoiling.FoodSpoiling;
import mod.acgaming.foodspoiling.config.FSConfig;

@Mod.EventBusSubscriber(modid = FoodSpoiling.MOD_ID)
public class FSLogic
{
    public static final String TAG_CREATION_TIME = "CreationTime";
    public static final Map<Item, Item> FOOD_CONVERSIONS = new Object2ObjectOpenHashMap<>();
    public static final Map<Item, Integer> FOOD_EXPIRATION_DAYS = new Object2IntOpenHashMap<>();
    public static final Map<EntityPlayer, Long> WARNING_TIMES = new Object2LongOpenHashMap<>();

    public static void initializeFoodMaps()
    {
        for (String entry : FSConfig.ROTTING.daysToRot)
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

    public static boolean canRot(ItemStack stack)
    {
        return FOOD_EXPIRATION_DAYS.containsKey(stack.getItem());
    }

    public static void updateInventory(EntityPlayer player)
    {
        if (player.world.isRemote || (player.isCreative() && !FSConfig.ROTTING.rotInCreative)) return;

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

    public static int getDaysToRot(ItemStack stack)
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

    public static void setCreationTime(ItemStack stack, long creationTime)
    {
        NBTTagCompound tag = stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);
        tag.setLong(TAG_CREATION_TIME, creationTime);
    }

    public static long getCreationTime(ItemStack stack)
    {
        NBTTagCompound tag = stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);
        return tag.getLong(TAG_CREATION_TIME);
    }

    private static void updateRot(EntityPlayer player, ItemStack stack, int inventorySlot, long currentWorldTime)
    {
        NBTTagCompound tag = stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);

        if (!tag.hasKey(TAG_CREATION_TIME))
        {
            setCreationTime(stack, currentWorldTime);
        }
        else
        {
            long creationTime = getCreationTime(stack);
            int daysToRot = getDaysToRot(stack);

            if (daysToRot > 0)
            {
                int maxSpoilTicks = daysToRot * FSConfig.GENERAL.dayLengthInTicks;

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
    }

    private static boolean shouldWarnPlayer(EntityPlayer player, ItemStack stack, long currentWorldTime)
    {
        if (!FSConfig.WARNING_MESSAGE.sendMessages) return false;

        NBTTagCompound tag = stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);
        if (!tag.hasKey(TAG_CREATION_TIME)) return false;

        long spoilTime = tag.getLong(TAG_CREATION_TIME);
        int daysToRot = getDaysToRot(stack);
        int maxSpoilTicks = daysToRot * FSConfig.GENERAL.dayLengthInTicks;

        if (daysToRot < 0) return false;

        long elapsedTime = currentWorldTime - spoilTime;
        int spoilPercentage = 100 - (int) ((elapsedTime * 100) / maxSpoilTicks);

        if (spoilPercentage <= FSConfig.WARNING_MESSAGE.messagePercentage)
        {
            long currentTime = System.currentTimeMillis();
            Long lastWarned = WARNING_TIMES.getOrDefault(player, 0L);
            if (currentTime - lastWarned >= FSConfig.WARNING_MESSAGE.messageCooldownMinutes * 60000L)
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
        if (FSConfig.WARNING_MESSAGE.sendMessagesActionBar)
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
        String[] messages = FSConfig.WARNING_MESSAGE.randomMessages;
        return messages[rand.nextInt(messages.length)];
    }
}
