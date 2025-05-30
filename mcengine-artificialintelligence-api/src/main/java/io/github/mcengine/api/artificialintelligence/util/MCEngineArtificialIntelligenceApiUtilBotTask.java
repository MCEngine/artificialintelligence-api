package io.github.mcengine.api.artificialintelligence.util;

import io.github.mcengine.api.artificialintelligence.MCEngineArtificialIntelligenceApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Async task that sends player input to the AI API and sends the response back.
 * Can be reused by any addon that uses MCEngineArtificialIntelligenceApiUtilBotManager.
 */
public class MCEngineArtificialIntelligenceApiUtilBotTask extends BukkitRunnable {

    private final Plugin plugin;
    private final Player player;
    private final String platform;
    private final String model;
    private final String message;

    /**
     * Constructs a new ChatBotTask.
     *
     * @param plugin   The plugin instance.
     * @param player   The player in conversation.
     * @param platform The AI platform to use.
     * @param model    The model name to use.
     * @param message  The message sent by the player.
     */
    public MCEngineArtificialIntelligenceApiUtilBotTask(Plugin plugin, Player player, String platform, String model, String message) {
        this.plugin = plugin;
        this.player = player;
        this.platform = platform;
        this.model = model;
        this.message = message;
    }

    @Override
    public void run() {
        MCEngineArtificialIntelligenceApi api = MCEngineArtificialIntelligenceApi.getApi();
        String response;

        try {
            // Full conversation used as input
            String fullPrompt = MCEngineArtificialIntelligenceApiUtilBotManager.get(player) + "[Player]: " + message;
            response = api.getResponse(platform, model, fullPrompt);
        } catch (Exception e) {
            Bukkit.getScheduler().runTask(plugin, () ->
                player.sendMessage("§c[ChatBot] Failed: " + e.getMessage())
            );
            MCEngineArtificialIntelligenceApiUtilBotManager.setWaiting(player, false);
            return;
        }

        String playerPrompt = "[Player]: " + message;
        String aiReply = "[Ai]: " + response;

        Bukkit.getScheduler().runTask(plugin, () -> {
            player.sendMessage("§e[ChatBot]§r " + response);
            MCEngineArtificialIntelligenceApiUtilBotManager.append(player, playerPrompt);
            MCEngineArtificialIntelligenceApiUtilBotManager.append(player, aiReply);
            MCEngineArtificialIntelligenceApiUtilBotManager.setWaiting(player, false);
        });
    }
}
