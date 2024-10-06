package mod.acgaming.foodspoiling.logic;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;

import mod.acgaming.foodspoiling.FoodSpoiling;
import mod.acgaming.foodspoiling.config.FSConfig;

public class FSLogic
{
    public static final String TAG_CREATION_TIME = "CreationTime";

    /**
     * Returns true if the given ItemStack is a food item that can rot, false otherwise.
     *
     * @param stack the stack to check
     * @return true if the stack can rot, false otherwise
     */
    public static boolean canRot(ItemStack stack)
    {
        return FSMaps.FOOD_EXPIRATION_DAYS.containsKey(stack.getItem());
    }

    /**
     * Updates the rot time of all food items in the player's inventory. If an item is about to rot, a warning message
     * is sent to the player.
     *
     * @param player the player whose inventory is being updated
     */
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

    /**
     * Get the number of days to rot for the given {@link ItemStack}.
     *
     * @param stack The item stack to check.
     * @return The number of days to rot, or -1 if no expiration days are set for the item.
     */
    public static int getDaysToRot(ItemStack stack)
    {
        if (stack != null)
        {
            Item item = stack.getItem();
            if (FSMaps.FOOD_EXPIRATION_DAYS.containsKey(item))
            {
                return FSMaps.FOOD_EXPIRATION_DAYS.get(item);
            }
        }
        return -1;
    }

    /**
     * Sets the creation time for the given {@link ItemStack}. This method will create a new NBTTagCompound
     * in the stack's tag compound with the given value if one does not already exist.
     *
     * @param stack        the stack to set the creation time for
     * @param creationTime the creation time to set
     */
    public static void setCreationTime(ItemStack stack, long creationTime)
    {
        NBTTagCompound tag = stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);
        long roundedCreationTime = ((creationTime + FSConfig.GENERAL.checkIntervalInTicks / 2) / FSConfig.GENERAL.checkIntervalInTicks) * FSConfig.GENERAL.checkIntervalInTicks;
        tag.setLong(TAG_CREATION_TIME, roundedCreationTime);
    }

    /**
     * Returns the creation time for the given {@link ItemStack}, or 0 if no creation time has been set.
     *
     * @param stack the stack to retrieve the creation time from
     * @return the creation time for the given stack
     */
    public static long getCreationTime(ItemStack stack)
    {
        NBTTagCompound tag = stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);
        return tag.getLong(TAG_CREATION_TIME);
    }

    /**
     * Updates the rot time of the given {@link ItemStack} in the player's inventory slot.
     * <p>
     * If the stack has not been given a creation time yet, it will be set to the current world time.
     * If the stack has a creation time, it will be checked against the current world time. If the stack's creation time
     * is in the future, it will be set to the current world time. If the stack has been in the inventory long enough to
     * rot, it will be replaced with a rotten equivalent if one exists.
     * </p>
     *
     * @param player           the player whose inventory is being updated
     * @param stack            the stack to update
     * @param inventorySlot    the slot in the player's inventory containing the stack
     * @param currentWorldTime the current world time
     */
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

            if (creationTime > currentWorldTime)
            {
                setCreationTime(stack, currentWorldTime);
            }

            int daysToRot = getDaysToRot(stack);

            if (daysToRot > 0)
            {
                int maxSpoilTicks = daysToRot * FSConfig.GENERAL.dayLengthInTicks;

                long elapsedTime = currentWorldTime - creationTime;

                if (elapsedTime >= maxSpoilTicks && FSMaps.FOOD_CONVERSIONS.containsKey(stack.getItem()))
                {
                    Item itemReplacement = FSMaps.FOOD_CONVERSIONS.get(stack.getItem());

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

    /**
     * Determines if the player should receive a warning message that their food is about to spoil.
     *
     * @param player           the player to check
     * @param stack            the stack of food to check
     * @param currentWorldTime the current world time
     * @return true if the player should receive a warning message, false otherwise
     */
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
            Long lastWarned = FSMaps.WARNING_TIMES.getOrDefault(player, 0L);
            if (currentTime - lastWarned >= FSConfig.WARNING_MESSAGE.messageCooldownMinutes * 60000L)
            {
                FSMaps.WARNING_TIMES.put(player, currentTime);
                return true;
            }
        }
        return false;
    }

    /**
     * Sends a warning message to the player about their food spoiling.
     * <p>
     * If the config option is set to send the message in the action bar, it will do so. Otherwise, it will send the message
     * in the chat.
     * </p>
     *
     * @param player the player to send the warning message to
     */
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

    /**
     * Gets a random warning message from the configuration.
     * <p>
     * Uses the given {@link Random} instance to select a random message from the array of messages specified in the
     * configuration.
     * </p>
     *
     * @param rand the random instance to use
     * @return a random message from the configuration
     */
    private static String getRandomWarningMessage(Random rand)
    {
        String[] messages = FSConfig.WARNING_MESSAGE.randomMessages;
        return messages[rand.nextInt(messages.length)];
    }
}
