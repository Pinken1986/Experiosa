package com.tynicraft.experiosa.block.entity

import com.tynicraft.experiosa.Experiosa
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos

class WorkflowTankBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(Experiosa.WORKFLOW_TANK_BLOCK_ENTITY, pos, state) {
    private var storedWorkflow: Double = 0.0

    companion object {
        const val MAX_CAPACITY = 1000.0
    }

    fun addWorkflow(amount: Double): Double {
        val addedAmount = minOf(amount, MAX_CAPACITY - storedWorkflow)
        storedWorkflow += addedAmount
        markDirty()
        return addedAmount
    }
    @Suppress("unused")
    fun getStoredWorkflow(): Double {
        return storedWorkflow
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        nbt.putDouble("stored_workflow", storedWorkflow)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        storedWorkflow = nbt.getDouble("stored_workflow")
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound {
        return createNbt()
    }
}