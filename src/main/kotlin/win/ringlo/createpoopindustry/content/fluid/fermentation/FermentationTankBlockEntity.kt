package win.ringlo.createpoopindustry.content.fluid.fermentation

import com.simibubi.create.api.connectivity.ConnectivityHandler
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.fluid.SmartFluidTank
import com.simibubi.create.infrastructure.config.AllConfigs
import net.createmod.catnip.animation.LerpedFloat
import net.createmod.catnip.animation.LerpedFloat.Chaser
import net.createmod.catnip.nbt.NBTHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.chat.Component
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import win.ringlo.createpoopindustry.AllBlockEntityTypes
import kotlin.math.abs


class FermentationTankBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : SmartBlockEntity(
    type,
    pos,
    state
), IHaveGoggleInformation, IMultiBlockEntityContainer.Fluid {
    companion object {
        fun registerCapabilities(event: RegisterCapabilitiesEvent) {
            event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                AllBlockEntityTypes.FERMENTATION_TANK.get()
            ) { be: FermentationTankBlockEntity, _: Direction? ->
                be.fluidCapability
            }
        }
    }

    private val maxSize: Int = 3

    private var fluidCapability: IFluidHandler = FluidTank(0)
    private var forceFluidLevelUpdate: Boolean = false
    private var tankInventory: FluidTank = FluidTank(0)
    private var controllerPos: BlockPos? = null
    private var lastKnownPos: BlockPos? = null
    var updateConnectivity: Boolean = false
    private var updateCapability: Boolean = false
    var window: Boolean = false
    var luminosity: Int = 0
    private var width: Int = 0
    private var height: Int = 0

    private val syncRate: Int = 8

    private var syncCooldown: Int = 0
    private var queuedSync: Boolean = false

    private var fluidLevel: LerpedFloat? = null

    init {
        tankInventory = createInventory()
        forceFluidLevelUpdate = true
        updateConnectivity = false
        updateCapability = false
        window = true
        height = 1
        width = 1
        refreshCapability()
    }

    fun updateConnectivity() {
        updateConnectivity = false
        if (level!!.isClientSide)
            return
        if (!isController)
            return
        ConnectivityHandler.formMulti(this)
    }

    override fun initialize() {
        super.initialize()
        sendData()
        if (level!!.isClientSide)
            invalidateRenderBoundingBox()
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component?>?, isPlayerSneaking: Boolean): Boolean {
        val controllerBE: FermentationTankBlockEntity = getControllerBE() ?: return false
        return containedFluidTooltip(
            tooltip, isPlayerSneaking,
            level!!.getCapability(
                Capabilities.FluidHandler.BLOCK,
                controllerBE.blockPos,
                null
            )
        )
    }

    override fun tick() {
        super.tick()
        if (syncCooldown > 0) {
            syncCooldown--
            if (syncCooldown == 0 && queuedSync)
                sendData()
        }

        if (lastKnownPos == null) lastKnownPos = blockPos
        else if (lastKnownPos != worldPosition && worldPosition != null) {
            onPositionChanged()
            return
        }

        if (updateCapability) {
            updateCapability = false
            refreshCapability()
        }
        if (updateConnectivity)
            updateConnectivity()
        if (fluidLevel != null)
            fluidLevel!!.tickChaser()
    }

    private fun onPositionChanged() {
        removeController(true)
        lastKnownPos = worldPosition
    }

    private fun createInventory(): SmartFluidTank {
        return SmartFluidTank(
            getCapacityMultiplier()
        ) { newFluidStack: FluidStack -> this.onFluidStackChanged(newFluidStack) }
    }

    private fun refreshCapability() {
        fluidCapability = handlerForCapability()
        invalidateCapabilities()
    }

    private fun handlerForCapability(): IFluidHandler {
        return if (isController)
            tankInventory
        else
            getControllerBE<FermentationTankBlockEntity>()?.handlerForCapability() ?: FluidTank(0)
    }

    private fun onFluidStackChanged(newFluidStack: FluidStack) {
        if (!hasLevel())
            return

        val attributes = newFluidStack.fluid
            .getFluidType()
        val luminosity = (attributes.getLightLevel(newFluidStack) / 1.2f).toInt()
        val reversed = attributes.isLighterThanAir
        val maxY = ((getFillState() * height) + 1).toInt()

        for (yOffset in 0..height) {
            val isBright = if (reversed) (height - yOffset <= maxY) else (yOffset < maxY)
            val actualLuminosity = if (isBright) luminosity else if (luminosity > 0) 1 else 0

            for (xOffset in 0..width) {
                for (zOffset in 0..width) {
                    val pos = this.worldPosition.offset(xOffset, yOffset, zOffset)
                    val tankAt = ConnectivityHandler.partAt<FermentationTankBlockEntity>(type, level, pos) ?: continue
                    level!!.updateNeighbourForOutputSignal(
                        pos, tankAt.blockState
                            .block
                    )
                    if (tankAt.luminosity == actualLuminosity)
                        continue

                    tankAt.luminosity = actualLuminosity
                }
            }
        }

        if (!level!!.isClientSide) {
            setChanged()
            sendData()
        }

        if (isVirtual) {
            if (fluidLevel == null)
                fluidLevel = LerpedFloat.linear()
                .startWithValue(getFillState().toDouble())
            fluidLevel?.chase(getFillState().toDouble(), .5, Chaser.EXP)
        }
    }

    fun getFillState(): Float {
        return tankInventory.fluidAmount.toFloat() / tankInventory.getCapacity()
    }

    fun getCapacityMultiplier(): Int {
        return AllConfigs.server().fluids.fluidTankCapacity.get() * 1000
    }

    fun getMaxHeight(): Int {
        return AllConfigs.server().fluids.fluidTankMaxHeight.get()
    }

    override fun addBehaviours(behaviours: List<BlockEntityBehaviour>) {
    }

    override fun getController(): BlockPos? {
        return if (isController) worldPosition else controllerPos
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getControllerBE(): T? where T : BlockEntity, T : IMultiBlockEntityContainer {
        if (isController || !hasLevel())
            return this as? T
        val blockEntity = level?.getBlockEntity(controllerPos ?: return null)
        return blockEntity as? T
    }

    override fun isController(): Boolean {
        return controllerPos == null || worldPosition.x == controllerPos?.x && worldPosition.y == controllerPos?.y && worldPosition.z == controllerPos?.z
    }

    override fun setController(pos: BlockPos) {
        if (level!!.isClientSide && !isVirtual)
            return
        if (pos == this.controllerPos)
            return
        this.controllerPos = pos
        refreshCapability()
        setChanged()
        sendData()
    }

    override fun removeController(keepContents: Boolean) {
        if (level!!.isClientSide)
            return
        updateConnectivity = true
        if (!keepContents)
            applyFluidTankSize(1)
        controllerPos = null
        width = 1
        height = 1
        onFluidStackChanged(tankInventory.getFluid())

        var state = blockState
        if (FermentationTankBlock.isTank(state)) {
            state = state.setValue(FermentationTankBlock.bottom, true)
            state = state.setValue(FermentationTankBlock.top, true)
            state = state.setValue(
                FermentationTankBlock.shape,
                if (window) FermentationTankBlock.Shape.WINDOW else FermentationTankBlock.Shape.PLAIN
            )
            getLevel()?.setBlock(
                worldPosition,
                state,
                Block.UPDATE_CLIENTS or Block.UPDATE_INVISIBLE or Block.UPDATE_KNOWN_SHAPE
            )
        }

        refreshCapability()
        setChanged()
        sendData()
    }

    fun applyFluidTankSize(blocks: Int) {
        tankInventory.capacity = blocks * getCapacityMultiplier()
        val overflow = tankInventory.fluidAmount - tankInventory.capacity
        if (overflow > 0)
            tankInventory.drain(overflow, FluidAction.EXECUTE)
        forceFluidLevelUpdate = true
    }

    override fun getLastKnownPos(): BlockPos {
        return lastKnownPos ?: BlockPos.ZERO
    }

    override fun preventConnectivityUpdate() {
        updateConnectivity = false
    }

    override fun notifyMultiUpdated() {
        var state = this.blockState
        if (FermentationTankBlock.isTank(state)) { // safety
            state = state.setValue(
                FermentationTankBlock.bottom,
                controller?.y == blockPos.y
            )
            controller?.y?.let {
                state = state.setValue(
                    FermentationTankBlock.top,
                    it + height - 1 == blockPos.y
                )
            }
            level!!.setBlock(blockPos, state, Block.UPDATE_CLIENTS or Block.UPDATE_INVISIBLE)
        }

        if (isController)
            setWindows(window)

        onFluidStackChanged(tankInventory.getFluid())
        setChanged()
    }

    fun setWindows(window: Boolean) {
        this.window = window
        for (yOffset in 0..<height) {
            for (xOffset in 0..<width) {
                for (zOffset in 0..<width) {
                    val pos = this.worldPosition.offset(xOffset, yOffset, zOffset)
                    val blockState = level!!.getBlockState(pos)
                    if (!FermentationTankBlock.isTank(blockState)) continue

                    var shape =FermentationTankBlock.Shape.PLAIN
                    if (window) {
                        // SIZE 1: Every tank has a window
                        if (width == 1) shape = FermentationTankBlock.Shape.WINDOW
                        // SIZE 2: Every tank has a corner window
                        if (width == 2) shape = if (xOffset == 0)
                            if (zOffset == 0) FermentationTankBlock.Shape.WINDOW_NW else FermentationTankBlock.Shape.WINDOW_SW
                        else
                            if (zOffset == 0) FermentationTankBlock.Shape.WINDOW_NE else FermentationTankBlock.Shape.WINDOW_SE
                        // SIZE 3: Tanks in the center have a window
                        if (width == 3 && abs(abs(xOffset) - abs(zOffset)) == 1) shape = FermentationTankBlock.Shape.WINDOW
                    }

                    level!!.setBlock(
                        pos,
                        blockState.setValue(FermentationTankBlock.shape, shape),
                        Block.UPDATE_CLIENTS or Block.UPDATE_INVISIBLE or Block.UPDATE_KNOWN_SHAPE
                    )
                    level!!.chunkSource
                        .lightEngine
                        .checkBlock(pos)
                }
            }
        }
    }

    fun toggleWindows() {
        val be: FermentationTankBlockEntity = getControllerBE() ?: return
        be.setWindows(!be.window)
    }

    override fun getMainConnectionAxis(): Direction.Axis {
        return Direction.Axis.Y
    }

    override fun getMaxLength(longAxis: Direction.Axis, width: Int): Int {
        if (longAxis === Direction.Axis.Y)
            return getMaxHeight()
        return maxWidth
    }

    override fun getMaxWidth(): Int {
        return maxSize
    }

    override fun getHeight(): Int {
        return height
    }

    override fun setHeight(height: Int) {
        this.height = height
    }

    override fun getWidth(): Int {
        return width
    }

    override fun setWidth(width: Int) {
        this.width = width
    }

    override fun sendData() {
        if (syncCooldown > 0) {
            queuedSync = true
            return
        }
        super.sendData()
        queuedSync = false
        syncCooldown = syncRate
    }

    fun getTotalTankSize(): Int {
        return width * width * height
    }

    override fun read(
        compound: CompoundTag,
        registries: HolderLookup.Provider,
        clientPacket: Boolean
    ) {
        super.read(compound, registries, clientPacket)

        val controllerBefore = controllerPos
        val prevSize = width
        val prevHeight = height
        val prevLum = luminosity

        updateConnectivity = compound.contains("Uninitialized")
        luminosity = compound.getInt("Luminosity")

        lastKnownPos = null
        if (compound.contains("LastKnownPos")) {
            lastKnownPos = NBTHelper.readBlockPos(compound, "LastKnownPos")
        }

        controllerPos = null
        if (compound.contains("Controller")) {
            controllerPos = NBTHelper.readBlockPos(compound, "Controller")
        }

        if (isController) {
            window = compound.getBoolean("Window")
            width = compound.getInt("Size")
            height = compound.getInt("Height")

            tankInventory.capacity =
                getTotalTankSize() * getCapacityMultiplier()

            tankInventory.readFromNBT(
                registries,
                compound.getCompound("TankContent")
            )

            if (tankInventory.space < 0) {
                tankInventory.drain(
                    -tankInventory.space,
                    FluidAction.EXECUTE
                )
            }
        }

        if (compound.contains("ForceFluidLevel") || fluidLevel == null) {
            fluidLevel = LerpedFloat.linear()
                .startWithValue(getFillState().toDouble())
        }

        updateCapability = true

        if (!clientPacket) return

        val changeOfController = controllerBefore != controllerPos

        if (changeOfController || prevSize != width || prevHeight != height) {
            if (hasLevel()) {
                level?.sendBlockUpdated(
                    blockPos,
                    blockState,
                    blockState,
                    16
                )
            }

            if (isController) {
                tankInventory.capacity =
                    getCapacityMultiplier() * getTotalTankSize()
            }

            invalidateRenderBoundingBox()
        }

        if (isController) {
            val fillState = getFillState().toDouble()

            if (compound.contains("ForceFluidLevel") || fluidLevel == null) {
                fluidLevel = LerpedFloat.linear()
                    .startWithValue(fillState)
            }

            fluidLevel?.chase(fillState, 0.5, Chaser.EXP)
        }

        if (luminosity != prevLum && hasLevel()) {
            level?.chunkSource
                ?.lightEngine
                ?.checkBlock(worldPosition)
        }

        if (compound.contains("LazySync")) {
            fluidLevel?.chase(
                (fluidLevel?.chaseTarget ?: 0f).toDouble(),
                0.125,
                Chaser.EXP
            )
        }
    }

    override fun write(
        compound: CompoundTag,
        registries: HolderLookup.Provider,
        clientPacket: Boolean
    ) {
        if (updateConnectivity) {
            compound.putBoolean("Uninitialized", true)
        }

        lastKnownPos?.let {
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(it))
        }

        if (!isController) {
            controllerPos?.let {
                compound.put("Controller", NbtUtils.writeBlockPos(it))
            }
        }

        if (isController) {
            compound.putBoolean("Window", window)
            compound.put(
                "TankContent",
                tankInventory.writeToNBT(registries, CompoundTag())
            )
            compound.putInt("Size", width)
            compound.putInt("Height", height)
        }

        compound.putInt("Luminosity", luminosity)

        super.write(compound, registries, clientPacket)

        if (!clientPacket) return

        if (forceFluidLevelUpdate) {
            compound.putBoolean("ForceFluidLevel", true)
        }

        if (queuedSync) {
            compound.putBoolean("LazySync", true)
        }

        forceFluidLevelUpdate = false
    }

    override fun writeSafe(
        compound: CompoundTag,
        registries: HolderLookup.Provider
    ) {
        if (isController) {
            compound.putBoolean("Window", window)
            compound.putInt("Size", width)
            compound.putInt("Height", height)
        }
    }

    override fun setExtraData(data: Any?) {
        if (data is Boolean) window = data
    }

    override fun getExtraData(): Any {
        return window
    }

    override fun modifyExtraData(data: Any?): Any? {
        return if (data is Boolean) {
            data || window
        } else {
            data
        }
    }

    fun getFluidTankControllerBE(): FluidTankBlockEntity? {
        val tankAt = ConnectivityHandler.partAt<FluidTankBlockEntity>(
            com.simibubi.create.AllBlockEntityTypes.FLUID_TANK.get(),
            level,
            blockPos.below()
        )
        return tankAt?.controllerBE
    }
}