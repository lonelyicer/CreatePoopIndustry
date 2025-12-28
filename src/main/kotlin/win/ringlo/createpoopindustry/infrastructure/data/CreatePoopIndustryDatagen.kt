package win.ringlo.createpoopindustry.infrastructure.data

import com.simibubi.create.infrastructure.data.GeneratedEntriesProvider
import com.tterrag.registrate.providers.ProviderType
import net.neoforged.neoforge.data.event.GatherDataEvent
import win.ringlo.createpoopindustry.CreatePoopIndustry
import win.ringlo.createpoopindustry.foundation.data.recipe.CreatePoopIndustryRecipeProvider


object CreatePoopIndustryDatagen {
    fun gatherDataHighPriority(event: GatherDataEvent) {
        if (!event.mods.contains(CreatePoopIndustry.MOD_ID))
            return

        CreatePoopIndustry.registrate().addDataGenerator(ProviderType.LANG) { provider ->
            val langConsumer: (String, String) -> Unit = provider::add


        }
    }

    fun gatherData(event: GatherDataEvent){
        if (!event.mods.contains(CreatePoopIndustry.MOD_ID))
            return

        val generator = event.generator
        val output = generator.packOutput
        var lookupProvider = event.lookupProvider
        val existingFileHelper = event.existingFileHelper


        val generatedEntriesProvider = GeneratedEntriesProvider(output, lookupProvider)
        lookupProvider = generatedEntriesProvider.registryProvider


        if (event.includeServer())
            CreatePoopIndustryRecipeProvider.registerAllProcessing(generator, output, lookupProvider)
    }
}