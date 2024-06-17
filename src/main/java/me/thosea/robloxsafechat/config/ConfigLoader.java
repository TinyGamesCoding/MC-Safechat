package me.thosea.robloxsafechat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import me.thosea.robloxsafechat.RobloxSafechat;
import me.thosea.robloxsafechat.element.GroupElement;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.function.Consumer;

import static me.thosea.robloxsafechat.RobloxSafechat.LOGGER;

public final class ConfigLoader {
	private ConfigLoader() {}

	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();

	private static boolean configError;
	private static boolean messageError;

	public static void reload() {
		if(ConfigFiles.CONFIG_FILE.exists()) {
			loadConfig();
		} else {
			configError = false;
			setDefaultConfig();
			writeConfig();
		}

		if(ConfigFiles.MESSAGES_FILE.exists()) {
			loadMessages();
		} else {
			messageError = false;
			RobloxSafechat.ROOT = DefaultChats.ROOT;
			writeFile(ConfigFiles.MESSAGES_FILE, "messages", DefaultChats.ROOT.serialize());
		}

		sendErrorMessagesInChat();
	}

	private static void loadConfig() {
		try(FileReader reader = new FileReader(ConfigFiles.CONFIG_FILE)) {
			JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();

			readObj(obj, "scale", prim -> RobloxSafechat.setScale(prim.getAsFloat()));
			readObj(obj, "groups_are_also_texts", prim -> {
				RobloxSafechat.GROUPS_ARE_ALSO_TEXTS = prim.getAsBoolean();
			});
			readObj(obj, "show_arrows_next_to_groups", prim -> {
				RobloxSafechat.SHOW_ARROWS_NEXT_TO_GROUPS = prim.getAsBoolean();
			});
			readObj(obj, "instantly_send", prim -> {
				RobloxSafechat.INSTANTLY_SEND = prim.getAsBoolean();
			});
			readObj(obj, "flip_groups", prim -> {
				RobloxSafechat.FLIP_GROUPS = prim.getAsBoolean();
			});
			readObj(obj, "close_after_send", prim -> {
				RobloxSafechat.CLOSE_AFTER_SEND = prim.getAsBoolean();
			});
			readObj(obj, "close_after_send", prim -> {
				RobloxSafechat.CLOSE_AFTER_SEND = prim.getAsBoolean();
			});
			readObj(obj, "min_text_scale_for_scrolling", prim -> {
				RobloxSafechat.MIN_TEXT_SCALE = prim.getAsFloat();
			});

			configError = false;
		} catch(Exception e) {
			LOGGER.error("Failed to read config file from " + ConfigFiles.CONFIG_FILE, e);
			configError = true;
			setDefaultConfig();
		}
	}

	private static void setDefaultConfig() {
		RobloxSafechat.setScale(0.8f);
		RobloxSafechat.GROUPS_ARE_ALSO_TEXTS = false;
		RobloxSafechat.SHOW_ARROWS_NEXT_TO_GROUPS = true;
		RobloxSafechat.INSTANTLY_SEND = true;
		RobloxSafechat.CLOSE_AFTER_SEND = true;
		RobloxSafechat.FLIP_GROUPS = true;
		RobloxSafechat.MIN_TEXT_SCALE = 0.7f;
	}

	private static void loadMessages() {
		try(FileReader reader = new FileReader(ConfigFiles.MESSAGES_FILE)) {
			JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
			RobloxSafechat.ROOT = GroupElement.deserializeRoot(object);
			messageError = false;
		} catch(Exception e) {
			LOGGER.error("Failed to read messages files from " + ConfigFiles.MESSAGES_FILE);
			LOGGER.error("Default messages will be used instead.", e);
			RobloxSafechat.ROOT = DefaultChats.ROOT;
			messageError = true;
		}
	}

	private static void readObj(JsonObject obj, String name,
	                            Consumer<JsonPrimitive> handler) {
		JsonElement element = obj.get(name);

		if(element == null) {
			throw new IllegalArgumentException("Config option not found: \"" + name + "\"");
		} else if(!(element instanceof JsonPrimitive primitive)) {
			throw new IllegalArgumentException("Config option is not a primitive: \"" + name + "\"");
		} else {
			try {
				handler.accept(primitive);
			} catch(Exception e) {
				throw new IllegalArgumentException("Config option is invalid: \"" + name + "\"", e);
			}
		}
	}

	private static long lastConfigTime = -1;

	public static void writeConfig() {
		if(configError) {
			ChatComponent chat = getChatHud();
			if(chat == null) return;

			if(System.currentTimeMillis() - lastConfigTime > 1000L) {
				lastConfigTime = System.currentTimeMillis();
				chat.addMessage(Component.literal(
								"[RobloxSafechat] Are you sure you want to overwrite your invalid config?" +
										" Click again to confirm.")
						.withStyle(ChatFormatting.RED));
				return;
			} else {
				lastConfigTime = -1;
				configError = false;
				chat.addMessage(Component.literal("[RobloxSafechat] Your invalid config was overwritten.")
						.withStyle(ChatFormatting.GOLD));
			}
		}

		writeFile(ConfigFiles.CONFIG_FILE, "config", makeConfigJson());
	}

	private static JsonObject makeConfigJson() {
		JsonObject config = new JsonObject();
		config.addProperty("scale", RobloxSafechat.getScale());
		config.addProperty("groups_are_also_texts", RobloxSafechat.GROUPS_ARE_ALSO_TEXTS);
		config.addProperty("show_arrows_next_to_groups", RobloxSafechat.SHOW_ARROWS_NEXT_TO_GROUPS);
		config.addProperty("instantly_send", RobloxSafechat.INSTANTLY_SEND);
		config.addProperty("flip_groups", RobloxSafechat.FLIP_GROUPS);
		config.addProperty("close_after_send", RobloxSafechat.CLOSE_AFTER_SEND);
		config.addProperty("min_text_scale_for_scrolling", RobloxSafechat.MIN_TEXT_SCALE);
		return config;
	}

	private static void writeFile(File file, String name, JsonObject object) {
		File dir = ConfigFiles.CONFIG_FOLDER;
		try {
			if(!dir.exists() && !dir.mkdirs()) {
				throw new Exception("An unknown error occurred");
			}
		} catch(Exception e) {
			LOGGER.error("Failed to make config directory at {}", dir);
			return;
		}

		try {
			if(!file.exists() && !file.createNewFile())
				throw new Exception("An unknown error occurred");
		} catch(Exception e) {
			LOGGER.error("Failed to create " + name + " file at " + file, e);
			return;
		}

		try(FileOutputStream stream = new FileOutputStream(file)) {
			try(JsonWriter writer = new JsonWriter(new OutputStreamWriter(stream))) {
				writer.setIndent("\t");
				writer.jsonValue(GSON.toJson(object));
			}
		} catch(Exception e) {
			LOGGER.error("Failed to write " + name + " file at " + file, e);
		}
	}

	public static void sendErrorMessagesInChat() {
		if(!configError && !messageError) return;

		ChatComponent chat = getChatHud();
		if(chat == null) return;

		Component logText = Component.literal("read the log for details.").withStyle(style -> {
			Path path = FabricLoader.getInstance().getGameDir().resolve("logs/latest.log");

			return style
					.withUnderlined(true)
					.withColor(ChatFormatting.GREEN)
					.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							Component.literal("Click to open the log file.")
									.withStyle(ChatFormatting.GREEN)))
					.withClickEvent(new ClickEvent(Action.OPEN_FILE, path.toString()));
		});

		if(configError) {
			chat.addMessage(Component
					.literal("RobloxSafechat failed to read the config file, and is currently using the default settings, ")
					.append(logText));
		}

		if(messageError) {
			chat.addMessage(Component
					.literal("RobloxSafechat failed to read the messages file, and is using default messages, ")
					.append(logText));
		}
	}

	private static ChatComponent getChatHud() {
		try {
			return Minecraft.getInstance().gui.getChat();
		} catch(NullPointerException e) {
			return null;
		}
	}
}
