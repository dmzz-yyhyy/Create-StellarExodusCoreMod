package io.nightfis.createstellarexoduscore.compat.createbigcannons

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile
import rbasamoyai.createbigcannons.munitions.ProjectileContext
import rbasamoyai.createbigcannons.munitions.fuzes.ProximityFuzeItem

class EntityProximityFuzeItem(properties: Properties) : ProximityFuzeItem(properties) {

    override fun onProjectileClip(
        stack: ItemStack,
        projectile: AbstractCannonProjectile,
        start: Vec3,
        end: Vec3,
        ctx: ProjectileContext,
        isPenetrating: Boolean
    ): Boolean {
        if (isPenetrating) {
            return false
        }
        val tag: CompoundTag = stack.orCreateTag
        val detonationDistance = maxOf(tag.getInt(TAG_DETONATION_DISTANCE), 1) - 0.5

        val probeBox = AABB.ofSize(start, 0.0, 0.0, 0.0)
            .inflate(detonationDistance)
            .expandTowards(end.subtract(start))
            .move(start.subtract(projectile.position()))
        val entities = projectile.level().getEntities(projectile, probeBox, projectile::canHitEntity)
        if (entities.isEmpty()) {
            return false
        }

        for (entity: Entity in entities) {
            val entityBox = entity.boundingBox.inflate(detonationDistance)
            if (entityBox.contains(start) || entityBox.contains(end) || entityBox.clip(start, end).isPresent) {
                return true
            }
        }
        return false
    }

    companion object {
        private const val TAG_DETONATION_DISTANCE = "DetonationDistance"
    }
}
