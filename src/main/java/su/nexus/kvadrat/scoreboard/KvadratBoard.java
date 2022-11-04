package su.nexus.kvadrat.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.mineacademy.fo.model.SimpleScoreboard;
import ru.den_abr.commonlib.utility.UtilityMethods;
import su.nexus.kvadrat.KvadratPlugin;
import su.nexus.kvadrat.data.KvadratData;
import su.nexus.kvadrat.data.KvadratSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KvadratBoard {

	public static Map<Player, KvadratBoard> players;
	private final Player player;

	static {
		players = new HashMap<>();
	}

	public KvadratBoard(Player player) {
		this.player = player;
		refill();
		players.put(player, this);
	}

	public static void update() {
		players.values().forEach(KvadratBoard::refill);
	}

	public static void hide(Player player) {
		players.remove(player);
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}

	private void refill() {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("main", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(UtilityMethods.color(KvadratSettings.Scoreboard.TITLE));

		Map<Integer, Map<String, Integer>> top = KvadratData.getTopClicks(KvadratSettings.Scoreboard.PLACES);

		if (!top.isEmpty()) {

			for (int place = 1; place <= Math.min(top.size(), KvadratSettings.Scoreboard.PLACES); place++) {

				Map<String, Integer> data = top.get(place);
				if (data == null || data.isEmpty())
					break;

				String name = new ArrayList<>(data.keySet()).get(0);
				String row = KvadratSettings.Scoreboard.ROW_FORMAT;
				if (row.contains("(place)"))
					row = row.replaceAll("\\(place\\)", String.valueOf(place));
				if (row.contains("(player)"))
					row = row.replaceAll("\\(player\\)", name);
				if (row.contains("(clicks)"))
					row = row.replaceAll("\\(clicks\\)", String.valueOf(data.get(name)));

				Score score = objective.getScore(UtilityMethods.color(row));
				score.setScore(KvadratSettings.Scoreboard.PLACES + 2 - place);
			}
		}

		int place = KvadratData.getPlace(player.getName());
		if (place > KvadratSettings.Scoreboard.PLACES) {
			String row = KvadratSettings.Scoreboard.ROW_FORMAT;
			if (row.contains("(place)"))
				row = row.replaceAll("\\(place\\)", String.valueOf(place));
			if (row.contains("(player)"))
				row = row.replaceAll("\\(player\\)", player.getName());
			if (row.contains("(clicks)"))
				row = row.replaceAll("\\(clicks\\)", String.valueOf(KvadratData.getClicks(player.getName())));

			Score score = objective.getScore(UtilityMethods.color(row));
			score.setScore(1);
		}

		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		player.setScoreboard(scoreboard);
	}
}