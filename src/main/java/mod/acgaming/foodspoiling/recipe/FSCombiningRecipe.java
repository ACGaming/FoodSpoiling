package mod.acgaming.foodspoiling.recipe;

import javax.annotation.Nonnull;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import mod.acgaming.foodspoiling.FoodSpoiling;
import mod.acgaming.foodspoiling.logic.FSData;
import mod.acgaming.foodspoiling.logic.FSLogic;

// Courtesy of BordListian
public class FSCombiningRecipe extends ShapelessOreRecipe
{
    public FSCombiningRecipe()
    {
        super(new ResourceLocation(FoodSpoiling.MOD_ID, "food_combining"), ItemStack.EMPTY);
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv)
    {
        ItemStack combinedStack = ItemStack.EMPTY;
        int matches = 0;
        long oldestDate = Long.MAX_VALUE;

        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack stack = inv.getStackInSlot(slot);

            if (stack.isEmpty()) continue;

            if (FSLogic.canRot(stack))
            {
                if (combinedStack.isEmpty())
                {
                    combinedStack = stack.copy();
                }
                else
                {
                    if (!ItemStack.areItemsEqual(stack, combinedStack))
                    {
                        return ItemStack.EMPTY;
                    }
                }

                long creationTime = FSData.getCreationTime(stack);
                oldestDate = Math.min(oldestDate, creationTime);
                matches++;
            }
        }

        if (matches < 2 || matches > combinedStack.getMaxStackSize()) return ItemStack.EMPTY;

        combinedStack.setCount(matches);
        FSData.setCreationTime(combinedStack, oldestDate);
        return combinedStack;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world)
    {
        ItemStack firstItem = ItemStack.EMPTY;
        int matches = 0;

        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack stack = inv.getStackInSlot(slot);

            if (stack.isEmpty() || !FSData.hasCreationTime(stack)) continue;

            if (FSLogic.canRot(stack))
            {
                if (firstItem.isEmpty())
                {
                    firstItem = stack.copy();
                }
                else
                {
                    if (!ItemStack.areItemsEqual(stack, firstItem))
                    {
                        return false;
                    }
                }
                matches++;
            }
        }

        return matches >= 2 && matches <= firstItem.getMaxStackSize();
    }
}
