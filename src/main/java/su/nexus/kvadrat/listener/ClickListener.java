package su.nexus.kvadrat.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.remain.CompParticle;
import ru.den_abr.commonlib.utility.UtilityMethods;
import su.nexus.kvadrat.data.KvadratData;
import su.nexus.kvadrat.data.KvadratSettings;
import su.nexus.kvadrat.menu.ClickerMenu;

import java.util.HashMap;
import java.util.Map;

public class ClickListener implements Listener {

	public static Map<Player, ClickerMenu> menus;

	static {
		menus = new HashMap<>();
	}

	@EventHandler
	public void onClick(final InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		Player player = (Player) event.getWhoClicked();

		if (event.getClickedInventory() != null) {
			if (event.getClickedInventory().getHolder() instanceof ClickerMenu) {

				ClickerMenu clickerMenu = menus.get(player);
				if (!KvadratData.inChestZone(player)) {
					player.sendMessage(UtilityMethods.color(KvadratSettings.Locale.NOT_IN_CHEST_ZONE));
					event.setCancelled(true);
					return;
				}

				if (event.getCurrentItem() == null) {
					player.sendMessage(UtilityMethods.color(KvadratSettings.Locale.NOT_CLICK_ALL));
					event.setCancelled(true);
					player.closeInventory();
					return;
				}

				if (event.getCurrentItem().getType().equals(Material.AIR)) {
					player.sendMessage(UtilityMethods.color(KvadratSettings.Locale.NOT_CLICK_ALL));
					event.setCancelled(true);
					player.closeInventory();
					return;
				}

				if (event.getCurrentItem().equals(KvadratSettings.Menu.BUTTON.make())) {
					clickerMenu.changeClickSlot();
					KvadratData.addClick(player);
					if (!JusticeListener.clickedInChest.contains(player))
						JusticeListener.clickedInChest.add(player);

					Location location = player.getLocation();
					location.setY(location.getY() + 2);
					CompParticle.VILLAGER_HAPPY.spawn(location);
				}

				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onMenuClose(final InventoryCloseEvent event) {
		if (KvadratData.players.contains(event.getPlayer()) && menus.containsKey(event.getPlayer()))
			menus.remove(event.getPlayer());
	}
}