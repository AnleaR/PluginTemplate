package su.nexus.kvadrat.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.abstractcoder.nexuscraft.antirelog.PlayerCombatTriggeredEvent;
import ru.den_abr.commonlib.utility.UtilityMethods;
import su.nexus.kvadrat.KvadratPlugin;
import su.nexus.kvadrat.bossbar.BossbarBuilder;
import su.nexus.kvadrat.bossbar.BossbarCountdown;
import su.nexus.kvadrat.data.KvadratData;
import su.nexus.kvadrat.data.KvadratSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class JusticeListener implements Listener {

	public static Map<Player, BossBar> bossbars;
	public static Cache<String, Long> safePlayers;
	public static List<Player> clickedInChest;
	public static Cache<String, Long> chestPlayers;

	static {
		bossbars = new HashMap<>();
		safePlayers = CacheBuilder.newBuilder().expireAfterWrite(KvadratSettings.General.PVP_SECONDS_AFTER_SAFE_ZONE, TimeUnit.SECONDS).build();
		clickedInChest = new ArrayList<>();
		chestPlayers = CacheBuilder.newBuilder().expireAfterWrite(KvadratSettings.General.PVP_SECONDS_AFTER_CLICKS, TimeUnit.SECONDS).build();
	}

	@EventHandler
	public void onMove(final PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location from = event.getFrom();
		Location to = event.getTo();

		if (KvadratData.players.contains(player)) {

			if (KvadratData.inPvpZone(from) && KvadratData.inSafeZone(to)) {

				if (inSafePvp(player) || inChestPvp(player)) {
					if (clickedInChest.contains(player)) {
						chestPlayers.invalidate(player.getName());
						chestPlayers.put(player.getName(), System.currentTimeMillis());
						sendPvpBossbarTimer(player, KvadratSettings.General.PVP_SECONDS_AFTER_CLICKS);
					}
					else {
						safePlayers.invalidate(player.getName());
						safePlayers.put(player.getName(), System.currentTimeMillis());
						sendPvpBossbarTimer(player, KvadratSettings.General.PVP_SECONDS_AFTER_SAFE_ZONE);
					}
				} else {
					if (clickedInChest.contains(player)) {
						chestPlayers.put(player.getName(), System.currentTimeMillis());
						sendPvpBossbarTimer(player, KvadratSettings.General.PVP_SECONDS_AFTER_CLICKS);
					}
					else {
						safePlayers.put(player.getName(), System.currentTimeMillis());
						sendPvpBossbarTimer(player, KvadratSettings.General.PVP_SECONDS_AFTER_SAFE_ZONE);
					}
				}
			}
		}
	}

	@EventHandler
	public void onCommand(final PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();

		if (inSafePvp(player) || inChestPvp(player) || KvadratData.inPvpZone(player) || KvadratData.inChestZone(player)) {
			String command = UtilityMethods.getOriginalCommand(event.getMessage());
			if (!KvadratSettings.General.PVP_ALLOWED_CMDS.contains(command)) {
				player.sendMessage(UtilityMethods.color(KvadratSettings.Locale.PVP_BLOCK_COMMAND));
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onAntiRelog(final PlayerCombatTriggeredEvent event) {
		Player player = event.getDamager();
		if (KvadratData.players.contains(player)) {
			event.setCancelled(true);
			return;
		}

		event.getVictims().removeIf(player1 -> KvadratData.players.contains(player1));
	}

	public static boolean inSafePvp(Player player) {
		return JusticeListener.safePlayers.getIfPresent(player.getName()) != null;
	}

	public static boolean inChestPvp(Player player) {
		return JusticeListener.chestPlayers.getIfPresent(player.getName()) != null;
	}

	public static Long getPvpTime(Player player) {
		if (safePlayers.getIfPresent(player.getName()) != null) {
			return safePlayers.getIfPresent(player.getName());
		}
		else if (chestPlayers.getIfPresent(player.getName()) != null) {
			return chestPlayers.getIfPresent(player.getName());
		}
		else {
			return null;
		}
	}

	public static void sendSafeBossbar(Player player) {

		if (bossbars.containsKey(player)) {
			bossbars.get(player).removePlayer(player);
			bossbars.remove(player);
		}

		BossBar bar = new BossbarBuilder(KvadratSettings.Bossbar.SAFE_ZONE_COLOR, KvadratSettings.Bossbar.SAFE_ZONE_STYLE)
				.title(KvadratSettings.Bossbar.SAFE_ZONE_TITLE).build();

		bar.addPlayer(player);

		KvadratPlugin.getInstance().getServer().getScheduler().runTaskLater(KvadratPlugin.getInstance(), () -> bar.removePlayer(player), 20L);

		bossbars.put(player, bar);
	}

	public static void sendPvpBossbar(Player player) {

		if (bossbars.containsKey(player)) {
			bossbars.get(player).removePlayer(player);
			bossbars.remove(player);
		}

		BossBar bar = new BossbarBuilder(KvadratSettings.Bossbar.PVP_ZONE_COLOR, KvadratSettings.Bossbar.PVP_ZONE_STYLE)
				.title(KvadratSettings.Bossbar.PVP_ZONE_TITLE).build();

		bar.addPlayer(player);

		KvadratPlugin.getInstance().getServer().getScheduler().runTaskLater(KvadratPlugin.getInstance(), () -> bar.removePlayer(player), 20L);

		bossbars.put(player, bar);
	}

	public static void sendPvpBossbarTimer(Player player, int seconds) {

		if (bossbars.containsKey(player)) {
			bossbars.get(player).removePlayer(player);
			bossbars.remove(player);
		}

		BossBar bar = new BossbarBuilder(KvadratSettings.Bossbar.TIMER_COLOR, KvadratSettings.Bossbar.TIMER_STYLE)
				.title(KvadratSettings.Bossbar.TIMER_TITLE.replaceAll("\\(sec\\)", String.valueOf(seconds))).build();

		bar.addPlayer(player);

		new BossbarCountdown(bar, player, UtilityMethods.color(KvadratSettings.Bossbar.TIMER_TITLE), seconds).runTaskTimer(KvadratPlugin.getInstance(), 0L, 20L);

		bossbars.put(player, bar);
	}
}