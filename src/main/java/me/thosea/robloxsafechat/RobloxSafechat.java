package me.thosea.robloxsafechat;

import me.thosea.robloxsafechat.config.DefaultChats;
import me.thosea.robloxsafechat.config.loader.ConfigLoader;
import me.thosea.robloxsafechat.element.ChatElement;
import me.thosea.robloxsafechat.element.GroupElement;
import me.thosea.robloxsafechat.mixin.IdentifierAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RobloxSafechat implements ClientModInitializer {
	public static final String MOD_ID = "robloxsafechat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ResourceLocation ICON_KEY = IdentifierAccessor.safechat$of(MOD_ID, "chaticon/default");
	public static final ResourceLocation HOVERED_KEY = IdentifierAccessor.safechat$of(MOD_ID, "chaticon/hover");
	public static final ResourceLocation SELECTED_KEY = IdentifierAccessor.safechat$of(MOD_ID, "chaticon/selected");

	public static final ResourceLocation SETTINGS_ICON_KEY =
			IdentifierAccessor.safechat$of(MOD_ID, "settingsicon/default");
	public static final ResourceLocation SETTINGS_HOVERED_KEY =
			IdentifierAccessor.safechat$of(MOD_ID, "settingsicon/hover");
	public static final ResourceLocation SETTINGS_SELECTED_KEY =
			IdentifierAccessor.safechat$of(MOD_ID, "settingsicon/selected");

	public static GroupElement ROOT = DefaultChats.ROOT;

	public static int renderY;

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
