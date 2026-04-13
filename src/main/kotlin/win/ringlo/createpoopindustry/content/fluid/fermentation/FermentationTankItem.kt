package win.ringlo.createpoopindustry.content.fluid.fermentation

import com.simibubi.create.api.connectivity.ConnectivityHandler
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandItem
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity
import com.simibubi.create.foundation.block.IBE
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.FluidStack
import win.ringlo.createpoopindustry.AllBlockEntityTypes
import kotlin.math.min

class FermentationTankItem(block: Block, properties: Properties) : BlockItem(block, properties) {
    override fun place(ctx: BlockPlaceContext): InteractionResult {
        val initialResult = super.place(ctx)
        if (!initialResult.consumesAction())
            return initialResult
        tryMultiPlace(ctx)
        return initialResult
    }

    override fun updateCustomBlockEntityTag(
        blockPos: BlockPos, level: Level, player: Player?,
        itemStack: ItemStack, blockState: BlockState
    ): Boolean {
        val minecraftServer = level.server ?: return false
        val blockEntityData = itemStack.get(DataComponents.BLOCK_ENTITY_DATA)
        if (blockEntityData != null) {
            val nbt = blockEntityData.copyTag()
            nbt.remove("Luminosity")
            nbt.remove("Size")
            nbt.remove("Height")
            nbt.remove("Controller")
            nbt.remove("LastKnownPos")
            if (nbt.contains("TankContent")) {
                val fluid = FluidStack.parseOptional(minecraftServer.registryAccess(), nbt.getCompound("TankContent"))
                if (!fluid.isEmpty) {
                    fluid.amount = min(FluidTankBlockEntity.getCapacityMultiplier(), fluid.amount)
                    nbt.put("TankContent", fluid.saveOptional(minecraftServer.registryAccess()))
                }
            }
            BlockEntity.addEntityType(nbt, (this.block as IBE<*>).getBlockEntityType())
            itemStack.set<CustomData?>(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(nbt))
        }
        return super.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState)
    }

    private fun tryMultiPlace(ctx: BlockPlaceContext) {
        val player = ctx.player ?: return
        if (player.isShiftKeyDown) return

        val face: Direction = ctx.clickedFace ?: return
        if (!face.axis.isVertical) return

        val stack = ctx.itemInHand
        val world = ctx.level
        val pos = ctx.clickedPos

        val placedOnPos = pos.relative(face.opposite)
        val placedOnState = world.getBlockState(placedOnPos)

        if (!FermentationTankBlock.isTank(placedOnState))
            return
        if (SymmetryWandItem.presentInHotbar(player))
            return

        val tankAt = ConnectivityHandler.partAt<FermentationTankBlockEntity>(
            AllBlockEntityTypes.FERMENTATION_TANK.get(),
            world,
            placedOnPos
        )

        val controllerBE = tankAt?.getControllerBE<FermentationTankBlockEntity>() ?: return

        val width = controllerBE.width
        if (width == 1) return

        val startPos = if (face == Direction.DOWN) {
            controllerBE.blockPos.below()
        } else {
            controllerBE.blockPos.above(controllerBE.height)
        }

        if (startPos.y != pos.y) return

        var tanksToPlace = 0

        for (x in 0 until width) {
            for (z in 0 until width) {
                val offsetPos = startPos.offset(x, 0, z)
                val state = world.getBlockState(offsetPos)

                if (FermentationTankBlock.isTank(state)) continue
                if (!state.canBeReplaced()) return

                tanksToPlace++
            }
        }

        if (!player.isCreative && stack.count < tanksToPlace) return

        for (x in 0 until width) {
            for (z in 0 until width) {
                val offsetPos = startPos.offset(x, 0, z)
                val state = world.getBlockState(offsetPos)

                if (FermentationTankBlock.isTank(state)) continue

                val context = BlockPlaceContext.at(ctx, offsetPos, face)

                player.persistentData.putBoolean("SilenceTankSound", true)
                super.place(context)
                player.persistentData.remove("SilenceTankSound")
            }
        }
    }
}