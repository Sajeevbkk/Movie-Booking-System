package com.moviebooking.app.cache;

import com.moviebooking.app.client.ApiClient;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import java.awt.Image;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class PosterCache {
    private static PosterCache instance;
    private final Map<Long, ImageIcon> memoryCache = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final File cacheDir;

    private PosterCache() {
        cacheDir = new File(System.getProperty("java.io.tmpdir"), "movie_posters_cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    public static synchronized PosterCache getInstance() {
        if (instance == null) {
            instance = new PosterCache();
        }
        return instance;
    }

    public void loadPosterAsync(Long movieId, int width, int height, Consumer<Icon> callback) {
        // Tier 1: Memory
        if (memoryCache.containsKey(movieId)) {
            callback.accept(scaleIcon(memoryCache.get(movieId), width, height));
            return;
        }
        
        executor.submit(() -> {
            try {
                File cachedFile = new File(cacheDir, movieId + ".jpg");
                byte[] bytes = null;
                
                // Tier 2: Disk Cache
                if (cachedFile.exists()) {
                    bytes = Files.readAllBytes(cachedFile.toPath());
                } else {
                    bytes = ApiClient.getInstance().downloadPosterBytes(movieId);
                    if (bytes != null && bytes.length > 0) {
                        Files.write(cachedFile.toPath(), bytes);
                    }
                }
                
                if (bytes != null && bytes.length > 0) {
                    ImageIcon originalIcon = new ImageIcon(bytes);
                    memoryCache.put(movieId, originalIcon);
                    ImageIcon scaled = scaleIcon(originalIcon, width, height);
                    SwingUtilities.invokeLater(() -> callback.accept(scaled));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
}
