package mod.acgaming.foodspoiling.logic;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.text.TextComponentString;

import mod.acgaming.foodspoiling.FoodSpoiling;
import mod.acgaming.foodspoiling.config.FSConfig;

public class FSLogic
{
    /**
     * Checks if the given item stack is eligible for further processing.
     *
     * @param stack the stack to check
     * @return {@link EnumActionResult#FAIL} if the stack is empty or isn't listed to rot,
     * {@link EnumActionResult#PASS} if explicitly listed as non-rotting,
     * {@link EnumActionResult#SUCCESS} otherwise
     */
    public static EnumActionResult canRot(ItemStack stack)
    {
        if (stack.isEmpty()) return EnumActionResult.FAIL;
        double days = getExpirationDays(stack);
        if (Double.isNaN(days)) return EnumActionResult.FAIL;
        if (days < 0) return EnumActionResult.PASS;
        return EnumActionResult.SUCCESS;
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
            if (canRot(stack) != EnumActionResult.SUCCESS) continue;

            updateRot(player, stack, i, currentWorldTime);

            if (shouldWarnPlayer(player, stack, currentWorldTime))
            {
                sendWarningMessage(player);
            }
        }
    }

    /**
     * Saves the remaining lifetime of all food items in the player's inventory. The remaining lifetime is calculated by
     * subtracting the elapsed time from the total spoil time, and is then stored in the item's NBT data.
     * If the remaining lifetime is zero or less, the item is replaced with a rotten equivalent.
     *
     * @param player the player whose inventory is being saved
     */
    public static void saveInventory(EntityPlayer player)
    {
        if (player.world.isRemote || (player.isCreative() && !FSConfig.ROTTING.rotInCreative)) return;

        long currentWorldTime = player.world.getTotalWorldTime();

        for (int i = 0; i < player.inventoryContainer.inventorySlots.size(); i++)
        {
            Slot slot = player.inventoryContainer.inventorySlots.get(i);
            ItemStack stack = slot.getStack();
            if (canRot(stack) == EnumActionResult.SUCCESS)
            {
                saveRemainingLifetime(player, stack, i, currentWorldTime);
            }
        }
    }

    /**
     * Saves the remaining lifetime of a food item. The remaining lifetime is calculated by subtracting the elapsed time
     * from the total spoil time, and is then stored in the item's NBT data. If the remaining lifetime is zero or less,
     * the item is replaced with a rotten equivalent.
     *
     * @param player           the player whose inventory is being saved
     * @param stack            the stack to save the remaining lifetime for
     * @param inventorySlot    the slot in the player's inventory containing the stack
     * @param currentWorldTime the current world time
     */
    public static void saveRemainingLifetime(EntityPlayer player, ItemStack stack, int inventorySlot, long currentWorldTime)
    {
        double lastLifetimeFactor = FSData.hasLastLifetimeFactor(stack) ? FSData.getLastLifetimeFactor(stack) : 1.0;
        long elapsedTime = currentWorldTime - FSData.getCreationTime(stack);
        double days = getExpirationDays(stack);
        int baseSpoilTicks = (int) (days * FSConfig.GENERAL.dayLengthInTicks);
        int remainingBase = baseSpoilTicks - (int) (elapsedTime / lastLifetimeFactor);
        int remainingLifetime = Math.max(0, remainingBase);
        if (remainingLifetime > 0)
        {
            FSData.setRemainingLifetime(stack, remainingLifetime);
        }
        else
        {
            replaceStack(player, stack, inventorySlot);
        }
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
    public static int getTicksToRot(@Nullable EntityPlayer player, ItemStack stack)
    {
        int ticksToRot = -1;
        if (stack != null)
        {
            double days = getExpirationDays(stack);
            if (!Double.isNaN(days))
            {
                ticksToRot = (int) (days * FSConfig.GENERAL.dayLengthInTicks);
                if (player != null)
                {
                    double lifetimeFactor = FSLogic.getLifetimeFactor(player, stack);
                    ticksToRot = lifetimeFactor > 0 ? (int) (ticksToRot * lifetimeFactor) : -1;
                }
            }
        }
        return ticksToRot;
    }

    /**
     * Gets the lifetime factor for the given stack in the player's open container.
     * Defaults to 1.0 if not specified in the config or if rotInPlayerInvOnly restricts it.
     *
     * @param player the player whose open container is being checked
     * @param stack  the stack to check
     * @return the lifetime factor (positive to modify rate, negative to pause)
     */
    public static double getLifetimeFactor(EntityPlayer player, ItemStack stack)
    {
        if (player.inventoryContainer.getInventory().contains(stack))
        {
            return 1.0;
        }
        else if (FSConfig.ROTTING.rotInPlayerInvOnly)
        {
            return -1.0;
        }
        String containerClass = player.openContainer.getClass().getName();
        return FSMaps.CONTAINER_CONDITIONS.getOrDefault(containerClass, 1.0);
    }

    /**
     * Updates the given {@link EntityItem} if its contained stack has fully rotted. If the stack has rotted,
     * it is replaced with a rotten equivalent.
     *
     * @param itemEntity the entity containing the stack to update
     * @param stack      the stack to update
     */
    public static void updateItemEntity(EntityItem itemEntity, ItemStack stack)
    {
        // Skip if the stack can't rot
        if (canRot(stack) != EnumActionResult.SUCCESS) return;

        // If no ID exists, set it now
        if (!FSData.hasID(stack))
        {
            FSData.setID(stack, stack.hashCode());
        }

        // If no creation time exists, set it to the current world time
        long currentWorldTime = itemEntity.world.getTotalWorldTime();
        if (!FSData.hasCreationTime(stack))
        {
            FSData.setCreationTime(stack, currentWorldTime);
            return; // Skip spoilage check on first tick to avoid premature spoilage
        }

        long elapsedTime = currentWorldTime - FSData.getCreationTime(stack);
        int totalSpoilTicks = FSLogic.getTicksToRot(null, stack);

        // Only replace if the item has fully rotted
        if (totalSpoilTicks > 0 && elapsedTime >= totalSpoilTicks)
        {
            FSLogic.replaceStack(itemEntity, stack, -1);
        }
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
    private static void updateRot(@Nullable EntityPlayer player, ItemStack stack, int inventorySlot, long currentWorldTime)
    {
        // If no ID exists, set it now
        if (!FSData.hasID(stack))
        {
            FSData.setID(stack, stack.hashCode());
        }

        if (player == null) return;

        double currentLifetimeFactor = getLifetimeFactor(player, stack);

        if (currentLifetimeFactor < 0)
        {
            // Pausing spoilage: Save remaining lifetime normalized to base (factor=1)
            if (FSData.hasCreationTime(stack))
            {
                saveRemainingLifetime(player, stack, inventorySlot, currentWorldTime);
            }
            // Skip further logic since spoilage is paused
            return;
        }

        // Handle factor change for positive factors (including default 1.0)
        double lastLifetimeFactor = FSData.hasLastLifetimeFactor(stack) ? FSData.getLastLifetimeFactor(stack) : 1.0;
        if (lastLifetimeFactor != currentLifetimeFactor)
        {
            if (FSData.hasCreationTime(stack))
            {
                long elapsedTime = currentWorldTime - FSData.getCreationTime(stack);
                long effectiveElapsed = (long) ((elapsedTime / lastLifetimeFactor) * currentLifetimeFactor);
                long newCreationTime = currentWorldTime - effectiveElapsed;
                FSData.setCreationTime(stack, newCreationTime);
            }
            FSData.setLastLifetimeFactor(stack, currentLifetimeFactor);
        }

        // Resuming spoilage: If item had paused spoilage (remaining lifetime), set new creation time, scaling remaining
        if (FSData.hasRemainingLifetime(stack))
        {
            int remainingBase = FSData.getRemainingLifetime(stack);
            int totalSpoilTicks = FSLogic.getTicksToRot(player, stack);
            long effectiveRemaining = (long) (remainingBase * currentLifetimeFactor);
            long elapsed = totalSpoilTicks - effectiveRemaining;
            long newCreationTime = currentWorldTime - elapsed;
            FSData.setCreationTime(stack, newCreationTime);
            FSData.removeRemainingLifetime(stack);
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
            long elapsedTime = currentWorldTime - creationTime;
            int totalSpoilTicks = FSLogic.getTicksToRot(player, stack);

            if (elapsedTime >= totalSpoilTicks)
            {
                replaceStack(player, stack, inventorySlot);
            }
        }
    }

    /**
     * Replaces the given item stack with its rotten equivalent if it has fully spoiled.
     * If no rotten equivalent is set, the stack count is set to 0.
     *
     * @param entity        the entity containing the stack to replace
     * @param stack         the stack to replace
     * @param inventorySlot the inventory slot of the stack to replace
     */
    private static void replaceStack(Entity entity, ItemStack stack, int inventorySlot)
    {
        ItemStack conversion = getConversion(stack);
        if (conversion != null)
        {
            ItemStack rottenStack = new ItemStack(conversion.getItem(), stack.getCount(), conversion.getMetadata());
            if (entity instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) entity;
                player.openContainer.inventorySlots.get(inventorySlot).putStack(rottenStack);
                player.openContainer.detectAndSendChanges();
            }
            else if (entity instanceof EntityItem)
            {
                EntityItem itemEntity = (EntityItem) entity;
                itemEntity.setItem(rottenStack);
            }
            else
            {
                FoodSpoiling.LOGGER.error("Invalid entity type for replacing item stack: {}", entity.getClass().getName());
            }
        }
        else
        {
            stack.setCount(0);
        }
    }

    /**
     * Gets the expiration days for a given item stack.
     * <p>
     * The method first checks if an expiration time is set for the given item stack with its metadata. If no such
     * expiration time is set, it checks for the item without its metadata. If no expiration time is set at all, the
     * method returns Double.NaN.
     *
     * @param stack the item stack to get the expiration days for
     * @return the expiration days for the given item stack, or Double.NaN if no expiration time is set
     */
    public static double getExpirationDays(ItemStack stack)
    {
        String reg = stack.getItem().getRegistryName().toString();
        String regMeta = reg + ":" + stack.getMetadata();
        if (FSMaps.FOOD_EXPIRATION_DAYS.containsKey(regMeta))
        {
            return FSMaps.FOOD_EXPIRATION_DAYS.get(regMeta);
        }
        if (FSMaps.FOOD_EXPIRATION_DAYS.containsKey(reg))
        {
            return FSMaps.FOOD_EXPIRATION_DAYS.get(reg);
        }
        return Double.NaN;
    }

    /**
     * Gets the conversion item stack for a given item stack.
     * <p>
     * The method first checks if a conversion item stack is set for the given item stack with its metadata. If no such
     * conversion item stack is set, it checks for the item without its metadata. If no conversion item stack is set at all, the
     * method returns null.
     *
     * @param stack the item stack to get the conversion item stack for
     * @return the conversion item stack for the given item stack, or null if no conversion item stack is set
     */
    private static ItemStack getConversion(ItemStack stack)
    {
        String reg = stack.getItem().getRegistryName().toString();
        String regMeta = reg + ":" + stack.getMetadata();
        if (FSMaps.FOOD_CONVERSIONS.containsKey(regMeta))
        {
            return FSMaps.FOOD_CONVERSIONS.get(regMeta);
        }
        if (FSMaps.FOOD_CONVERSIONS.containsKey(reg))
        {
            return FSMaps.FOOD_CONVERSIONS.get(reg);
        }
        return null;
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
