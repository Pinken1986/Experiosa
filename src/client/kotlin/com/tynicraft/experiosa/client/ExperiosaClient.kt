package com.tynicraft.experiosa.client

import com.tynicraft.experiosa.Experiosa
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.render.RenderLayer

class ExperiosaClient : ClientModInitializer {
    override fun onInitializeClient() {
        // Set the render layer for the Workflow Tank Block to CUTOUT
        BlockRenderLayerMap.INSTANCE.putBlock(Experiosa.WORKFLOW_TANK_BLOCK, RenderLayer.getCutout())

        // Register the HUD rendering callback
        HudRenderCallback.EVENT.register { drawContext, _ ->
            WorkflowHudOverlay.render(drawContext)
        }
    }
}