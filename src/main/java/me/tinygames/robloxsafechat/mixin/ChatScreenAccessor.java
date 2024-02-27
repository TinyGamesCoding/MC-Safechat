package me.tinygames.robloxsafechat.mixin;

import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChatScreen.class)
public interface ChatScreenAccessor {
	@Invoker("insertText")
	void insertText(String text, boolean overwrite);
}
