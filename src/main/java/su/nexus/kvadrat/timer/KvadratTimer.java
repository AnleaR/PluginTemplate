package su.nexus.kvadrat.timer;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.den_abr.commonlib.utility.UtilityMethods;
import su.nexus.kvadrat.KvadratPlugin;
import su.nexus.kvadrat.data.KvadratData;
import su.nexus.kvadrat.data.KvadratSettings;
import su.nexus.kvadrat.listener.JusticeListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class KvadratTimer {

	private static Integer second;

	public static void start() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(KvadratPlugin.getInstance(), () -> {
			if (checkEventTime()) {
				if (Bukkit.getOnlinePlayers().size() >= KvadratSettings.SmallLocation.MIN_ONLINE) {
					KvadratData.startEvent();
				} else {
					for (Player player : Bukkit.getOnlinePlayers()) {
						for (String row : KvadratSettings.Locale.EVENT_NOT_STARTED_MIN_ONLINE) {
							player.sendMessage(UtilityMethods.color(row));
						}
					}
				}
			}
		}, 0L, 60 * 20L);
	}

	public static void startEvent() {

		second = 300;

		BukkitTask timer = Bukkit.getScheduler().runTaskTimerAsynchronously(KvadratPlugin.getInstance(),() -> {
			second--;
			if (KvadratSettings.General.ALERT_SECONDS.contains(second)) {
				for (String row : KvadratSettings.Locale.ALERT_FORMAT) {
					if (row.contains("(time)"))
						row = row.replaceAll("\\(time\\)", UtilityMethods.getTimeInMaxUnit(second*1000));
					for (Player player : KvadratData.players)
						player.sendMessage(UtilityMethods.color(row));
				}
			}
		},0L, 20L);

		BukkitTask bossbar = Bukkit.getScheduler().runTaskTimerAsynchronously(KvadratPlugin.getInstance(),
				KvadratTimer::sendBossbar,0L, KvadratSettings.General.BOSSBAR_CHANGE_TICKS);

		BukkitTask checker = Bukkit.getScheduler().runTaskTimerAsynchronously(KvadratPlugin.getInstance(),
				KvadratTimer::checkZones,0L, KvadratSettings.General.PAYMENT_TICKS);

		Bukkit.getScheduler().runTaskLaterAsynchronously(KvadratPlugin.getInstance(),() -> {
			timer.cancel();
			bossbar.cancel();
			checker.cancel();
			Bukkit.getScheduler().runTask(KvadratPlugin.getInstance(),KvadratData::endEvent);
		},KvadratSettings.General.EVENT_TIME * 20L);
	}

	private static void sendBossbar() {
		for (Player player : KvadratData.players) {
			if (KvadratData.inChestZone(player) || KvadratData.inPvpZone(player)) {
				JusticeListener.sendPvpBossbar(player);
			}
			else
				if (!JusticeListener.inSafePvp(player) && !JusticeListener.inChestPvp(player)) {
					JusticeListener.sendSafeBossbar(player);
				}
		}
	}

	private static void checkZones() {
		for (Player player : KvadratData.players) {
			if (KvadratData.inChestZone(player)) {
				if (JusticeListener.safePlayers.getIfPresent(player.getName()) != null)
					JusticeListener.safePlayers.invalidate(player.getName());
				if (JusticeListener.chestPlayers.getIfPresent(player.getName()) != null)
					JusticeListener.chestPlayers.invalidate(player.getName());
				UtilityMethods.addBalance(player, KvadratSettings.General.MONEY_FOR_CHEST_ZONE);
				player.sendMessage(UtilityMethods.color(KvadratSettings.Locale.ActionBar.GOT_CHEST_ZONE_MONEY
						.replaceAll("\\(sum\\)", String.valueOf(KvadratSettings.General.MONEY_FOR_CHEST_ZONE))));
			}
			else if (KvadratData.inPvpZone(player)) {
				if (JusticeListener.safePlayers.getIfPresent(player.getName()) != null)
					JusticeListener.safePlayers.invalidate(player.getName());
				if (JusticeListener.chestPlayers.getIfPresent(player.getName()) != null)
					JusticeListener.chestPlayers.invalidate(player.getName());
				UtilityMethods.addBalance(player, KvadratSettings.General.MONEY_FOR_PVP_ZONE);
				player.sendMessage(UtilityMethods.color(KvadratSettings.Locale.ActionBar.GOT_PVP_ZONE_MONEY
						.replaceAll("\\(sum\\)", String.valueOf(KvadratSettings.General.MONEY_FOR_PVP_ZONE))));
			}
		}
	}

	public static boolean checkEventTime() {
		long closest = getClosestEventDate();

		Calendar closestDate = new GregorianCalendar();
		closestDate.setTime(new Date(closest));

		return Calendar.getInstance().get(Calendar.MINUTE) == closestDate.get(Calendar.MINUTE);
	}

	private static Long getClosestEventDate() {
		long minimum = -1;
		for (String weekday : KvadratSettings.General.WEEK_DAYS) {
			for (String time : KvadratSettings.General.TIMES) {

				Date date = getEventDate(getWeekday(weekday), time);

				if (isInMinute(date)) {

					if (minimum == -1) {
						minimum = date.getTime();
					} else {
						if (date.getTime() < minimum) {
							minimum = date.getTime();
						}
					}
				}
			}
		}

		return minimum;
	}

	public static boolean isInMinute(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		return Calendar.getInstance().get(Calendar.MINUTE) == calendar.get(Calendar.MINUTE);
	}

	@SneakyThrows
	private static Date getEventDate(Date weekday, String time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Date dayTime = dateFormat.parse(time);

		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(weekday.getTime());
		// MOSCOW
		calendar.add(Calendar.HOUR_OF_DAY, 3);
		calendar.add(Calendar.MILLISECOND, (int) dayTime.getTime());

		return calendar.getTime();
	}

	/**
	 * Returns long time of a day, that is the closest those weekday now.
	 */
	private static Date getWeekday(String rusWeekday) {
		int dayOfWeek = translateWeekDay(rusWeekday);

		Calendar now = Calendar.getInstance();
		int weekday = now.get(Calendar.DAY_OF_WEEK);

		int days = (Calendar.SATURDAY - weekday + dayOfWeek) % 7;
		now.add(Calendar.DAY_OF_YEAR, days);

		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);

		return now.getTime();
	}

	/**
	 * Returns long time of a day time
	 */
	private static long calculateDayTime(String dayTime) {
		int hours = Integer.parseInt(dayTime.substring(0, 2));
		int minutes = Integer.parseInt(dayTime.substring(3, 5));
		return (long) minutes *60*1000 + (long) hours *60*60*1000;
	}

	private static int translateWeekDay(String day) {
		switch (day) {
			case "пн":
				return Calendar.MONDAY;
			case "вт":
				return Calendar.TUESDAY;
			case "ср":
				return Calendar.WEDNESDAY;
			case "чт":
				return Calendar.THURSDAY;
			case "пт":
				return Calendar.FRIDAY;
			case "сб":
				return Calendar.SATURDAY;
			default:
				return Calendar.SUNDAY;
		}
	}
}