package su.nexus.kvadrat.bossbar;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import su.nexus.kvadrat.data.KvadratData;
import su.nexus.kvadrat.listener.JusticeListener;

public class BossbarCountdown extends BukkitRunnable {

	private final BossBar bar;
	private final Player player;
	private final String title;
	private final Long startTime;
	private Long endTime;
	private Integer seconds;

	public BossbarCountdown(BossBar b, Player player, String title, int seconds) {
		this.bar = b;
		this.player = player;
		this.title = title;
		this.startTime = JusticeListener.getPvpTime(player);
		if (startTime == null)
			return;
		this.endTime = startTime + seconds * 1000L;
		this.seconds = seconds;
	}

	@Override
	public void run() {
		if (KvadratData.players.contains(player)) {

			if (endTime < System.currentTimeMillis()) {
				bar.removePlayer(player);
				this.cancel();
				return;
			}

			Long pvpTime = JusticeListener.getPvpTime(player);
			if (pvpTime != null) {
				if (!startTime.equals(pvpTime)) {
					bar.removePlayer(player);
					this.cancel();
					return;
				}
			} else {
				bar.removePlayer(player);
				this.cancel();
				return;
			}

			Integer secondsLast = (int) Math.ceil((endTime - System.currentTimeMillis()) / 1000) + 1;
			bar.setTitle(title.replaceAll("\\(sec\\)", String.valueOf(secondsLast)));
			bar.setProgress(secondsLast.doubleValue() / seconds.doubleValue());

		} else {
			bar.removePlayer(player);
			this.cancel();
		}
	}
}