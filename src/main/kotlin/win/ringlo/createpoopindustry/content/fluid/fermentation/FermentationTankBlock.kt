package win.ringlo.createpoopindustry.content.fluid.fermentation

import com.simibubi.create.content.equipment.wrench.IWrenchable
import com.simibubi.create.foundation.block.IBE
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import win.ringlo.createpoopindustry.AllBlockEntityTypes

class FermentationTankBlock(properties: Properties) : Block(properties), IWrenchable, IBE<FermentationTankBlockEntity> {
    override fun getBlockEntityClass(): Class<FermentationTankBlockEntity> {
        return FermentationTankBlockEntity::class.java
    }

    override fun getBlockEntityType(): BlockEntityType<out FermentationTankBlockEntity?> {
        return AllBlockEntityTypes.FERMENTATION_TANK.get()
    }
}