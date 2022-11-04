package su.nexus.kvadrat.command;

import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import ru.den_abr.commonlib.utility.UtilityMethods;
import su.nexus.kvadrat.KvadratPlugin;
import su.nexus.kvadrat.data.KvadratData;
import su.nexus.kvadrat.data.KvadratPermissions;
import su.nexus.kvadrat.data.KvadratSettings;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@AutoRegister
public final class KvadCommand extends SimpleCommand {

	public KvadCommand() {
		super("kvadrat", Collections.singletonList("kvad"));
		setPermission(null);
	}

	@Override
	protected void onCommand() {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("reload") && sender.hasPermission(KvadratPermissions.Command.RELOAD)) {
				KvadratPlugin.getInstance().reload();
				return;
			}
		}

		if (!KvadratData.isGoing()) {
			getPlayer().sendMessage(UtilityMethods.color(KvadratSettings.Locale.EVENT_NOT_STARTED));
			return;
		}

		KvadratData.addPlayer(getPlayer());
		setCooldown(KvadratSettings.General.COOLDOWN, TimeUnit.SECONDS);
	}
}
