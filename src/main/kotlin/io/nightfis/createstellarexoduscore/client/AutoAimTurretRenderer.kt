package io.nightfis.createstellarexoduscore.client

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import io.nightfis.createstellarexoduscore.block.entity.AutoAimTurretBlockEntity
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction

class AutoAimTurretRenderer(context: BlockEntityRendererProvider.Context) :
    KineticBlockEntityRenderer<AutoAimTurretBlockEntity>(context) {

    override fun renderSafe(
        be: AutoAimTurretBlockEntity,
        partialTicks: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val state = be.blockState
        val facing = Direction.DOWN
        val axis = facing.axis

        val shaft: SuperByteBuffer = CachedBuffers.partialFacing(AllPartialModels.SHAFT, state, facing)
        val angle = getAngleForBe(be, be.blockPos, axis)
        kineticRotationTransform(shaft, be, axis, angle, packedLight)
        shaft.renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()))
    }
}
