package me.thosea.robloxsafechat.element;

import com.google.gson.JsonElement;
import me.thosea.robloxsafechat.button.SCButton;
import me.thosea.robloxsafechat.config.loader.ConfigLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ButtonElement implements SafechatElement {
	protected final SCButton button;
	protected final String name;
	protected GroupElement parent;

	public ButtonElement(String name, Runnable action) {
		this(name, action, null);
	}

	public ButtonElement(Runnable action, Consumer<Button> configUpdate) {
		this("", action, configUpdate);
	}

	public ButtonElement(String name, Runnable action, Consumer<Button> configUpdate) {
		this.name = name;

		Runnable onPress = configUpdate == null ? action : () -> {
			action.run();
			configUpdate.accept(getButton().getButton());
			ConfigLoader.writeConfig();
		};

		this.button = new SCButton(Component.literal(name), false, onPress);

		if(configUpdate != null)
			configUpdate.accept(button.getButton());
	}

	@Override
	public boolean mouseClicked(GuiGraphics graphics, int mouseX, int mouseY, int clickType) {
		return button.mouseClicked(mouseX, mouseY, clickType);
	}

	@Override
	public boolean shouldShow(GuiGraphics graphics, int mouseX, int mouseY) {
		return parent.show && button.isHovered(graphics, mouseX, mouseY);
	}

	@Override
	public void setParent(GroupElement parent) {
		this.parent = parent;
	}

	@Override
	public JsonElement serialize() {
		return null;
	}

	@Override
	public SCButton getButton() {
		return button;
	}
}
