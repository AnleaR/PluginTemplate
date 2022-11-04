package su.nexus.kvadrat.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import ru.den_abr.commonlib.utility.UtilityMethods;
import su.nexus.kvadrat.data.KvadratData;
import su.nexus.kvadrat.data.KvadratSettings;
import su.nexus.kvadrat.menu.ClickerMenu;

public class PlayerListener implements Listener {

	@EventHandler
	public void onTeleport(final PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		if (KvadratData.players.contains(player)) {
			if (!event.getTo().getWorld().equals(KvadratSettings.SmallLocation.WORLD))
				KvadratData.removeAndTake(player);
		}
	}

	@EventHandler
	public void onMove(final PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location from = event.getFrom();
		Location to = event.getTo();

		if (KvadratData.players.contains(player) && !KvadratData.inCube(KvadratData.safeZone, to))
			KvadratData.removeAndTake(player);

		if (KvadratData.players.contains(player) &&
				KvadratData.inCube(KvadratData.chestZone, from) && !KvadratData.inCube(KvadratData.chestZone, to)) {
			if (ClickListener.menus.containsKey(player))
				player.closeInventory();
		}
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (KvadratData.players.contains(player) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (KvadratData.big) {
				if (event.getClickedBlock().getLocation().equals(KvadratSettings.BigLocation.DISPENSER_LOCATION)) {
					if (!KvadratData.inChestZone(player))
						player.sendMessage(UtilityMethods.color(KvadratSettings.Locale.NOT_IN_CHEST_ZONE));
					else
						new ClickerMenu(player).open();
					event.setCancelled(true);
				}
			} else {
				if (event.getClickedBlock().getLocation().equals(KvadratSettings.SmallLocation.DISPENSER_LOCATION)) {
					if (!KvadratData.inChestZone(player))
						player.sendMessage(UtilityMethods.color(KvadratSettings.Locale.NOT_IN_CHEST_ZONE));
					else
						new ClickerMenu(player).open();
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onLeave(final PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (KvadratData.players.contains(player)) {
			if (JusticeListener.inSafePvp(player) || JusticeListener.inChestPvp(player) || KvadratData.inPvpZone(player) || KvadratData.inChestZone(player)) {
				player.setHealth(0.0);
				return;
			}
			player.teleport(KvadratSettings.Spawn.SPAWN);
			KvadratData.removeAndTake(player);
		}
	}
}