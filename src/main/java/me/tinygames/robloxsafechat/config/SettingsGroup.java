package me.tinygames.robloxsafechat.config;

import me.tinygames.robloxsafechat.RobloxSafechat;
import me.tinygames.robloxsafechat.element.ButtonElement;
import me.tinygames.robloxsafechat.element.GroupElement;
import me.tinygames.robloxsafechat.element.SafechatElement;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class SettingsGroup extends GroupElement {
	public static final SettingsGroup INSTANCE = new SettingsGroup();

	private SettingsGroup() {
		super(null);
	}

	SettingsGroup(String name) {
		super(name);
	}

	public void init() {
		list.clear();

		add(new ButtonElement("", makeNumberHandler(
				RobloxSafechat::getScale,
				0.4, 1.2, RobloxSafechat::setScale
		), button -> {
			String msg = String.format("Scale: %.2f", RobloxSafechat.getScale());
			button.setMessage(Component.literal(msg));
			button.setTooltip(Tooltip.create(Component.literal(
					"Loops from 0.4 to 1.2 in 0.1 increments, " +
							"use the config file for other values."
			)));
		}));

		add(new ButtonElement("", () -> {
			RobloxSafechat.INSTANTLY_SEND = !RobloxSafechat.INSTANTLY_SEND;
		}, button -> {
			String msg;
			String tooltip;

			if(RobloxSafechat.INSTANTLY_SEND) {
				msg = "Instantly Send: ON";
				tooltip = msg + "\n\nMessages will instantly send in chat when clicked, " +
						"but will not close the chat screen.";
			} else {
				msg = "Instantly Send: OFF";
				tooltip = msg + "\n\nMessages will paste at the cursor in the chat box when clicked.";
			}

			button.setMessage(Component.literal(msg));
			button.setTooltip(Tooltip.create(Component.literal(tooltip)));
		}));

		add(SettingsGroupGroup.INSTANCE);

		add(new ButtonElement("", () -> {
			RobloxSafechat.CLOSE_AFTER_SEND = !RobloxSafechat.CLOSE_AFTER_SEND;
		}, button -> {
			String msg;
			String tooltip;

			if(RobloxSafechat.CLOSE_AFTER_SEND) {
				msg = "Close after sending: ON";
				tooltip = msg + "\n\nSafechat menu will close after selecting a chat.";
			} else {
				msg = "Close after sending: OFF";
				tooltip = msg + "\n\nSafechat menu will stay open after selecting a chat.";
			}

			button.setMessage(Component.literal(msg));
			button.setTooltip(Tooltip.create(Component.literal(tooltip)));
		}));

		add(new ButtonElement("", makeNumberHandler(
				() -> RobloxSafechat.MIN_TEXT_SCALE,
				0.4, 0.91,
				val -> RobloxSafechat.MIN_TEXT_SCALE = (float) val
		), button -> {
			String msg = String.format("Tooltip Text Scale: %.2f", RobloxSafechat.MIN_TEXT_SCALE);
			button.setMessage(Component.literal(msg));
			button.setTooltip(Tooltip.create(Component.literal(
					"If a button's width divided by the text's width is this or smaller, " +
							"it will display a tooltip displaying the text when you hover." +
							"\n\nLoops from 0.4 to 0.9 in 0.1 increments, " +
							"use the config file for other values " +
							"(values equal or above 1.0 have no effect)."
			)));
		}));

		add(new ButtonElement("Open config folder", () -> {
			Util.getPlatform().openUri(ConfigFiles.CONFIG_FOLDER.toURI());
		}));
		add(new ButtonElement("Open messages file", () -> {
			Util.getPlatform().openUri(ConfigFiles.MESSAGES_FILE.toURI());
		}));
		add(new ButtonElement("Reload messages & config", () -> {
			ConfigLoader.reload();
			init();
		}));

		SettingsGroupGroup.INSTANCE.init();
	}

	protected Runnable makeNumberHandler(DoubleSupplier supplier, double min, double max,
	                                   DoubleConsumer handler) {
		return () -> {
			double value = supplier.getAsDouble() + 0.1;
			if(value > max || value < min) {
				value = min;
			} else {
				value = Math.round(value * 10) / 10.0f; // round to nearest 0.1
			}

			handler.accept(value);
		};
	}

	@Override
	public GroupElement add(SafechatElement element) {
		element.getButton().setIsSettingsButton(true);
		return super.add(element);
	}

	@Override
	protected List<SafechatElement> listView() {
		return list;
	}
}
