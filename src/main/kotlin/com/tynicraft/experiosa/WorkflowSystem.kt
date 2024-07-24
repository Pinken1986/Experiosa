package com.tynicraft.experiosa

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.Block
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.registry.tag.BlockTags
import net.minecraft.util.ActionResult
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object WorkflowSystem {
    private val playerWorkflows = ConcurrentHashMap<UUID, Double>()
    private val playerPlacedBlocks = ConcurrentHashMap<BlockPos, UUID>()

    fun init() {
        registerEvents()
    }

    private fun registerEvents() {
        PlayerBlockBreakEvents.AFTER.register { world, player, pos, state, _ ->
            if (player is ServerPlayerEntity && !isPlayerPlacedBlock(pos, player.uuid)) {
                addWorkflow(player, calculateWorkflowForBlock(state.block))
            }
        }

        UseBlockCallback.EVENT.register { player, world, _, hitResult ->
            if (player is ServerPlayerEntity && !world.isClient) {
                val pos = hitResult.blockPos.offset(hitResult.side)
                if (player.mainHandStack.item is net.minecraft.item.BlockItem) {
                    playerPlacedBlocks[pos] = player.uuid
                }
            }
            ActionResult.PASS
        }
    }

    private fun isPlayerPlacedBlock(pos: BlockPos, playerUuid: UUID): Boolean {
        return playerPlacedBlocks[pos] == playerUuid
    }

    private fun calculateWorkflowForBlock(block: Block): Double {
        return when {
            block.defaultState.isIn(BlockTags.LOGS) -> 2.0
            block.defaultState.isIn(BlockTags.CROPS) -> 1.5
            else -> 1.0
        }
    }

    private fun addWorkflow(player: ServerPlayerEntity, amount: Double) {
        val currentWorkflow = getWorkflow(player)
        setWorkflow(player, currentWorkflow + amount)
    }

    private fun getWorkflow(player: ServerPlayerEntity): Double {
        return playerWorkflows.getOrDefault(player.uuid, 0.0)
    }

    private fun setWorkflow(player: ServerPlayerEntity, amount: Double) {
        playerWorkflows[player.uuid] = amount
        player.sendMessage(net.minecraft.text.Text.literal("Workflow: $amount"), true)
    }
}