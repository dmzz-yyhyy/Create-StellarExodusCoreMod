package io.nightfis.createstellarexoduscore.item;

import io.nightfis.createstellarexoduscore.block.entity.AutoAimTurretBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutoAimTurretItem extends BlockItem {
    public AutoAimTurretItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(
            @NotNull BlockPos pos,
            Level level,
            @Nullable Player player,
            @NotNull ItemStack itemStack,
            @NotNull BlockState blockState
    ) {
        if (!(level.getBlockEntity(pos) instanceof AutoAimTurretBlockEntity autoAimTurretBlockEntity)) return false;
        if (player == null) return false;
        autoAimTurretBlockEntity.initWhiteList(player);
        return true;
    }
}
