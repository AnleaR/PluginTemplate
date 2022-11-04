package su.nexus.kvadrat.menu;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.den_abr.commonlib.utility.UtilityMethods;
import su.nexus.kvadrat.data.KvadratSettings;
import su.nexus.kvadrat.listener.ClickListener;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ClickerMenu implements InventoryHolder {

	@Getter
	public Inventory inventory;
	public Player player;
	public ItemStack clickButton;
	public int clickSlot;

	public ClickerMenu(final Player player) {
		this.player = player;
		this.clickButton = KvadratSettings.Menu.BUTTON.make();
		this.clickSlot = randomSlot(-1);
	}

	public void open() {
		inventory = Bukkit.createInventory(this, InventoryType.DISPENSER, UtilityMethods.color(KvadratSettings.Menu.TITLE));
		inventory.setItem(clickSlot, clickButton);
		player.openInventory(inventory);
		ClickListener.menus.put(player, this);
	}

	public void changeClickSlot() {
		inventory.remove(KvadratSettings.Menu.BUTTON.make());
		clickSlot = randomSlot(clickSlot);
		inventory.setItem(clickSlot, KvadratSettings.Menu.BUTTON.make());
	}

	private int randomSlot(final int previousSlot) {
		int random = new Random().nextInt(9);
		while (random == previousSlot)
			random = new Random().nextInt(9);
		return random;
	}
}
