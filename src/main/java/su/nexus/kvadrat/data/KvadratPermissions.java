package su.nexus.kvadrat.data;

import org.mineacademy.fo.command.annotation.Permission;
import org.mineacademy.fo.command.annotation.PermissionGroup;

public final class KvadratPermissions {

	@PermissionGroup("Execute main plugin command for /kvad.")
	public static final class Command {

		@Permission("Test for starting /kvad")
		public static final String TEST = "kvadrat.test";

		@Permission("Test for starting /kvad reload")
		public static final String RELOAD = "kvadrat.admin";

	}
}
