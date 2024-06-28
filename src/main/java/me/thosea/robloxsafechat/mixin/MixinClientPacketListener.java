package me.thosea.robloxsafechat.mixin;

import me.thosea.robloxsafechat.config.loader.ConfigLoader;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {
	@Inject(method = "handleLogin", at = @At("RETURN"))
	private void onSetLevel(CallbackInfo ci) {
		ConfigLoader.sendErrorMessagesInChat();
	}
}
