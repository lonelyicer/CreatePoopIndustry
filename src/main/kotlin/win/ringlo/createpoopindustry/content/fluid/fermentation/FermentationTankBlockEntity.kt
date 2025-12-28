package win.ringlo.createpoopindustry.content.fluid.fermentation

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class FermentationTankBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : SmartBlockEntity(
    type,
    pos,
    state
), IHaveGoggleInformation, IMultiBlockEntityContainer.Fluid {
    override fun addBehaviours(behaviours: List<BlockEntityBehaviour>) {
        TODO("Not yet implemented")
    }

    override fun getController(): BlockPos {
        TODO("Not yet implemented")
    }

    override fun <T> getControllerBE(): T where T : BlockEntity, T : IMultiBlockEntityContainer {
        TODO("Not yet implemented")
    }

    override fun isController(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setController(pos: BlockPos) {
        TODO("Not yet implemented")
    }

    override fun removeController(keepContents: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getLastKnownPos(): BlockPos {
        TODO("Not yet implemented")
    }

    override fun preventConnectivityUpdate() {
        TODO("Not yet implemented")
    }

    override fun notifyMultiUpdated() {
        TODO("Not yet implemented")
    }

    override fun getMainConnectionAxis(): Direction.Axis {
        TODO("Not yet implemented")
    }

    override fun getMaxLength(longAxis: Direction.Axis, width: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getMaxWidth(): Int {
        TODO("Not yet implemented")
    }

    override fun getHeight(): Int {
        TODO("Not yet implemented")
    }

    override fun setHeight(height: Int) {
        TODO("Not yet implemented")
    }

    override fun getWidth(): Int {
        TODO("Not yet implemented")
    }

    override fun setWidth(width: Int) {
        TODO("Not yet implemented")
    }
}