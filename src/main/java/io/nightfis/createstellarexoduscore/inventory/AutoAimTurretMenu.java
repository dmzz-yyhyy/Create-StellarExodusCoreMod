package io.nightfis.createstellarexoduscore.inventory;

import io.nightfis.createstellarexoduscore.StellarExodusCore;
import io.nightfis.createstellarexoduscore.block.entity.AutoAimTurretBlockEntity;
import io.nightfis.createstellarexoduscore.filter.TargetFilter;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AutoAimTurretMenu extends AbstractContainerMenu {

    private final AutoAimTurretBlockEntity blockEntity;
    private final Level level;

    public AutoAimTurretMenu(MenuType<?> menuType, int containerId, Inventory inventory, BlockEntity machine) {
        super(menuType, containerId);
        this.blockEntity = (AutoAimTurretBlockEntity) machine;
        this.level = inventory.player.level();
    }

    public AutoAimTurretMenu(MenuType<?> menuType, int containerId, Inventory inventory,
            FriendlyByteBuf extraData) {
        this(menuType, containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player,
                StellarExodusCore.AUTO_AIM_TURRET.get());
    }

    public void addFilter(String id, String arg) {
        blockEntity.addFilter(id, arg);
    }

    public void removeFilter(String id, String arg) {
        blockEntity.removeFilter(id, arg);
    }

    public void handleSync(List<Pair<TargetFilter, String>> filters) {
        blockEntity.handleSync(filters);
    }
}
