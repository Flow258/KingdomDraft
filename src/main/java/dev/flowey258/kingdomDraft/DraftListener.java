package dev.flowey258.kingdomDraft;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Random;

public class DraftListener implements Listener {

    private final KingdomDraft plugin;
    private final Random random = new Random();
    private final DiscordRoleAssigner roleAssigner;
    private final AccountLinkListener linkListener;

    // House emblems shown in chat — short, lore-friendly
    // Full lore is in the house info but chat only shows the crest symbol + name
    private static final java.util.Map<String, String[]> HOUSE_INFO = new java.util.LinkedHashMap<>();

    static {
        // Format: kingdom key -> { display emblem, monarch, color code }
        HOUSE_INFO.put("Vlossaire",  new String[]{ "⚔",  "House of Vlossaire",  "&c" });
        HOUSE_INFO.put("Arcnaria",   new String[]{ "✦",  "House of Arcnaria",   "&9" });
        HOUSE_INFO.put("Hushpierre", new String[]{ "❄",  "House of Hushpierre", "&a" });
        HOUSE_INFO.put("Slypharis",  new String[]{ "◆",  "House of Slypharis",  "&6" });
    }

    public DraftListener(KingdomDraft plugin,
                         DiscordRoleAssigner roleAssigner,
                         AccountLinkListener linkListener) {
        this.plugin       = plugin;
        this.roleAssigner = roleAssigner;
        this.linkListener = linkListener;
    }

    // -----------------------------------------------------------------------
    // BLOCK kingdom creation for non-admins
    // -----------------------------------------------------------------------
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.getConfig().getBoolean("block-kingdom-creation", true)) return;

        String bypassPerm = plugin.getConfig().getString("bypass-permission", "kingdomdraft.bypass");
        Player player = event.getPlayer();

        // Allow admins through
        if (player.hasPermission(bypassPerm) || player.isOp()) return;

        String msg = event.getMessage().toLowerCase().trim();

        // Match: /k create, /kingdoms create, /k admin create (and variants with spaces)
        boolean isCreate =
            msg.matches("^/k\\s+create.*") ||
            msg.matches("^/kingdoms\\s+create.*") ||
            msg.matches("^/k\\s+admin\\s+create.*");

        if (isCreate) {
            event.setCancelled(true);
            player.sendMessage(color("&8&m----------------------------------------"));
            player.sendMessage(color("&c&lKINGDOM CREATION DISABLED"));
            player.sendMessage(color("&7In Sepherune, kingdoms are not made — they are &dbirthrights&7."));
            player.sendMessage(color("&7You were drafted into your house upon arrival."));
            player.sendMessage(color("&8&m----------------------------------------"));
        }
    }

    // -----------------------------------------------------------------------
    // AUTO-DRAFT on first join
    // -----------------------------------------------------------------------
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        long checkDelay   = plugin.getConfig().getLong("check-delay", 40L);
        long messageDelay = plugin.getConfig().getLong("message-delay", 20L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            if (!player.isOnline()) return;

            String hasKingdom = PlaceholderAPI.setPlaceholders(player, "%kingdoms_has_kingdom%");

            if (!"true".equalsIgnoreCase(hasKingdom)) {

                List<String> kingdoms = plugin.getConfig().getStringList("kingdoms");
                if (kingdoms.isEmpty()) {
                    plugin.getLogger().warning("[KingdomDraft] No kingdoms defined in config.yml!");
                    return;
                }

                String chosenKingdom = kingdoms.get(random.nextInt(kingdoms.size()));

                plugin.getLogger().info(
                    "[KingdomDraft] Drafting " + player.getName() + " -> " + chosenKingdom
                );

                Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "k admin join " + player.getName() + " " + chosenKingdom
                );

                Bukkit.getScheduler().runTaskLater(plugin, () -> {

                    if (!player.isOnline()) return;

                    String kingdomName = PlaceholderAPI.setPlaceholders(player, "%kingdoms_name%");

                    sendDraftMessage(player, chosenKingdom, kingdomName);

                    if (plugin.isDiscordSRVEnabled()) {
                        boolean assigned = roleAssigner.assignRole(player, chosenKingdom);
                        if (!assigned) {
                            linkListener.addPending(player.getUniqueId(), chosenKingdom);
                            player.sendMessage(color("&e&l[Discord] &7Link your account: &b/discord link"));
                            player.sendMessage(color("&7Your house role will be given automatically!"));
                        }
                    }

                }, messageDelay);
            }

        }, checkDelay);
    }

    // -----------------------------------------------------------------------
    // Draft message — emblem + house name only (short version for chat)
    // -----------------------------------------------------------------------
    private void sendDraftMessage(Player player, String kingdomKey, String kingdomName) {
        String[] info = HOUSE_INFO.getOrDefault(kingdomKey,
                new String[]{ "✦", kingdomName, "&d" });

        String emblem    = info[0];
        String houseName = info[1];
        String color     = info[2];

        player.sendMessage(color("&8&m----------------------------------------"));
        player.sendMessage(color("        &d&lTHE 4 HOUSES OF SEPHERUNE"));
        player.sendMessage(color("&8&m----------------------------------------"));
        player.sendMessage(color("  &7Your fate has been placed in the hands"));
        player.sendMessage(color("           &7of chance..."));
        player.sendMessage("");
        player.sendMessage(color("  " + color + emblem + " &fYou belong to: " + color + "&l" + houseName));
        player.sendMessage("");
        player.sendMessage(color("&8&m----------------------------------------"));
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
