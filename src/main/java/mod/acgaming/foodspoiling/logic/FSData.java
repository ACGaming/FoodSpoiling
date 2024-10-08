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

    public static void setCreationTime(ItemStack stack, long creationTime)
    {
        NBTTagCompound tag = getNBTTag(stack);
        tag.removeTag(TAG_REMAINING_LIFETIME);
        long roundedCreationTime = ((creationTime + FSConfig.GENERAL.checkIntervalInTicks / 2) / FSConfig.GENERAL.checkIntervalInTicks) * FSConfig.GENERAL.checkIntervalInTicks;
        tag.setLong(TAG_CREATION_TIME, roundedCreationTime);
    }

    public static long getCreationTime(ItemStack stack)
    {
        NBTTagCompound tag = getNBTTag(stack);
        return tag.getLong(TAG_CREATION_TIME);
    }

    public static boolean hasCreationTime(ItemStack stack)
    {
        NBTTagCompound tag = getNBTTag(stack);
        return tag.hasKey(TAG_CREATION_TIME);
    }

    public static void setRemainingLifetime(ItemStack stack, int remainingLifetime)
    {
        NBTTagCompound tag = getNBTTag(stack);
        tag.removeTag(TAG_CREATION_TIME);
        tag.setInteger(TAG_REMAINING_LIFETIME, remainingLifetime);
    }

    public static int getRemainingLifetime(ItemStack stack)
    {
        NBTTagCompound tag = getNBTTag(stack);
        return tag.getInteger(TAG_REMAINING_LIFETIME);
    }

    public static boolean hasRemainingLifetime(ItemStack stack)
    {
        NBTTagCompound tag = getNBTTag(stack);
        return tag.hasKey(TAG_REMAINING_LIFETIME);
    }

    public static void setID(ItemStack stack, int id)
    {
        NBTTagCompound tag = getNBTTag(stack);
        tag.setInteger(TAG_ID, id);
    }

    public static int getID(ItemStack stack)
    {
        NBTTagCompound tag = getNBTTag(stack);
        return tag.getInteger(TAG_ID);
    }

    public static boolean hasID(ItemStack stack)
    {
        NBTTagCompound tag = getNBTTag(stack);
        return tag.hasKey(TAG_ID);
    }

    private static NBTTagCompound getNBTTag(ItemStack stack)
    {
        return stack.getOrCreateSubCompound(FoodSpoiling.MOD_ID);
    }
}
