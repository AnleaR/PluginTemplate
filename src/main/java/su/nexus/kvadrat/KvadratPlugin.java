package su.nexus.kvadrat;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.mineacademy.fo.plugin.SimplePlugin;
import su.nexus.kvadrat.command.KvadCommand;
import su.nexus.kvadrat.command.TestKvadCommand;
import su.nexus.kvadrat.listener.*;
import su.nexus.kvadrat.scoreboard.KvadratBoard;
import su.nexus.kvadrat.timer.KvadratTimer;

public final class KvadratPlugin extends SimplePlugin {

	@Getter
	public static KvadratPlugin Instance;

	@Override
	protected void onPluginStart() {
	}

	@Override
	protected void onReloadablesStart() {
		Instance = this;
		getDataFolder().mkdirs();

		registerEvents(new FightListener());
		registerEvents(new ClickListener());
		registerEvents(new PlayerListener());
		registerEvents(new SafeListener());
		registerEvents(new JusticeListener());

		registerCommand(new KvadCommand());
		registerCommand(new TestKvadCommand());

		Bukkit.getScheduler().runTaskTimer(KvadratPlugin.getInstance(), KvadratBoard::update, 0L, 20L);

		KvadratTimer.start();
	}

	@Override
	protected void onPluginStop() {
		super.onPluginStop();
	}
}
