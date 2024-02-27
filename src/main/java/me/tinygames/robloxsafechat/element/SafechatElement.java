package me.tinygames.robloxsafechat.element;

import com.google.gson.JsonElement;
import me.tinygames.robloxsafechat.RobloxSafechat;
import me.tinygames.robloxsafechat.button.SCButton;
import me.tinygames.robloxsafechat.mixin.ChatScreenAccessor;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface SafechatElement {
	Consumer<String > SEND_MESSAGE = text -> {
		Minecraft client = Minecraft.getInstance();

		if(RobloxSafechat.INSTANTLY_SEND) {
			client.player.connection.sendChat(text);
		} else {
			((ChatScreenAccessor) client.screen).insertText(text, false);
		}
	};

	SCButton getButton();
	boolean mouseClicked(int mouseX, int mouseY, int clickType);
	boolean shouldShow(int mouseX, int mouseY);
	void setParent(GroupElement parent);

	@Nullable
	JsonElement serialize();
}
