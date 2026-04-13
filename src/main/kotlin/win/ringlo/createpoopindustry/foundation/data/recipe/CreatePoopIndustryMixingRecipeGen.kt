package win.ringlo.createpoopindustry.foundation.data.recipe

import com.altnoir.poopsky.fluid.PSFluids
import com.altnoir.poopsky.item.PSItems
import com.simibubi.create.api.data.recipe.MixingRecipeGen
import com.simibubi.create.content.processing.recipe.HeatCondition
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.world.level.material.Fluids
import win.ringlo.createpoopindustry.CreatePoopIndustry
import java.util.concurrent.CompletableFuture

class CreatePoopIndustryMixingRecipeGen(
    output: PackOutput, 
    registries: CompletableFuture<HolderLookup.Provider>
) : MixingRecipeGen(output, registries, CreatePoopIndustry.MOD_ID) {

    val POOP_FLUID = create("poop_fluid") { b -> b.require(Fluids.WATER, 250)
            .require(PSItems.POOP)
            .output(PSFluids.POOP.get(), 250)
            .requiresHeat(HeatCondition.NONE)
    }
}