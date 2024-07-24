package com.tynicraft.experiosa.client

import com.mojang.blaze3d.systems.RenderSystem
import com.tynicraft.experiosa.Experiosa
import com.tynicraft.experiosa.WorkflowSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.GameRenderer
import net.minecraft.util.Identifier

object WorkflowHudOverlay {
    private val WORKFLOW_ICON = Identifier(Experiosa.MOD_ID, "textures/gui/workflow_icon.png")

    fun render(context: DrawContext) {
        val client = MinecraftClient.getInstance()
        val player = client.player ?: return

        val x = 10
        val y = 10
        val workflow = WorkflowSystem.getPlayerWorkflow(player)

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, WORKFLOW_ICON)

        context.drawTexture(WORKFLOW_ICON, x, y, 0f, 0f, 16, 16, 16, 16)

        RenderSystem.disableBlend()

        val text = String.format("%.1f / %.1f", workflow, WorkflowSystem.MAX_PLAYER_WORKFLOW)
        context.drawTextWithShadow(client.textRenderer, text, x + 20, y + 4, 0xFFFFFF)
    }
}