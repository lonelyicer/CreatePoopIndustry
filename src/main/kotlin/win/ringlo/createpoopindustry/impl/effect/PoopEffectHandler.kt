package win.ringlo.createpoopindustry.impl.effect

import com.simibubi.create.api.effect.OpenPipeEffectHandler
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.fluids.FluidStack
import java.util.function.Predicate

object PoopEffectHandler : OpenPipeEffectHandler {
    override fun apply(
        level: Level,
        area: AABB,
        fluid: FluidStack
    ) {
        if (level.gameTime % 5 != 0L) return

        val entities = level.getEntitiesOfClass(
            LivingEntity::class.java,
            area,
            Predicate { obj: LivingEntity -> obj.isAffectedByPotions })

        for (entity in entities) {
            entity.addEffect(MobEffectInstance(MobEffects.BLINDNESS, 40, 0, true, true, true))
        }
    }
}