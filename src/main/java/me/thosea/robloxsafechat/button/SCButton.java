package me.thosea.robloxsafechat.button;

import me.thosea.robloxsafechat.config.SafechatConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class SCButton {
	public static final int BASE_WIDTH = 100;
	public static final int BASE_HEIGHT = 20;

	public static int WIDTH = BASE_WIDTH;
	public static int HEIGHT = BASE_HEIGHT;

	static {
		SafechatConfig.SCALE.changeListeners.add(scale -> {
			WIDTH = (int) (SCButton.BASE_WIDTH * scale);
			HEIGHT = (int) (SCButton.BASE_HEIGHT * scale);
		});
	}

	private final SCButtonHandle element;

	private final boolean hasArrow;
	private final Component noArrow;
	private final Component withArrow;

	private float lastScale = Float.MIN_VALUE;
	private boolean lastShowArrow;
	private float lastMinTextScale;

	public SCButton(Component text, boolean hasArrow, Runnable onPress) {
		this.element = new SCButtonHandle(text, onPress);
		this.hasArrow = hasArrow;
		this.noArrow = text;
		this.withArrow = Component.literal("< ").append(noArrow);
	}

	public void render(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
		boolean redoScaleAndPos = false;

		if(hasArrow && lastShowArrow != SafechatConfig.SHOW_ARROWS_NEXT_TO_GROUPS.get()) {
			lastShowArrow = SafechatConfig.SHOW_ARROWS_NEXT_TO_GROUPS.get();
			element.setMessage(lastShowArrow ? withArrow : noArrow);
			redoScaleAndPos = true;
		}

		if(lastScale != SafechatConfig.SCALE.get()) {
			lastScale = SafechatConfig.SCALE.get();
			element.setWidth(WIDTH);
			element.setHeight(HEIGHT);
			redoScaleAndPos = true;
		}

		if(x != element.getX() || y != element.getY()) {
			element.setX(x);
			element.setY(y);
			redoScaleAndPos = true;
		}

		if(lastMinTextScale != SafechatConfig.TEXT_SCALE_THRESHOLD.get()) {
			lastMinTextScale = SafechatConfig.TEXT_SCALE_THRESHOLD.get();
			redoScaleAndPos = true;
		}

		if(redoScaleAndPos) {
			element.calculatePosAndScale();
		}

		element.render(graphics, mouseX, mouseY, 1.0f);
	}

	public boolean mouseClicked(int mouseX, int mouseY, int clickType) {
		return element.mouseClicked(mouseX, mouseY, clickType);
	}

	public boolean isHovered(GuiGraphics graphics, int mouseX, int mouseY) {
		element.skipRender = true;
		element.render(graphics, mouseX, mouseY, 1.0f);
		return element.isHovered();
	}

	public Button getButton() {
		return element;
	}

	public void setText(Component text) {
		element.setMessage(text);
	}

	public Component getText() {
		return element.getMessage();
	}

	public void setIsSettingsButton(boolean isSettingsButton) {
		element.isSettingsButton = isSettingsButton;
	}

}
