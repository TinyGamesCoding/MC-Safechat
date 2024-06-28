package me.thosea.robloxsafechat.config.loader.updater;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.thosea.robloxsafechat.config.SafechatConfig;
import me.thosea.robloxsafechat.config.loader.updater.ConfigUpdater.UpdateHandler;

/**
 * From config version 0 (v1.0.0, used when the JSON specifies no config option
 * because 1.0.0 didn't have one) to config version 1 (v1.1.0)
 */
public class Update0To1 implements UpdateHandler {
	@Override
	public void transform(JsonObject root) {
		JsonElement textScaleThreshold = root.get("min_text_scale_for_scrolling");
		root.remove("min_text_scale_for_scrolling");
		// Text scrolling? Whoops. Lets forget I ever tried that.
		root.add("text_scale_threshold", textScaleThreshold);

		// New options
		root.addProperty("opacity_multiplier", SafechatConfig.OPACITY_MULTIPLIER.getDefault());
		root.addProperty("chat_field_fill_affects_opacity", SafechatConfig.CHAT_FIELD_FILL_AFFECTS_OPACITY.getDefault());
		root.addProperty("mouse_affects_opacity", SafechatConfig.MOUSE_AFFECTS_OPACITY.getDefault());
	}
}
