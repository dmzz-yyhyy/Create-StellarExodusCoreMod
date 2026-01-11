package io.nightfis.createstellarexoduscore.block.entity;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import io.nightfis.createstellarexoduscore.StellarExodusCore;
import io.nightfis.createstellarexoduscore.block.AutoAimTurretBlock;
import io.nightfis.createstellarexoduscore.filter.HasCustomNameFilter;
import io.nightfis.createstellarexoduscore.filter.IsEntityIdFilter;
import io.nightfis.createstellarexoduscore.filter.IsFriendlyFilter;
import io.nightfis.createstellarexoduscore.filter.IsOnVehicleFilter;
import io.nightfis.createstellarexoduscore.filter.IsPetFilter;
import io.nightfis.createstellarexoduscore.filter.IsPlayerIdFilter;
import io.nightfis.createstellarexoduscore.inventory.AutoAimTurretMenu;
import io.nightfis.createstellarexoduscore.filter.TargetFilter;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlock;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption;
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AbstractAutocannonBreechBlockEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AutoAimTurretBlockEntity extends KineticBlockEntity implements MenuProvider {


    private static final float BASE_STRESS_IMPACT = 32f;
    private final List<Pair<TargetFilter, String>> whiteList = new ArrayList<>();

    public AutoAimTurretBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        this.tick();
        if (level == null) return;
        if (this.isOverStressed()) return;
        var cannonMountBlockPos = pos.relative(state.getValue(AutoAimTurretBlock.FACING).getOpposite());
        var blockEntity = level.getBlockEntity(cannonMountBlockPos);
        var blockState = level.getBlockState(cannonMountBlockPos);
        if (!(blockEntity instanceof CannonMountBlockEntity cannonMountBlockEntity)) return;
        var verticalDirection = blockState.getValue(CannonMountBlock.VERTICAL_DIRECTION);
        var cannonMountPos = cannonMountBlockPos.getCenter();
        if (verticalDirection.equals(Direction.DOWN)) {
            cannonMountPos = cannonMountPos.add(0, 2, 0);
        } else {
            cannonMountPos = cannonMountPos.add(0, -2, 0);
        }
        double range = 64;
        AABB area = new AABB(this.worldPosition).inflate(range);
        AtomicReference<Double> minDistanceSqr = new AtomicReference<>(Double.MAX_VALUE);
        AtomicReference<LivingEntity> targetAtomicReference = new AtomicReference<>(null);
        var rangeSqr = range * range;
        Vec3 finalCannonMountPos = cannonMountPos;
        level.getEntitiesOfClass(LivingEntity.class, area, e -> {
            if (!e.isAlive() || e.isSpectator()) return false;
            if (isFiltered(e)) return false;
            var distanceSqr = e.position().distanceToSqr(finalCannonMountPos);
            if (distanceSqr > rangeSqr || minDistanceSqr.get() <= distanceSqr) return false;
            for (Pair<TargetFilter, String> filterStringPair : whiteList) {
                if (filterStringPair.key().match(e, filterStringPair.value())) return false;
            }
            minDistanceSqr.set(distanceSqr);
            targetAtomicReference.set(e);
            return true;
        });
        if (targetAtomicReference.get() == null) {
            var contraption = cannonMountBlockEntity.getContraption();
            if (contraption == null) return;
            var cannonContraption = (AbstractMountedCannonContraption) contraption.getContraption();
            if (cannonContraption == null) return;
            if (!cannonContraption.canBeFiredOnController(contraption.getController())) return;
            Object var4 = cannonContraption.presentBlockEntities.get(cannonContraption.getStartPos());
            if (var4 instanceof AbstractAutocannonBreechBlockEntity breech) {
                breech.setFireRate(0);
            }
            return;
        }
        var targetVec = EntityAnchorArgument.Anchor.EYES.apply(targetAtomicReference.get())
                .add(EntityAnchorArgument.Anchor.FEET.apply(targetAtomicReference.get()))
                .scale(0.5);
        double d0 = cannonMountPos.x - targetVec.x;
        double d1 = cannonMountPos.y - targetVec.y;
        double d2 = cannonMountPos.z - targetVec.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        var xRot = (Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)))));
        var yRot = (Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) + 90.0F));
        cannonMountBlockEntity.setPitch(xRot);
        cannonMountBlockEntity.setYaw(yRot);
        if (!(level instanceof ServerLevel serverLevel)) return;
        var contraption = cannonMountBlockEntity.getContraption();
        if (contraption == null) return;
        if (!cannonMountBlockEntity.isRunning()) return;
        var cannonContraption = (AbstractMountedCannonContraption) contraption.getContraption();
        if (cannonContraption == null) return;
        if (!cannonContraption.canBeFiredOnController(contraption.getController())) return;
        Object var4 = cannonContraption.presentBlockEntities.get(cannonContraption.getStartPos());
        if (var4 instanceof AbstractAutocannonBreechBlockEntity breech) {
             breech.setFireRate(15);
        }
        cannonContraption.fireShot(serverLevel, contraption);
    }

    @Override
    public float calculateStressApplied() {
        return BASE_STRESS_IMPACT;
    }

    private boolean isFiltered(LivingEntity entity) {
        for (Pair<TargetFilter, String> filter : whiteList) {
            if (filter.left().match(entity, filter.right())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        ListTag filters = new ListTag();
        for (Pair<TargetFilter, String> entry : whiteList) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("id", entry.left().getId());
            entryTag.putString("arg", entry.right());
            filters.add(entryTag);
        }
        tag.put("Filters", filters);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        whiteList.clear();
        for (Tag entry : tag.getList("Filters", Tag.TAG_COMPOUND)) {
            if (entry instanceof CompoundTag filter) {
                whiteList.add(Pair.of(TargetFilter.getFilter(filter.getString("id")), filter.getString("arg")));
            }
        }
    }

    public void addFilter(String id, String arg) {
        whiteList.add(Pair.of(TargetFilter.getFilter(id), arg));
        setChanged();
    }

    public void removeFilter(String id, String arg) {
        whiteList.removeIf(pair -> pair.left().getId().equals(id) && pair.right().equals(arg));
        setChanged();
    }

    public void handleSync(List<Pair<TargetFilter, String>> filters) {
        whiteList.clear();
        whiteList.addAll(filters);
        setChanged();
    }

    public List<Pair<TargetFilter, String>> getWhiteList() {
        return new ArrayList<>(whiteList);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.createstellarexoduscore.auto_aim_turret");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory, @NotNull Player player) {
        if (level == null || player.isSpectator()) {
            return null;
        }
        return new AutoAimTurretMenu(StellarExodusCore.AUTO_AIM_TURRET_MENU.get(), containerId, inventory, this);
    }

    public void initWhiteList(Player player) {
        whiteList.add(Pair.of(new IsPlayerIdFilter(), player.getName().getString()));
        whiteList.add(Pair.of(new IsPetFilter(), ""));
        whiteList.add(Pair.of(new HasCustomNameFilter(), ""));
        whiteList.add(Pair.of(new IsEntityIdFilter(), Component.translatable("entity.minecraft.villager").getString()));
        whiteList.add(Pair.of(new IsEntityIdFilter(), Component.translatable("entity.minecraft.wandering_trader").getString()));
        whiteList.add(Pair.of(new IsFriendlyFilter(), ""));
        whiteList.add(Pair.of(new IsOnVehicleFilter(), ""));
    }
}
