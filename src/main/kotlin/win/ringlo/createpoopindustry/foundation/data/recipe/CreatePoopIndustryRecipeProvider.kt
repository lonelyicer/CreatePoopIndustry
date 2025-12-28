package win.ringlo.createpoopindustry.foundation.data.recipe

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import java.util.concurrent.CompletableFuture

class CreatePoopIndustryRecipeProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) : RecipeProvider(
    output,
    registries
) {
    override fun buildRecipes(recipeOutput: RecipeOutput) {}

    companion object {
        private val GENERATORS = ArrayList<ProcessingRecipeGen<*, *, *>?>()

        fun registerAllProcessing(
            gen: DataGenerator,
            output: PackOutput,
            registries: CompletableFuture<HolderLookup.Provider>
        ) {
            GENERATORS.add(CreatePoopIndustryMixingRecipeGen(output, registries))

            gen.addProvider(true, object : DataProvider {
                override fun getName(): String {
                    return "Create Poop Industry's Processing Recipes"
                }

                override fun run(dc: CachedOutput): CompletableFuture<*> {
                    return CompletableFuture.allOf(
                        *GENERATORS.stream()
                            .map { gen -> gen!!.run(dc) }
                            .toArray { size -> arrayOfNulls<CompletableFuture<*>>(size) }
                    )
                }
            })
        }
    }
}