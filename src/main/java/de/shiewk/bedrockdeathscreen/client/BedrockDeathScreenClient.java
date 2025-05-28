package de.shiewk.bedrockdeathscreen.client;

import com.google.gson.Gson;
import de.shiewk.bedrockdeathscreen.config.BedrockDeathScreenConfig;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class BedrockDeathScreenClient implements ClientModInitializer {

    private static final Logger log = LoggerFactory.getLogger(BedrockDeathScreenClient.class);
    private static final Gson gson = new Gson();
    private static BedrockDeathScreenConfig config = new BedrockDeathScreenConfig();
    private static Path configPath;

    @Override
    public void onInitializeClient() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        configPath = minecraftClient.runDirectory.toPath().resolve("config/bedrockdeathscreen.json");

        try {
            loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void loadConfig() throws IOException {
        log.info("Loading config");
        if (Files.isRegularFile(configPath)){
            try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                BedrockDeathScreenConfig loadedConfig = gson.fromJson(reader, BedrockDeathScreenConfig.class);
                if (loadedConfig != null) {
                    config = loadedConfig;
                } else {
                    log.info("Config could not be deserialized, not loading.");
                }
            }
        } else {
            log.info("Config file is not a regular file, not loading.");
        }
    }

    public static BedrockDeathScreenConfig getConfig() {
        return config;
    }

    public static void saveConfig() throws IOException {
        log.info("Saving config");
        Files.createDirectories(configPath.getParent());
        if (!Files.isRegularFile(configPath)){
            Files.createFile(configPath);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(configPath, StandardOpenOption.TRUNCATE_EXISTING)) {
            gson.toJson(config, writer);
        }
    }
}
