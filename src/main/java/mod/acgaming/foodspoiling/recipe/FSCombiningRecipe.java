package mod.acgaming.foodspoiling.recipe;

import javax.annotation.Nonnull;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import mod.acgaming.foodspoiling.FoodSpoiling;
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
        ItemStack prevMatch = ItemStack.EMPTY;
        int matches = 0;
        long latestDate = Long.MAX_VALUE;

        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack stack = inv.getStackInSlot(slot);

            if (stack.isEmpty()) continue;

            if (FSLogic.canRot(stack))
            {
                if (prevMatch.isEmpty()) prevMatch = stack.copy();
                else
                {
                    long date = FSLogic.getCreationTime(stack);
                    latestDate = Math.min(latestDate, date);
                    FSLogic.setCreationTime(prevMatch, date);
                    if (stack.getItem() != prevMatch.getItem() || stack.getMetadata() != prevMatch.getMetadata() || !ItemStack.areItemStackTagsEqual(prevMatch, stack))
                    {
                        return ItemStack.EMPTY;
                    }
                }
                matches++;
            }
        }

        if (matches > prevMatch.getMaxStackSize()) return ItemStack.EMPTY;

        prevMatch.setCount(matches);
        FSLogic.setCreationTime(prevMatch, latestDate);

        return matches >= 2 ? prevMatch : ItemStack.EMPTY;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world)
    {
        ItemStack prevMatch = ItemStack.EMPTY;
        int matches = 0;

        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack stack = inv.getStackInSlot(slot);

            if (stack.isEmpty()) continue;

            if (FSLogic.canRot(stack))
            {
                if (prevMatch.isEmpty()) prevMatch = stack.copy();
                else
                {
                    FSLogic.setCreationTime(prevMatch, Math.min(FSLogic.getCreationTime(prevMatch), FSLogic.getCreationTime(stack)));
                    if (stack.getItem() != prevMatch.getItem() || stack.getMetadata() != prevMatch.getMetadata() || !ItemStack.areItemStackTagsEqual(prevMatch, stack))
                    {
                        return false;
                    }
                }
                matches++;
            }
        }

        if (matches > prevMatch.getMaxStackSize()) return false;

        return matches >= 2;
    }
}
