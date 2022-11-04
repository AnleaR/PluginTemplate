package su.nexus.kvadrat.data;

import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.den_abr.commonlib.utility.UtilityMethods;
import ru.vanya.vrubles.VRubles;
import su.nexus.kvadrat.KvadratPlugin;
import su.nexus.kvadrat.listener.JusticeListener;
import su.nexus.kvadrat.scoreboard.KvadratBoard;
import su.nexus.kvadrat.timer.KvadratTimer;

import java.util.*;

public class KvadratData {

	@Getter
	public static boolean going;
	public static boolean big;
	public static List<Player> players;
	public static Map<Location, Location> safeZone;
	public static Map<Location, Location> pvpZone;
	public static Map<Location, Location> chestZone;
	private static Map<String, Integer> playersClicks;
	private static int lastTeleport;

	static {
		going = false;
		big = false;
		players = new ArrayList<>();
		safeZone = new HashMap<>();
		pvpZone = new HashMap<>();
		chestZone = new HashMap<>();
		playersClicks = new HashMap<>();
		lastTeleport = -1;
	}

	public static void addClick(Player player) {
		playersClicks.replace(player.getName(), getClicks(player.getName()) + 1);
	}

	public static int getClicks(String name) {
		return playersClicks.get(name);
	}

	public static int takeQuarterClicks(String name) {
		int sum = (int) Math.floor(playersClicks.get(name) * KvadratSettings.General.PERCENT_FOR_KILL / 100);
		int newClicks = playersClicks.get(name) - sum;
		playersClicks.remove(name);
		playersClicks.put(name, newClicks);
		return sum;
	}

	public static void giveClicks(String name, double clicks) {
		int sum = playersClicks.get(name) + (int) Math.floor(clicks);
		playersClicks.remove(name);
		playersClicks.put(name, sum);
	}

	/**
	 * All locations must be in one world
	 * @param cube - map, key - min location, value - max location
	 * @param location - location to check
	 * @return is location in area
	 */
	public static boolean inCube(Map<Location, Location> cube, Location location) {
		Location minLoc = new ArrayList<>(cube.keySet()).get(0);
		Location maxLoc = new ArrayList<>(cube.values()).get(0);

		return (minLoc.getBlockX() <= location.getBlockX() && location.getBlockX() <= maxLoc.getBlockX()) &&
				(minLoc.getBlockY() <= location.getBlockY() && location.getBlockY() <= maxLoc.getBlockY()) &&
				(minLoc.getBlockZ() <= location.getBlockZ() && location.getBlockZ() <= maxLoc.getBlockZ());
	}

	public static boolean inSafeZone(Player player) {
		return inCube(safeZone, player.getLocation()) && !inCube(pvpZone,player.getLocation()) && !inCube(chestZone,player.getLocation());
	}

	public static boolean inPvpZone(Player player) {
		return inCube(pvpZone, player.getLocation()) && !inCube(chestZone,player.getLocation());
	}

	public static boolean inChestZone(Player player) {
		return inCube(chestZone, player.getLocation());
	}

	public static boolean inSafeZone(Location location) {
		return inCube(safeZone, location) && !inCube(pvpZone, location) && !inCube(chestZone, location);
	}

	public static boolean inPvpZone(Location location) {
		return inCube(pvpZone, location) && !inCube(chestZone, location);
	}

	public static boolean inChestZone(Location location) {
		return inCube(chestZone, location);
	}


	public static LinkedHashMap<String, Integer> sortByValue(Map<String, Integer> map) {
		List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());
		list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

		LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/**
	 * Get list of top players
	 * @param places - how many places will have final Map (Integer Place)
	 *
	 * @return Map with <Place, Map2>, Map2 - <Name, Clicks>
	 */
	public static Map<Integer, Map<String, Integer>> getTopClicks(final int places) {
		Map<Integer, Map<String, Integer>> result = new HashMap<>();
		LinkedHashMap<String, Integer> map = sortByValue(playersClicks);

		int count = 1;
		for (String name : map.keySet()) {
			if (count > places)
				break;

			int clicks = map.get(name);
			result.put(count, Collections.singletonMap(name, clicks));
			count++;
		}

		return result;
	}

	public static int getPlace(String name) {
		Map<Integer, Map<String, Integer>> top = getTopClicks(playersClicks.size());
		for (int place : top.keySet())
			if (new ArrayList<>(top.get(place).keySet()).get(0).equals(name))
				return place;
		return -1;
	}

	private static int random(final int previous, final int border) {
		int random = new Random().nextInt(border);
		while (random == previous)
			random = new Random().nextInt(border);
		return random;
	}

	public static void teleportOnEvent(final Player player) {
		int teleport;
		if (big) {
			teleport = random(lastTeleport, KvadratSettings.BigLocation.SPAWNS.size());
			player.teleport(KvadratSettings.BigLocation.SPAWNS.get(teleport));
		}
		else {
			teleport = random(lastTeleport, KvadratSettings.SmallLocation.SPAWNS.size());
			player.teleport(KvadratSettings.SmallLocation.SPAWNS.get(teleport));
		}
		lastTeleport = teleport;
	}

	public static void teleportOnSpawn(final Player player) {
		player.teleport(KvadratSettings.Spawn.SPAWN);
	}

	public static void addPlayer(final Player player) {
		if (!players.contains(player))
			players.add(player);
		if (!playersClicks.containsKey(player.getName()))
			playersClicks.put(player.getName(), 0);
		new KvadratBoard(player);
		teleportOnEvent(player);
	}

	public static void removePlayer(final Player player) {
		teleportOnSpawn(player);
		players.remove(player);
		KvadratBoard.hide(player);
		JusticeListener.clickedInChest.remove(player);
		JusticeListener.safePlayers.getIfPresent(player.getName());
		JusticeListener.chestPlayers.getIfPresent(player.getName());
	}

	public static int removeAndTake(final Player player) {
		int clicks = KvadratData.takeQuarterClicks(player.getName());
		KvadratData.removePlayer(player);
		player.sendMessage(UtilityMethods.color(
				KvadratSettings.Locale.WE_TOOK_PERCENT_CLICKS.replaceAll("\\(perc\\)", String.valueOf(KvadratSettings.General.PERCENT_FOR_KILL))));
		return clicks;
	}

	public static void giveClicksForKill(final Player player, final String killed, int clicks) {
		KvadratData.giveClicks(player.getName(), clicks);
		player.sendMessage(UtilityMethods.color(
				KvadratSettings.Locale.GIVE_CLICKS_FOR_KILL.replaceAll("\\(clicks\\)", String.valueOf(clicks)).replaceAll("\\(killed\\)", killed)));
	}

	public static void startEvent() {
		going = true;
		big = false;

		if (Bukkit.getOnlinePlayers().size() >= KvadratSettings.BigLocation.MIN_ONLINE) {
			big = true;
			safeZone = KvadratSettings.BigLocation.SAFE_ZONE;
			pvpZone = KvadratSettings.BigLocation.PVP_ZONE;
			chestZone = KvadratSettings.BigLocation.CHEST_ZONE;
		} else {
			safeZone = KvadratSettings.SmallLocation.SAFE_ZONE;
			pvpZone = KvadratSettings.SmallLocation.PVP_ZONE;
			chestZone = KvadratSettings.SmallLocation.CHEST_ZONE;
		}

		playersClicks = new HashMap<>();

		for (Player player : Bukkit.getOnlinePlayers()) {
			for (String row : KvadratSettings.General.START_MESSAGES) {
				if (row.contains("<c>")) {
					TextComponent text = new TextComponent(TextComponent.fromLegacyText(UtilityMethods.color(
							row.substring(0, row.indexOf("<c>"))
					)));
					TextComponent clickable = new TextComponent(TextComponent.fromLegacyText(UtilityMethods.color(
							row.substring(row.indexOf("<c>"),row.indexOf("</c>")).replace("<c>", "")
					)));
					text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kvad"));
					TextComponent end = new TextComponent(TextComponent.fromLegacyText(UtilityMethods.color(
							row.substring(row.indexOf("</c>")).replace("</c>", "")
					)));
					text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							TextComponent.fromLegacyText(UtilityMethods.color(KvadratSettings.Locale.ON_HOVER_KVAD))));

					text.addExtra(clickable);
					text.addExtra(end);

					player.spigot().sendMessage(text);
				} else {
					player.sendMessage(UtilityMethods.color(row));
				}
			}
		}

		KvadratTimer.startEvent();
	}

	public static void sendResults() {

		List<String> format = KvadratSettings.Locale.RESULT_FORMAT;

		List<String> message = new ArrayList<>();

		Map<Integer, Map<String, Integer>> top = getTopClicks(KvadratSettings.General.PRIZES.size());
		for (Integer place : top.keySet()) {
			Map<String, Integer> data = top.get(place);
			String name = new ArrayList<>(data.keySet()).get(0);
			VRubles.addRubles(name, KvadratSettings.General.PRIZES.get(place));

			for (String row : format) {
				if (row.contains("(place_" + place + ")"))
					row = row.replaceAll("\\(place_" + place + "\\)", name);
				if (row.contains("(clicks_" + place + ")"))
					row = row.replaceAll("\\(clicks_" + place + "\\)", String.valueOf(data.get(name)));
				message.add(row);
			}
		}

		if (top.keySet().size() == 0) {
			for (String row : format) {
				if (row.contains("(place_"))
					row = row.replaceAll(row.substring(row.indexOf("\\(place_"), row.indexOf("\\(place_") + 2), "");
				if (row.contains("(clicks_"))
					row = row.replaceAll(row.substring(row.indexOf("\\(clicks_"), row.indexOf("\\(clicks_") + 2), "");
				message.add(row);
			}
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			for (String row : message) {
				player.sendMessage(UtilityMethods.color(row));
			}
		}
	}

	public static void endEvent() {
		sendResults();

		for (Player player : players) {
			KvadratBoard.hide(player);
			teleportOnSpawn(player);
		}

		JusticeListener.safePlayers.invalidateAll();
		JusticeListener.chestPlayers.invalidateAll();

		going = false;
		players = new ArrayList<>();
		safeZone = new HashMap<>();
		pvpZone = new HashMap<>();
		chestZone = new HashMap<>();
		playersClicks = new HashMap<>();
		lastTeleport = -1;
	}
}