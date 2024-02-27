package me.tinygames.robloxsafechat;

import me.tinygames.robloxsafechat.button.SCButton;
import me.tinygames.robloxsafechat.config.ConfigLoader;
import me.tinygames.robloxsafechat.config.DefaultChats;
import me.tinygames.robloxsafechat.element.ChatElement;
import me.tinygames.robloxsafechat.element.GroupElement;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RobloxSafechat implements ClientModInitializer {
	public static final String MOD_ID = "robloxsafechat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ResourceLocation ICON_KEY = new ResourceLocation(MOD_ID, "chaticon/default");
	public static final ResourceLocation HOVERED_KEY = new ResourceLocation(MOD_ID, "chaticon/hover");
	public static final ResourceLocation SELECTED_KEY = new ResourceLocation(MOD_ID, "chaticon/selected");

	public static final ResourceLocation SETTINGS_ICON_KEY =
			new ResourceLocation(MOD_ID, "settingsicon/default");
	public static final ResourceLocation SETTINGS_HOVERED_KEY =
			new ResourceLocation(MOD_ID, "settingsicon/hover");
	public static final ResourceLocation SETTINGS_SELECTED_KEY =
			new ResourceLocation(MOD_ID, "settingsicon/selected");

	private static float SCALE;
	public static boolean INSTANTLY_SEND;
	public static boolean GROUPS_ARE_ALSO_TEXTS;
	public static boolean SHOW_ARROWS_NEXT_TO_GROUPS;
	public static boolean FLIP_GROUPS;
	public static boolean CLOSE_AFTER_SEND;
	public static float MIN_TEXT_SCALE;
	public static GroupElement ROOT = DefaultChats.ROOT;

	public static int renderY;

	public static float getScale() {
		return SCALE;
	}

	public static void setScale(float scale) {
		SCALE = scale;
		SCButton.WIDTH = (int) (SCButton.BASE_WIDTH * scale);
		SCButton.HEIGHT = (int) (SCButton.BASE_HEIGHT * scale);
	}

	public static void setScale(double scale) {
		setScale((float) scale);
	}

	@Override
	public void onInitializeClient() {
		ConfigLoader.reload();
	}

	public static final class Builderman { // BUILDERMAN!??!!?!?!?1/1/1!?!?/1//!/?!/1/
		private final GroupElement result = new GroupElement(null);
		private GroupElement current = result;

		public Builderman text(String text) {
			current.add(new ChatElement(text));
			return this;
		}

		public Builderman group(String name) {
			GroupElement group = new GroupElement(name);
			current.add(group);
			current.sort();
			current = group;
			return this;
		}

		public Builderman goBack() {
			if(current.parent == null) {
				throw new IllegalStateException("No parent? Probably went to get the milk.");
			}

			current = current.parent;
			return this;
		}

		public GroupElement build() {
			return result;
		}
	}
}
