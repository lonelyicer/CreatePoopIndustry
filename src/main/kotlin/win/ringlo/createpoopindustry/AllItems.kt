package win.ringlo.createpoopindustry

import com.simibubi.create.foundation.data.CreateRegistrate
import com.tterrag.registrate.util.entry.ItemEntry
import net.minecraft.world.item.Item
import net.neoforged.neoforge.common.Tags
import win.ringlo.createpoopindustry.content.fluid.fermentation.FermentationTankControllerItem

object AllItems {
    private val REGISTRATE: CreateRegistrate = CreatePoopIndustry.registrate()

    val FERMENTATION_TANK_CONTROLLER: ItemEntry<FermentationTankControllerItem> = REGISTRATE
        .item("fermentation_tank_controller", ::FermentationTankControllerItem)
        .register()
}