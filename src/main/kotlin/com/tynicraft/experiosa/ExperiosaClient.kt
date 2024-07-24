package com.tynicraft.experiosa

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer

class ExperiosaClient : ClientModInitializer {
    override fun onInitializeClient() {
        // Set the render layer for the Workflow Tank Block to CUTOUT
        BlockRenderLayerMap.INSTANCE.putBlock(Experiosa.WORKFLOW_TANK_BLOCK, RenderLayer.getCutout())
    }
}