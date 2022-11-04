package su.nexus.kvadrat.data;

import java.util.*;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.settings.ConfigSection;
import org.mineacademy.fo.settings.SimpleSettings;
import ru.den_abr.commonlib.utility.UtilityMethods;

@SuppressWarnings("unused")
public final class KvadratSettings extends SimpleSettings {

	@Override
	protected int getConfigVersion() {
		return 1;
	}

	@Override
	protected List<String> getUncommentedSections() {
		return new ArrayList<>();
	}

	public static class General {

		public static List<String> WEEK_DAYS;
		public static List<String> TIMES;
		public static Integer EVENT_TIME;
		public static List<String> START_MESSAGES;
		public static Integer PAYMENT_TICKS;
		public static Integer BOSSBAR_CHANGE_TICKS;
		public static Integer MONEY_FOR_PVP_ZONE;
		public static Integer MONEY_FOR_CHEST_ZONE;
		public static Integer PERCENT_FOR_KILL;
		public static Integer PVP_SECONDS_AFTER_SAFE_ZONE;
		public static Integer PVP_SECONDS_AFTER_CLICKS;
		public static Integer COOLDOWN;
		public static List<Integer> ALERT_SECONDS;
		public static Map<Integer, Integer> PRIZES;
		public static List<String> PVP_ALLOWED_CMDS;

		private static void init() {

			setPathPrefix("General");

			WEEK_DAYS = getStringList("WeekDays");
			TIMES = getStringList("Times");
			EVENT_TIME = getInteger("EventTime");
			START_MESSAGES = getStringList("StartMessages");
			PAYMENT_TICKS = getInteger("PaymentTicks");
			BOSSBAR_CHANGE_TICKS = getInteger("BossbarChangeTicks");
			MONEY_FOR_PVP_ZONE = getInteger("MoneyForPvpZone");
			COOLDOWN = getInteger("KvadCooldown");
			MONEY_FOR_CHEST_ZONE = getInteger("MoneyForChestZone");
			PERCENT_FOR_KILL = getInteger("PercentForKill");
			PVP_SECONDS_AFTER_SAFE_ZONE = getInteger("PvpSecondsAfterSafeZone");
			PVP_SECONDS_AFTER_CLICKS = getInteger("PvpSecondsAfterClicks");
			ALERT_SECONDS = getList("AlertSeconds", Integer.class);
			PVP_ALLOWED_CMDS = getStringList("PvpAllowedCmds");

			initPrizes();
		}

		private static void initPrizes() {
			PRIZES = new HashMap<>();
			ConfigSection keys = (ConfigSection) getObject("Prizes");
			for (final String key : keys.getKeys(false)) {
				Integer place = Integer.parseInt(key);
				Integer sum = getInteger("Prizes." + key);
				PRIZES.put(place,sum);
			}
		}
	}

	public static class SmallLocation {

		public static Integer MIN_ONLINE;
		public static World WORLD;
		public static Location DISPENSER_LOCATION;
		public static List<Location> SPAWNS;
		public static Map<Location, Location> SAFE_ZONE;
		public static Map<Location, Location> PVP_ZONE;
		public static Map<Location, Location> CHEST_ZONE;

		private static void init() {

			setPathPrefix("SmallLocation");

			MIN_ONLINE = getInteger("MinOnline");
			WORLD = Bukkit.getWorld(getString("World"));

			initSpawns();
			initDispenser();

			initChest();
			initPvp();
			initSafe();

			KvadratData.safeZone = SAFE_ZONE;
			KvadratData.pvpZone = PVP_ZONE;
			KvadratData.chestZone = CHEST_ZONE;
		}

		private static void initSpawns() {
			setPathPrefix("SmallLocation");

			SPAWNS = new ArrayList<>();
			ConfigSection keys = (ConfigSection) getObject("Spawns");
			for (final String key : keys.getKeys(false)) {
				int number = Integer.parseInt(key);
				setPathPrefix("SmallLocation.Spawns." + number);
				Location location = new Location(WORLD, getDouble("X"), getDouble("Y"), getDouble("Z"), (float) getDouble("Yaw"), (float) getDouble("Pitch"));
				SPAWNS.add(location);
			}
		}

		private static void initDispenser() {
			setPathPrefix("SmallLocation.Dispenser");
			DISPENSER_LOCATION = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));
		}

		private static void initChest() {
			setPathPrefix("SmallLocation.ChestZoneA");
			Location firstLoc = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));
			setPathPrefix("SmallLocation.ChestZoneB");
			Location secLoc = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));

			Location minLoc = new Location(firstLoc.getWorld(),
					Math.min(firstLoc.getBlockX(), secLoc.getBlockX()),
					Math.min(firstLoc.getBlockY(), secLoc.getBlockY()),
					Math.min(firstLoc.getBlockZ(), secLoc.getBlockZ()));

			Location maxLoc = new Location(firstLoc.getWorld(),
					Math.max(firstLoc.getBlockX(), secLoc.getBlockX()),
					Math.max(firstLoc.getBlockY(), secLoc.getBlockY()),
					Math.max(firstLoc.getBlockZ(), secLoc.getBlockZ()));

			CHEST_ZONE = Collections.singletonMap(minLoc, maxLoc);
		}

		private static void initPvp() {
			setPathPrefix("SmallLocation.PvpZoneA");
			Location firstLoc = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));
			setPathPrefix("SmallLocation.PvpZoneB");
			Location secLoc = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));

			Location minLoc = new Location(firstLoc.getWorld(),
					Math.min(firstLoc.getBlockX(), secLoc.getBlockX()),
					Math.min(firstLoc.getBlockY(), secLoc.getBlockY()),
					Math.min(firstLoc.getBlockZ(), secLoc.getBlockZ()));

			Location maxLoc = new Location(firstLoc.getWorld(),
					Math.max(firstLoc.getBlockX(), secLoc.getBlockX()),
					Math.max(firstLoc.getBlockY(), secLoc.getBlockY()),
					Math.max(firstLoc.getBlockZ(), secLoc.getBlockZ()));

			PVP_ZONE = Collections.singletonMap(minLoc, maxLoc);
		}

		private static void initSafe() {
			setPathPrefix("SmallLocation.SafeZoneA");
			Location firstLoc = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));
			setPathPrefix("SmallLocation.SafeZoneB");
			Location secLoc = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));

			Location minLoc = new Location(firstLoc.getWorld(),
					Math.min(firstLoc.getBlockX(), secLoc.getBlockX()),
					Math.min(firstLoc.getBlockY(), secLoc.getBlockY()),
					Math.min(firstLoc.getBlockZ(), secLoc.getBlockZ()));

			Location maxLoc = new Location(firstLoc.getWorld(),
					Math.max(firstLoc.getBlockX(), secLoc.getBlockX()),
					Math.max(firstLoc.getBlockY(), secLoc.getBlockY()),
					Math.max(firstLoc.getBlockZ(), secLoc.getBlockZ()));

			SAFE_ZONE = Collections.singletonMap(minLoc, maxLoc);
		}
	}

	public static class BigLocation {

		public static Integer MIN_ONLINE;
		public static World WORLD;
		public static Location DISPENSER_LOCATION;
		public static List<Location> SPAWNS;
		public static Map<Location, Location> SAFE_ZONE;
		public static Map<Location, Location> PVP_ZONE;
		public static Map<Location, Location> CHEST_ZONE;

		private static void init() {

			setPathPrefix("BigLocation");

			MIN_ONLINE = getInteger("MinOnline");
			WORLD = Bukkit.getWorld(getString("World"));

			initSpawns();
			initDispenser();

			initChest();
			initPvp();
			initSafe();
		}

		private static void initSpawns() {
			setPathPrefix("BigLocation");

			SPAWNS = new ArrayList<>();
			ConfigSection keys = (ConfigSection) getObject("Spawns");
			for (final String key : keys.getKeys(false)) {
				int number = Integer.parseInt(key);
				setPathPrefix("BigLocation.Spawns." + number);
				Location location = new Location(WORLD, getDouble("X"), getDouble("Y"), getDouble("Z"), (float) getDouble("Yaw"), (float) getDouble("Pitch"));
				SPAWNS.add(location);
			}
		}

		private static void initDispenser() {
			setPathPrefix("BigLocation.Dispenser");
			DISPENSER_LOCATION = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));
		}

		private static void initChest() {
			setPathPrefix("BigLocation.ChestZoneA");
			Location firstLoc = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));
			setPathPrefix("BigLocation.ChestZoneB");
			Location secLoc = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));

			Location minLoc = new Location(firstLoc.getWorld(),
					Math.min(firstLoc.getBlockX(), secLoc.getBlockX()),
					Math.min(firstLoc.getBlockY(), secLoc.getBlockY()),
					Math.min(firstLoc.getBlockZ(), secLoc.getBlockZ()));

			Location maxLoc = new Location(firstLoc.getWorld(),
					Math.max(firstLoc.getBlockX(), secLoc.getBlockX()),
					Math.max(firstLoc.getBlockY(), secLoc.getBlockY()),
					Math.max(firstLoc.getBlockZ(), secLoc.getBlockZ()));

			CHEST_ZONE = Collections.singletonMap(minLoc, maxLoc);
		}

		private static void initPvp() {
			setPathPrefix("BigLocation.PvpZoneA");
			Location firstLoc = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));
			setPathPrefix("BigLocation.PvpZoneB");
			Location secLoc = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));

			Location minLoc = new Location(firstLoc.getWorld(),
					Math.min(firstLoc.getBlockX(), secLoc.getBlockX()),
					Math.min(firstLoc.getBlockY(), secLoc.getBlockY()),
					Math.min(firstLoc.getBlockZ(), secLoc.getBlockZ()));

			Location maxLoc = new Location(firstLoc.getWorld(),
					Math.max(firstLoc.getBlockX(), secLoc.getBlockX()),
					Math.max(firstLoc.getBlockY(), secLoc.getBlockY()),
					Math.max(firstLoc.getBlockZ(), secLoc.getBlockZ()));

			PVP_ZONE = Collections.singletonMap(minLoc, maxLoc);
		}

		private static void initSafe() {
			setPathPrefix("BigLocation.SafeZoneA");
			Location firstLoc = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));
			setPathPrefix("BigLocation.SafeZoneB");
			Location secLoc = new Location(WORLD, getInteger("X"), getInteger("Y"), getInteger("Z"));

			Location minLoc = new Location(firstLoc.getWorld(),
					Math.min(firstLoc.getBlockX(), secLoc.getBlockX()),
					Math.min(firstLoc.getBlockY(), secLoc.getBlockY()),
					Math.min(firstLoc.getBlockZ(), secLoc.getBlockZ()));

			Location maxLoc = new Location(firstLoc.getWorld(),
					Math.max(firstLoc.getBlockX(), secLoc.getBlockX()),
					Math.max(firstLoc.getBlockY(), secLoc.getBlockY()),
					Math.max(firstLoc.getBlockZ(), secLoc.getBlockZ()));

			SAFE_ZONE = Collections.singletonMap(minLoc, maxLoc);
		}
	}

	public static class Spawn {

		public static Location SPAWN;
		public static String SPAWN_WORLD;
		public static Double SPAWN_X;
		public static Double SPAWN_Y;
		public static Double SPAWN_Z;
		public static Float SPAWN_YAW;
		public static Float SPAWN_PITCH;

		private static void init() {

			setPathPrefix("Spawn");

			SPAWN_WORLD = getString("World");
			SPAWN_X = getDouble("X");
			SPAWN_Y = getDouble("Y");
			SPAWN_Z = getDouble("Z");
			SPAWN_YAW = (float) getDouble("Yaw");
			SPAWN_PITCH = (float) getDouble("Pitch");

			SPAWN = new Location(Bukkit.getWorld(SPAWN_WORLD), SPAWN_X, SPAWN_Y, SPAWN_Z, SPAWN_YAW, SPAWN_PITCH);
		}
	}

	public static class Menu {

		public static String TITLE;
		public static ItemCreator BUTTON;

		private static void init() {

			setPathPrefix("Menu");

			TITLE = getString("Title");

			setPathPrefix("Menu.Button");
			BUTTON = ItemCreator.of(getMaterial("Material"))
					.name(UtilityMethods.color(getString("Name")))
					.clearLore()
					.lore(UtilityMethods.color(getStringList("Lore")));
		}
	}

	public static class Scoreboard {

		public static String TITLE;
		public static String ROW_FORMAT;
		public static Integer PLACES;

		private static void init() {

			setPathPrefix("Scoreboard");

			TITLE = getString("Title");
			ROW_FORMAT = getString("RowFormat");
			PLACES = getInteger("Places");
		}
	}

	public static class Bossbar {

		public static String SAFE_ZONE_TITLE;
		public static BarColor SAFE_ZONE_COLOR;
		public static BarStyle SAFE_ZONE_STYLE;

		public static String PVP_ZONE_TITLE;
		public static BarColor PVP_ZONE_COLOR;
		public static BarStyle PVP_ZONE_STYLE;

		public static String TIMER_TITLE;
		public static BarColor TIMER_COLOR;
		public static BarStyle TIMER_STYLE;

		private static void init() {
			setPathPrefix("Bossbar.SafeZone");

			SAFE_ZONE_TITLE = getString("Title");
			SAFE_ZONE_COLOR = BarColor.valueOf(getString("Color"));
			SAFE_ZONE_STYLE = BarStyle.valueOf(getString("Style"));

			setPathPrefix("Bossbar.PvpZone");

			PVP_ZONE_TITLE = getString("Title");
			PVP_ZONE_COLOR = BarColor.valueOf(getString("Color"));
			PVP_ZONE_STYLE = BarStyle.valueOf(getString("Style"));

			setPathPrefix("Bossbar.Timer");

			TIMER_TITLE = getString("Title");
			TIMER_COLOR = BarColor.valueOf(getString("Color"));
			TIMER_STYLE = BarStyle.valueOf(getString("Style"));
		}
	}

	public static class Locale {

		public static List<String> EVENT_NOT_STARTED_MIN_ONLINE;
		public static String EVENT_NOT_STARTED;
		public static String NOT_IN_CHEST_ZONE;
		public static String NOT_CLICK_ALL;
		public static String ON_HOVER_KVAD;
		public static String WE_TOOK_PERCENT_CLICKS;
		public static String GIVE_CLICKS_FOR_KILL;
		public static List<String> ALERT_FORMAT;
		public static List<String> RESULT_FORMAT;
		public static String PVP_BLOCK_COMMAND;

		private static void init() {

			setPathPrefix("KvadratLocale");

			EVENT_NOT_STARTED_MIN_ONLINE = getStringList("EVENT_NOT_STARTED_MIN_ONLINE");
			EVENT_NOT_STARTED = getString("EVENT_NOT_STARTED");
			NOT_IN_CHEST_ZONE = getString("NOT_IN_CHEST_ZONE");
			NOT_CLICK_ALL = getString("NOT_CLICK_ALL");
			ON_HOVER_KVAD = getString("ON_HOVER_KVAD");
			WE_TOOK_PERCENT_CLICKS = getString("WE_TOOK_PERCENT_CLICKS");
			GIVE_CLICKS_FOR_KILL = getString("GIVE_CLICKS_FOR_KILL");
			ALERT_FORMAT = getStringList("ALERT_FORMAT");
			RESULT_FORMAT = getStringList("RESULT_FORMAT");
			PVP_BLOCK_COMMAND = getString("PVP_BLOCK_COMMAND");
		}

		public static class ActionBar {

			public static String GOT_PVP_ZONE_MONEY;
			public static String GOT_CHEST_ZONE_MONEY;
			public static String IN_SAFE_ZONE;

			private static void init() {

				setPathPrefix("KvadratLocale.ActionBar");

				GOT_PVP_ZONE_MONEY = getString("GOT_PVP_ZONE_MONEY");
				GOT_CHEST_ZONE_MONEY = getString("GOT_CHEST_ZONE_MONEY");
				IN_SAFE_ZONE = getString("IN_SAFE_ZONE");
			}
		}
	}

	private static void init() {
		setPathPrefix(null);
	}
}
