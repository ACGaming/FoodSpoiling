package mod.acgaming.foodspoiling.logic;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import mod.acgaming.foodspoiling.FoodSpoiling;
import mod.acgaming.foodspoiling.config.FSConfig;

public class FSData
{
    public static final String TAG_ID = "ID";
    public static final String TAG_CREATION_TIME = "CreationTime";
    public static final String TAG_REMAINING_LIFETIME = "RemainingLifetime";
    public static final String TAG_LAST_LIFETIME_FACTOR = "LastLifetimeFactor";

    public static void setCreationTime(ItemStack stack, long creationTime)
    {
        NBTTagCompound tag = setNBTTagCompound(stack);
        tag.removeTag(TAG_REMAINING_LIFETIME);
        long roundedCreationTime = ((creationTime + FSConfig.GENERAL.checkIntervalInTicks / 2) / FSConfig.GENERAL.checkIntervalInTicks) * FSConfig.GENERAL.checkIntervalInTicks;
        tag.setLong(TAG_CREATION_TIME, roundedCreationTime);
    }

    public static long getCreationTime(ItemStack stack)
    {
        NBTTagCompound tag = getNBTTagCompound(stack);
        return tag != null ? tag.getLong(TAG_CREATION_TIME) : 0;
    }

    public static boolean hasCreationTime(ItemStack stack)
    {
        NBTTagCompound tag = getNBTTagCompound(stack);
        return tag != null && tag.hasKey(TAG_CREATION_TIME);
    }

    public static void setRemainingLifetime(ItemStack stack, int remainingLifetime)
    {
        NBTTagCompound tag = setNBTTagCompound(stack);
        tag.removeTag(TAG_CREATION_TIME);
        tag.setInteger(TAG_REMAINING_LIFETIME, remainingLifetime);
    }

    public static int getRemainingLifetime(ItemStack stack)
    {
        NBTTagCompound tag = getNBTTagCompound(stack);
        return tag != null ? tag.getInteger(TAG_REMAINING_LIFETIME) : 0;
    }

    public static boolean hasRemainingLifetime(ItemStack stack)
    {
        NBTTagCompound tag = getNBTTagCompound(stack);
        return tag != null && tag.hasKey(TAG_REMAINING_LIFETIME);
    }

    public static void removeRemainingLifetime(ItemStack stack)
    {
        NBTTagCompound tag = stack.getSubCompound(FoodSpoiling.MOD_ID);
        if (tag != null)
        {
            tag.removeTag(TAG_REMAINING_LIFETIME);
        }
    }

    public static boolean hasLastLifetimeFactor(ItemStack stack)
    {
        NBTTagCompound tag = stack.getSubCompound(FoodSpoiling.MOD_ID);
        return tag != null && tag.hasKey(TAG_LAST_LIFETIME_FACTOR);
    }

    public static double getLastLifetimeFactor(ItemStack stack)
    {
        NBTTagCompound tag = stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);
        return tag.getDouble(TAG_LAST_LIFETIME_FACTOR);
    }

    public static void setLastLifetimeFactor(ItemStack stack, double factor)
    {
        NBTTagCompound tag = stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);
        tag.setDouble(TAG_LAST_LIFETIME_FACTOR, factor);
    }

    public static void removeLastLifetimeFactor(ItemStack stack)
    {
        NBTTagCompound tag = stack.getSubCompound(FoodSpoiling.MOD_ID);
        if (tag != null)
        {
            tag.removeTag(TAG_LAST_LIFETIME_FACTOR);
        }
    }

    public static void setID(ItemStack stack, int id)
    {
        NBTTagCompound tag = setNBTTagCompound(stack);
        tag.setInteger(TAG_ID, id);
    }

    public static int getID(ItemStack stack)
    {
        NBTTagCompound tag = getNBTTagCompound(stack);
        return tag != null ? tag.getInteger(TAG_ID) : 0;
    }

    public static boolean hasID(ItemStack stack)
    {
        NBTTagCompound tag = getNBTTagCompound(stack);
        return tag != null && tag.hasKey(TAG_ID);
    }

    private static NBTTagCompound setNBTTagCompound(ItemStack stack)
    {
        return stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);
    }

    private static NBTTagCompound getNBTTagCompound(ItemStack stack)
    {
        return stack.getSubCompound(FoodSpoiling.MOD_ID);
    }
}
