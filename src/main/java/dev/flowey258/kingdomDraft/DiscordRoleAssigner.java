package dev.flowey258.kingdomDraft;

import dev.flowey258.kingdomDraft.KingdomDraft;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import org.bukkit.entity.Player;

public class DiscordRoleAssigner {

    private final KingdomDraft plugin;

    public DiscordRoleAssigner(KingdomDraft plugin) {
        this.plugin = plugin;
    }

    public boolean assignRole(Player player, String kingdomName) {
        if (!plugin.getConfig().getBoolean("discord.enabled", true)) return false;

        String discordUserId = DiscordSRV.getPlugin()
                .getAccountLinkManager()
                .getDiscordId(player.getUniqueId());

        if (discordUserId == null) return false;

        String guildId  = plugin.getConfig().getString("discord.guild-id", "");
        String roleId   = plugin.getConfig().getString("discord.kingdom-roles." + kingdomName, "");

        if (guildId.isEmpty() || roleId.isEmpty()
                || guildId.startsWith("YOUR") || roleId.startsWith("DISCORD")) {
            plugin.getLogger().warning(
                "[KingdomDraft] Role ID not configured for kingdom: " + kingdomName
            );
            return false;
        }

        Guild guild = DiscordSRV.getPlugin().getJda().getGuildById(guildId);
        if (guild == null) {
            plugin.getLogger().warning("[KingdomDraft] Guild not found: " + guildId);
            return false;
        }

        Role role = guild.getRoleById(roleId);
        if (role == null) {
            plugin.getLogger().warning(
                "[KingdomDraft] Role not found for " + kingdomName + " (ID: " + roleId + ")"
            );
            return false;
        }

        guild.retrieveMemberById(discordUserId).queue(
            member -> guild.addRoleToMember(member, role).queue(
                success -> plugin.getLogger().info(
                    "[KingdomDraft] Discord role '" + role.getName() + "' -> " + player.getName()
                ),
                err -> plugin.getLogger().warning(
                    "[KingdomDraft] Role assign failed for " + player.getName() + ": " + err.getMessage()
                )
            ),
            err -> plugin.getLogger().warning(
                "[KingdomDraft] Discord member not found for " + player.getName()
            )
        );

        return true;
    }

    public void removeAllKingdomRoles(Player player) {
        String discordUserId = DiscordSRV.getPlugin()
                .getAccountLinkManager()
                .getDiscordId(player.getUniqueId());
        if (discordUserId == null) return;

        String guildId = plugin.getConfig().getString("discord.guild-id", "");
        if (guildId.isEmpty() || guildId.startsWith("YOUR")) return;

        Guild guild = DiscordSRV.getPlugin().getJda().getGuildById(guildId);
        if (guild == null) return;

        guild.retrieveMemberById(discordUserId).queue(member -> {
            for (String kingdom : plugin.getConfig().getStringList("kingdoms")) {
                String roleId = plugin.getConfig().getString("discord.kingdom-roles." + kingdom, "");
                if (!roleId.isEmpty() && !roleId.startsWith("DISCORD")) {
                    Role role = guild.getRoleById(roleId);
                    if (role != null && member.getRoles().contains(role)) {
                        guild.removeRoleFromMember(member, role).queue();
                    }
                }
            }
        }, err -> {});
    }
}
