package me.tinygames.robloxsafechat.mixin;

import me.tinygames.robloxsafechat.RobloxSafechat;
import me.tinygames.robloxsafechat.config.SettingsGroup;
import me.tinygames.robloxsafechat.element.GroupElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen {
	@Unique private ImageButton chatsButton;
	@Unique private ImageButton settingsButton;

	@Unique private GroupElement renderGroup;
	@Unique private ImageButton openedButton;

	@Inject(method = "init", at = @At("TAIL"))
	private void afterInit(CallbackInfo ci) {
		chatsButton = makeButton(width - 20,
				RobloxSafechat.ICON_KEY,
				RobloxSafechat.HOVERED_KEY,
				RobloxSafechat.SELECTED_KEY);
		settingsButton = makeButton(width - 34,
				RobloxSafechat.SETTINGS_ICON_KEY,
				RobloxSafechat.SETTINGS_HOVERED_KEY,
				RobloxSafechat.SETTINGS_SELECTED_KEY);

		addRenderableWidget(chatsButton);
		addRenderableWidget(settingsButton);
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void afterRender(GuiGraphics graphics, int mouseX, int mouseY, float f, CallbackInfo ci) {
		if(renderGroup != null) {
			float scale = RobloxSafechat.getScale();

			renderGroup.renderGroup(
					graphics,
					(int) (openedButton.getX() - (80 * scale)),
					RobloxSafechat.renderY = (int) (openedButton.getY() - (20 * scale)),
					mouseX, mouseY);
		}
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	private void onMouseClicked(double x, double y, int type, CallbackInfoReturnable<Boolean> cir) {
		if(renderGroup != null) {
			if(!renderGroup.mouseClicked((int) x, (int) y, type)
					|| (openedButton == chatsButton && RobloxSafechat.CLOSE_AFTER_SEND)) {
				renderGroup = null;
				openedButton = null;
			}

			cir.setReturnValue(true);
		} else if(chatsButton.mouseClicked(x, y, type)) {
			renderGroup = RobloxSafechat.ROOT;
			openedButton = chatsButton;
			cir.setReturnValue(true);
		} else if(settingsButton.mouseClicked(x, y, type)) {
			renderGroup = SettingsGroup.INSTANCE;
			openedButton = settingsButton;
			SettingsGroup.INSTANCE.init();
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "resize", at = @At("HEAD"))
	private void onResize(Minecraft minecraft, int i, int j, CallbackInfo ci) {
		renderGroup = null;
		openedButton = null;
	}

	@Unique
	private ImageButton makeButton(int x,
	                               ResourceLocation normal, ResourceLocation hovered,
	                               ResourceLocation selected) {
		return new ImageButton(
				x, height - 14, // x/y
				12, 12, // width/height
				null,
				ignored -> {},
				Component.empty()
		) {
			@Override
			public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
				ResourceLocation icon;

				if(openedButton == this) {
					icon = selected;
				} else if(this.isHovered()) {
					icon = hovered;
				} else {
					icon = normal;
				}

				graphics.blitSprite(icon,
						this.getX(), this.getY(),
						this.width, this.height);
			}

			// Don't allow arrow keys
			@Override
			public ComponentPath nextFocusPath(FocusNavigationEvent event) {
				return null;
			}
		};
	}

	protected MixinChatScreen(Component title) {
		super(title);
		throw new AssertionError("nuh uh");
	}
}
