package win.ringlo.createpoopindustry

import com.simibubi.create.content.fluids.VirtualFluid
import com.simibubi.create.foundation.data.CreateRegistrate
import com.tterrag.registrate.util.entry.FluidEntry


object AllFluids {
    private val REGISTRATE: CreateRegistrate = CreatePoopIndustry.registrate()

    val BIOGAS: FluidEntry<VirtualFluid> = REGISTRATE
        .virtualFluid("biogas")
        .lang("Biogas")
        .tag(AllTags.AllFluidTags.BIOGAS.tag)
        .register()

    fun register() {}
}