package mod.acgaming.foodspoiling.event;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import mod.acgaming.foodspoiling.FoodSpoiling;
import mod.acgaming.foodspoiling.config.FSConfig;
import mod.acgaming.foodspoiling.logic.FSLogic;
import mod.acgaming.foodspoiling.recipe.FSCombiningRecipe;

@Mod.EventBusSubscriber(modid = FoodSpoiling.MOD_ID)
public class FSEvents
{
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase != TickEvent.PlayerTickEvent.Phase.END || event.player.world.getTotalWorldTime() % FSConfig.GENERAL.checkIntervalInTicks != 0) return;
        FSLogic.updateInventory(event.player);
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        FSLogic.updateInventory(event.getEntityPlayer());
    }

    @SubscribeEvent
    public static void onRegisterRecipe(RegistryEvent.Register<IRecipe> event)
    {
        if (!FSConfig.ROTTING.allowFoodMerge) return;
        event.getRegistry().register(new FSCombiningRecipe().setRegistryName(new ResourceLocation(FoodSpoiling.MOD_ID, "food_combining")));
    }
}
