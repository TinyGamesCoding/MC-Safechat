package me.thosea.robloxsafechat.config.loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.thosea.robloxsafechat.element.ButtonElement;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConfigOption<T> {
	public static final OptionType<Boolean> BOOLEAN_TYPE = new OptionType<>() {
		@Override
		public JsonElement serialize(Boolean value) {
			return new JsonPrimitive(value);
		}
		@Override
		public Boolean deserialize(JsonElement element) {
			return element.getAsBoolean();
		}
		@Override
		public Boolean nextValue(Boolean value) {
			return !value;
		}
	};
	public static OptionType<Float> floatOptionType(float min, float max) {
		return new OptionType<>() {
			@Override
			public JsonElement serialize(Float value) {
				return new JsonPrimitive(value);
			}
			@Override
			public Float deserialize(JsonElement element) {
				return element.getAsFloat();
			}
			@Override
			public Float nextValue(Float value) {
				value += 0.1f;

				if(value > max || value < min) {
					value = min;
				} else {
					value = Math.round(value * 10) / 10.0f; // round to nearest 0.1
				}

				return value;
			}
			@Override
			public boolean nameInTooltip() {
				return false;
			}
		};
	}

	public static final Map<String, ConfigOption<?>> CONFIG_OPTIONS = new HashMap<>();

	private T value;
	private final T defaultValue;
	private final OptionType<T> type;

	private final BiConsumer<T, BiConsumer<String, String>> nameAndTooltipUpdater;

	public final List<Consumer<T>> changeListeners = new ArrayList<>(0);

	public ConfigOption(
			String name,
			OptionType<T> type,
			T defaultValue,
			BiConsumer<T, BiConsumer<String, String>> nameAndTooltipUpdater) {
		if(CONFIG_OPTIONS.containsKey(name)) {
			throw new IllegalArgumentException("Duplicate config option name " + name);
		}
		CONFIG_OPTIONS.put(name, this);

		this.type = type;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.nameAndTooltipUpdater = nameAndTooltipUpdater;
	}

	public T get() {return value;}
	public <C> C getCast() {return (C) value;}
	public T getDefault() {return defaultValue;}
	public OptionType<T> getType() {return type;}

	public ButtonElement makeButton() {
		return new ButtonElement(() -> {
			this.set(type.nextValue(value));
		}, button -> {
			nameAndTooltipUpdater.accept(value, (name, tooltip) -> {
				button.setMessage(Component.literal(name));

				if(type.nameInTooltip()) {
					tooltip = name + "\n\n" + tooltip;
				}

				button.setTooltip(tooltip == null ? null : Tooltip.create(Component.literal(tooltip)));
			});
		});
	}

	public void reset() {
		this.set(getDefault());
	}

	public void set(T value) {
		this.value = value;
		changeListeners.forEach(listener -> listener.accept(value));
	}

	public interface OptionType<T> {
		JsonElement serialize(T value);
		T deserialize(JsonElement element);
		T nextValue(T current);

		default boolean nameInTooltip() {
			return true;
		}
	}
}
