package com.tynicraft.experiosa.block

import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import com.tynicraft.experiosa.block.entity.WorkflowTankBlockEntity
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import com.mojang.serialization.MapCodec
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.BlockRenderType
import net.minecraft.block.Blocks

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

    @Deprecated("Overrides deprecated member in BlockWithEntity")
    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    @Deprecated("Overrides deprecated member in BlockWithEntity")
    override fun getCodec(): MapCodec<out BlockWithEntity> {
        return createCodec { WorkflowTankBlock() }
    }
}