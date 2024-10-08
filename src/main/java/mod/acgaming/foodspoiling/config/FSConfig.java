package mod.acgaming.foodspoiling.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import mod.acgaming.foodspoiling.FoodSpoiling;
import mod.acgaming.foodspoiling.logic.FSMaps;

@Config(modid = FoodSpoiling.MOD_ID, name = "FoodSpoiling")
public class FSConfig
{
    @Config.Comment("General settings for the Food Spoiling mod")
    public static final General GENERAL = new General();

    @Config.Comment("Settings for rotting food items")
    public static final Rotting ROTTING = new Rotting();

    @Config.Comment("Settings for warning messages to alert the player about rotting food")
    public static final WarningMessage WARNING_MESSAGE = new WarningMessage();

    @Config.Comment("Settings for tooltips on food items indicating spoilage status")
    public static final Tooltips TOOLTIPS = new Tooltips();

    public static class General
    {
        @Config.Name("Check Interval")
        @Config.Comment
            ({
                "How often player food items are evaluated in ticks",
                "Increase this value to allow for easier stacking"
            })
        public int checkIntervalInTicks = 100;

        @Config.Name("Day Length")
        @Config.Comment("Length of each day in ticks")
        public int dayLengthInTicks = 24000;

        @Config.Name("Debug Container Class Printing")
        @Config.Comment("Prints the class name of any container upon opening")
        public boolean debugContainerClass = false;
    }

    public static class Rotting
    {
        @Config.RequiresMcRestart
        @Config.Name("Allow Food Merge")
        @Config.Comment("Allows merging the same rotting items with different rot times (uses worse food rot time)")
        public boolean allowFoodMerge = true;

        @Config.Name("Container Conditions")
        @Config.Comment
            ({
                "Special conditions for containers to rot food in",
                "Format: 'container_class,lifetime_factor'",
                "The higher the lifetime factor, the slower the food will rot",
                "Use a negative lifetime factor to prevent food from rotting"
            })
        public String[] containerConditions =
            {
                "net.minecraft.inventory.ContainerChest,1.2",
            };

        @Config.Name("Days To Rot")
        @Config.Comment
            ({
                "Food items do not decay by default, and must be added below",
                "Format: 'modid:food_item,modid:rotten_item,days' |OR| 'modid:food_item,-1' for explicit tooltip to state \"Does not rot\"",
                "Instead of 'modid', 'ore' can be used as a namespace for ore dictionary names",
                "Any item added here will be given a tooltip that says \"Good for % days\" when unspoiled"
            })
        public String[] daysToRot =
            {
                "minecraft:porkchop,minecraft:rotten_flesh,3",
                "minecraft:fish,minecraft:rotten_flesh,3",
                "minecraft:beef,minecraft:rotten_flesh,3",
                "minecraft:chicken,minecraft:rotten_flesh,3",
                "minecraft:rabbit,minecraft:rotten_flesh,3",
                "minecraft:mutton,minecraft:rotten_flesh,3",
                "minecraft:cooked_porkchop,minecraft:rotten_flesh,7",
                "minecraft:cooked_fish,minecraft:rotten_flesh,7",
                "minecraft:cooked_beef,minecraft:rotten_flesh,7",
                "minecraft:cooked_chicken,minecraft:rotten_flesh,7",
                "minecraft:cooked_rabbit,minecraft:rotten_flesh,7",
                "minecraft:cooked_mutton,minecraft:rotten_flesh,7",
                "minecraft:apple,minecraft:air,5",
                "minecraft:golden_apple,-1"
            };

        @Config.RequiresMcRestart
        @Config.Name("Render Rotten State")
        @Config.Comment("Applies an increasing green tint on food items as they rot")
        public boolean renderRottenState = true;

        @Config.Name("Rot In Creative Mode")
        @Config.Comment
            ({
                "Allows items specified in 'Days To Rot' to rot in creative mode",
                "Already rotting items will continue to rot nonetheless"
            })
        public boolean rotInCreative = false;
    }

    public static class WarningMessage
    {
        @Config.Name("Message Cooldown")
        @Config.Comment("The cooldown for sending a warning message in minutes")
        public int messageCooldownMinutes = 1;

        @Config.Name("Message Percentage")
        @Config.Comment("The remaining food percentage for warning messages to send")
        @Config.RangeInt(min = 1, max = 100)
        public int messagePercentage = 10;

        @Config.Name("Random Warning Messages")
        @Config.Comment("Randomly chosen warning messages")
        public String[] randomMessages =
            {
                "Something in my inventory smells...",
                "My food is about to go bad!",
                "My food is about to rot..."
            };

        @Config.Name("Send As Action Bar Messages")
        @Config.Comment("If false, sends as a chat message instead of the action bar")
        public boolean sendMessagesActionBar = true;

        @Config.Name("Send Warning Messages")
        @Config.Comment("Sends warning messages to players when one or more food items spoilage is above 'Message Percentage'")
        public boolean sendMessages = true;
    }

    public static class Tooltips
    {
        @Config.Name("Show Food Tooltip")
        @Config.Comment("Shows a status tooltip on food items")
        public boolean showFoodTooltip = true;

        @Config.Name("Show Remaining Days")
        @Config.Comment("Shows remaining days until rotten")
        public boolean tooltipFoodDays = true;

        @Config.Name("Show Remaining Percentage")
        @Config.Comment("Shows remaining percentage until rotten")
        public boolean tooltipFoodPercent = true;
    }

    @Mod.EventBusSubscriber(modid = FoodSpoiling.MOD_ID)
    public static class EventHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(FoodSpoiling.MOD_ID))
            {
                ConfigManager.sync(FoodSpoiling.MOD_ID, Config.Type.INSTANCE);
                FSMaps.initializeFoodMaps();
                FSMaps.initializeContainerConditions();
            }
        }
    }
}
