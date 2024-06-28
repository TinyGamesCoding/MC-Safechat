package me.thosea.robloxsafechat.config.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import me.thosea.robloxsafechat.RobloxSafechat;
import me.thosea.robloxsafechat.config.ConfigFiles;
import me.thosea.robloxsafechat.config.DefaultChats;
import me.thosea.robloxsafechat.config.loader.updater.ConfigUpdater;
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

import static me.thosea.robloxsafechat.RobloxSafechat.LOGGER;

@SuppressWarnings("StringConcatenationArgumentToLogCall") // exceptions won't print stacktrace
public final class ConfigLoader {
	private ConfigLoader() {}

	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();
	private static final int CONFIG_VERSION = 1;

	private static boolean configError;
	private static boolean messageError;

	public static void reload() {
		if(ConfigFiles.CONFIG_FILE.exists()) {
			try(FileReader reader = new FileReader(ConfigFiles.CONFIG_FILE)) {
				JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
				loadConfig(obj);
			} catch(Exception e) {
				LOGGER.error("[RobloxSafechat] Failed to read config file from " + ConfigFiles.CONFIG_FILE, e);
				ConfigOption.CONFIG_OPTIONS.values().forEach(ConfigOption::reset);
				configError = true;
			}
		} else {
			configError = false;
			ConfigOption.CONFIG_OPTIONS.values().forEach(ConfigOption::reset);
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

	private static void loadConfig(JsonObject obj) {
		int configVer;
		if(obj.has("config_version")) {
			configVer = obj.get("config_version").getAsInt();
		} else {
			configVer = 0;
		}

		if(configVer > CONFIG_VERSION) {
			LOGGER.warn("[RobloxSafechat] Config version {} is above supported version {}", configVer, CONFIG_VERSION);
		} else if(configVer < CONFIG_VERSION) {
			LOGGER.info("[RobloxSafechat] Updating config from version {} to supported version {}", configVer, CONFIG_VERSION);
			if(configVer < 0) {
				throw new IllegalStateException("Config has negative config version " + configVer);
			}

			obj.addProperty("config_version", CONFIG_VERSION);
			for(int i = configVer; i < CONFIG_VERSION; i++) {
				LOGGER.info("[RobloxSafechat] Updating from {} to {}", configVer, configVer + 1);
				ConfigUpdater.UPDATERS[i].transform(obj);
			}

			writeFile(ConfigFiles.CONFIG_FILE, "config", obj);
		}

		LOGGER.info("[RobloxSafechat] Loading config with version {}", CONFIG_VERSION);

		ConfigOption.CONFIG_OPTIONS.forEach((name, option) -> {
			@SuppressWarnings({"UtilityClassWithoutPrivateConstructor", "NonFinalUtilityClass"})
			class Cast {
				static <T> T cast(Object obj) {return (T) obj;}
			}
			option.set(Cast.cast(option.getType().deserialize(obj.get(name))));
		});

		configError = false;
	}

	private static void loadMessages() {
		try(FileReader reader = new FileReader(ConfigFiles.MESSAGES_FILE)) {
			JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
			RobloxSafechat.ROOT = GroupElement.deserializeRoot(object);
			messageError = false;
		} catch(Exception e) {
			LOGGER.error("[RobloxSafechat] Failed to read messages files from " + ConfigFiles.MESSAGES_FILE);
			LOGGER.error("[RobloxSafechat] Default messages will be used instead.", e);
			RobloxSafechat.ROOT = DefaultChats.ROOT;
			messageError = true;
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
		JsonObject obj = new JsonObject();
		obj.addProperty("config_version", CONFIG_VERSION);
		ConfigOption.CONFIG_OPTIONS.forEach((name, option) -> {
			obj.add(name, option.getType().serialize(option.getCast()));
		});
		return obj;
	}

	private static void writeFile(File file, String name, JsonObject object) {
		File dir = ConfigFiles.CONFIG_FOLDER;
		try {
			if(!dir.exists() && !dir.mkdirs()) {
				throw new Exception("An unknown error occurred");
			}
		} catch(Exception e) {
			LOGGER.error("[RobloxSafechat] Failed to make config directory at {}", dir);
			return;
		}

		try {
			if(!file.exists() && !file.createNewFile())
				throw new Exception("An unknown error occurred");
		} catch(Exception e) {
			LOGGER.error("[RobloxSafechat] Failed to create " + name + " file at " + file, e);
			return;
		}

		try(FileOutputStream stream = new FileOutputStream(file)) {
			try(JsonWriter writer = new JsonWriter(new OutputStreamWriter(stream))) {
				writer.setIndent("\t");
				writer.jsonValue(GSON.toJson(object));
			}
		} catch(Exception e) {
			LOGGER.error("[RobloxSafechat] Failed to write " + name + " file at " + file, e);
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
