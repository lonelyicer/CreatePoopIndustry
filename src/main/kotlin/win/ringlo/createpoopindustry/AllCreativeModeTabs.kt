package win.ringlo.createpoopindustry

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.builder
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.jetbrains.annotations.ApiStatus.Internal


object AllCreativeModeTabs {
    private val REGISTER =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreatePoopIndustry.MOD_ID)

    val BASE_TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> = REGISTER.register("base") { _ ->
        builder()
            .title(Component.translatable("itemGroup.createpoopindustry.base"))
            .icon(com.simibubi.create.AllItems.WRENCH::asStack)
            .displayItems { _, output ->
                output.accept(com.simibubi.create.AllItems.WRENCH::get)
                output.accept(AllItems.FERMENTATION_TANK_CONTROLLER::get)
            }
            .build()
    }

    @Internal
    fun register(modEventBus: IEventBus) {
        REGISTER.register(modEventBus)
    }
}