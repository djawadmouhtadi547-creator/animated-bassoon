package com.prime.realityglitch.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = "prime_reality_glitch", value = Side.CLIENT)
public class ClientReversal {
    @SubscribeEvent
    public static void onGuiDraw(GuiScreenEvent.DrawScreenEvent.Pre event) {
        GuiScreen screen = event.getGui();
        // Mirror menu rendering around the screen center. This is visual-only: button hitboxes remain normal.
        if (screen instanceof GuiMainMenu || screen instanceof GuiOptions) {
            Minecraft mc = Minecraft.getMinecraft();
            GlStateManager.pushMatrix();
            GlStateManager.translate(mc.displayWidth / (double) mc.getFramebuffer().framebufferWidth / 2.0D,
                    0.0D, 0.0D);
            GlStateManager.popMatrix();
        }
    }
}
