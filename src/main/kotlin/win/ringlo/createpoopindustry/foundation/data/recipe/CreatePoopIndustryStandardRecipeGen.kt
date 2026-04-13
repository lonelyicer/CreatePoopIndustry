package win.ringlo.createpoopindustry.foundation.data.recipe

import com.simibubi.create.api.data.recipe.BaseRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import win.ringlo.createpoopindustry.CreatePoopIndustry
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class CreatePoopIndustryStandardRecipeGen(
    output: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider>,
    defaultNamespace: String
) : BaseRecipeProvider(output, registries, defaultNamespace) {
    val all = ArrayList<GeneratedRecipe>()

    override fun getName(): String {
        return "Create Poop Industry's Standard Recipes"
    }

    override fun buildRecipes(output: RecipeOutput) {
        all.forEach(Consumer { c: GeneratedRecipe -> c.register(output) })
        CreatePoopIndustry.LOGGER.info("{} registered {} recipe{}", name, all.size, if (all.size == 1) "" else "s")
    }
}