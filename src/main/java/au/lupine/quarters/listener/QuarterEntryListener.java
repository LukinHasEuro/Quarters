package au.lupine.quarters.listener;

import au.lupine.quarters.api.QuartersMessaging;
import au.lupine.quarters.api.manager.ConfigManager;
import au.lupine.quarters.api.manager.QuarterManager;
import au.lupine.quarters.api.manager.ResidentMetadataManager;
import au.lupine.quarters.object.entity.Quarter;
import au.lupine.quarters.object.state.EntryNotificationType;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.object.Resident;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class handles the notifications that may appear when entering a quarter
 */
public class QuarterEntryListener implements Listener {

    private static final Map<UUID, Optional<Quarter>> QUARTER_PLAYER_IS_IN = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        Player player = event.getPlayer();

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        Location to = event.getTo();

        Quarter quarter = QuarterManager.getInstance().getQuarter(to);

        Optional<Quarter> previousQuarter = QUARTER_PLAYER_IS_IN.get(player.getUniqueId());
        if (quarter != null && (previousQuarter.isEmpty() || !previousQuarter.get().equals(quarter)))
            onQuarterEntry(quarter, resident);

        QUARTER_PLAYER_IS_IN.put(player.getUniqueId(), Optional.ofNullable(quarter));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { // This listener is to prevent an alert when connecting inside a quarter
        Player player = event.getPlayer();
        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) return;

        Quarter quarter = QuarterManager.getInstance().getQuarter(player.getLocation());

        QUARTER_PLAYER_IS_IN.put(player.getUniqueId(), Optional.ofNullable(quarter));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        QUARTER_PLAYER_IS_IN.remove(event.getPlayer().getUniqueId());
    }

    private void onQuarterEntry(Quarter quarter, Resident resident) {
        ResidentMetadataManager rmm = ResidentMetadataManager.getInstance();

        if (rmm.hasEntryNotifications(resident) && ConfigManager.areEntryNotificationsAllowed())
            sendEntryNotification(quarter, resident);

        if (rmm.hasEntryBlinking(resident) && ConfigManager.isEntryParticleBlinkingAllowed())
            quarter.blinkForResident(resident);
    }

private void sendEntryNotification(Quarter quarter, Resident resident) {
    // Components for the subtitle (lower title) only
    Component quarterName = Component.text(quarter.getName(), TextColor.color(quarter.getColour().getRGB()));
    Component comma = Component.text(", ", NamedTextColor.GRAY); // Gray comma

    // Get the town name for the lower title
    String townName = resident.hasTown() ? resident.getTownOrNull().getName() : "Unknown Town";
    Component townNameComponent = Component.text(townName, NamedTextColor.GREEN); // Green town name

    // Join the quarter name and town name with a gray comma to form the lower title
    Component subtitle = quarterName.append(comma).append(townNameComponent);

    // Get the player's notification type (Action Bar, Chat, or Title)
    EntryNotificationType notificationType = ResidentMetadataManager.getInstance().getEntryNotificationType(resident);

    // Get the player
    Player player = resident.getPlayer();
    if (player == null) return;

    // Handle the different notification types
    switch (notificationType) {
        case ACTION_BAR -> player.sendActionBar(subtitle); // Send only the lower title in the action bar
        case CHAT -> QuartersMessaging.sendMessage(player, subtitle); // Send lower title in chat
        case TITLE -> {
            // Send only the lower title (no main title)
            player.showTitle(net.kyori.adventure.title.Title.title(Component.empty(), subtitle));
        }
    }
}
