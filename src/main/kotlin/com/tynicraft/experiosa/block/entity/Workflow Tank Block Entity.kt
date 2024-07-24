package com.tynicraft.experiosa.block.entity

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import com.tynicraft.experiosa.Experiosa

class WorkflowTankBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(Experiosa.WORKFLOW_TANK_BLOCK_ENTITY, pos, state) {
    var storedWorkflow: Double = 0.0
        private set

    fun addWorkflow(amount: Double) {
        storedWorkflow += amount
    }

    fun removeWorkflow(amount: Double): Double {
        val removed = minOf(amount, storedWorkflow)
        storedWorkflow -= removed
        return removed
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        nbt.putDouble("stored_workflow", storedWorkflow)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        storedWorkflow = nbt.getDouble("stored_workflow")
    }
}