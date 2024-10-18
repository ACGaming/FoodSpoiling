package mod.acgaming.foodspoiling.recipe;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

import mod.acgaming.foodspoiling.logic.FSData;
import mod.acgaming.foodspoiling.logic.FSLogic;

// Based on TerraFirmaCraft food stacking, courtesy of alcatrazEscapee
// https://github.com/TerraFirmaCraft/TerraFirmaCraft/blob/1.12.x/src/main/java/net/dries007/tfc/objects/recipes/FoodStackingRecipe.java
@SuppressWarnings("unused")
public class FSCombiningRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    /**
     * @return true if the inventory matches this recipe, false otherwise.
     * A recipe match occurs when all non-empty slots contain a similar partially spoiled stack and there are at least two slots filled.
     */
    @Override
    public boolean matches(InventoryCrafting inv, World world)
    {
        ItemStack firstStack = ItemStack.EMPTY;
        int matches = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack invStack = inv.getStackInSlot(i);
            if (!invStack.isEmpty() && FSLogic.canRot(invStack))
            {
                if (!FSData.hasCreationTime(invStack) && !FSData.hasRemainingLifetime(invStack))
                {
                    return false;
                }

                if (firstStack.isEmpty())
                {
                    firstStack = invStack;
                }
                else if (!ItemStack.areItemsEqual(invStack, firstStack))
                {
                    return false;
                }

                matches++;
            }
        }

        return matches > 1;
    }

    /**
     * Combines and returns a new ItemStack representing the result of all valid spoiling stacks in the given crafting inventory.
     * The resulting stack's count is the sum of all input stack counts and its creation time or remaining lifetime is updated to reflect the minimum time found among the input stacks.
     *
     * @param inv The crafting inventory containing the item stacks to be combined.
     * @return A new ItemStack representing the combined result, or an empty ItemStack if no valid combination is found.
     */
    @Override
    @Nonnull
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack resultStack = ItemStack.EMPTY;
        int outputAmount = 0;
        long minTime = -1;

        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && FSLogic.canRot(stack) && (FSData.hasCreationTime(stack) || FSData.hasRemainingLifetime(stack)))
            {
                outputAmount++;
                long currentTime = getMinimumTime(stack);

                if (minTime == -1 || currentTime < minTime)
                {
                    minTime = currentTime;
                }

                if (resultStack.isEmpty())
                {
                    resultStack = stack.copy();
                }
            }
        }

        if (resultStack.isEmpty())
        {
            return ItemStack.EMPTY;
        }

        resultStack.setCount(outputAmount);
        updateTime(resultStack, minTime);

        return resultStack;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }

    private long getMinimumTime(ItemStack stack)
    {
        if (FSData.hasCreationTime(stack))
        {
            return FSData.getCreationTime(stack);
        }
        else if (FSData.hasRemainingLifetime(stack))
        {
            return FSData.getRemainingLifetime(stack);
        }
        return -1;
    }

    private void updateTime(ItemStack resultStack, long minTime)
    {
        if (FSLogic.canRot(resultStack))
        {
            if (FSData.hasCreationTime(resultStack))
            {
                FSData.setCreationTime(resultStack, minTime);
            }
            else if (FSData.hasRemainingLifetime(resultStack))
            {
                FSData.setRemainingLifetime(resultStack, (int) minTime);
            }
        }
    }

    public static class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json)
        {
            return new FSCombiningRecipe();
        }
    }
}
