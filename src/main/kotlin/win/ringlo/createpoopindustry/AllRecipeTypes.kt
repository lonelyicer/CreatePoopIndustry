package win.ringlo.createpoopindustry

import com.simibubi.create.Create
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.registries.DeferredRegister

object AllRecipeTypes : IRecipeTypeInfo, StringRepresentable {
    override fun getId(): ResourceLocation? {
        TODO("Not yet implemented")
    }

    override fun <T : RecipeSerializer<*>?> getSerializer(): T? {
        TODO("Not yet implemented")
    }

    override fun <I : RecipeInput?, R : Recipe<I?>?> getType(): RecipeType<R?>? {
        TODO("Not yet implemented")
    }

    override fun getSerializedName(): String {
        TODO("Not yet implemented")
    }


    object Registers {
        private val SERIALIZER_REGISTER =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Create.ID)
        private val TYPE_REGISTER =
            DeferredRegister.create(Registries.RECIPE_TYPE, Create.ID)
    }
}