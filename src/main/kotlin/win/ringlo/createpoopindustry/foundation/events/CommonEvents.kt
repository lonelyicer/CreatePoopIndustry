package win.ringlo.createpoopindustry.foundation.events

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import win.ringlo.createpoopindustry.content.fluid.fermentation.FermentationTankBlockEntity

class CommonEvents {
    @EventBusSubscriber
    class ModBusEvents {
        companion object {
            @SubscribeEvent
            @JvmStatic
            fun registerCapabilities(event: RegisterCapabilitiesEvent){
                FermentationTankBlockEntity.registerCapabilities(event)
            }
        }
    }
}