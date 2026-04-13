package win.ringlo.createpoopindustry

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
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

    enum class AllItemTags(namespace: NameSpace, pathOverride: String? = null) {
        DRY_POOP(NameSpace.COMMON);

        val tag: TagKey<Item> = TagKey.create(Registries.ITEM, namespace.id(this, pathOverride))

        constructor() : this(NameSpace.MOD, null)

        @Suppress("DEPRECATION")
        fun matches(item: Item): Boolean {
            return item.builtInRegistryHolder()
                .`is`(tag)
        }

        fun matches(itemStack: ItemStack): Boolean {
            return itemStack.`is`(tag)
        }
    }

    enum class AllFluidTags(namespace: NameSpace, pathOverride: String? = null) {
        BIOGAS(NameSpace.COMMON);

        val tag: TagKey<Fluid> = TagKey.create(Registries.FLUID, namespace.id(this, pathOverride))

        constructor() : this(NameSpace.MOD, null)

        @Suppress("DEPRECATION")
        fun matches(fluid: Fluid): Boolean {
            return fluid.`is`(tag)
        }

        fun matches(state: FluidState): Boolean {
            return state.`is`(tag)
        }
    }
}