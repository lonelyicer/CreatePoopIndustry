package win.ringlo.createpoopindustry

import com.altnoir.poopsky.fluid.PSFluids
import com.simibubi.create.api.effect.OpenPipeEffectHandler
import win.ringlo.createpoopindustry.impl.effect.BiogasEffectHandler
import win.ringlo.createpoopindustry.impl.effect.PoopEffectHandler

object AllOpenPipeEffectHandlers {
    fun registerDefaults() {
        OpenPipeEffectHandler.REGISTRY.register(PSFluids.POOP.get(), PoopEffectHandler)
        OpenPipeEffectHandler.REGISTRY.register(AllFluids.BIOGAS.getSource(), BiogasEffectHandler)
    }
}