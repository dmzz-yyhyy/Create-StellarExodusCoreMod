package io.nightfis.createstellarexoduscore.block;

import javax.annotation.Nullable;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;

import io.nightfis.createstellarexoduscore.StellarExodusCore;
import io.nightfis.createstellarexoduscore.block.entity.AutoAimTurretBlockEntity;
import io.nightfis.createstellarexoduscore.network.AutoAimTurretFilterSyncPacket;
import io.nightfis.createstellarexoduscore.network.ModNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class AutoAimTurretBlock extends DirectionalKineticBlock implements EntityBlock {

    public AutoAimTurretBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return StellarExodusCore.AUTO_AIM_TURRET_ENTITY.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return type == StellarExodusCore.AUTO_AIM_TURRET_ENTITY.get()
                ? (lvl, pos, st, be) -> ((AutoAimTurretBlockEntity) be).tick(lvl, pos, st)
                : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hit
    ) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AutoAimTurretBlockEntity turret && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, turret, pos);
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new AutoAimTurretFilterSyncPacket(turret.getWhiteList()));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
