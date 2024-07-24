package com.tynicraft.experiosa

import net.fabricmc.api.ModInitializer
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import com.tynicraft.experiosa.block.WorkflowTankBlock
import com.tynicraft.experiosa.block.entity.WorkflowTankBlockEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item

class Experiosa : ModInitializer {
    companion object {
        const val MOD_ID = "experiosa"
        private val LOGGER = LoggerFactory.getLogger(MOD_ID)

        val WORKFLOW_TANK_BLOCK = WorkflowTankBlock()
        lateinit var WORKFLOW_TANK_BLOCK_ENTITY: BlockEntityType<WorkflowTankBlockEntity>
    }

    override fun onInitialize() {
        LOGGER.info("Initializing Experiosa mod")

        // Register the Workflow Tank Block
        Registry.register(Registries.BLOCK, Identifier(MOD_ID, "workflow_tank"), WORKFLOW_TANK_BLOCK)

        // Register the Workflow Tank Item
        Registry.register(
            Registries.ITEM,
            Identifier(MOD_ID, "workflow_tank"),
            BlockItem(WORKFLOW_TANK_BLOCK, Item.Settings())
        )

        // Register the Workflow Tank Block Entity
        WORKFLOW_TANK_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier(MOD_ID, "workflow_tank"),
            BlockEntityType.Builder.create(::WorkflowTankBlockEntity, WORKFLOW_TANK_BLOCK).build(null)
        )

        // Initialize WorkflowSystem
        WorkflowSystem.init()

        LOGGER.info("Experiosa mod initialization complete")
    }
}