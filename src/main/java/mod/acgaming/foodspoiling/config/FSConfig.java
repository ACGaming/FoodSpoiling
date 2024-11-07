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
                "sweetmagic.init.tile.container.ContainerFreezer,1.8"
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
                "minecraft:apple,minecraft:air,5",
                "minecraft:baked_potato,minecraft:poisonous_potato,5",
                "minecraft:beef,minecraft:rotten_flesh,3",
                "minecraft:beetroot,minecraft:air,10",
                "minecraft:beetroot_soup,minecraft:bowl,4",
                "minecraft:bread,minecraft:air,7",
                "minecraft:cake,minecraft:air,3",
                "minecraft:carrot,minecraft:air,10",
                "minecraft:chicken,minecraft:rotten_flesh,3",
                "minecraft:cooked_beef,minecraft:rotten_flesh,4",
                "minecraft:cooked_chicken,minecraft:rotten_flesh,4",
                "minecraft:cooked_fish,minecraft:rotten_flesh,4",
                "minecraft:cooked_mutton,minecraft:rotten_flesh,4",
                "minecraft:cooked_porkchop,minecraft:rotten_flesh,4",
                "minecraft:cooked_rabbit,minecraft:rotten_flesh,4",
                "minecraft:cookie,minecraft:air,5",
                "minecraft:fish,minecraft:rotten_flesh,3",
                "minecraft:golden_apple,-1",
                "minecraft:melon,minecraft:air,3",
                "minecraft:mushroom_stew,minecraft:bowl,3",
                "minecraft:mutton,minecraft:rotten_flesh,3",
                "minecraft:porkchop,minecraft:rotten_flesh,3",
                "minecraft:potato,minecraft:poisonous_potato,10",
                "minecraft:pumpkin_pie,minecraft:air,4",
                "minecraft:rabbit,minecraft:rotten_flesh,3",
                "minecraft:rabbit_stew,minecraft:bowl,4",

                // Miscellaneous
                "vanillaplus:pumpkin_slice,minecraft:air,5",
                "inspirations:potato_soup,minecraft:bowl,4",
                "charm:suspicious_soup,minecraft:bowl,4",

                // Pam's HarvestCraft Berries
                "harvestcraft:blackberryitem,minecraft:air,3",
                "harvestcraft:blueberryitem,minecraft:air,3",
                "harvestcraft:candleberryitem,minecraft:air,3",
                "harvestcraft:raspberryitem,minecraft:air,3",
                "harvestcraft:strawberryitem,minecraft:air,3",
                "harvestcraft:cranberryitem,minecraft:air,3",
                "harvestcraft:elderberryitem,minecraft:air,3",
                "harvestcraft:gooseberryitem,minecraft:air,3",
                "harvestcraft:mulberryitem,minecraft:air,3",
                "harvestcraft:boysenberryitem,minecraft:air,3",
                "harvestcraft:juniperberryitem,minecraft:air,3",
                "harvestcraft:gojiberryitem,minecraft:air,3",
                "harvestcraft:cloudberryitem,minecraft:air,3",
                "harvestcraft:huckleberryitem,minecraft:air,3",

                // Pam's HarvestCraft Fruits
                "harvestcraft:apricotitem,minecraft:air,5",
                "harvestcraft:avocadoitem,minecraft:air,5",
                "harvestcraft:bananaitem,minecraft:air,7",
                "harvestcraft:cashewitem,minecraft:air,6",
                "harvestcraft:cherryitem,minecraft:air,5",
                "harvestcraft:chestnutitem,minecraft:air,5",
                "harvestcraft:coconutitem,minecraft:air,5",
                "harvestcraft:dateitem,minecraft:air,5",
                "harvestcraft:dragonfruititem,minecraft:air,5",
                "harvestcraft:durianitem,minecraft:air,5",
                "harvestcraft:figitem,minecraft:air,5",
                "harvestcraft:grapefruititem,minecraft:air,5",
                "harvestcraft:grapeitem,minecraft:air,5",
                "harvestcraft:jackfruititem,minecraft:air,5",
                "harvestcraft:kiwiitem,minecraft:air,5",
                "harvestcraft:lemonitem,minecraft:air,5",
                "harvestcraft:limeitem,minecraft:air,5",
                "harvestcraft:mangoitem,minecraft:air,5",
                "harvestcraft:orangeitem,minecraft:air,5",
                "harvestcraft:papayaitem,minecraft:air,5",
                "harvestcraft:passionfruititem,minecraft:air,5",
                "harvestcraft:peachitem,minecraft:air,5",
                "harvestcraft:pearitem,minecraft:air,5",
                "harvestcraft:persimmonitem,minecraft:air,5",
                "harvestcraft:plumitem,minecraft:air,5",
                "harvestcraft:pomegranateitem,minecraft:air,5",
                "harvestcraft:pineappleitem,minecraft:air,5",
                "harvestcraft:rambutanitem,minecraft:air,5",
                "harvestcraft:soursopitem,minecraft:air,5",
                "harvestcraft:starfruititem,minecraft:air,5",
                "harvestcraft:tamarinditem,minecraft:air,5",

                // Pam's HarvestCraft Basic Food Items
                "harvestcraft:asparagusitem,minecraft:air,4",
                "harvestcraft:beetitem,minecraft:air,4",
                "harvestcraft:bellpepperitem,minecraft:air,4",
                "harvestcraft:broccoliitem,minecraft:air,4",
                "harvestcraft:brusselsproutitem,minecraft:air,4",
                "harvestcraft:cabbageitem,minecraft:air,4",
                "harvestcraft:caulifloweritem,minecraft:air,4",
                "harvestcraft:celeryitem,minecraft:air,4",
                "harvestcraft:chilipepperitem,minecraft:air,4",
                "harvestcraft:cornitem,minecraft:air,4",
                "harvestcraft:cucumberitem,minecraft:air,4",
                "harvestcraft:eggplantitem,minecraft:air,4",
                "harvestcraft:garlicitem,minecraft:air,10",
                "harvestcraft:gingeritem,minecraft:air,10",
                "harvestcraft:leekitem,minecraft:air,4",
                "harvestcraft:lettuceitem,minecraft:air,7",
                "harvestcraft:mustarditem,minecraft:air,4",
                "harvestcraft:okraitem,minecraft:air,4",
                "harvestcraft:onionitem,minecraft:air,30",
                "harvestcraft:parsnipitem,minecraft:air,4",
                "harvestcraft:peanutitem,minecraft:air,180",
                "harvestcraft:peasitem,minecraft:air,4",
                "harvestcraft:pineappleseeditem,minecraft:air,4",
                "harvestcraft:radishitem,minecraft:air,4",
                "harvestcraft:rhubarbitem,minecraft:air,4",
                "harvestcraft:rutabagaitem,minecraft:air,4",
                "harvestcraft:scallionitem,minecraft:air,4",
                "harvestcraft:seaweeditem,minecraft:air,4",
                "harvestcraft:spinachitem,minecraft:air,4",
                "harvestcraft:sweetpotatoitem,minecraft:air,10",
                "harvestcraft:tomatoitem,minecraft:air,5",
                "harvestcraft:turnipitem,minecraft:air,4",
                "harvestcraft:waterchestnutitem,minecraft:air,4",
                "harvestcraft:whitemushroomitem,minecraft:air,3",
                "harvestcraft:wintersquashitem,minecraft:air,30",
                "harvestcraft:zucchiniitem,minecraft:air,4",
                "harvestcraft:artichokeitem,minecraft:air,4",
                "harvestcraft:bambooshootitem,minecraft:air,4",
                "harvestcraft:kohlrabiitem,minecraft:air,4",
                "harvestcraft:chickpeaitem,minecraft:air,180",
                "harvestcraft:lentilitem,minecraft:air,180",
                "harvestcraft:wintersquashitem,minecraft:air,30",
                "harvestcraft:rutabagaitem,minecraft:air,30",
                "harvestcraft:kaleitem,minecraft:air,7",
                "harvestcraft:cassavaitem,minecraft:air,4",

                // Pam's HarvestCraft Raw Items
                "harvestcraft:anchovyrawitem,minecraft:air,3",
                "harvestcraft:bassrawitem,minecraft:air,3",
                "harvestcraft:carprawitem,minecraft:air,3",
                "harvestcraft:catfishrawitem,minecraft:air,3",
                "harvestcraft:charrrawitem,minecraft:air,3",
                "harvestcraft:clamrawitem,minecraft:air,3",
                "harvestcraft:crabrawitem,minecraft:air,3",
                "harvestcraft:crayfishrawitem,minecraft:air,3",
                "harvestcraft:eelrawitem,minecraft:air,3",
                "harvestcraft:frograwitem,minecraft:air,3",
                "harvestcraft:grouperrawitem,minecraft:air,3",
                "harvestcraft:herringrawitem,minecraft:air,3",
                "harvestcraft:jellyfishrawitem,minecraft:air,3",
                "harvestcraft:mudfishrawitem,minecraft:air,3",
                "harvestcraft:octopusrawitem,minecraft:air,3",
                "harvestcraft:perchrawitem,minecraft:air,3",
                "harvestcraft:scalloprawitem,minecraft:air,3",
                "harvestcraft:shrimprawitem,minecraft:air,3",
                "harvestcraft:snailrawitem,minecraft:air,3",
                "harvestcraft:snapperrawitem,minecraft:air,3",
                "harvestcraft:tilapiarawitem,minecraft:air,3",
                "harvestcraft:troutrawitem,minecraft:air,3",
                "harvestcraft:tunarawitem,minecraft:air,3",
                "harvestcraft:turtlerawitem,minecraft:air,3",
                "harvestcraft:walleyerawitem,minecraft:air,3",
                "harvestcraft:calamarirawitem,minecraft:air,3",
                "harvestcraft:turkeyrawitem,minecraft:air,3",
                "harvestcraft:venisonrawitem,minecraft:air,3",
                "harvestcraft:duckrawitem,minecraft:air,3",
                "harvestcraft:terrawortseeditem,minecraft:air,3",

                // Pam's HarvestCraft Meals and Dishes
                "harvestcraft:anchovycookeditem,minecraft:air,4",
                "harvestcraft:basscookeditem,minecraft:air,4",
                "harvestcraft:carpcookeditem,minecraft:air,4",
                "harvestcraft:catfishcookeditem,minecraft:air,4",
                "harvestcraft:charrcookeditem,minecraft:air,4",
                "harvestcraft:clamcookeditem,minecraft:air,4",
                "harvestcraft:crabcookeditem,minecraft:air,4",
                "harvestcraft:crayfishcookeditem,minecraft:air,4",
                "harvestcraft:eelcookeditem,minecraft:air,4",
                "harvestcraft:frogcookeditem,minecraft:air,4",
                "harvestcraft:groupercookeditem,minecraft:air,4",
                "harvestcraft:herringcookeditem,minecraft:air,4",
                "harvestcraft:jellyfishcookeditem,minecraft:air,4",
                "harvestcraft:mudfishcookeditem,minecraft:air,4",
                "harvestcraft:octopuscookeditem,minecraft:air,4",
                "harvestcraft:perchcookeditem,minecraft:air,4",
                "harvestcraft:scallopcookeditem,minecraft:air,4",
                "harvestcraft:shrimpcookeditem,minecraft:air,4",
                "harvestcraft:snailcookeditem,minecraft:air,4",
                "harvestcraft:snappercookeditem,minecraft:air,4",
                "harvestcraft:tilapiacookeditem,minecraft:air,4",
                "harvestcraft:troutcookeditem,minecraft:air,4",
                "harvestcraft:tunacookeditem,minecraft:air,4",
                "harvestcraft:turtlecookeditem,minecraft:air,4",
                "harvestcraft:walleyecookeditem,minecraft:air,4",
                "harvestcraft:calamaricookeditem,minecraft:air,4",
                "harvestcraft:turkeycookeditem,minecraft:air,4",
                "harvestcraft:venisoncookeditem,minecraft:air,4",
                "harvestcraft:duckcookeditem,minecraft:air,4",
                "harvestcraft:applejellyitem,minecraft:air,90",
                "harvestcraft:applejuiceitem,minecraft:air,7",
                "harvestcraft:applepieitem,minecraft:air,3",
                "harvestcraft:applesauceitem,minecraft:air,7",
                "harvestcraft:applecideritem,minecraft:air,14",
                "harvestcraft:asparagussoupitem,minecraft:bowl,4",
                "harvestcraft:baconandeggsitem,minecraft:air,4",
                "harvestcraft:bakedbeansitem,minecraft:air,5",
                "harvestcraft:bakedhamitem,minecraft:air,5",
                "harvestcraft:bakedsweetpotatoitem,minecraft:air,7",
                "harvestcraft:banananutbreaditem,minecraft:air,7",
                "harvestcraft:bananasmoothieitem,minecraft:air,1",
                "harvestcraft:bananasplititem,minecraft:air,1",
                "harvestcraft:bbqchickenpizzaitem,minecraft:air,3",
                "harvestcraft:bbqpotatochipsitem,minecraft:air,5",
                "harvestcraft:bbqsauceitem,minecraft:air,12",
                "harvestcraft:beansandriceitem,minecraft:air,4",
                "harvestcraft:beefjerkyitem,minecraft:air,30",
                "harvestcraft:beefwellingtonitem,minecraft:air,5",
                "harvestcraft:beetburgeritem,minecraft:air,4",
                "harvestcraft:blackberrycobbleritem,minecraft:air,3",
                "harvestcraft:blackberryjellyitem,minecraft:air,90",
                "harvestcraft:blackberryjuiceitem,minecraft:air,7",
                "harvestcraft:blackberrypieitem,minecraft:air,3",
                "harvestcraft:blackberrysmoothieitem,minecraft:air,3",
                "harvestcraft:blackberryteaitem,minecraft:air,6",
                "harvestcraft:blackberrywinessenceitem,minecraft:air,6",
                "harvestcraft:blueberryjellyitem,minecraft:air,90",
                "harvestcraft:blueberryjuiceitem,minecraft:air,7",
                "harvestcraft:blueberrymuffinitem,minecraft:air,3",
                "harvestcraft:blueberrypancakesitem,minecraft:air,3",
                "harvestcraft:blueberrypieitem,minecraft:air,3",
                "harvestcraft:blueberrysmoothieitem,minecraft:air,3",
                "harvestcraft:blueberryteaitem,minecraft:air,6",
                "harvestcraft:blueberrywinessenceitem,minecraft:air,6",
                "harvestcraft:breadedporkchopitem,minecraft:air,5",
                "harvestcraft:breakfastburritoitem,minecraft:air,4",
                "harvestcraft:broccolimacitem,minecraft:air,4",
                "harvestcraft:brownieitem,minecraft:air,3",
                "harvestcraft:brusselsproutitem,minecraft:air,4",
                "harvestcraft:buttercookieitem,minecraft:air,7",
                "harvestcraft:butteritem,minecraft:air,30",
                "harvestcraft:caramelitem,minecraft:air,14",
                "harvestcraft:carrotcakeitem,minecraft:air,5",
                "harvestcraft:carrotsoupitem,minecraft:bowl,4",
                "harvestcraft:celeryandpeanutbutteritem,minecraft:air,5",
                "harvestcraft:cheeseburgeritem,minecraft:air,3",
                "harvestcraft:cheeseontoastitem,minecraft:air,4",
                "harvestcraft:cherrycheesecakeitem,minecraft:air,4",
                "harvestcraft:cherrypieitem,minecraft:air,3",
                "harvestcraft:chickencelerycasseroleitem,minecraft:air,4",
                "harvestcraft:chickenchowmeinitem,minecraft:air,4",
                "harvestcraft:chickencurryitem,minecraft:air,4",
                "harvestcraft:chickengumboitem,minecraft:air,4",
                "harvestcraft:chickenparmasanitem,minecraft:air,4",
                "harvestcraft:chickenpotpieitem,minecraft:air,4",
                "harvestcraft:chickensandwichitem,minecraft:air,4",
                "harvestcraft:chickennoodlesoupitem,minecraft:bowl,1",
                "harvestcraft:chilipoppersitem,minecraft:air,4",
                "harvestcraft:chocolatebaconitem,minecraft:air,4",
                "harvestcraft:chocolatebaritem,minecraft:air,5",
                "harvestcraft:chocolatecaramelfudgeitem,minecraft:air,7",
                "harvestcraft:chocolatecherryitem,minecraft:air,5",
                "harvestcraft:chocolatedonutitem,minecraft:air,3",
                "harvestcraft:chocolateicecreamitem,minecraft:air,1",
                "harvestcraft:chocolatemilkitem,minecraft:air,7",
                "harvestcraft:chocolatemilkshakeitem,minecraft:air,3",
                "harvestcraft:chocolatemousseitem,minecraft:air,5",
                "harvestcraft:chocolateorangeitem,minecraft:air,5",
                "harvestcraft:chocolatepuddingitem,minecraft:air,4",
                "harvestcraft:chocolaterollitem,minecraft:air,4",
                "harvestcraft:chocolatesprinklecakeitem,minecraft:air,4",
                "harvestcraft:chocolatestrawberryitem,minecraft:air,4",
                "harvestcraft:chocolateyogurtitem,minecraft:air,4",
                "harvestcraft:coconutcreamitem,minecraft:air,5",
                "harvestcraft:coconutmilkitem,minecraft:air,5",
                "harvestcraft:coconutshrimpitem,minecraft:air,5",
                "harvestcraft:coconutyogurtitem,minecraft:air,4",
                "harvestcraft:coffeeconlecheitem,minecraft:air,5",
                "harvestcraft:coffeecupitem,minecraft:air,4",
                "harvestcraft:cornbreaditem,minecraft:air,7",
                "harvestcraft:crackeritem,minecraft:air,14",
                "harvestcraft:creepercookieitem,minecraft:air,7",
                "harvestcraft:croissantitem,minecraft:air,5",
                "harvestcraft:curryitem,minecraft:air,4",
                "harvestcraft:deluxecheeseburgeritem,minecraft:air,3",
                "harvestcraft:delightedmealitem,minecraft:air,3",
                "harvestcraft:durianmilkshakeitem,minecraft:air,1",
                "harvestcraft:eggplantparmitem,minecraft:air,4",
                "harvestcraft:extremechiliitem,minecraft:air,4",
                "harvestcraft:fishnchipsitem,minecraft:air,3",
                "harvestcraft:fishsandwichitem,minecraft:air,3",
                "harvestcraft:fishandchipsitem,minecraft:air,3",
                "harvestcraft:friedchickenitem,minecraft:air,5",
                "harvestcraft:friedeggitem,minecraft:air,3",
                "harvestcraft:friesitem,minecraft:air,3",
                "harvestcraft:fruitpunchitem,minecraft:air,3",
                "harvestcraft:garlicbreaditem,minecraft:air,5",
                "harvestcraft:garlicchickenitem,minecraft:air,4",
                "harvestcraft:gingerbreaditem,minecraft:air,7",
                "harvestcraft:gingersnapsitem,minecraft:air,7",
                "harvestcraft:glazedcarrotsitem,minecraft:air,4",
                "harvestcraft:grilledcheeseitem,minecraft:air,3",
                "harvestcraft:grilledmushroomitem,minecraft:air,3",
                "harvestcraft:guacamoleitem,minecraft:air,2",
                "harvestcraft:hamburgeritem,minecraft:air,3",
                "harvestcraft:heartybreakfastitem,minecraft:air,3",
                "harvestcraft:herbbutterparsnipsitem,minecraft:air,4",
                "harvestcraft:honeybreaditem,minecraft:air,7",
                "harvestcraft:honeybunitem,minecraft:air,5",
                "harvestcraft:honeyglazedcarrotsitem,minecraft:air,4",
                "harvestcraft:honeyglazedhamitem,minecraft:air,5",
                "harvestcraft:honeysandwichitem,minecraft:air,4",
                "harvestcraft:hotandsoursoupitem,minecraft:bowl,3",
                "harvestcraft:hotchocolateitem,minecraft:air,3",
                "harvestcraft:hotwingsitem,minecraft:air,4",
                "harvestcraft:hummusitem,minecraft:air,5",
                "harvestcraft:icecreamsandwichitem,minecraft:air,1",
                "harvestcraft:jelliedeelitem,minecraft:air,7",
                "harvestcraft:keylimepieitem,minecraft:air,3",
                "harvestcraft:kungpaochickenitem,minecraft:air,4",
                "harvestcraft:lasagnaitem,minecraft:air,4",
                "harvestcraft:lemonbaritem,minecraft:air,3",
                "harvestcraft:lemonchickenitem,minecraft:air,4",
                "harvestcraft:lemoncupcakeitem,minecraft:air,3",
                "harvestcraft:lemondrizzlecakeitem,minecraft:air,4",
                "harvestcraft:lemonmeringueitem,minecraft:air,3",
                "harvestcraft:lobsterthermidoritem,minecraft:air,4",
                "harvestcraft:mapleoatmealitem,minecraft:air,3",
                "harvestcraft:mashedpotatoesitem,minecraft:air,4",
                "harvestcraft:meatloafitem,minecraft:air,5",
                "harvestcraft:mincepieitem,minecraft:air,3",
                "harvestcraft:mintchocolatechipicecreamitem,minecraft:air,1",
                "harvestcraft:mixturesaladitem,minecraft:air,3",
                "harvestcraft:mochicakeitem,minecraft:air,4",
                "harvestcraft:mushroomrisottoitem,minecraft:air,4",
                "harvestcraft:nachoesitem,minecraft:air,3",
                "harvestcraft:oatmealcookieitem,minecraft:air,7",
                "harvestcraft:oatmealitem,minecraft:air,3",
                "harvestcraft:okrachipsitem,minecraft:air,5",
                "harvestcraft:oldworldveggiesoupitem,minecraft:bowl,3",
                "harvestcraft:omeletitem,minecraft:air,3",
                "harvestcraft:onionhamburgeritem,minecraft:air,3",
                "harvestcraft:onionsoupitem,minecraft:bowl,3",
                "harvestcraft:pancakesitem,minecraft:air,3",
                "harvestcraft:papayajuiceitem,minecraft:air,7",
                "harvestcraft:peanutbuttercookiesitem,minecraft:air,7",
                "harvestcraft:peanutbuttercupitem,minecraft:air,7",
                "harvestcraft:peaandhamsoupitem,minecraft:bowl,3",
                "harvestcraft:peachcobbleritem,minecraft:air,3",
                "harvestcraft:pepperoniitem,minecraft:air,14",
                "harvestcraft:pickledbeetsitem,minecraft:air,14",
                "harvestcraft:pickledonionsitem,minecraft:air,14",
                "harvestcraft:picklesitem,minecraft:air,14",
                "harvestcraft:pinacoladaitem,minecraft:air,3",
                "harvestcraft:pineappleupsidedowncakeitem,minecraft:air,4",
                "harvestcraft:plaintainitem,minecraft:air,4",
                "harvestcraft:popcornitem,minecraft:air,7",
                "harvestcraft:porkrindsitem,minecraft:air,7",
                "harvestcraft:porksausageitem,minecraft:air,5",
                "harvestcraft:potatoandcheesepirogiitem,minecraft:air,4",
                "harvestcraft:potatocakesitem,minecraft:air,4",
                "harvestcraft:potatosalادitem,minecraft:air,3",
                "harvestcraft:potatosoupitem,minecraft:bowl,3",
                "harvestcraft:pumpkinbreaditem,minecraft:air,7",
                "harvestcraft:pumpkinmuffinitem,minecraft:air,4",
                "harvestcraft:pumpkinoatsconesitem,minecraft:air,4",
                "harvestcraft:pumpkinpieitem,minecraft:air,3",
                "harvestcraft:raisincookiesitem,minecraft:air,7",
                "harvestcraft:raspberrypieitem,minecraft:air,3",
                "harvestcraft:ratatouilleitem,minecraft:air,4",
                "harvestcraft:rawtofaconitem,minecraft:air,3",
                "harvestcraft:rawtofeakitem,minecraft:air,3",
                "harvestcraft:rawtofuttonitem,minecraft:air,3",
                "harvestcraft:ricepuddingitem,minecraft:air,3",
                "harvestcraft:roastchickenitem,minecraft:air,5",
                "harvestcraft:roastedpumpkinseedsitem,minecraft:air,7",
                "harvestcraft:salmonpattiesitem,minecraft:air,4",
                "harvestcraft:sausageinbreaditem,minecraft:air,4",
                "harvestcraft:sausageitem,minecraft:air,5",
                "harvestcraft:sausagerollitem,minecraft:air,4",
                "harvestcraft:scrambledeggitem,minecraft:air,3",
                "harvestcraft:sesameballitem,minecraft:air,4",
                "harvestcraft:sesamesnapsitem,minecraft:air,7",
                "harvestcraft:shepherdspieitem,minecraft:air,4",
                "harvestcraft:softpretzelandmustarditem,minecraft:air,4",
                "harvestcraft:softpretzelitem,minecraft:air,4",
                "harvestcraft:spinachpieitem,minecraft:air,4",
                "harvestcraft:springsaladitem,minecraft:air,3",
                "harvestcraft:steamedpeasitem,minecraft:air,4",
                "harvestcraft:steamedspinachitem,minecraft:air,4",
                "harvestcraft:stuffedeggplantitem,minecraft:air,4",
                "harvestcraft:stuffedmushroomitem,minecraft:air,4",
                "harvestcraft:stuffedpepperitem,minecraft:air,4",
                "harvestcraft:supremepizzaitem,minecraft:air,3",
                "harvestcraft:sushiitem,minecraft:air,2",
                "harvestcraft:sweetandsoursauceitem,minecraft:air,7",
                "harvestcraft:sweetpotatopieitem,minecraft:air,3",
                "harvestcraft:sweetpotatosouffleitem,minecraft:air,3",
                "harvestcraft:tacoitem,minecraft:air,3",
                "harvestcraft:teriyakichickenitem,minecraft:air,4",
                "harvestcraft:toastitem,minecraft:air,4",
                "harvestcraft:toastedsandwichitem,minecraft:air,4",
                "harvestcraft:tomatosoupitem,minecraft:bowl,3",
                "harvestcraft:tortillaitem,minecraft:air,7",
                "harvestcraft:vegemiteitem,minecraft:air,180",
                "harvestcraft:vegetablesoupitem,minecraft:bowl,3",
                "harvestcraft:vindalooitem,minecraft:air,4",
                "harvestcraft:wafflesitem,minecraft:air,3",
                "harvestcraft:watermelonjellyitem,minecraft:air,90",
                "harvestcraft:zestyzucchiniitem,minecraft:air,4",
                "harvestcraft:zucchinibreaditem,minecraft:air,7",
                "harvestcraft:zucchinifriesitem,minecraft:air,3",
                "harvestcraft:saltedsunflowerseedsitem,minecraft:air,180",
                "harvestcraft:saltedpistachioitem,minecraft:air,180",
                "harvestcraft:pickledbeansitem,minecraft:air,14",
                "harvestcraft:pickledcabbageitem,minecraft:air,14",
                "harvestcraft:pickledcornitem,minecraft:air,14",
                "harvestcraft:pickledcucumberitem,minecraft:air,14",
                "harvestcraft:pickledonionitem,minecraft:air,14",
                "harvestcraft:pickledpepperitem,minecraft:air,14",
                "harvestcraft:roastedchestnutitem,minecraft:air,14",
                "harvestcraft:roastedpeanutitem,minecraft:air,180",
                "harvestcraft:saltandvinegarchipsitem,minecraft:air,14",

                // Additional Pam's HarvestCraft Meals and Food Items
                "harvestcraft:ricesoupitem,minecraft:bowl,3",
                "harvestcraft:friedriceitem,minecraft:air,4",
                "harvestcraft:mushroomsoupitem,minecraft:bowl,2",
                "harvestcraft:chickennoodlesoupitem,minecraft:bowl,3",
                "harvestcraft:spaghettiitem,minecraft:air,4",
                "harvestcraft:spaghettiandmeatballsitem,minecraft:air,4",
                "harvestcraft:tomatosoupitem,minecraft:bowl,3",
                "harvestcraft:chickenparmesamitem,minecraft:air,4",
                "harvestcraft:pizzaitem,minecraft:air,3",
                "harvestcraft:cheeseburgeritem,minecraft:air,3",
                "harvestcraft:chickenburgeritem,minecraft:air,3",
                "harvestcraft:fishburgeritem,minecraft:air,3",
                "harvestcraft:baconcheeseburgeritem,minecraft:air,3",
                "harvestcraft:hotdogitem,minecraft:air,3",
                "harvestcraft:baconandeggsitem,minecraft:air,3",
                "harvestcraft:macncheeseitem,minecraft:air,4",
                "harvestcraft:cornonthecobitem,minecraft:air,4",
                "harvestcraft:potatosaladitem,minecraft:air,3",
                "harvestcraft:eggsaladitem,minecraft:air,3",
                "harvestcraft:fruitsaladitem,minecraft:air,2",
                "harvestcraft:springrollitem,minecraft:air,3",
                "harvestcraft:meatpieitem,minecraft:air,4",
                "harvestcraft:cottoncandyitem,minecraft:air,7",
                "harvestcraft:cookiesitem,minecraft:air,7",
                "harvestcraft:snickerdoodleitem,minecraft:air,7",
                "harvestcraft:cinnamonrollitem,minecraft:air,4",
                "harvestcraft:cinnamonbreaditem,minecraft:air,7",
                "harvestcraft:cornflakesitem,minecraft:air,7",
                "harvestcraft:crackersitem,minecraft:air,14",
                "harvestcraft:ediblerootitem,minecraft:air,4",
                "harvestcraft:ediblefloweritem,minecraft:air,2",
                "harvestcraft:rawtofishitem,minecraft:air,3",
                "harvestcraft:rawtofickenitem,minecraft:air,3",
                "harvestcraft:rawtobeefitem,minecraft:air,3",
                "harvestcraft:rawtofabbititem,minecraft:air,3",
                "harvestcraft:rawtoffalkitem,minecraft:air,3",
                "harvestcraft:rawtofurkeyitem,minecraft:air,3",
                "harvestcraft:rawtofenisonitem,minecraft:air,3",
                "harvestcraft:rawtofuduckitem,minecraft:air,3",
                "harvestcraft:cookedtofishitem,minecraft:air,7",
                "harvestcraft:cookedtofickenitem,minecraft:air,7",
                "harvestcraft:cookedtobeefitem,minecraft:air,7",
                "harvestcraft:cookedtofabbititem,minecraft:air,7",
                "harvestcraft:cookedtoffalkitem,minecraft:air,7",
                "harvestcraft:cookedtofurkeyitem,minecraft:air,7",
                "harvestcraft:cookedtofenisonitem,minecraft:air,7",
                "harvestcraft:cookedtofuduckitem,minecraft:air,7",
                "harvestcraft:ricecakeitem,minecraft:air,4",
                "harvestcraft:teaitem,minecraft:air,4",
                "harvestcraft:coffeeitem,minecraft:air,4",
                "harvestcraft:gingersodaitem,minecraft:air,3",
                "harvestcraft:rootbeeritem,minecraft:air,3",
                "harvestcraft:grapesodaitem,minecraft:air,3",
                "harvestcraft:colaitem,minecraft:air,3",
                "harvestcraft:energydrinkitem,minecraft:air,3",
                "harvestcraft:hotcocoaitem,minecraft:air,3",
                "harvestcraft:chocolatemilkitem,minecraft:air,3",
                "harvestcraft:strawberrymilkitem,minecraft:air,3",
                "harvestcraft:grapejuiceitem,minecraft:air,7",
                "harvestcraft:cranberryjuiceitem,minecraft:air,7",
                "harvestcraft:cherryjuiceitem,minecraft:air,7",
                "harvestcraft:plumjuiceitem,minecraft:air,7",
                "harvestcraft:pearsmoothieitem,minecraft:air,1",
                "harvestcraft:apricotsmoothieitem,minecraft:air,1",
                "harvestcraft:figjuiceitem,minecraft:air,7",
                "harvestcraft:mangojuiceitem,minecraft:air,7",
                "harvestcraft:orangejuiceitem,minecraft:air,7",
                "harvestcraft:peachjuiceitem,minecraft:air,7",
                "harvestcraft:limejuiceitem,minecraft:air,7",
                "harvestcraft:pomegranatejuiceitem,minecraft:air,7",
                "harvestcraft:mangosmoothieitem,minecraft:air,1",
                "harvestcraft:grapefruitsmoothieitem,minecraft:air,1",
                "harvestcraft:persimmonsmoothieitem,minecraft:air,1",
                "harvestcraft:gooseberrysmoothieitem,minecraft:air,1",
                "harvestcraft:applejellysandwichitem,minecraft:air,3",
                "harvestcraft:blackberryjellysandwichitem,minecraft:air,3",
                "harvestcraft:blueberryjellysandwichitem,minecraft:air,3",
                "harvestcraft:cherryjellysandwichitem,minecraft:air,3",
                "harvestcraft:cranberryjellysandwichitem,minecraft:air,3",
                "harvestcraft:kiwijellysandwichitem,minecraft:air,3",
                "harvestcraft:lemonjellysandwichitem,minecraft:air,3",
                "harvestcraft:limejellysandwichitem,minecraft:air,3",
                "harvestcraft:mangojellysandwichitem,minecraft:air,3",
                "harvestcraft:orangejellysandwichitem,minecraft:air,3",
                "harvestcraft:papayajellysandwichitem,minecraft:air,3",
                "harvestcraft:peachjellysandwichitem,minecraft:air,3",
                "harvestcraft:pomegranatejellysandwichitem,minecraft:air,3",
                "harvestcraft:starfruitjellysandwichitem,minecraft:air,3",
                "harvestcraft:strawberryjellysandwichitem,minecraft:air,3",
                "harvestcraft:watermelonjellysandwichitem,minecraft:air,3",
                "harvestcraft:caramelicecreamitem,minecraft:air,1",
                "harvestcraft:mintchocolatechipicemcreamitem,minecraft:air,1",
                "harvestcraft:strawberryicecreamitem,minecraft:air,1",
                "harvestcraft:vanillaicecreamitem,minecraft:air,1",
                "harvestcraft:ediblerootitem,minecraft:air,4",
                "harvestcraft:sunflowerseedsitem,minecraft:air,180",
                "harvestcraft:vanillaitem,minecraft:air,180",
                "harvestcraft:saltitem,minecraft:air,-1",
                "harvestcraft:vinegaritem,minecraft:air,-1",
                "harvestcraft:oliveoilitem,minecraft:air,90",
                "harvestcraft:mayoitem,minecraft:air,30",
                "harvestcraft:mustardseedsitem,minecraft:air,180",
                "harvestcraft:blackpepperitem,minecraft:air,180",
                "harvestcraft:groundcinnamonitem,minecraft:air,180",
                "harvestcraft:groundnutmegitem,minecraft:air,180",
                "harvestcraft:peanutbutteritem,minecraft:air,90",
                "harvestcraft:pistachiobutteritem,minecraft:air,90",
                "harvestcraft:almondbutteritem,minecraft:air,90",
                "harvestcraft:cashewbutteritem,minecraft:air,90",
                "harvestcraft:chestnutbutteritem,minecraft:air,90",
                "harvestcraft:cornmealitem,minecraft:air,180",
                "harvestcraft:doughitem,minecraft:air,1",
                "harvestcraft:flouritem,minecraft:air,180",
                "harvestcraft:heavycreamitem,minecraft:air,3",
                "harvestcraft:honeycombitem,minecraft:air,180",
                "harvestcraft:honeyitem,minecraft:air,180",
                "harvestcraft:mayonnaisitem,minecraft:air,30",
                "harvestcraft:oliveoilitem,minecraft:air,90",
                "harvestcraft:pumkinseedsitem,minecraft:air,180",
                "harvestcraft:sesameoilitem,minecraft:air,90",
                "harvestcraft:stockitem,minecraft:air,7",
                "harvestcraft:sunfloweroilitem,minecraft:air,90",
                "harvestcraft:vanillaextractitem,minecraft:air,180",
                "harvestcraft:freshwateritem,minecraft:air,-1",
                "harvestcraft:freshmilkitem,minecraft:air,3",
                "harvestcraft:saltitem,minecraft:air,-1",
                "harvestcraft:vinegaritem,minecraft:air,-1"
            };

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
