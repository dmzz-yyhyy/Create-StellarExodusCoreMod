package io.nightfis.createstellarexoduscore.compat.createbigcannons;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.fuzes.ProximityFuzeItem;

import java.util.List;

public class EntityProximityFuzeItem extends ProximityFuzeItem {

    private static final String TAG_DETONATION_DISTANCE = "DetonationDistance";

    public EntityProximityFuzeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onProjectileClip(ItemStack stack, AbstractCannonProjectile projectile, Vec3 start, Vec3 end,
            ProjectileContext ctx, boolean isPenetrating) {
        if (isPenetrating) {
            return false;
        }
        CompoundTag tag = stack.getOrCreateTag();
        double detonationDistance = Math.max(tag.getInt(TAG_DETONATION_DISTANCE), 1) - 0.5;

        AABB probeBox = AABB.ofSize(start, 0, 0, 0)
                .inflate(detonationDistance)
                .expandTowards(end.subtract(start))
                .move(start.subtract(projectile.position()));
        List<Entity> entities = projectile.level().getEntities(projectile, probeBox, projectile::canHitEntity);
        if (entities.isEmpty()) {
            return false;
        }

        for (Entity entity : entities) {
            AABB entityBox = entity.getBoundingBox().inflate(detonationDistance);
            if (entityBox.contains(start) || entityBox.contains(end) || entityBox.clip(start, end).isPresent()) {
                return true;
            }
        }
        return false;
    }
}
