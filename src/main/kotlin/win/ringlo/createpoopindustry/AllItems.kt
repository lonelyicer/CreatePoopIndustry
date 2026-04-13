package win.ringlo.createpoopindustry

import com.simibubi.create.foundation.data.CreateRegistrate
import com.tterrag.registrate.util.entry.ItemEntry
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import win.ringlo.createpoopindustry.AllTags.AllItemTags

object AllItems {
    private val REGISTRATE: CreateRegistrate = CreatePoopIndustry.registrate()

    val DRY_POOP = taggedIngredient("dry_poop", AllItemTags.DRY_POOP.tag)

    @SafeVarargs
    private fun taggedIngredient(name: String, vararg tags: TagKey<Item>): ItemEntry<Item> {
        return REGISTRATE.item(name)
            { properties: Item.Properties -> Item(properties) }
            .tag(*tags)
            .register()
    }
}