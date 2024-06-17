package me.thosea.robloxsafechat.element; // ooohoh ohhhh ohohh oh oh oh story of undertaleeeeeeeeeeeeee - tinygames

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.thosea.robloxsafechat.RobloxSafechat;
import me.thosea.robloxsafechat.button.SCButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GroupElement implements SafechatElement {
	protected final List<SafechatElement> list = new ArrayList<>();
	protected final List<SafechatElement> reversed = Lists.reverse(list);

	@Nullable // null for root
	private final SCButton button;

	private final String name;

	boolean show = true;

	public GroupElement parent;

	public GroupElement(@Nullable String name) {
		if(name == null) {
			this.button = null;
			this.name = null;
		} else {
			this.button = new SCButton(Component.literal(name), true, this.makeAction());
			this.name = name;
		}
	}

	protected Runnable makeAction() {
		return () -> {
			if(RobloxSafechat.GROUPS_ARE_ALSO_TEXTS) {
				SEND_MESSAGE.accept(name);
			}
		};
	}

	public void renderGroup(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
		for(SafechatElement element : listView()) {
			element.getButton().render(graphics, x, y, mouseX, mouseY);

			if(element instanceof GroupElement subgroup && subgroup.shouldShow(mouseX, mouseY)) {
				subgroup.renderGroup(graphics,
						x - SCButton.WIDTH,
						getSubgroupY(y, subgroup),
						mouseX, mouseY);
			}

			y -= SCButton.HEIGHT;
		}
	}

	private int getSubgroupY(int y, GroupElement subgroup) {
		if(subgroup.list.size() < 2) {
			return y;
		}

		int height = subgroup.list.size() * SCButton.HEIGHT;
		y += (height / 2);

		if((y - height) < 0) { // top
			y = height;
			y -= SCButton.HEIGHT;
		}
		if(y > RobloxSafechat.renderY) { // bottom of screen
			y = RobloxSafechat.renderY;
		}

		int difference = Math.abs(y - RobloxSafechat.renderY) % SCButton.HEIGHT;

		if(difference >= SCButton.HEIGHT / 2) {
			y += difference;
		} else {
			y -= difference;
		}

		return y;
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int clickType) {
		if(!shouldShow(mouseX, mouseY)) return false;

		for(SafechatElement element : listView()) {
			if(element.mouseClicked(mouseX, mouseY, clickType)) {
				return true;
			}
		}

		return button != null && button.mouseClicked(mouseX, mouseY, clickType);
	}

	@Override
	public boolean shouldShow(int mouseX, int mouseY) {
		if(parent == null) {
			show = true;
			return true;
		} else if(!parent.show) {
			show = false;
			return false;
		}

		if(button != null && button.isHovered(mouseX, mouseY)) {
			show = true;
			return true;
		} else {
			for(SafechatElement element : listView()) {
				if(element.shouldShow(mouseX, mouseY)) {
					return true;
				}
			}
		}

		show = false;
		return false;
	}

	protected List<SafechatElement> listView() {
		return RobloxSafechat.FLIP_GROUPS ? reversed : list;
	}

	@Override
	@Nullable
	public SCButton getButton() {
		return button;
	}

	@Override
	public void setParent(GroupElement parent) {
		this.parent = parent;
	}

	@Override
	public JsonObject serialize() {
		JsonObject object = new JsonObject();
		object.addProperty("type", "group");

		if(name != null) {
			object.addProperty("name", name);
		}

		JsonArray array = new JsonArray();

		for(SafechatElement element : list) { // always use normal list view
			JsonElement json = element.serialize();
			if(json != null) {
				array.add(json);
			}
		}

		object.add("array", array);
		return object;
	}

	public GroupElement add(SafechatElement element) {
		list.add(element);
		element.setParent(this);
		return this;
	}

	public GroupElement sort() {
		list.sort((thing1, thing2) -> {
			if(thing1 instanceof GroupElement) {
				if(!(thing2 instanceof GroupElement))
					// first is a group, second isn't
					return -1;
			} else if(thing2 instanceof GroupElement) {
				// second is a group, first isn't
				return 1;
			}

			return 0;
		});
		return this;
	}

	public static GroupElement deserializeRoot(JsonElement rootElement) {
		if(!(rootElement instanceof JsonObject root)) {
			throw new IllegalStateException("root element not an object");
		}

		root.remove("name"); // make sure root has null name

		SafechatElement element = deserialize(root);

		if(element instanceof GroupElement group) {
			return group;
		} else {
			throw new IllegalStateException("root is not a group");
		}
	}

	private static SafechatElement deserialize(JsonObject object) {
		JsonElement typeElement = object.get("type");

		if(typeElement == null) {
			throw new IllegalStateException("No type element");
		}

		String type = typeElement.getAsString();

		if(type.equalsIgnoreCase("group")) {
			String name = null;
			if(object.has("name")) {
				name = object.get("name").getAsString();
			}

			JsonElement arrayElement = object.get("array");

			if(!(arrayElement instanceof JsonArray array)) {
				throw new IllegalStateException("Group element ha sno array");
			}

			GroupElement group = new GroupElement(name);
			array.forEach(element -> {
				if(!(element instanceof JsonObject arrayObj)) {
					throw new IllegalStateException("Object is not an array");
				}

				group.add(deserialize(arrayObj));
			});
			return group;
		} else if(type.equalsIgnoreCase("chat")) {
			JsonElement chatElement = object.get("chat");

			if(chatElement == null) {
				throw new IllegalStateException("Chat element has no text");
			}

			return new ChatElement(chatElement.getAsString());
		} else {
			throw new IllegalStateException("Invalid type " + type);
		}
	}
}
