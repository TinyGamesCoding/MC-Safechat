package me.thosea.robloxsafechat.mixin;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// For 1.21 support
@Mixin(ResourceLocation.class)
public interface IdentifierAccessor {
	@Invoker("<init>")
	static ResourceLocation safechat$of(String namespace, String path) {
		throw new AssertionError();
	}
}
