package com.actram.configent.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 *
 * @author Peter André Johansen
 */
public class Config {
	private final Map<String, ConfigEntry<?>> entries = new HashMap<>();
	private final Map<String, Supplier<?>> defaultValues = new HashMap<>();

	Config(Map<String, ConfigEntry<?>> entries, Map<String, Supplier<?>> defaultValues) {
		assert entries != null : "entries cannot be null";
		assert defaultValues != null : "default value suppliers cannot be null";
		this.entries.putAll(entries);
		this.defaultValues.putAll(defaultValues);
	}

	@SuppressWarnings("unchecked")
	public <T> T getDefault(String key) {
		if (!defaultValues.containsKey(key)) {
			throw new IllegalArgumentException("no config entry with key: " + key);
		}
		return (T) defaultValues.get(key).get();
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String key) {
		if (!hasEntry(key)) {
			throw new IllegalArgumentException("no config entry with key: " + key);
		}
		return (T) entries.get(key).cast();
	}

	/**
	 * @return whether the config has an entry with the given key
	 */
	public boolean hasEntry(String key) {
		return entries.containsKey(key);
	}
}