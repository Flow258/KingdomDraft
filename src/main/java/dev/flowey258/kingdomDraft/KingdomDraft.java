package dev.flowey258.kingdomDraft;

import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.plugin.java.JavaPlugin;

public class KingdomDraft extends JavaPlugin {

    private static KingdomDraft instance;
    private boolean discordSRVEnabled = false;
    private AccountLinkListener linkListener;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config.yml if not present
        saveDefaultConfig();

        // Check if DiscordSRV is available
        if (getServer().getPluginManager().getPlugin("DiscordSRV") != null) {
            discordSRVEnabled = true;
            getLogger().info("[KingdomDraft] DiscordSRV found — Discord role sync is ENABLED.");
        } else {
            getLogger().info("[KingdomDraft] DiscordSRV not found — Discord role sync is DISABLED.");
        }

        // Build helpers
        DiscordRoleAssigner roleAssigner = new DiscordRoleAssigner(this);
        linkListener = new AccountLinkListener(this, roleAssigner);

        // Register Bukkit event listener (for PlayerJoin)
        getServer().getPluginManager().registerEvents(
            new DraftListener(this, roleAssigner, linkListener),
            this
        );

        // Register DiscordSRV listener (for account link events)
        if (discordSRVEnabled) {
            DiscordSRV.api.subscribe(linkListener);
            getLogger().info("[KingdomDraft] Subscribed to DiscordSRV account link events.");
        }

        getLogger().info("KingdomDraft enabled — The 4 Kingdoms of Sepherune await.");
    }

    @Override
    public void onDisable() {
        if (discordSRVEnabled && linkListener != null) {
            DiscordSRV.api.unsubscribe(linkListener);
        }
        getLogger().info("KingdomDraft disabled.");
    }

    public static KingdomDraft getInstance() {
        return instance;
    }

    public boolean isDiscordSRVEnabled() {
        return discordSRVEnabled;
    }
}
