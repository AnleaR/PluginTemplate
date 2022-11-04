package su.nexus.kvadrat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.mineacademy.fo.annotation.AutoRegister;
import su.nexus.kvadrat.data.KvadratData;

@AutoRegister
public final class SafeListener implements Listener {

	@EventHandler
	public static void onPlace(final BlockPlaceEvent event) {
		if (KvadratData.inCube(KvadratData.safeZone, event.getBlockPlaced().getLocation()))
			event.setCancelled(true);
	}

	@EventHandler
	public static void onBreak(final BlockBreakEvent event) {
		if (KvadratData.inCube(KvadratData.safeZone, event.getBlock().getLocation()))
			event.setCancelled(true);
	}
}