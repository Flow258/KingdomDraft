package dev.flowey258.kingdomDraft;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Listens for DiscordSRV account link events.
 *
 * If a player was drafted into a kingdom BEFORE they linked their Discord,
 * their pending kingdom is stored here. Once they link, the role is assigned
 * automatically — they don't need to rejoin.
 */
public class AccountLinkListener {

    private final KingdomDraft plugin;
    private final DiscordRoleAssigner roleAssigner;

    // Stores players who were drafted but hadn't linked Discord yet
    // UUID (Minecraft) → Kingdom name
    private final Map<UUID, String> pendingRoles = new ConcurrentHashMap<>();

    public AccountLinkListener(KingdomDraft plugin, DiscordRoleAssigner roleAssigner) {
        this.plugin = plugin;
        this.roleAssigner = roleAssigner;
    }

    /**
     * Called by DraftListener when a player was drafted but Discord not linked yet.
     */
    public void addPending(UUID playerId, String kingdomName) {
        pendingRoles.put(playerId, kingdomName);
        plugin.getLogger().info(
                "[KingdomDraft] Stored pending Discord role for " +
                        playerId + " → " + kingdomName
        );
    }

    /**
     * DiscordSRV fires this when a player successfully links their account.
     * The @Subscribe annotation is how DiscordSRV registers listeners (not Bukkit events).
     *
     * NOTE: event.getPlayer() returns OfflinePlayer — we use Bukkit.getPlayer(UUID)
     * to get the online Player instance needed for role assignment and messaging.
     */
    @Subscribe
    public void onAccountLinked(AccountLinkedEvent event) {
        UUID minecraftId = event.getPlayer().getUniqueId();

        if (!pendingRoles.containsKey(minecraftId)) return;

        String kingdomName = pendingRoles.remove(minecraftId);

        // Run role assignment back on the main thread just to be safe
        Bukkit.getScheduler().runTask(plugin, () -> {

            // Bukkit.getPlayer(UUID) returns Player (online) — not OfflinePlayer
            Player player = Bukkit.getPlayer(minecraftId);

            if (player == null || !player.isOnline()) return;

            roleAssigner.assignRole(player, kingdomName);

            player.sendMessage(color("&8&m----------------------------------------"));
            player.sendMessage(color("&d&lKINGDOM DISCORD ROLE ASSIGNED"));
            player.sendMessage(color("&7Your Discord account has been linked!"));
            player.sendMessage(color("&7You have received the &b" + kingdomName + " &7role."));
            player.sendMessage(color("&8&m----------------------------------------"));
        });
    }

    private String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}