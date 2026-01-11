package io.nightfis.createstellarexoduscore;

import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import io.nightfis.createstellarexoduscore.block.AutoAimTurretBlock;
import io.nightfis.createstellarexoduscore.block.entity.AutoAimTurretBlockEntity;
import io.nightfis.createstellarexoduscore.client.gui.screen.AutoAimTurretScreen;
import io.nightfis.createstellarexoduscore.compat.createbigcannons.EntityProximityFuzeItem;
import io.nightfis.createstellarexoduscore.inventory.AutoAimTurretMenu;
import io.nightfis.createstellarexoduscore.item.AutoAimTurretItem;
import io.nightfis.createstellarexoduscore.network.ModNetwork;
import io.nightfis.createstellarexoduscore.filter.TargetFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import io.nightfis.createstellarexoduscore.client.AutoAimTurretRenderer;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(StellarExodusCore.MODID)
public class StellarExodusCore {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "createstellarexoduscore";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Registrate REGISTRATE = Registrate.create(MODID);

    // Auto-aim turret block (kinetic machine, participates in the Create stress network)
    public static final BlockEntry<AutoAimTurretBlock> AUTO_AIM_TURRET = REGISTRATE
            .block("auto_aim_turret", AutoAimTurretBlock::new)
            .properties(props -> props.mapColor(MapColor.METAL).strength(3.5f).requiresCorrectToolForDrops())
            .item(AutoAimTurretItem::new)
            .tab(CreativeModeTabs.REDSTONE_BLOCKS)
            .build()
            .register();
    public static final BlockEntityEntry<AutoAimTurretBlockEntity> AUTO_AIM_TURRET_ENTITY = REGISTRATE
            .blockEntity("auto_aim_turret", AutoAimTurretBlockEntity::new)
            .validBlock(AUTO_AIM_TURRET)
            .register();
    @SuppressWarnings("DataFlowIssue")
    public static final MenuEntry<AutoAimTurretMenu> AUTO_AIM_TURRET_MENU = REGISTRATE
            .menu("auto_aim_turret", AutoAimTurretMenu::new, () -> AutoAimTurretScreen::new)
            .register();
    public static final ItemEntry<EntityProximityFuzeItem> ENTITY_PROXIMITY_FUZE = REGISTRATE
            .item("entity_proximity_fuze", EntityProximityFuzeItem::new)
            .properties(props -> props.stacksTo(16))
            .tab(CreativeModeTabs.COMBAT)
            .register();

    @SuppressWarnings("removal")
    public StellarExodusCore() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));

        TargetFilter.init();
        ModNetwork.init();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> BlockEntityRenderers.register(
                    AUTO_AIM_TURRET_ENTITY.get(),
                    AutoAimTurretRenderer::new));
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
