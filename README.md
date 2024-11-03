## Food Spoiling

###### Spoiled for choice!

A simple mod that checks for food items in any configurable inventory and increases the spoilage over time until they are considered rotten. Foul play!

### Technical details

Whenever a player first views an item, it gets a creation time applied to it. After that, the creation time is only compared to the current world time periodically. This way, it's as performance-friendly as it can get because nothing is ticked continuously and most rot indication is client-sided. If spoilage is paused via preserving containers or players logging off on multiplayer servers, a remaining lifetime value is applied to the item which gets calculated into a new creation time once spoilage is allowed to resume.

### Configuration

The mod is highly configurable. The default config file looks like this:

```
##########################################################################################################
# general
#--------------------------------------------------------------------------------------------------------#
# General settings for the Food Spoiling mod
##########################################################################################################

general {
    # How often player food items are evaluated in ticks
    # Increase this value to allow for easier stacking
    I:"Check Interval"=100

    # Length of each day in ticks
    I:"Day Length"=24000

    # Prints the class name of any container upon opening
    B:"Debug Container Class Printing"=false
}

##########################################################################################################
# rotting
#--------------------------------------------------------------------------------------------------------#
# Settings for rotting food items
##########################################################################################################

rotting {
    # Special conditions for containers to rot food in
    # Format: 'container_class,lifetime_factor'
    # The higher the lifetime factor, the slower the food will rot
    # Use a negative lifetime factor to prevent food from rotting
    S:"Container Conditions" <
        net.minecraft.inventory.ContainerChest,1.2
     >

    # Food items do not decay by default, and must be added below
    # Format: 'modid:food_item,modid:rotten_item,days' |OR| 'modid:food_item,-1' for explicit tooltip to state "Does not rot"
    # Instead of 'modid', 'ore' can be used as a namespace for ore dictionary names
    # Any item added here will be given a tooltip that says "Good for % days" when unspoiled
    S:"Days To Rot" <
        minecraft:porkchop,minecraft:rotten_flesh,3
        minecraft:fish,minecraft:rotten_flesh,3
        minecraft:beef,minecraft:rotten_flesh,3
        minecraft:chicken,minecraft:rotten_flesh,3
        minecraft:rabbit,minecraft:rotten_flesh,3
        minecraft:mutton,minecraft:rotten_flesh,3
        minecraft:cooked_porkchop,minecraft:rotten_flesh,7
        minecraft:cooked_fish,minecraft:rotten_flesh,7
        minecraft:cooked_beef,minecraft:rotten_flesh,7
        minecraft:cooked_chicken,minecraft:rotten_flesh,7
        minecraft:cooked_rabbit,minecraft:rotten_flesh,7
        minecraft:cooked_mutton,minecraft:rotten_flesh,7
        minecraft:apple,minecraft:air,5
        minecraft:golden_apple,-1
     >

    # Applies an increasing green tint on items as they rot
    B:"Render Rotten Overlay"=true

    # When 'Render Rotten Overlay' is enabled, it only applies on items that extend from ItemFood.class
    B:"Render Rotten Overlay Food Only"=true

    # Allows items specified in 'Days To Rot' to rot in creative mode
    # Already rotting items will continue to rot nonetheless
    B:"Rot In Creative Mode"=false

    # Allows items to rot in the player's inventory only
    B:"Rot In Player Inventory Only"=false
}

##########################################################################################################
# warning_message
#--------------------------------------------------------------------------------------------------------#
# Settings for warning messages to alert the player about rotting food
##########################################################################################################

warning_message {
    # The cooldown for sending a warning message in minutes
    I:"Message Cooldown"=1

    # The remaining food percentage for warning messages to send
    # Min: 1
    # Max: 100
    I:"Message Percentage"=10

    # Randomly chosen warning messages
    S:"Random Warning Messages" <
        Something in my inventory smells...
        My food is about to go bad!
        My food is about to rot...
     >

    # If false, sends as a chat message instead of the action bar
    B:"Send As Action Bar Messages"=true

    # Sends warning messages to players when one or more food items spoilage is above 'Message Percentage'
    B:"Send Warning Messages"=true
}

##########################################################################################################
# tooltips
#--------------------------------------------------------------------------------------------------------#
# Settings for tooltips on food items indicating spoilage status
##########################################################################################################

tooltips {
    # Shows a status tooltip on food items
    B:"Show Food Tooltip"=true

    # Shows remaining days until rotten
    B:"Show Remaining Days"=true

    # Shows remaining percentage until rotten
    B:"Show Remaining Percentage"=true
}
```