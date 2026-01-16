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
        @Config.Name("Affect Item Entities")
        @Config.Comment("Include dropped items in rot calculations")
        public boolean affectItemEntities = true;

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
                "com.mrcrayfish.furniture.gui.containers.ContainerEski,1.8",
                "com.mrcrayfish.furniture.gui.containers.ContainerFridge,1.8",
                "net.blay09.mods.cookingforblockheads.container.ContainerFridge,1.8",
                "sweetmagic.init.tile.container.ContainerFreezer,1.8",
                "noppes.npcs.containers.ContainerNPCTrader,-1",
                "noppes.npcs.containers.ContainerNPCTraderSetup,-1"
            };

        @Config.Name("Days To Rot")
        @Config.Comment
            ({
                "Food items with unique rotting behavior",
                "Format: 'modid:food_item[:meta],modid:rotten_item[:rotten_meta],days' |OR| 'modid:food_item[:meta],-1' for explicit tooltip to state \"Does not rot\"",
                "Instead of 'modid', 'ore' can be used as a namespace for ore dictionary names",
                "Any item added here will be given a tooltip that says \"Good for % days\" when unspoiled"
            })
        public String[] daysToRot =
            {
                "minecraft:apple,5",
                "minecraft:baked_potato,minecraft:poisonous_potato,5",
                "minecraft:beef,minecraft:rotten_flesh,3",
                "minecraft:beetroot,10",
                "minecraft:beetroot_soup,minecraft:bowl,4",
                "minecraft:bread,7",
                "minecraft:cake,3",
                "minecraft:carrot,10",
                "minecraft:chicken,minecraft:rotten_flesh,3",
                "minecraft:cooked_beef,minecraft:rotten_flesh,4",
                "minecraft:cooked_chicken,minecraft:rotten_flesh,4",
                "minecraft:cooked_fish,minecraft:rotten_flesh,4",
                "minecraft:cooked_mutton,minecraft:rotten_flesh,4",
                "minecraft:cooked_porkchop,minecraft:rotten_flesh,4",
                "minecraft:cooked_rabbit,minecraft:rotten_flesh,4",
                "minecraft:cookie,5",
                "minecraft:fish,minecraft:rotten_flesh,3",
                "minecraft:golden_apple,-1",
                "minecraft:golden_carrot,-1",
                "minecraft:melon,3",
                "minecraft:mushroom_stew,minecraft:bowl,3",
                "minecraft:mutton,minecraft:rotten_flesh,3",
                "minecraft:poisonous_potato,-1",
                "minecraft:porkchop,minecraft:rotten_flesh,3",
                "minecraft:potato,minecraft:poisonous_potato,10",
                "minecraft:pumpkin_pie,4",
                "minecraft:rabbit,minecraft:rotten_flesh,3",
                "minecraft:rabbit_stew,minecraft:bowl,4",
                "minecraft:rotten_flesh,-1",
                "minecraft:spider_eye,-1",

                // Miscellaneous
                "vanillaplus:pumpkin_slice,5",
                "inspirations:potato_soup,minecraft:bowl,4",
                "charm:suspicious_soup,minecraft:bowl,4",

                // Pam's HarvestCraft Berries
                "harvestcraft:blackberryitem,3",
                "harvestcraft:blueberryitem,3",
                "harvestcraft:candleberryitem,3",
                "harvestcraft:raspberryitem,3",
                "harvestcraft:strawberryitem,3",
                "harvestcraft:cranberryitem,3",
                "harvestcraft:elderberryitem,3",
                "harvestcraft:gooseberryitem,3",
                "harvestcraft:mulberryitem,3",
                "harvestcraft:boysenberryitem,3",
                "harvestcraft:juniperberryitem,3",
                "harvestcraft:gojiberryitem,3",
                "harvestcraft:cloudberryitem,3",
                "harvestcraft:huckleberryitem,3",

                // Pam's HarvestCraft Fruits
                "harvestcraft:apricotitem,5",
                "harvestcraft:avocadoitem,5",
                "harvestcraft:bananaitem,7",
                "harvestcraft:cashewitem,6",
                "harvestcraft:cherryitem,5",
                "harvestcraft:chestnutitem,5",
                "harvestcraft:coconutitem,5",
                "harvestcraft:dateitem,5",
                "harvestcraft:dragonfruititem,5",
                "harvestcraft:durianitem,5",
                "harvestcraft:figitem,5",
                "harvestcraft:grapefruititem,5",
                "harvestcraft:grapeitem,5",
                "harvestcraft:jackfruititem,5",
                "harvestcraft:kiwiitem,5",
                "harvestcraft:lemonitem,5",
                "harvestcraft:limeitem,5",
                "harvestcraft:mangoitem,5",
                "harvestcraft:orangeitem,5",
                "harvestcraft:papayaitem,5",
                "harvestcraft:passionfruititem,5",
                "harvestcraft:peachitem,5",
                "harvestcraft:pearitem,5",
                "harvestcraft:persimmonitem,5",
                "harvestcraft:plumitem,5",
                "harvestcraft:pomegranateitem,5",
                "harvestcraft:pineappleitem,5",
                "harvestcraft:rambutanitem,5",
                "harvestcraft:soursopitem,5",
                "harvestcraft:starfruititem,5",
                "harvestcraft:tamarinditem,5",

                // Pam's HarvestCraft Basic Food Items
                "harvestcraft:asparagusitem,4",
                "harvestcraft:beetitem,4",
                "harvestcraft:bellpepperitem,4",
                "harvestcraft:broccoliitem,4",
                "harvestcraft:brusselsproutitem,4",
                "harvestcraft:cabbageitem,4",
                "harvestcraft:caulifloweritem,4",
                "harvestcraft:celeryitem,4",
                "harvestcraft:chilipepperitem,4",
                "harvestcraft:cornitem,4",
                "harvestcraft:cucumberitem,4",
                "harvestcraft:eggplantitem,4",
                "harvestcraft:garlicitem,10",
                "harvestcraft:gingeritem,10",
                "harvestcraft:leekitem,4",
                "harvestcraft:lettuceitem,7",
                "harvestcraft:mustarditem,4",
                "harvestcraft:okraitem,4",
                "harvestcraft:onionitem,30",
                "harvestcraft:parsnipitem,4",
                "harvestcraft:peanutitem,180",
                "harvestcraft:peasitem,4",
                "harvestcraft:pineappleseeditem,4",
                "harvestcraft:radishitem,4",
                "harvestcraft:rhubarbitem,4",
                "harvestcraft:rutabagaitem,4",
                "harvestcraft:scallionitem,4",
                "harvestcraft:seaweeditem,4",
                "harvestcraft:spinachitem,4",
                "harvestcraft:sweetpotatoitem,10",
                "harvestcraft:tomatoitem,5",
                "harvestcraft:turnipitem,4",
                "harvestcraft:waterchestnutitem,4",
                "harvestcraft:whitemushroomitem,3",
                "harvestcraft:wintersquashitem,30",
                "harvestcraft:zucchiniitem,4",
                "harvestcraft:artichokeitem,4",
                "harvestcraft:bambooshootitem,4",
                "harvestcraft:kohlrabiitem,4",
                "harvestcraft:chickpeaitem,180",
                "harvestcraft:lentilitem,180",
                "harvestcraft:wintersquashitem,30",
                "harvestcraft:rutabagaitem,30",
                "harvestcraft:kaleitem,7",
                "harvestcraft:cassavaitem,4",

                // Pam's HarvestCraft Raw Items
                "harvestcraft:anchovyrawitem,3",
                "harvestcraft:bassrawitem,3",
                "harvestcraft:carprawitem,3",
                "harvestcraft:catfishrawitem,3",
                "harvestcraft:charrrawitem,3",
                "harvestcraft:clamrawitem,3",
                "harvestcraft:crabrawitem,3",
                "harvestcraft:crayfishrawitem,3",
                "harvestcraft:eelrawitem,3",
                "harvestcraft:frograwitem,3",
                "harvestcraft:grouperrawitem,3",
                "harvestcraft:herringrawitem,3",
                "harvestcraft:jellyfishrawitem,3",
                "harvestcraft:mudfishrawitem,3",
                "harvestcraft:octopusrawitem,3",
                "harvestcraft:perchrawitem,3",
                "harvestcraft:scalloprawitem,3",
                "harvestcraft:shrimprawitem,3",
                "harvestcraft:snailrawitem,3",
                "harvestcraft:snapperrawitem,3",
                "harvestcraft:tilapiarawitem,3",
                "harvestcraft:troutrawitem,3",
                "harvestcraft:tunarawitem,3",
                "harvestcraft:turtlerawitem,3",
                "harvestcraft:walleyerawitem,3",
                "harvestcraft:calamarirawitem,3",
                "harvestcraft:turkeyrawitem,3",
                "harvestcraft:venisonrawitem,3",
                "harvestcraft:duckrawitem,3",
                "harvestcraft:terrawortseeditem,3",

                // Pam's HarvestCraft Meals and Dishes
                "harvestcraft:anchovycookeditem,4",
                "harvestcraft:basscookeditem,4",
                "harvestcraft:carpcookeditem,4",
                "harvestcraft:catfishcookeditem,4",
                "harvestcraft:charrcookeditem,4",
                "harvestcraft:clamcookeditem,4",
                "harvestcraft:crabcookeditem,4",
                "harvestcraft:crayfishcookeditem,4",
                "harvestcraft:eelcookeditem,4",
                "harvestcraft:frogcookeditem,4",
                "harvestcraft:groupercookeditem,4",
                "harvestcraft:herringcookeditem,4",
                "harvestcraft:jellyfishcookeditem,4",
                "harvestcraft:mudfishcookeditem,4",
                "harvestcraft:octopuscookeditem,4",
                "harvestcraft:perchcookeditem,4",
                "harvestcraft:scallopcookeditem,4",
                "harvestcraft:shrimpcookeditem,4",
                "harvestcraft:snailcookeditem,4",
                "harvestcraft:snappercookeditem,4",
                "harvestcraft:tilapiacookeditem,4",
                "harvestcraft:troutcookeditem,4",
                "harvestcraft:tunacookeditem,4",
                "harvestcraft:turtlecookeditem,4",
                "harvestcraft:walleyecookeditem,4",
                "harvestcraft:calamaricookeditem,4",
                "harvestcraft:turkeycookeditem,4",
                "harvestcraft:venisoncookeditem,4",
                "harvestcraft:duckcookeditem,4",
                "harvestcraft:applejellyitem,90",
                "harvestcraft:applejuiceitem,7",
                "harvestcraft:applepieitem,3",
                "harvestcraft:applesauceitem,7",
                "harvestcraft:applecideritem,14",
                "harvestcraft:asparagussoupitem,minecraft:bowl,4",
                "harvestcraft:baconandeggsitem,4",
                "harvestcraft:bakedbeansitem,5",
                "harvestcraft:bakedhamitem,5",
                "harvestcraft:bakedsweetpotatoitem,7",
                "harvestcraft:banananutbreaditem,7",
                "harvestcraft:bananasmoothieitem,1",
                "harvestcraft:bananasplititem,1",
                "harvestcraft:bbqchickenpizzaitem,3",
                "harvestcraft:bbqpotatochipsitem,5",
                "harvestcraft:bbqsauceitem,12",
                "harvestcraft:beansandriceitem,4",
                "harvestcraft:beefjerkyitem,30",
                "harvestcraft:beefwellingtonitem,5",
                "harvestcraft:beetburgeritem,4",
                "harvestcraft:blackberrycobbleritem,3",
                "harvestcraft:blackberryjellyitem,90",
                "harvestcraft:blackberryjuiceitem,7",
                "harvestcraft:blackberrypieitem,3",
                "harvestcraft:blackberrysmoothieitem,3",
                "harvestcraft:blackberryteaitem,6",
                "harvestcraft:blackberrywinessenceitem,6",
                "harvestcraft:blueberryjellyitem,90",
                "harvestcraft:blueberryjuiceitem,7",
                "harvestcraft:blueberrymuffinitem,3",
                "harvestcraft:blueberrypancakesitem,3",
                "harvestcraft:blueberrypieitem,3",
                "harvestcraft:blueberrysmoothieitem,3",
                "harvestcraft:blueberryteaitem,6",
                "harvestcraft:blueberrywinessenceitem,6",
                "harvestcraft:breadedporkchopitem,5",
                "harvestcraft:breakfastburritoitem,4",
                "harvestcraft:broccolimacitem,4",
                "harvestcraft:brownieitem,3",
                "harvestcraft:brusselsproutitem,4",
                "harvestcraft:buttercookieitem,7",
                "harvestcraft:butteritem,30",
                "harvestcraft:caramelitem,14",
                "harvestcraft:carrotcakeitem,5",
                "harvestcraft:carrotsoupitem,minecraft:bowl,4",
                "harvestcraft:celeryandpeanutbutteritem,5",
                "harvestcraft:cheeseburgeritem,3",
                "harvestcraft:cheeseontoastitem,4",
                "harvestcraft:cherrycheesecakeitem,4",
                "harvestcraft:cherrypieitem,3",
                "harvestcraft:chickencelerycasseroleitem,4",
                "harvestcraft:chickenchowmeinitem,4",
                "harvestcraft:chickencurryitem,4",
                "harvestcraft:chickengumboitem,4",
                "harvestcraft:chickenparmasanitem,4",
                "harvestcraft:chickenpotpieitem,4",
                "harvestcraft:chickensandwichitem,4",
                "harvestcraft:chickennoodlesoupitem,minecraft:bowl,1",
                "harvestcraft:chilipoppersitem,4",
                "harvestcraft:chocolatebaconitem,4",
                "harvestcraft:chocolatebaritem,5",
                "harvestcraft:chocolatecaramelfudgeitem,7",
                "harvestcraft:chocolatecherryitem,5",
                "harvestcraft:chocolatedonutitem,3",
                "harvestcraft:chocolateicecreamitem,1",
                "harvestcraft:chocolatemilkitem,7",
                "harvestcraft:chocolatemilkshakeitem,3",
                "harvestcraft:chocolatemousseitem,5",
                "harvestcraft:chocolateorangeitem,5",
                "harvestcraft:chocolatepuddingitem,4",
                "harvestcraft:chocolaterollitem,4",
                "harvestcraft:chocolatesprinklecakeitem,4",
                "harvestcraft:chocolatestrawberryitem,4",
                "harvestcraft:chocolateyogurtitem,4",
                "harvestcraft:coconutcreamitem,5",
                "harvestcraft:coconutmilkitem,5",
                "harvestcraft:coconutshrimpitem,5",
                "harvestcraft:coconutyogurtitem,4",
                "harvestcraft:coffeeconlecheitem,5",
                "harvestcraft:coffeecupitem,4",
                "harvestcraft:cornbreaditem,7",
                "harvestcraft:crackeritem,14",
                "harvestcraft:creepercookieitem,7",
                "harvestcraft:croissantitem,5",
                "harvestcraft:curryitem,4",
                "harvestcraft:deluxecheeseburgeritem,3",
                "harvestcraft:delightedmealitem,3",
                "harvestcraft:durianmilkshakeitem,1",
                "harvestcraft:eggplantparmitem,4",
                "harvestcraft:extremechiliitem,4",
                "harvestcraft:fishnchipsitem,3",
                "harvestcraft:fishsandwichitem,3",
                "harvestcraft:fishandchipsitem,3",
                "harvestcraft:friedchickenitem,5",
                "harvestcraft:friedeggitem,3",
                "harvestcraft:friesitem,3",
                "harvestcraft:fruitpunchitem,3",
                "harvestcraft:garlicbreaditem,5",
                "harvestcraft:garlicchickenitem,4",
                "harvestcraft:gingerbreaditem,7",
                "harvestcraft:gingersnapsitem,7",
                "harvestcraft:glazedcarrotsitem,4",
                "harvestcraft:grilledcheeseitem,3",
                "harvestcraft:grilledmushroomitem,3",
                "harvestcraft:guacamoleitem,2",
                "harvestcraft:hamburgeritem,3",
                "harvestcraft:heartybreakfastitem,3",
                "harvestcraft:herbbutterparsnipsitem,4",
                "harvestcraft:honeybreaditem,7",
                "harvestcraft:honeybunitem,5",
                "harvestcraft:honeyglazedcarrotsitem,4",
                "harvestcraft:honeyglazedhamitem,5",
                "harvestcraft:honeysandwichitem,4",
                "harvestcraft:hotandsoursoupitem,minecraft:bowl,3",
                "harvestcraft:hotchocolateitem,3",
                "harvestcraft:hotwingsitem,4",
                "harvestcraft:hummusitem,5",
                "harvestcraft:icecreamsandwichitem,1",
                "harvestcraft:jelliedeelitem,7",
                "harvestcraft:keylimepieitem,3",
                "harvestcraft:kungpaochickenitem,4",
                "harvestcraft:lasagnaitem,4",
                "harvestcraft:lemonbaritem,3",
                "harvestcraft:lemonchickenitem,4",
                "harvestcraft:lemoncupcakeitem,3",
                "harvestcraft:lemondrizzlecakeitem,4",
                "harvestcraft:lemonmeringueitem,3",
                "harvestcraft:lobsterthermidoritem,4",
                "harvestcraft:mapleoatmealitem,3",
                "harvestcraft:mashedpotatoesitem,4",
                "harvestcraft:meatloafitem,5",
                "harvestcraft:mincepieitem,3",
                "harvestcraft:mintchocolatechipicecreamitem,1",
                "harvestcraft:mixturesaladitem,3",
                "harvestcraft:mochicakeitem,4",
                "harvestcraft:mushroomrisottoitem,4",
                "harvestcraft:nachoesitem,3",
                "harvestcraft:oatmealcookieitem,7",
                "harvestcraft:oatmealitem,3",
                "harvestcraft:okrachipsitem,5",
                "harvestcraft:oldworldveggiesoupitem,minecraft:bowl,3",
                "harvestcraft:omeletitem,3",
                "harvestcraft:onionhamburgeritem,3",
                "harvestcraft:onionsoupitem,minecraft:bowl,3",
                "harvestcraft:pancakesitem,3",
                "harvestcraft:papayajuiceitem,7",
                "harvestcraft:peanutbuttercookiesitem,7",
                "harvestcraft:peanutbuttercupitem,7",
                "harvestcraft:peaandhamsoupitem,minecraft:bowl,3",
                "harvestcraft:peachcobbleritem,3",
                "harvestcraft:pepperoniitem,14",
                "harvestcraft:pickledbeetsitem,14",
                "harvestcraft:pickledonionsitem,14",
                "harvestcraft:picklesitem,14",
                "harvestcraft:pinacoladaitem,3",
                "harvestcraft:pineappleupsidedowncakeitem,4",
                "harvestcraft:plaintainitem,4",
                "harvestcraft:popcornitem,7",
                "harvestcraft:porkrindsitem,7",
                "harvestcraft:porksausageitem,5",
                "harvestcraft:potatoandcheesepirogiitem,4",
                "harvestcraft:potatocakesitem,4",
                "harvestcraft:potatosalادitem,3",
                "harvestcraft:potatosoupitem,minecraft:bowl,3",
                "harvestcraft:pumpkinbreaditem,7",
                "harvestcraft:pumpkinmuffinitem,4",
                "harvestcraft:pumpkinoatsconesitem,4",
                "harvestcraft:pumpkinpieitem,3",
                "harvestcraft:raisincookiesitem,7",
                "harvestcraft:raspberrypieitem,3",
                "harvestcraft:ratatouilleitem,4",
                "harvestcraft:rawtofaconitem,3",
                "harvestcraft:rawtofeakitem,3",
                "harvestcraft:rawtofuttonitem,3",
                "harvestcraft:ricepuddingitem,3",
                "harvestcraft:roastchickenitem,5",
                "harvestcraft:roastedpumpkinseedsitem,7",
                "harvestcraft:salmonpattiesitem,4",
                "harvestcraft:sausageinbreaditem,4",
                "harvestcraft:sausageitem,5",
                "harvestcraft:sausagerollitem,4",
                "harvestcraft:scrambledeggitem,3",
                "harvestcraft:sesameballitem,4",
                "harvestcraft:sesamesnapsitem,7",
                "harvestcraft:shepherdspieitem,4",
                "harvestcraft:softpretzelandmustarditem,4",
                "harvestcraft:softpretzelitem,4",
                "harvestcraft:spinachpieitem,4",
                "harvestcraft:springsaladitem,3",
                "harvestcraft:steamedpeasitem,4",
                "harvestcraft:steamedspinachitem,4",
                "harvestcraft:stuffedeggplantitem,4",
                "harvestcraft:stuffedmushroomitem,4",
                "harvestcraft:stuffedpepperitem,4",
                "harvestcraft:supremepizzaitem,3",
                "harvestcraft:sushiitem,2",
                "harvestcraft:sweetandsoursauceitem,7",
                "harvestcraft:sweetpotatopieitem,3",
                "harvestcraft:sweetpotatosouffleitem,3",
                "harvestcraft:tacoitem,3",
                "harvestcraft:teriyakichickenitem,4",
                "harvestcraft:toastitem,4",
                "harvestcraft:toastedsandwichitem,4",
                "harvestcraft:tomatosoupitem,minecraft:bowl,3",
                "harvestcraft:tortillaitem,7",
                "harvestcraft:vegemiteitem,180",
                "harvestcraft:vegetablesoupitem,minecraft:bowl,3",
                "harvestcraft:vindalooitem,4",
                "harvestcraft:wafflesitem,3",
                "harvestcraft:watermelonjellyitem,90",
                "harvestcraft:zestyzucchiniitem,4",
                "harvestcraft:zucchinibreaditem,7",
                "harvestcraft:zucchinifriesitem,3",
                "harvestcraft:saltedsunflowerseedsitem,180",
                "harvestcraft:saltedpistachioitem,180",
                "harvestcraft:pickledbeansitem,14",
                "harvestcraft:pickledcabbageitem,14",
                "harvestcraft:pickledcornitem,14",
                "harvestcraft:pickledcucumberitem,14",
                "harvestcraft:pickledonionitem,14",
                "harvestcraft:pickledpepperitem,14",
                "harvestcraft:roastedchestnutitem,14",
                "harvestcraft:roastedpeanutitem,180",
                "harvestcraft:saltandvinegarchipsitem,14",

                // Additional Pam's HarvestCraft Meals and Food Items
                "harvestcraft:ricesoupitem,minecraft:bowl,3",
                "harvestcraft:friedriceitem,4",
                "harvestcraft:mushroomsoupitem,minecraft:bowl,2",
                "harvestcraft:chickennoodlesoupitem,minecraft:bowl,3",
                "harvestcraft:spaghettiitem,4",
                "harvestcraft:spaghettiandmeatballsitem,4",
                "harvestcraft:tomatosoupitem,minecraft:bowl,3",
                "harvestcraft:chickenparmesamitem,4",
                "harvestcraft:pizzaitem,3",
                "harvestcraft:cheeseburgeritem,3",
                "harvestcraft:chickenburgeritem,3",
                "harvestcraft:fishburgeritem,3",
                "harvestcraft:baconcheeseburgeritem,3",
                "harvestcraft:hotdogitem,3",
                "harvestcraft:baconandeggsitem,3",
                "harvestcraft:macncheeseitem,4",
                "harvestcraft:cornonthecobitem,4",
                "harvestcraft:potatosaladitem,3",
                "harvestcraft:eggsaladitem,3",
                "harvestcraft:fruitsaladitem,2",
                "harvestcraft:springrollitem,3",
                "harvestcraft:meatpieitem,4",
                "harvestcraft:cottoncandyitem,7",
                "harvestcraft:cookiesitem,7",
                "harvestcraft:snickerdoodleitem,7",
                "harvestcraft:cinnamonrollitem,4",
                "harvestcraft:cinnamonbreaditem,7",
                "harvestcraft:cornflakesitem,7",
                "harvestcraft:crackersitem,14",
                "harvestcraft:ediblerootitem,4",
                "harvestcraft:ediblefloweritem,2",
                "harvestcraft:rawtofishitem,3",
                "harvestcraft:rawtofickenitem,3",
                "harvestcraft:rawtobeefitem,3",
                "harvestcraft:rawtofabbititem,3",
                "harvestcraft:rawtoffalkitem,3",
                "harvestcraft:rawtofurkeyitem,3",
                "harvestcraft:rawtofenisonitem,3",
                "harvestcraft:rawtofuduckitem,3",
                "harvestcraft:cookedtofishitem,7",
                "harvestcraft:cookedtofickenitem,7",
                "harvestcraft:cookedtobeefitem,7",
                "harvestcraft:cookedtofabbititem,7",
                "harvestcraft:cookedtoffalkitem,7",
                "harvestcraft:cookedtofurkeyitem,7",
                "harvestcraft:cookedtofenisonitem,7",
                "harvestcraft:cookedtofuduckitem,7",
                "harvestcraft:ricecakeitem,4",
                "harvestcraft:teaitem,4",
                "harvestcraft:coffeeitem,4",
                "harvestcraft:gingersodaitem,3",
                "harvestcraft:rootbeeritem,3",
                "harvestcraft:grapesodaitem,3",
                "harvestcraft:colaitem,3",
                "harvestcraft:energydrinkitem,3",
                "harvestcraft:hotcocoaitem,3",
                "harvestcraft:chocolatemilkitem,3",
                "harvestcraft:strawberrymilkitem,3",
                "harvestcraft:grapejuiceitem,7",
                "harvestcraft:cranberryjuiceitem,7",
                "harvestcraft:cherryjuiceitem,7",
                "harvestcraft:plumjuiceitem,7",
                "harvestcraft:pearsmoothieitem,1",
                "harvestcraft:apricotsmoothieitem,1",
                "harvestcraft:figjuiceitem,7",
                "harvestcraft:mangojuiceitem,7",
                "harvestcraft:orangejuiceitem,7",
                "harvestcraft:peachjuiceitem,7",
                "harvestcraft:limejuiceitem,7",
                "harvestcraft:pomegranatejuiceitem,7",
                "harvestcraft:mangosmoothieitem,1",
                "harvestcraft:grapefruitsmoothieitem,1",
                "harvestcraft:persimmonsmoothieitem,1",
                "harvestcraft:gooseberrysmoothieitem,1",
                "harvestcraft:applejellysandwichitem,3",
                "harvestcraft:blackberryjellysandwichitem,3",
                "harvestcraft:blueberryjellysandwichitem,3",
                "harvestcraft:cherryjellysandwichitem,3",
                "harvestcraft:cranberryjellysandwichitem,3",
                "harvestcraft:kiwijellysandwichitem,3",
                "harvestcraft:lemonjellysandwichitem,3",
                "harvestcraft:limejellysandwichitem,3",
                "harvestcraft:mangojellysandwichitem,3",
                "harvestcraft:orangejellysandwichitem,3",
                "harvestcraft:papayajellysandwichitem,3",
                "harvestcraft:peachjellysandwichitem,3",
                "harvestcraft:pomegranatejellysandwichitem,3",
                "harvestcraft:starfruitjellysandwichitem,3",
                "harvestcraft:strawberryjellysandwichitem,3",
                "harvestcraft:watermelonjellysandwichitem,3",
                "harvestcraft:caramelicecreamitem,1",
                "harvestcraft:mintchocolatechipicemcreamitem,1",
                "harvestcraft:strawberryicecreamitem,1",
                "harvestcraft:vanillaicecreamitem,1",
                "harvestcraft:ediblerootitem,4",
                "harvestcraft:sunflowerseedsitem,180",
                "harvestcraft:vanillaitem,180",
                "harvestcraft:saltitem,-1",
                "harvestcraft:vinegaritem,-1",
                "harvestcraft:oliveoilitem,90",
                "harvestcraft:mayoitem,30",
                "harvestcraft:mustardseedsitem,180",
                "harvestcraft:blackpepperitem,180",
                "harvestcraft:groundcinnamonitem,180",
                "harvestcraft:groundnutmegitem,180",
                "harvestcraft:peanutbutteritem,90",
                "harvestcraft:pistachiobutteritem,90",
                "harvestcraft:almondbutteritem,90",
                "harvestcraft:cashewbutteritem,90",
                "harvestcraft:chestnutbutteritem,90",
                "harvestcraft:cornmealitem,180",
                "harvestcraft:doughitem,1",
                "harvestcraft:flouritem,180",
                "harvestcraft:heavycreamitem,3",
                "harvestcraft:honeycombitem,180",
                "harvestcraft:honeyitem,180",
                "harvestcraft:mayonnaisitem,30",
                "harvestcraft:oliveoilitem,90",
                "harvestcraft:pumkinseedsitem,180",
                "harvestcraft:sesameoilitem,90",
                "harvestcraft:stockitem,7",
                "harvestcraft:sunfloweroilitem,90",
                "harvestcraft:vanillaextractitem,180",
                "harvestcraft:freshwateritem,-1",
                "harvestcraft:freshmilkitem,3",
                "harvestcraft:saltitem,-1",
                "harvestcraft:vinegaritem,-1"
            };

        @Config.Name("Default Food Rotting")
        @Config.Comment("Allows all items that extend from ItemFood.class to rot when not specified in 'Days To Rot'")
        public boolean defaultFoodRotting = true;

        @Config.Name("Default Food Rotting Days")
        @Config.Comment
            ({
                "Specified days for all items that extend from ItemFood.class to rot when not specified in 'Days To Rot'",
                "Requires 'Default Food Rotting' to be enabled"
            })
        public int defaultFoodRottingDays = 7;

        @Config.RequiresMcRestart
        @Config.Name("Render Rotten Overlay")
        @Config.Comment("Applies an increasing green tint on items as they rot")
        public boolean renderRottenState = true;

        @Config.Name("Render Rotten Overlay Food Only")
        @Config.Comment("When 'Render Rotten Overlay' is enabled, it only applies on items that extend from ItemFood.class")
        public boolean renderRottenStateFoodOnly = true;

        @Config.Name("Rot In Creative Mode")
        @Config.Comment
            ({
                "Allows items specified in 'Days To Rot' to rot in creative mode",
                "Already rotting items will continue to rot nonetheless"
            })
        public boolean rotInCreative = false;

        @Config.Name("Rot In Player Inventory Only")
        @Config.Comment("Allows items to rot in the player's inventory only")
        public boolean rotInPlayerInvOnly = false;
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
