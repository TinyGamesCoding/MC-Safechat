package me.tinygames.robloxsafechat.config;

import me.tinygames.robloxsafechat.RobloxSafechat;
import me.tinygames.robloxsafechat.element.ButtonElement;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public final class SettingsGroupGroup extends SettingsGroup {
	public static final SettingsGroupGroup INSTANCE = new SettingsGroupGroup();

	private SettingsGroupGroup() {
		super("Group Settings");
	}

	@Override
	protected Runnable makeAction() {
		return () -> {};
	}

	@Override
	public void init() {
		list.clear();

		add(new ButtonElement("", () -> {
			RobloxSafechat.GROUPS_ARE_ALSO_TEXTS = !RobloxSafechat.GROUPS_ARE_ALSO_TEXTS;
		}, button -> {
			String msg;
			String tooltip;

			if(RobloxSafechat.GROUPS_ARE_ALSO_TEXTS) {
				msg = "Groups are also texts: ON";
				tooltip = msg + "\n\nClicking a group will send it as a chat message.";
			} else {
				msg = "Groups are also texts: OFF";
				tooltip = msg + "\n\nClicking a group won't do anything.";
			}

			button.setMessage(Component.literal(msg));
			button.setTooltip(Tooltip.create(Component.literal(tooltip)));
		}));

		add(new ButtonElement("", () -> {
			RobloxSafechat.SHOW_ARROWS_NEXT_TO_GROUPS = !RobloxSafechat.SHOW_ARROWS_NEXT_TO_GROUPS;
		}, button -> {
			String msg;
			String tooltip;

			if(RobloxSafechat.SHOW_ARROWS_NEXT_TO_GROUPS) {
				msg = "Show arrows next to groups: ON";
				tooltip = msg + "\n\nEach group's name will have an arrow to the left side.";
			} else {
				msg = "Show arrows next to groups: OFF";
				tooltip = msg + "\n\nEach group's names appear as they are read.";
			}

			button.setMessage(Component.literal(msg));
			button.setTooltip(Tooltip.create(Component.literal(tooltip)));
		}));

		add(new ButtonElement("", () -> {
			RobloxSafechat.FLIP_GROUPS = !RobloxSafechat.FLIP_GROUPS;
		}, button -> {
			String msg;
			String tooltip;

			if(RobloxSafechat.FLIP_GROUPS) {
				msg = "Flip groups: ON";
				tooltip = msg + "\n\nGroups will render in the opposite order they are read from the config. " +
						"(Top of screen -> Bottom of screen)";
			} else {
				msg = "Flip groups: OFF";
				tooltip = msg + "\n\nGroups will render in the order they are read from the config. " +
						"(Bottom of screen -> Top of screen)";
			}

			button.setMessage(Component.literal(msg));
			button.setTooltip(Tooltip.create(Component.literal(tooltip)));
		}));
	}
}
