package win.ringlo.createpoopindustry

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState


object AllTags {
    enum class NameSpace(val id: String) {
        MOD(CreatePoopIndustry.MOD_ID),
        COMMON("c");

        fun id(value: Enum<*>, pathOverride: String?): ResourceLocation {
            val path = pathOverride ?: value.name.lowercase()
            return ResourceLocation.fromNamespaceAndPath(id, path)
        }
    }

    enum class AllFluidTags(namespace: NameSpace, pathOverride: String? = null) {
        BIOGAS(NameSpace.COMMON);

        val tag: TagKey<Fluid> = TagKey.create(Registries.FLUID, namespace.id(this, pathOverride))

        constructor() : this(NameSpace.MOD, null)

        fun matches(state: FluidState): Boolean {
            return state.`is`(tag)
        }
    }
}