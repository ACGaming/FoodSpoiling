package mod.acgaming.foodspoiling.compat.toughasnails;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import mod.acgaming.foodspoiling.logic.FSLogic;
import toughasnails.api.TANPotions;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.config.json.DrinkData;
import toughasnails.init.ModConfig;
import toughasnails.thirst.ThirstHandler;

public class TANDrinkHandler
{
    private static void applyDrink(final EntityPlayer player, final int thirstRestored, final float hydrationRestored, final float poisonChance)
    {
        IThirst thirstStats = ThirstHelper.getThirstData(player);
        thirstStats.addStats(thirstRestored, hydrationRestored);

        if (!player.world.isRemote && (player.world.rand.nextFloat() < poisonChance) && SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST))
        {
            player.addPotionEffect(new PotionEffect(TANPotions.thirst, 600));
        }
    }

    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event)
    {
        if (event.getEntityLiving() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            ItemStack stack = event.getItem();
            ThirstHandler thirstHandler = (ThirstHandler) ThirstHelper.getThirstData(player);

            if (thirstHandler.isThirsty() && FSLogic.canRot(stack) == EnumActionResult.SUCCESS)
            {
                String registryName = stack.getItem().getRegistryName().toString();

                if (ModConfig.drinkData.containsKey(registryName))
                {
                    for (DrinkData drinkData : ModConfig.drinkData.get(registryName))
                    {
                        applyDrink(player, drinkData.getThirstRestored(), drinkData.getHydrationRestored(), drinkData.getPoisonChance());
                        break;
                    }
                }
            }
        }
    }
}
