package win.ringlo.createpoopindustry.content.fluid.fermentation

import com.simibubi.create.api.connectivity.ConnectivityHandler
import com.simibubi.create.content.equipment.wrench.IWrenchable
import com.simibubi.create.content.fluids.tank.FluidTankBlock
import com.simibubi.create.foundation.advancement.AdvancementBehaviour
import com.simibubi.create.foundation.block.IBE
import com.simibubi.create.foundation.blockEntity.ComparatorUtil
import net.createmod.catnip.lang.Lang
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.StringRepresentable
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.common.util.DeferredSoundType
import win.ringlo.createpoopindustry.AllBlockEntityTypes

class FermentationTankBlock(properties: Properties) : Block(properties), IWrenchable, IBE<FermentationTankBlockEntity> {
    companion object {
        var top: BooleanProperty = BooleanProperty.create("top")
        var bottom: BooleanProperty = BooleanProperty.create("bottom")
        var shape: EnumProperty<Shape> = EnumProperty.create("shape", Shape::class.java)

        fun isTank(state: BlockState): Boolean {
            return state.block is FermentationTankBlock
        }
    }

    init {
        registerDefaultState(
            defaultBlockState().setValue(top, true)
                .setValue(bottom, true)
                .setValue(
                    shape,
                    Shape.WINDOW
                )
        )
    }

    val silencedMetal: SoundType =
        DeferredSoundType(
            0.1f,
            1.5f,
            { SoundEvents.METAL_BREAK },
            { SoundEvents.METAL_STEP },
            { SoundEvents.METAL_PLACE },
            { SoundEvents.METAL_HIT },
            { SoundEvents.METAL_FALL })

    val campFireSmokeClip: VoxelShape = box(0.0, 4.0, 0.0, 16.0, 16.0, 16.0)

    override fun setPlacedBy(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pPlacer: LivingEntity?,
        pStack: ItemStack
    ) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack)
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer)
    }

    override fun getBlockEntityClass(): Class<FermentationTankBlockEntity> {
        return FermentationTankBlockEntity::class.java
    }

    override fun getBlockEntityType(): BlockEntityType<out FermentationTankBlockEntity?> {
        return AllBlockEntityTypes.FERMENTATION_TANK.get()
    }

    override fun createBlockStateDefinition(p: StateDefinition.Builder<Block?, BlockState?>) {
        p.add(top, bottom, shape)
    }

    public override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        var state = state
        for (i in 0..<rotation.ordinal)
            state = rotateOnce(state)
        return state
    }

    private fun rotateOnce(state: BlockState): BlockState {
        when (state.getValue(shape)) {
            Shape.WINDOW_NE -> return state.setValue(
                shape,
                Shape.WINDOW_SE
            )

            Shape.WINDOW_NW -> return state.setValue(
                shape,
                Shape.WINDOW_NE
            )

            Shape.WINDOW_SE -> return state.setValue(
                shape,
                Shape.WINDOW_SW
            )

            Shape.WINDOW_SW -> return state.setValue(
                shape,
                Shape.WINDOW_NW
            )

            else -> return state
        }
    }

    public override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        if (mirror == Mirror.NONE)
            return state
        val x = mirror == Mirror.FRONT_BACK
        when (state.getValue(shape)) {
            Shape.WINDOW_NE -> return state.setValue(
                shape,
                if (x) Shape.WINDOW_NW else Shape.WINDOW_SE
            )

            Shape.WINDOW_NW -> return state.setValue(
                shape,
                if (x) Shape.WINDOW_NE else Shape.WINDOW_SW
            )

            Shape.WINDOW_SE -> return state.setValue(
                shape,
                if (x) Shape.WINDOW_SW else Shape.WINDOW_NE
            )

            Shape.WINDOW_SW -> return state.setValue(
                shape,
                if (x) Shape.WINDOW_SE else Shape.WINDOW_NW
            )

            else -> return state
        }
    }

    public override fun hasAnalogOutputSignal(state: BlockState): Boolean {
        return true
    }

    public override fun getAnalogOutputSignal(
        blockState: BlockState,
        worldIn: Level,
        pos: BlockPos
    ): Int {
        val be = getBlockEntityOptional(worldIn, pos).orElse(null)
            ?.getControllerBE<FermentationTankBlockEntity>()
            ?: return 0

        return ComparatorUtil.fractionToRedstoneLevel(
            be.getFillState().toDouble()
        )
    }

    override fun getSoundType(state: BlockState, world: LevelReader, pos: BlockPos, entity: Entity?): SoundType {
        val soundType = super.getSoundType(state, world, pos, entity)
        if (entity != null && entity.getPersistentData()
                .contains("SilenceTankSound")
        ) return silencedMetal
        return soundType
    }

    public override fun onRemove(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        newState: BlockState,
        isMoving: Boolean
    ) {
        if (state.hasBlockEntity() && (state.block !== newState.block || !newState.hasBlockEntity())) {
            val be = world.getBlockEntity(pos)
            if (be !is FermentationTankBlockEntity)
                return
            world.removeBlockEntity(pos)
            ConnectivityHandler.splitMulti(be)
        }
    }

    public override fun getBlockSupportShape(pState: BlockState, pReader: BlockGetter, pPos: BlockPos): VoxelShape {
        return Shapes.block()
    }


    public override fun getCollisionShape(
        pState: BlockState, pLevel: BlockGetter, pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape {
        if (pContext === CollisionContext.empty())
            return campFireSmokeClip
        return pState.getShape(pLevel, pPos)
    }

    override fun onWrenched(state: BlockState?, context: UseOnContext): InteractionResult {
        withBlockEntityDo(
            context.level,
            context.clickedPos
        ) { obj: FermentationTankBlockEntity -> obj.toggleWindows() }
        return InteractionResult.SUCCESS
    }

    override fun getLightEmission(state: BlockState, world: BlockGetter, pos: BlockPos): Int {
        val tankAt = ConnectivityHandler.partAt<FermentationTankBlockEntity>(blockEntityType, world, pos)
        if (tankAt == null || !tankAt.hasLevel()) return 0
        val controllerBE = tankAt.getControllerBE<FermentationTankBlockEntity>()
        if (controllerBE == null || !controllerBE.window)
            return 0
        return tankAt.luminosity
    }

    public override fun onPlace(state: BlockState, world: Level, pos: BlockPos, oldState: BlockState, moved: Boolean) {
        if (oldState.block === state.block)
            return
        if (moved)
            return
        withBlockEntityDo(world, pos) { obj: FermentationTankBlockEntity -> obj.updateConnectivity() }
    }

    enum class Shape : StringRepresentable {
        PLAIN, WINDOW, WINDOW_NW, WINDOW_SW, WINDOW_NE, WINDOW_SE;

        override fun getSerializedName(): String {
            return Lang.asId(name)
        }
    }

}