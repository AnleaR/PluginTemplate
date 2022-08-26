package su.nexus.template;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.plugin.SimplePlugin;

/**
 * PluginTemplate is a simple template you can use every time you make
 * a new plugin. This will save you time because you no longer have to
 * recreate the same skeleton and features each time.
 *
 * It uses Foundation for fast and efficient development process.
 */
public final class PluginTemplate extends SimplePlugin {

	@Getter @Setter
	public static PluginTemplate Instance;

	/**
	* Automatically perform login ONCE when the plugin starts.
	*/
	@Override
	protected void onPluginStart() {
	}

	/**
	 * Automatically perform login when the plugin starts and each time it is reloaded.
	 */
	@Override
	protected void onReloadablesStart() {

		if (Bukkit.getPluginManager().getPlugin("NexusLib") == null) {
			getLogger().severe("Nexus Library is not enabled. Without it, no plugins would work.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		Instance = this;
	}
}
