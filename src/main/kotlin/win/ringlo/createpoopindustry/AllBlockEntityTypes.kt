package win.ringlo.createpoopindustry

import com.simibubi.create.foundation.data.CreateRegistrate
import com.tterrag.registrate.util.entry.BlockEntityEntry
import win.ringlo.createpoopindustry.content.fluid.fermentation.FermentationTankBlockEntity

object AllBlockEntityTypes {
    private val REGISTRATE: CreateRegistrate = CreatePoopIndustry.registrate()
    
    val FERMENTATION_TANK: BlockEntityEntry<FermentationTankBlockEntity> = REGISTRATE
        .blockEntity("fermentation_tank", ::FermentationTankBlockEntity)
        .validBlock(AllBlocks.FERMENTATION_TANK)
        .register()
}
