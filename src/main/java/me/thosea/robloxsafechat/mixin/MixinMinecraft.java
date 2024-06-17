package me.thosea.robloxsafechat.mixin;

import me.thosea.robloxsafechat.config.ConfigLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
	@Inject(method = "setLevel", at = @At("RETURN"))
	private void onSetLevel(ClientLevel level, CallbackInfo ci) {
		ConfigLoader.sendErrorMessagesInChat();
	}
}
