package io.nightfis.createstellarexoduscore.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import io.nightfis.createstellarexoduscore.block.entity.StellarShuttleControllerBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import org.joml.Matrix4f

class StellarShuttleControllerRenderer(
    context: BlockEntityRendererProvider.Context
) : BlockEntityRenderer<StellarShuttleControllerBlockEntity> {

    override fun shouldRenderOffScreen(blockEntity: StellarShuttleControllerBlockEntity): Boolean = true

    override fun shouldRender(blockEntity: StellarShuttleControllerBlockEntity, cameraPos: Vec3): Boolean = true

    override fun render(
        blockEntity: StellarShuttleControllerBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val level = blockEntity.level ?: return
        val base = blockEntity.blockPos.below()
        val buffer = bufferSource.getBuffer(RenderType.debugQuads())
        val pose = poseStack.last().pose()
        val edges = HashMap<EdgeKey, Int>()
        val targetPos = BlockPos.MutableBlockPos()

        for (face in BOUNDARY_FACES) {
            val offset = face.offset
            targetPos.set(base.x + offset.x, base.y + offset.y, base.z + offset.z)
            if (level.getBlockState(targetPos).isAir) {
                continue
            }
            addFaceEdges(edges, offset, face.direction)
        }

        val mergedEdges = mergeEdges(edges.filterValues { it == 1 }.keys)
        for (edge in mergedEdges) {
            drawThickEdge(buffer, pose, edge, LINE_THICKNESS)
        }
    }

    companion object {
        private const val RADIUS = 16
        private const val RADIUS_SQR = RADIUS * RADIUS
        private const val BASE_Y = -1
        private const val COLOR_R = 1.0f
        private const val COLOR_G = 0.6f
        private const val COLOR_B = 0.0f
        private const val COLOR_A = 1.0f
        private const val LINE_THICKNESS = 0.12f

        private val BOUNDARY_FACES: List<BoundaryFace> = buildList {
            for (x in -RADIUS..RADIUS) {
                for (y in 0..RADIUS) {
                    for (z in -RADIUS..RADIUS) {
                        val distSqr = x * x + y * y + z * z
                        if (distSqr > RADIUS_SQR) {
                            continue
                        }
                        for (direction in Direction.entries) {
                            if (isOutside(x + direction.stepX, y + direction.stepY, z + direction.stepZ)) {
                                add(BoundaryFace(BlockPos(x, y, z), direction))
                            }
                        }
                    }
                }
            }
        }

        private fun isOutside(x: Int, y: Int, z: Int): Boolean {
            if (y < 0) {
                return true
            }
            val distSqr = x * x + y * y + z * z
            return distSqr > RADIUS_SQR
        }
    }

    private data class BoundaryFace(val offset: BlockPos, val direction: Direction)

    private data class EdgeKey(
        val x1: Int,
        val y1: Int,
        val z1: Int,
        val x2: Int,
        val y2: Int,
        val z2: Int
    )

    private fun addFaceEdges(edges: MutableMap<EdgeKey, Int>, offset: BlockPos, direction: Direction) {
        val x0 = offset.x
        val y0 = offset.y + BASE_Y
        val z0 = offset.z
        when (direction) {
            Direction.EAST -> {
                val x = x0 + 1
                addEdge(edges, x, y0, z0, x, y0 + 1, z0)
                addEdge(edges, x, y0, z0 + 1, x, y0 + 1, z0 + 1)
                addEdge(edges, x, y0, z0, x, y0, z0 + 1)
                addEdge(edges, x, y0 + 1, z0, x, y0 + 1, z0 + 1)
            }

            Direction.WEST -> {
                val x = x0
                addEdge(edges, x, y0, z0, x, y0 + 1, z0)
                addEdge(edges, x, y0, z0 + 1, x, y0 + 1, z0 + 1)
                addEdge(edges, x, y0, z0, x, y0, z0 + 1)
                addEdge(edges, x, y0 + 1, z0, x, y0 + 1, z0 + 1)
            }

            Direction.UP -> {
                val y = y0 + 1
                addEdge(edges, x0, y, z0, x0 + 1, y, z0)
                addEdge(edges, x0, y, z0 + 1, x0 + 1, y, z0 + 1)
                addEdge(edges, x0, y, z0, x0, y, z0 + 1)
                addEdge(edges, x0 + 1, y, z0, x0 + 1, y, z0 + 1)
            }

            Direction.DOWN -> {
                val y = y0
                addEdge(edges, x0, y, z0, x0 + 1, y, z0)
                addEdge(edges, x0, y, z0 + 1, x0 + 1, y, z0 + 1)
                addEdge(edges, x0, y, z0, x0, y, z0 + 1)
                addEdge(edges, x0 + 1, y, z0, x0 + 1, y, z0 + 1)
            }

            Direction.SOUTH -> {
                val z = z0 + 1
                addEdge(edges, x0, y0, z, x0 + 1, y0, z)
                addEdge(edges, x0, y0 + 1, z, x0 + 1, y0 + 1, z)
                addEdge(edges, x0, y0, z, x0, y0 + 1, z)
                addEdge(edges, x0 + 1, y0, z, x0 + 1, y0 + 1, z)
            }

            Direction.NORTH -> {
                val z = z0
                addEdge(edges, x0, y0, z, x0 + 1, y0, z)
                addEdge(edges, x0, y0 + 1, z, x0 + 1, y0 + 1, z)
                addEdge(edges, x0, y0, z, x0, y0 + 1, z)
                addEdge(edges, x0 + 1, y0, z, x0 + 1, y0 + 1, z)
            }
        }
    }

    private fun addEdge(
        edges: MutableMap<EdgeKey, Int>,
        x1: Int,
        y1: Int,
        z1: Int,
        x2: Int,
        y2: Int,
        z2: Int
    ) {
        var ax1 = x1
        var ay1 = y1
        var az1 = z1
        var ax2 = x2
        var ay2 = y2
        var az2 = z2
        if (ax1 > ax2 || (ax1 == ax2 && ay1 > ay2) || (ax1 == ax2 && ay1 == ay2 && az1 > az2)) {
            val tx = ax1
            val ty = ay1
            val tz = az1
            ax1 = ax2
            ay1 = ay2
            az1 = az2
            ax2 = tx
            ay2 = ty
            az2 = tz
        }
        val key = EdgeKey(ax1, ay1, az1, ax2, ay2, az2)
        edges[key] = (edges[key] ?: 0) + 1
    }

    private fun mergeEdges(edges: Collection<EdgeKey>): List<EdgeKey> {
        val result = mutableListOf<EdgeKey>()
        result += mergeAxisX(edges.filter { it.x1 != it.x2 })
        result += mergeAxisY(edges.filter { it.y1 != it.y2 })
        result += mergeAxisZ(edges.filter { it.z1 != it.z2 })
        return result
    }

    private fun mergeAxisX(edges: List<EdgeKey>): List<EdgeKey> {
        val result = mutableListOf<EdgeKey>()
        val grouped = edges.groupBy { Pair(it.y1, it.z1) }
        for ((key, list) in grouped) {
            val sorted = list.sortedBy { it.x1 }
            var start = sorted[0].x1
            var end = sorted[0].x2
            for (edge in sorted.drop(1)) {
                if (edge.x1 == end) {
                    end = edge.x2
                } else {
                    result.add(EdgeKey(start, key.first, key.second, end, key.first, key.second))
                    start = edge.x1
                    end = edge.x2
                }
            }
            result.add(EdgeKey(start, key.first, key.second, end, key.first, key.second))
        }
        return result
    }

    private fun mergeAxisY(edges: List<EdgeKey>): List<EdgeKey> {
        val result = mutableListOf<EdgeKey>()
        val grouped = edges.groupBy { Pair(it.x1, it.z1) }
        for ((key, list) in grouped) {
            val sorted = list.sortedBy { it.y1 }
            var start = sorted[0].y1
            var end = sorted[0].y2
            for (edge in sorted.drop(1)) {
                if (edge.y1 == end) {
                    end = edge.y2
                } else {
                    result.add(EdgeKey(key.first, start, key.second, key.first, end, key.second))
                    start = edge.y1
                    end = edge.y2
                }
            }
            result.add(EdgeKey(key.first, start, key.second, key.first, end, key.second))
        }
        return result
    }

    private fun mergeAxisZ(edges: List<EdgeKey>): List<EdgeKey> {
        val result = mutableListOf<EdgeKey>()
        val grouped = edges.groupBy { Pair(it.x1, it.y1) }
        for ((key, list) in grouped) {
            val sorted = list.sortedBy { it.z1 }
            var start = sorted[0].z1
            var end = sorted[0].z2
            for (edge in sorted.drop(1)) {
                if (edge.z1 == end) {
                    end = edge.z2
                } else {
                    result.add(EdgeKey(key.first, key.second, start, key.first, key.second, end))
                    start = edge.z1
                    end = edge.z2
                }
            }
            result.add(EdgeKey(key.first, key.second, start, key.first, key.second, end))
        }
        return result
    }

    private fun drawThickEdge(
        buffer: VertexConsumer,
        pose: Matrix4f,
        edge: EdgeKey,
        thickness: Float
    ) {
        val half = thickness / 2.0f
        val x1 = edge.x1.toFloat()
        val y1 = edge.y1.toFloat()
        val z1 = edge.z1.toFloat()
        val x2 = edge.x2.toFloat()
        val y2 = edge.y2.toFloat()
        val z2 = edge.z2.toFloat()

        if (edge.x1 != edge.x2) {
            val minX = minOf(x1, x2)
            val maxX = maxOf(x1, x2)
            drawBox(buffer, pose, minX, y1 - half, z1 - half, maxX, y1 + half, z1 + half)
        } else if (edge.y1 != edge.y2) {
            val minY = minOf(y1, y2)
            val maxY = maxOf(y1, y2)
            drawBox(buffer, pose, x1 - half, minY, z1 - half, x1 + half, maxY, z1 + half)
        } else {
            val minZ = minOf(z1, z2)
            val maxZ = maxOf(z1, z2)
            drawBox(buffer, pose, x1 - half, y1 - half, minZ, x1 + half, y1 + half, maxZ)
        }
    }

    private fun drawBox(
        buffer: VertexConsumer,
        pose: Matrix4f,
        minX: Float,
        minY: Float,
        minZ: Float,
        maxX: Float,
        maxY: Float,
        maxZ: Float
    ) {
        quad(buffer, pose, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ)
        quad(buffer, pose, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ)
        quad(buffer, pose, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ)
        quad(buffer, pose, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ)
        quad(buffer, pose, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ)
        quad(buffer, pose, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ)
    }

    private fun quad(
        buffer: VertexConsumer,
        pose: Matrix4f,
        x1: Float,
        y1: Float,
        z1: Float,
        x2: Float,
        y2: Float,
        z2: Float,
        x3: Float,
        y3: Float,
        z3: Float,
        x4: Float,
        y4: Float,
        z4: Float
    ) {
        buffer.vertex(pose, x1, y1, z1).color(COLOR_R, COLOR_G, COLOR_B, COLOR_A).endVertex()
        buffer.vertex(pose, x2, y2, z2).color(COLOR_R, COLOR_G, COLOR_B, COLOR_A).endVertex()
        buffer.vertex(pose, x3, y3, z3).color(COLOR_R, COLOR_G, COLOR_B, COLOR_A).endVertex()
        buffer.vertex(pose, x4, y4, z4).color(COLOR_R, COLOR_G, COLOR_B, COLOR_A).endVertex()
    }
}
