package win.ringlo.createpoopindustry.content.fluid.fermentation

import com.simibubi.create.content.fluids.tank.FluidTankBlock
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext

class FermentationTankControllerItem(properties: Properties) : Item(properties) {
    override fun useOn(context: UseOnContext): InteractionResult {
        if (context.level.isClientSide)
            return super.useOn(context)

        val state = context.level.getBlockState(context.clickedPos)
        val block = state.block

        if (block is FluidTankBlock)
            return super.useOn(context)



        return InteractionResult.SUCCESS
    }

    fun checkMultiblock(interactiveBlock: FluidTankBlock) {

    }
}