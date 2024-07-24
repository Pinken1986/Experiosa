package com.tynicraft.experiosa

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object WorkflowSystem {
    private val playerPlacedBlocks = ConcurrentHashMap<BlockPos, UUID>()
    private val playerWorkflow = ConcurrentHashMap<UUID, Double>()
    const val MAX_PLAYER_WORKFLOW = 100.0

    fun init() {
        registerEvents()
    }

    private fun registerEvents() {
        PlayerBlockBreakEvents.AFTER.register { _, player, pos, state, _ ->
            if (player is ServerPlayerEntity && !isPlayerPlacedBlock(pos, player.uuid)) {
                val workflowAmount = calculateWorkflowForBlock(state.block)
                addWorkflowToPlayer(player, workflowAmount)
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

    private fun addWorkflowToPlayer(player: ServerPlayerEntity, amount: Double) {
        val currentWorkflow = playerWorkflow.getOrDefault(player.uuid, 0.0)
        val newWorkflow = minOf(currentWorkflow + amount, MAX_PLAYER_WORKFLOW)
        playerWorkflow[player.uuid] = newWorkflow
        val stored = newWorkflow - currentWorkflow
        val lost = amount - stored

        player.sendMessage(net.minecraft.text.Text.literal("Stored $stored workflow. Current: $newWorkflow/$MAX_PLAYER_WORKFLOW"), true)
        if (lost > 0) {
            player.sendMessage(net.minecraft.text.Text.literal("$lost workflow was lost due to reaching capacity"), true)
        }
    }

    fun getPlayerWorkflow(player: PlayerEntity): Double {
        return playerWorkflow.getOrDefault(player.uuid, 0.0)
    }

    fun removePlayerWorkflow(player: PlayerEntity, amount: Double): Double {
        val currentWorkflow = playerWorkflow.getOrDefault(player.uuid, 0.0)
        val removedAmount = minOf(currentWorkflow, amount)
        playerWorkflow[player.uuid] = currentWorkflow - removedAmount
        return removedAmount
    }
}