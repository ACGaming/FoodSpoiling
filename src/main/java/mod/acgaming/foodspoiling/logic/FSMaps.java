package mod.acgaming.foodspoiling.logic;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mod.acgaming.foodspoiling.FoodSpoiling;
import mod.acgaming.foodspoiling.config.FSConfig;

public class FSMaps
{
    public static final Map<String, Double> CONTAINER_CONDITIONS = new Object2DoubleOpenHashMap<>();
    public static final Map<String, ItemStack> FOOD_CONVERSIONS = new Object2ObjectOpenHashMap<>();
    public static final Map<String, Double> FOOD_EXPIRATION_DAYS = new Object2DoubleOpenHashMap<>();
    public static final Map<Integer, Integer> FOOD_TINTS = new Int2IntOpenHashMap();
    public static final Map<EntityPlayer, Long> WARNING_TIMES = new Object2LongOpenHashMap<>();

    public static void initializeFoodMaps()
    {
        FOOD_CONVERSIONS.clear();
        FOOD_EXPIRATION_DAYS.clear();
        for (String entry : FSConfig.ROTTING.daysToRot)
        {
            String[] parts = entry.split(",");
            if (parts.length < 2) continue;

            String inputStr = parts[0].trim();
            String daysStr = parts[parts.length - 1].trim();

            double days;
            try
            {
                days = Double.parseDouble(daysStr);
            }
            catch (NumberFormatException e)
            {
                continue;
            }

            ItemStack replacement = ItemStack.EMPTY;
            if (parts.length >= 3)
            {
                String outputStr = parts[1].trim();
                replacement = parseItemStack(outputStr);
                if (replacement.isEmpty()) continue;
            }

            addToMaps(inputStr, days, replacement);
        }
        if (FSConfig.ROTTING.defaultFoodRotting)
        {
            for (Item item : ForgeRegistries.ITEMS)
            {
                if (item instanceof ItemFood)
                {
                    String generalKey = item.getRegistryName().toString();
                    if (!FOOD_EXPIRATION_DAYS.containsKey(generalKey))
                    {
                        FOOD_EXPIRATION_DAYS.put(generalKey, (double) FSConfig.ROTTING.defaultFoodRottingDays);
                        FoodSpoiling.LOGGER.debug("Added default lifetime of {} days to {}", FSConfig.ROTTING.defaultFoodRottingDays, generalKey);
                    }
                }
            }
        }
    }

    public static void initializeContainerConditions()
    {
        CONTAINER_CONDITIONS.clear();
        for (String entry : FSConfig.ROTTING.containerConditions)
        {
            String[] parts = entry.split(",");
            if (parts.length >= 2)
            {
                String containerClass = parts[0].trim();
                String lifetimeFactorString = parts[parts.length - 1].trim();
                double lifetimeFactor = Double.parseDouble(lifetimeFactorString);
                CONTAINER_CONDITIONS.put(containerClass, lifetimeFactor);
                FoodSpoiling.LOGGER.debug("Added a lifetime factor of {} for container {}", lifetimeFactor, containerClass);
            }
        }
    }

    private static ItemStack parseItemStack(String str)
    {
        String[] parts = str.split(":");
        if (parts.length < 2) return ItemStack.EMPTY;

        ResourceLocation loc = new ResourceLocation(parts[0], parts[1]);
        Item item = ForgeRegistries.ITEMS.getValue(loc);
        if (item == null) return ItemStack.EMPTY;

        int meta = 0;
        if (parts.length == 3)
        {
            try
            {
                meta = Integer.parseInt(parts[2]);
            }
            catch (NumberFormatException e)
            {
                return ItemStack.EMPTY;
            }
        }

        return new ItemStack(item, 1, meta);
    }

    private static void addToMaps(String inputStr, double days, ItemStack replacement)
    {
        String[] inputParts = inputStr.split(":");
        if (inputParts.length < 2) return;

        String namespace = inputParts[0];
        String path = inputParts[1];

        int meta = OreDictionary.WILDCARD_VALUE;
        if (inputParts.length == 3)
        {
            try
            {
                meta = Integer.parseInt(inputParts[2]);
            }
            catch (NumberFormatException e)
            {
                return;
            }
        }

        if (namespace.equals("ore"))
        {
            List<ItemStack> ores = OreDictionary.getOres(path);
            for (ItemStack ore : ores)
            {
                if (ore.isEmpty()) continue;

                String reg = ore.getItem().getRegistryName().toString();
                String key = reg + ":" + ore.getMetadata();

                if (FOOD_EXPIRATION_DAYS.containsKey(key)) continue;

                FOOD_EXPIRATION_DAYS.put(key, days);
                FoodSpoiling.LOGGER.debug("Added a lifetime of {} days to {}", days, key);

                if (!replacement.isEmpty())
                {
                    FOOD_CONVERSIONS.put(key, replacement.copy());
                    FoodSpoiling.LOGGER.debug("Added {} as a replacement for {}", replacement, key);
                }
            }
        }
        else
        {
            ResourceLocation loc = new ResourceLocation(namespace, path);
            Item item = ForgeRegistries.ITEMS.getValue(loc);
            if (item == null) return;

            String key = loc.toString();
            if (meta != OreDictionary.WILDCARD_VALUE)
            {
                key += ":" + meta;
            }

            if (FOOD_EXPIRATION_DAYS.containsKey(key)) return;

            FOOD_EXPIRATION_DAYS.put(key, days);
            FoodSpoiling.LOGGER.debug("Added a lifetime of {} days to {}", days, key);

            if (!replacement.isEmpty())
            {
                FOOD_CONVERSIONS.put(key, replacement.copy());
                FoodSpoiling.LOGGER.debug("Added {} as a replacement for {}", replacement, key);
            }
        }
    }
}
