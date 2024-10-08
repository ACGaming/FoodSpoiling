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
    /**
     * Returns true if the given ItemStack is a food item that can rot, false otherwise.
     *
     * @param stack the stack to check
     * @return true if the stack has a registered expiration time, false otherwise
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

    public static void saveInventory(EntityPlayer player)
    {
        if (player.world.isRemote || (player.isCreative() && !FSConfig.ROTTING.rotInCreative)) return;

        long currentWorldTime = player.world.getTotalWorldTime();

        for (int i = 0; i < player.inventoryContainer.inventorySlots.size(); i++)
        {
            Slot slot = player.inventoryContainer.inventorySlots.get(i);
            ItemStack stack = slot.getStack();
            if (canRot(stack))
            {
                saveStack(stack, currentWorldTime);
            }
        }
    }

    public static void saveStack(ItemStack stack, long currentWorldTime)
    {
        long elapsedTime = currentWorldTime - FSData.getCreationTime(stack);
        int totalSpoilTicks = FSMaps.FOOD_EXPIRATION_DAYS.get(stack.getItem()) * FSConfig.GENERAL.dayLengthInTicks;
        int remainingLifetime = Math.max(0, totalSpoilTicks - (int) elapsedTime);
        FSData.setRemainingLifetime(stack, remainingLifetime);
    }

    /**
     * Gets the number of ticks a food item has before it's considered rotten.
     * If the item is in a container with a custom lifetime factor, that multiplier is applied.
     * If the item is not applicable, this method returns -1.
     *
     * @param player the player whose container is being checked for a custom lifetime factor
     * @param stack  the stack to get the number of ticks for
     * @return the number of ticks the stack has before it rots, or -1 if it's not applicable
     */
    public static int getTicksToRot(EntityPlayer player, ItemStack stack)
    {
        int ticksToRot = -1;
        if (stack != null)
        {
            Item item = stack.getItem();
            if (FSMaps.FOOD_EXPIRATION_DAYS.containsKey(item))
            {
                ticksToRot = FSMaps.FOOD_EXPIRATION_DAYS.get(item) * FSConfig.GENERAL.dayLengthInTicks;
                if (FSLogic.hasCustomContainerConditions(player, stack))
                {
                    double lifetimeFactor = FSLogic.getCustomContainerConditions(player, stack);
                    ticksToRot = lifetimeFactor > 0 ? (int) (ticksToRot * lifetimeFactor) : -1;
                }
            }
        }
        return ticksToRot;
    }

    /**
     * Returns true if the player has a custom container condition defined for the given {@link ItemStack}'s container,
     * and the stack is not contained in the player's inventory, false otherwise.
     *
     * @param player the player to check for custom container conditions
     * @param stack  the stack to check
     * @return true if the player has a custom container condition defined for the stack, and the stack is not
     * contained in the player's inventory, false otherwise
     */
    public static boolean hasCustomContainerConditions(EntityPlayer player, ItemStack stack)
    {
        if (FSConfig.ROTTING.rotInPlayerInvOnly) return true;
        String containerClass = player.openContainer.getClass().getName();
        return FSMaps.CONTAINER_CONDITIONS.containsKey(containerClass) && !player.inventoryContainer.getInventory().contains(stack);
    }

    /**
     * Gets the custom container lifetime factor for the given {@link EntityPlayer}'s open container.
     *
     * @param player the player whose open container is being checked for a custom lifetime factor
     * @return the custom lifetime factor associated with the player's open container's class name
     */
    public static double getCustomContainerConditions(EntityPlayer player, ItemStack stack)
    {
        if (FSConfig.ROTTING.rotInPlayerInvOnly)
        {
            if (!player.inventoryContainer.getInventory().contains(stack)) return -1;
            return 1;
        }
        String containerClass = player.openContainer.getClass().getName();
        return FSMaps.CONTAINER_CONDITIONS.get(containerClass);
    }

    /**
     * Updates the rot time of the given {@link ItemStack} in the given {@link EntityPlayer}'s inventory at the given
     * slot. If the stack is in a container that pauses spoilage, the remaining lifetime is saved. If the stack has a
     * remaining lifetime, the creation time is updated to the current world time minus the remaining lifetime.
     * If the item is fresh or has an invalid time, the creation time is set to the current world time.
     * Finally, the method checks if the item has fully rotted and, if so, replaces it with the rotten equivalent.
     *
     * @param player           the player whose inventory is being updated
     * @param stack            the stack to update
     * @param inventorySlot    the slot in the player's inventory containing the stack
     * @param currentWorldTime the current world time
     */
    private static void updateRot(EntityPlayer player, ItemStack stack, int inventorySlot, long currentWorldTime)
    {
        // If no ID exists, set it now
        if (!FSData.hasID(stack))
        {
            FSData.setID(stack, stack.hashCode());
        }

        // Check if the container pauses spoilage (negative value in CONTAINER_CONDITIONS)
        if (FSLogic.hasCustomContainerConditions(player, stack) && FSLogic.getCustomContainerConditions(player, stack) < 0)
        {
            // Pausing spoilage: Save remaining lifetime
            if (FSData.hasCreationTime(stack))
            {
                FSLogic.saveStack(stack, currentWorldTime);
            }
            // Skip further spoilage logic since spoilage is paused
            return;
        }

        // Resuming spoilage: If item has paused spoilage (remaining lifetime), set new creation time
        if (FSData.hasRemainingLifetime(stack))
        {
            int remainingLifetime = FSData.getRemainingLifetime(stack);
            int totalSpoilTicks = FSLogic.getTicksToRot(player, stack);

            // Calculate the new creation time by subtracting remaining lifetime from the total spoil ticks
            long newCreationTime = currentWorldTime - (totalSpoilTicks - remainingLifetime);
            FSData.setCreationTime(stack, newCreationTime);  // Correct the creation time
        }

        // If no creation time exists, set it now
        if (!FSData.hasCreationTime(stack))
        {
            FSData.setCreationTime(stack, currentWorldTime);
        }
        else
        {
            long creationTime = FSData.getCreationTime(stack);

            // If creation time is in the future (due to time sync issues), reset it
            if (creationTime > currentWorldTime)
            {
                FSData.setCreationTime(stack, currentWorldTime);
            }

            // Calculate spoilage and check if the item has fully rotted
            int totalSpoilTicks = FSLogic.getTicksToRot(player, stack);
            long elapsedTime = currentWorldTime - creationTime;

            if (elapsedTime >= totalSpoilTicks && FSMaps.FOOD_CONVERSIONS.containsKey(stack.getItem()))
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
        if (!FSData.hasCreationTime(stack)) return false;

        long spoilTime = tag.getLong(FSData.TAG_CREATION_TIME);
        int maxSpoilTicks = getTicksToRot(player, stack);

        if (maxSpoilTicks < 0) return false;

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
