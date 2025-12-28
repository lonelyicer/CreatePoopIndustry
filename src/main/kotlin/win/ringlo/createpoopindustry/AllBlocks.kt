package win.ringlo.createpoopindustry

import com.simibubi.create.foundation.data.CreateRegistrate
import com.simibubi.create.foundation.data.SharedProperties
import com.simibubi.create.foundation.data.TagGen.pickaxeOnly
import com.tterrag.registrate.util.entry.BlockEntry
import net.minecraft.world.level.block.state.BlockBehaviour
import win.ringlo.createpoopindustry.content.fluid.fermentation.FermentationTankBlock
import win.ringlo.createpoopindustry.content.fluid.fermentation.FermentationTankGenerator

object AllBlocks {
    private val REGISTRATE: CreateRegistrate = CreatePoopIndustry.registrate()

    val FERMENTATION_TANK: BlockEntry<FermentationTankBlock> = REGISTRATE.block("fermentation_tank", ::FermentationTankBlock)
        .initialProperties(SharedProperties::copperMetal)
        .properties { p: BlockBehaviour.Properties ->
            p.noOcclusion()
                .isRedstoneConductor { _, _, _ -> true }
        }
        .transform(pickaxeOnly())
        .blockstate { ctx, prov -> FermentationTankGenerator().generate(ctx, prov) }
        .register()
    
    fun register() {}
}