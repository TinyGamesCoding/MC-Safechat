package me.thosea.robloxsafechat.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ChatElement extends ButtonElement {
	public ChatElement(String name) {
		super(name, () -> {
			SEND_MESSAGE.accept(name);
		});
	}

	@Override
	public JsonElement serialize() {
		JsonObject object = new JsonObject();
		object.addProperty("type", "chat");
		object.addProperty("chat", name);
		return object;
	}
}
