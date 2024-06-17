package me.thosea.robloxsafechat.button;

import com.mojang.blaze3d.vertex.PoseStack;
import me.thosea.robloxsafechat.RobloxSafechat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

final class SCButtonHandle extends Button {
	boolean isSettingsButton = false;
	boolean skipRender = false;

	private float scale;
	private int renderX;
	private int renderY;

	SCButtonHandle(Component text, Runnable onPress) {
		super(0, 0, // x/y set at render
				1, 1,  // width/height set at render
				text,
				ignored -> onPress.run(),
				Supplier::get); // narration
	}

	@Override
	public void setMessage(Component text) {
		super.setMessage(text);
		calculatePosAndScale();
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
		if(isSettingsButton) {
			super.playDownSound(soundManager);
		}
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
		if(skipRender) {
			skipRender = false;
		} else {
			super.renderWidget(guiGraphics, i, j, f);
		}
	}

	void calculatePosAndScale() {
		int textWidth = Minecraft.getInstance().font.width(getMessage());

		if(textWidth > SCButton.BASE_WIDTH) {
			scale = ((float) (SCButton.BASE_WIDTH - 4) / (float) textWidth);

			if(!isSettingsButton) {
				if(scale <= RobloxSafechat.MIN_TEXT_SCALE) {
					setTooltip(Tooltip.create(getMessage()));
				} else {
					setTooltip(null);
				}
			}

			scale *= RobloxSafechat.getScale();
		} else {
			scale = RobloxSafechat.getScale();
		}

		int scaledWidth = Mth.ceil(textWidth * scale);
		this.renderX = (int) (((getX() + getWidth() / 2) - scaledWidth / 2) / scale) + 1;

		// for some reason scales slower than the global scale
		// cause a slight offset in height (it goes UP)
		int yOff = 5; // minimum of 5 for all scales

		if(scale < RobloxSafechat.getScale()) {
			// increase if needed
			yOff = (int) ((float) yOff * (RobloxSafechat.getScale() / scale) * 1.5);
		}

		this.renderY = (int) (getY() / scale) + yOff;
	}

	@Override
	protected void renderScrollingString(GuiGraphics graphics, Font font, int i, int color) {
		PoseStack pose = graphics.pose();

		pose.pushPose();
		pose.scale(scale, scale, 1);
		graphics.drawString(font, getMessage(), renderX, renderY, color);
		pose.popPose();
	}
}
