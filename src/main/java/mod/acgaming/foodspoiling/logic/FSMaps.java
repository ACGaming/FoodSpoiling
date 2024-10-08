package mod.acgaming.foodspoiling.logic;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mod.acgaming.foodspoiling.FoodSpoiling;
import mod.acgaming.foodspoiling.config.FSConfig;

public class FSMaps
{
    public static final Map<String, Double> CONTAINER_CONDITIONS = new Object2DoubleOpenHashMap<>();
    public static final Map<Item, Item> FOOD_CONVERSIONS = new Object2ObjectOpenHashMap<>();
    public static final Map<Item, Integer> FOOD_EXPIRATION_DAYS = new Object2IntOpenHashMap<>();
    public static final Map<Integer, Integer> FOOD_TINTS = new Int2IntOpenHashMap();
    public static final Map<EntityPlayer, Long> WARNING_TIMES = new Object2LongOpenHashMap<>();

    public static void initializeFoodMaps()
    {
        FOOD_CONVERSIONS.clear();
        FOOD_EXPIRATION_DAYS.clear();
        for (String entry : FSConfig.ROTTING.daysToRot)
        {
            String[] parts = entry.split(",");
            if (parts.length >= 2)
            {
                String itemInputIdentifier = parts[0].trim();
                String rotDaysString = parts[parts.length - 1].trim();
                int rotDays = Integer.parseInt(rotDaysString);
                processInputItem(itemInputIdentifier, rotDays);

                if (parts.length > 2)
                {
                    String itemOutputIdentifier = parts[1].trim();
                    processOutputItem(itemInputIdentifier, itemOutputIdentifier);
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

    private static void processInputItem(String itemIdentifier, int rotDays)
    {
        String[] itemParts = itemIdentifier.split(":");
        if (itemParts.length >= 2)
        {
            String namespace = itemParts[0];
            String path = itemParts[1];

            if ("ore".equals(namespace))
            {
                List<ItemStack> oreItems = OreDictionary.getOres(path);
                for (ItemStack oreItem : oreItems)
                {
                    FOOD_EXPIRATION_DAYS.put(oreItem.getItem(), rotDays);
                    FoodSpoiling.LOGGER.debug("Added a lifetime of {} days to {}", rotDays, oreItem.getItem().getRegistryName());
                }
            }
            else
            {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(namespace, path));
                if (item == null) return;
                FOOD_EXPIRATION_DAYS.put(item, rotDays);
                FoodSpoiling.LOGGER.debug("Added a lifetime of {} days to {}", rotDays, item.getRegistryName());
            }
        }
    }

    private static void processOutputItem(String itemInputIdentifier, String itemOutputIdentifier)
    {
        String[] inputParts = itemInputIdentifier.split(":");
        String[] outputParts = itemOutputIdentifier.split(":");
        if (inputParts.length >= 2 && outputParts.length >= 2)
        {
            String namespaceInput = inputParts[0];
            String pathInput = inputParts[1];

            String namespaceOutput = outputParts[0];
            String pathOutput = outputParts[1];

            Item replacementItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(namespaceOutput, pathOutput));
            if (replacementItem == null) return;

            if ("ore".equals(namespaceInput))
            {
                List<ItemStack> oreItems = OreDictionary.getOres(pathInput);
                for (ItemStack oreItem : oreItems)
                {
                    FOOD_CONVERSIONS.put(oreItem.getItem(), replacementItem);
                    FoodSpoiling.LOGGER.debug("Added {} as a replacement for {}", replacementItem.getRegistryName(), oreItem.getItem().getRegistryName());
                }
            }
            else
            {
                Item originalItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemInputIdentifier));
                if (originalItem == null) return;
                FOOD_CONVERSIONS.put(originalItem, replacementItem);
                FoodSpoiling.LOGGER.debug("Added {} as a replacement for {}", replacementItem.getRegistryName(), originalItem.getRegistryName());
            }
        }
    }
}
