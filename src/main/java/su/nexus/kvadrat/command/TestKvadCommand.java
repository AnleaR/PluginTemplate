package su.nexus.kvadrat.command;

import org.mineacademy.fo.command.SimpleCommand;
import ru.den_abr.commonlib.utility.UtilityMethods;
import su.nexus.kvadrat.data.KvadratData;
import su.nexus.kvadrat.data.KvadratPermissions;

public class TestKvadCommand extends SimpleCommand {

	public TestKvadCommand() {
		super("testkvad");
	}

	@Override
	protected void onCommand() {
		getPlayer().sendMessage(UtilityMethods.color("&aНачинаем &9&lКвадрат"));

		if (getPlayer().hasPermission(KvadratPermissions.Command.TEST)) {
			KvadratData.startEvent();
		}
	}
}
