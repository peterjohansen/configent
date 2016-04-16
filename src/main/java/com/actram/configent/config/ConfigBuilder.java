package com.actram.configent.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 
 * 
 * @author Peter André Johansen
 */
public class ConfigBuilder {
	public class ConfigItemBuilder<T> {

		// See builder setter methods for details
		// about each property's purpose.
		private String name;
		private Class<?> ofType;
		private ConfigValidator<T> constraints;
		private Class<?> toType;
		private Function<?, T> typeMapper;

		/**
		 * @param originalItem the item to copy properties from (except name),
		 *            or {@code null} if no properties should be copied
		 * @param name the new item's name
		 */
		private ConfigItemBuilder(ConfigItemBuilder<T> originalItem, String name) {
			this.name = name;

			if (originalItem != null) {
				// Copy the original item's properties in the
				// same manner as the user would set them
				this.ofType(originalItem.ofType);
				this.mapToType(originalItem.toType, originalItem.typeMapper);
				this.withConstraints(constraints);
			}
		}

		/**
		 * Creates a brand new config builder item.
		 */
		private ConfigItemBuilder(String name) {
			this(null, name);
		}

		public ConfigItemBuilder<T> mapToType(Class<?> type, Function<?, T> mapper) {
			assert ((toType == null) == (mapper == null)) : "type and mapper should both either be set or not set";
			if (this.toType != null && this.typeMapper != null) {
				throw new ConfigBuilderException("mapping has already been specified for: " + name);
			}
			Objects.requireNonNull(type, "type to map to cannot be null");
			Objects.requireNonNull(mapper, "type mapper cannot be null");

			this.toType = type;
			this.typeMapper = mapper;
			return this;
		}

		public ConfigItemBuilder<T> ofType(Class<?> type) {
			if (this.ofType != null) {
				throw new ConfigBuilderException("type has already been specified for: " + name);
			}
			Objects.requireNonNull(type, "configuration item's type cannot be null");

			this.ofType = type;
			return this;
		}

		public ConfigItemBuilder<T> withConstraints(ConfigValidator<T> val) {
			if (this.constraints != null) {
				throw new ConfigBuilderException("constraints have already been specified for: " + name);
			}
			Objects.requireNonNull(val, "configuration item's constraints cannot be null");

			this.constraints = val;
			return this;
		}
	}

	private final List<ConfigItemBuilder<?>> items = new ArrayList<>();

	public void addCopy(String newName, String original) {
		for (ConfigItemBuilder<?> item : items) {
			if (newName.equals(item.name)) {
				addItem(new ConfigItemBuilder<>(item, newName));
			}
		}
		throw new ConfigBuilderException("no previous configuration item with that name has been defined: " + original);
	}

	public void addCopyOfPrevious(String newName) {
		ConfigItemBuilder<?> prev = getPreviousItem();
		if (prev == null) {
			throw new ConfigBuilderException("no previous configuration item has been defined");
		}
		addCopy(prev.name, newName);
	}

	private <T> ConfigItemBuilder<T> addItem(ConfigItemBuilder<T> item) {
		assert item != null : "item cannot be null";
		items.add(item);
		return getCurrentItem();
	}

	public <T> ConfigItemBuilder<T> addItem(String name) {
		Objects.requireNonNull(name, "configuration item's name cannot be null");
		return addItem(new ConfigItemBuilder<>(name));
	}

	public Config build() {
		return new Config(null, null);
	}

	private <T> ConfigItemBuilder<T> getCurrentItem() {
		return (items.size() != 0 ? getItem(0) : null);
	}

	@SuppressWarnings("unchecked")
	private <T> ConfigItemBuilder<T> getItem(int index) {
		return (ConfigItemBuilder<T>) items.get(index);
	}

	private <T> ConfigItemBuilder<T> getPreviousItem() {
		return (items.size() >= 1 ? getItem(1) : null);
	}
}