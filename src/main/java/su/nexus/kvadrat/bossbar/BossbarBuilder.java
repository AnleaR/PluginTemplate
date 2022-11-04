package su.nexus.kvadrat.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import ru.den_abr.commonlib.utility.UtilityMethods;

public class BossbarBuilder {

	private final BossBar bar;

	public BossbarBuilder(BarColor defaultColor, BarStyle defaultStyle) {
		this.bar = Bukkit.createBossBar("", defaultColor, defaultStyle);
	}

	public BossbarBuilder title(String title) {
		bar.setTitle(UtilityMethods.color(title));
		return this;
	}

	public BossBar build() {
		return bar;
	}
}