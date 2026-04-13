package win.ringlo.createpoopindustry

import com.mojang.logging.LogUtils
import com.simibubi.create.Create
import com.simibubi.create.foundation.data.CreateRegistrate
import com.simibubi.create.foundation.item.ItemDescription
import com.simibubi.create.foundation.item.KineticStats
import com.simibubi.create.foundation.item.TooltipModifier
import net.createmod.catnip.lang.FontHelper
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import org.slf4j.Logger
import win.ringlo.createpoopindustry.infrastructure.data.CreatePoopIndustryDatagen

@Mod(CreatePoopIndustry.MOD_ID)
class CreatePoopIndustry(modEventBus: IEventBus, modContainer: ModContainer) {
    companion object {
        const val MOD_ID = "createpoopindustry"
        val LOGGER: Logger = LogUtils.getLogger()
        private val REGISTRATE: CreateRegistrate = CreateRegistrate.create(MOD_ID)
                .defaultCreativeTab(null as ResourceKey<CreativeModeTab>?)
                .setTooltipModifierFactory { item ->
                    ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
                }

        fun registrate(): CreateRegistrate {
            return REGISTRATE
        }
    }
    
    init {
        LOGGER.info("Initializing...")

        REGISTRATE.registerEventListeners(modEventBus)

        AllCreativeModeTabs.register(modEventBus)

        AllItems
        AllFluids
        AllBlocks
        AllBlockEntityTypes

        modEventBus.addListener(this::init)
        modEventBus.addListener(EventPriority.HIGHEST, CreatePoopIndustryDatagen::gatherDataHighPriority)
        modEventBus.addListener(EventPriority.LOWEST, CreatePoopIndustryDatagen::gatherData)
    }

    fun init(event: FMLCommonSetupEvent) {
        event.enqueueWork({
            AllOpenPipeEffectHandlers.registerDefaults()
        })
    }

    fun asResource(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
    }
}