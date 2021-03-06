package seng302.utility;

import java.util.HashMap;

import javafx.scene.image.Image;

/**
 * Caches stored images to the memory.
 */
public class ImageCache {
    HashMap<String, Image> cache = new HashMap<>();

    public ImageCache() {
    }

    public Image retrieve(String path, int size) {
        String key = cacheKey(path, size);

        if (!cache.containsKey(key)) {
            cache.put(key, new Image(path, size, size, true, true));
        }

        return cache.get(key);
    }

    private String cacheKey(String path, int size) {
        return String.format("%1$s:%2$d", path, size);
    }
}
