package win.ringlo.createpoopindustry.content.fluid.fermentation

import com.simibubi.create.foundation.data.SpecialBlockStateGen
import com.tterrag.registrate.providers.DataGenContext
import com.tterrag.registrate.providers.RegistrateBlockstateProvider
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.client.model.generators.ModelFile

class FermentationTankGenerator : SpecialBlockStateGen() {
    override fun getXRotation(state: BlockState): Int {
        return 0
    }

    override fun getYRotation(state: BlockState): Int {
        return 0
    }

    override fun <T : Block?> getModel(
        ctx: DataGenContext<Block?, T?>?,
        prov: RegistrateBlockstateProvider?,
        state: BlockState?
    ): ModelFile? {
        TODO("Not yet implemented")
    }
}