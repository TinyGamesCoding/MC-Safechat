package me.thosea.robloxsafechat.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public final class ConfigFiles {
	private ConfigFiles() {}

	public static final File CONFIG_FOLDER = new File(
			FabricLoader.getInstance().getConfigDir().toFile(),
			"robloxsafechat");

	public static final File CONFIG_FILE = new File(CONFIG_FOLDER, "config.json");
	public static final File MESSAGES_FILE = new File(CONFIG_FOLDER, "messages.json");
}
