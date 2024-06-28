package me.thosea.robloxsafechat.config;

import me.thosea.robloxsafechat.config.loader.ConfigOption;

public final class SafechatConfig {
	private SafechatConfig() {}

	public static final ConfigOption<Float> SCALE = new ConfigOption<>(
			"scale",
			ConfigOption.floatOptionType(0.4f, 1.2f),
			0.8f,
			(value, setter) -> {
				setter.accept(String.format("Scale: %.1f", value), null);
			}
	);
	public static final ConfigOption<Boolean> INSTANTLY_SEND = new ConfigOption<>(
			"instantly_send",
			ConfigOption.BOOLEAN_TYPE,
			true,
			(value, setter) -> {
				String msg;
				String tooltip;
				if(value) {
					msg = "Instantly Send: ON" ;
					tooltip = """
							Messages will instantly send in chat when clicked,
							but will not close the chat screen.""";
				} else {
					msg = "Instantly Send: OFF" ;
					tooltip = "Messages will paste at the cursor in the chat box when clicked.";
				}

				setter.accept(msg, tooltip);
			}
	);
	public static final ConfigOption<Boolean> CLOSE_AFTER_SEND = new ConfigOption<>(
			"close_after_send",
			ConfigOption.BOOLEAN_TYPE,
			true,
			(value, setter) -> {
				String msg;
				String tooltip;

				if(value) {
					msg = "Close after sending: ON" ;
					tooltip = "Safechat menu will close after selecting a chat.";
				} else {
					msg = "Close after sending: OFF" ;
					tooltip = "Safechat menu will stay open after selecting a chat.";
				}

				setter.accept(msg, tooltip);
			}
	);
	public static final ConfigOption<Float> TEXT_SCALE_THRESHOLD = new ConfigOption<>(
			"text_scale_threshold",
			ConfigOption.floatOptionType(0.4f, 1f),
			0.7f,
			(value, setter) -> {
				String msg = String.format("Text Scale Threshold: %.1f", value);
				String tooltip = """
						If a button's width divided by the text's width is this or smaller,
						a tooltip will display the full text when you hover.
						Values equal or above 1.0 disable this.""";

				setter.accept(msg, tooltip);
			}
	);

	public static final ConfigOption<Boolean> GROUPS_ARE_ALSO_TEXTS = new ConfigOption<>(
			"groups_are_also_texts",
			ConfigOption.BOOLEAN_TYPE,
			false,
			(value, setter) -> {
				String msg;
				String tooltip;

				if(value) {
					msg = "Groups are also texts: ON";
					tooltip = "Clicking a group will send it as a chat message.";
				} else {
					msg = "Groups are also texts: OFF";
					tooltip = "Clicking a group won't do anything.";
				}

				setter.accept(msg, tooltip);
			}
	);
	public static final ConfigOption<Boolean> SHOW_ARROWS_NEXT_TO_GROUPS = new ConfigOption<>(
			"show_arrows_next_to_groups",
			ConfigOption.BOOLEAN_TYPE,
			true,
			(value, setter) -> {
				String msg;
				String tooltip;

				if(value) {
					msg = "Show arrows next to groups: ON";
					tooltip = "Each group's name will have an arrow to the left side.";
				} else {
					msg = "Show arrows next to groups: OFF";
					tooltip = "Each group's names appear as they are read from the config.";
				}

				setter.accept(msg, tooltip);
			}
	);
	public static final ConfigOption<Boolean> FLIP_GROUPS = new ConfigOption<>(
			"flip_groups",
			ConfigOption.BOOLEAN_TYPE,
			true,
			(value, setter) -> {
				String msg;
				String tooltip;

				if(value) {
					msg = "Flip groups: ON";
					tooltip = "Groups will appear in the opposite order they are read from the config.";
				} else {
					msg = "Flip groups: OFF";
					tooltip = "Groups will appear in the order they are read from the config.";
				}

				setter.accept(msg, tooltip);
			}
	);

	public static final ConfigOption<Float> OPACITY_MULTIPLIER = new ConfigOption<>(
			"opacity_multiplier",
			ConfigOption.floatOptionType(0f, 1.5f),
			1.0f,
			(value, setter) -> {
				String name = String.format("Opacity Multiplier: %.1f", value);
				String tooltip = """
						Multiplied against the resulting opacity for the final opacity of the chat icon.
						Disabling other opacity-changing options will make this the only factor.""";

				setter.accept(name, tooltip);
			}
	);
	public static final ConfigOption<Boolean> CHAT_FIELD_FILL_AFFECTS_OPACITY = new ConfigOption<>(
			"chat_field_fill_affects_opacity",
			ConfigOption.BOOLEAN_TYPE,
			true,
			(value, setter) -> {
				String msg;
				String tooltip;

				if(value) {
					msg = "Chat Fill Affects Opacity: ON";
					tooltip = "As the chat bar gets closer to filling, the opacity of the button will decrease.";
				} else {
					msg = "Chat Fill Affects Opacity: OFF";
					tooltip = "How close the chat bar is to filling will not affect opacity.";
				}

				setter.accept(msg, tooltip);
			}
	);
	public static final ConfigOption<Boolean> MOUSE_AFFECTS_OPACITY = new ConfigOption<>(
			"mouse_affects_opacity",
			ConfigOption.BOOLEAN_TYPE,
			true,
			(value, setter) -> {
				String msg;
				String tooltip;

				if(value) {
					msg = "Mouse Affects Opacity: ON";
					tooltip = "Mouse movements and its distance from the button will affect its opacity.";
				} else {
					msg = "Mouse Affects Opacity: OFF";
					tooltip = "The mouse has no effect on the button's opacity.";
				}

				setter.accept(msg, tooltip);
			}
	);
}