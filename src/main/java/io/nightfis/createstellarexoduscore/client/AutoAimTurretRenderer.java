package io.nightfis.createstellarexoduscore.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import io.nightfis.createstellarexoduscore.block.AutoAimTurretBlock;
import io.nightfis.createstellarexoduscore.block.entity.AutoAimTurretBlockEntity;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class AutoAimTurretRenderer extends KineticBlockEntityRenderer<AutoAimTurretBlockEntity> {

    public AutoAimTurretRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(AutoAimTurretBlockEntity be, float partialTicks, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        Direction facing = state.getValue(AutoAimTurretBlock.FACING);
        Direction.Axis axis = facing.getAxis();

        SuperByteBuffer shaft = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state, facing);
        float angle = KineticBlockEntityRenderer.getAngleForBe(be, be.getBlockPos(), axis);
        KineticBlockEntityRenderer.kineticRotationTransform(shaft, be, axis, angle, packedLight);
        shaft.renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()));
    }
}
