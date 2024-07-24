package com.tynicraft.experiosa.block

import com.mojang.serialization.MapCodec
import com.tynicraft.experiosa.WorkflowSystem
import com.tynicraft.experiosa.block.entity.WorkflowTankBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class WorkflowTankBlock : BlockWithEntity(
    FabricBlockSettings.copyOf(Blocks.GLASS)
        .nonOpaque()
        .solidBlock(Blocks::never)
        .suffocates(Blocks::never)
        .blockVision(Blocks::never)
) {
    companion object {
        private val SHAPE: VoxelShape = VoxelShapes.union(
            createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0),  // Main body
            createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),   // Bottom
            createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0)  // Top
        )
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape = SHAPE

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return WorkflowTankBlockEntity(pos, state)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("BlockRenderType.MODEL"))
    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    @Deprecated("Deprecated in Java")
    override fun getCodec(): MapCodec<out BlockWithEntity> {
        return createCodec { WorkflowTankBlock() }
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (!world.isClient && player.isSneaking) {
            val blockEntity = world.getBlockEntity(pos) as? WorkflowTankBlockEntity ?: return ActionResult.FAIL
            val playerWorkflow = WorkflowSystem.getPlayerWorkflow(player)

            if (playerWorkflow > 0) {
                val transferAmount = minOf(playerWorkflow, 10.0) // Transfer up to 10 workflow points at a time
                val transferred = blockEntity.addWorkflow(transferAmount)
                if (transferred > 0) {
                    WorkflowSystem.removePlayerWorkflow(player, transferred)
                    spawnTransferParticles(world as ServerWorld, player.pos, pos)
                    player.sendMessage(net.minecraft.text.Text.literal("Transferred $transferred workflow to tank"), true)
                    return ActionResult.SUCCESS
                }
            }
        }
        return ActionResult.PASS
    }

    private fun spawnTransferParticles(world: ServerWorld, playerPos: Vec3d, tankPos: BlockPos) {
        val tankCenter = Vec3d(tankPos.x + 0.5, tankPos.y + 0.5, tankPos.z + 0.5)
        val direction = tankCenter.subtract(playerPos).normalize()

        for (i in 0 until 20) {
            val xOffset = world.random.nextDouble() * 0.4 - 0.2
            val yOffset = world.random.nextDouble() * 0.4 - 0.2
            val zOffset = world.random.nextDouble() * 0.4 - 0.2

            val particlePos = playerPos.add(xOffset, yOffset + 1, zOffset)
            val particleVelocity = direction.multiply(0.5)

            world.spawnParticles(
                ParticleTypes.END_ROD,  // Placeholder particle, replace with your custom particle when available
                particlePos.x, particlePos.y, particlePos.z,
                1, // count
                particleVelocity.x, particleVelocity.y, particleVelocity.z,
                0.1 // speed
            )
        }
    }
}