package su.nexus.kvadrat.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;
import su.nexus.kvadrat.data.KvadratData;
import su.nexus.kvadrat.data.KvadratSettings;

public class FightListener implements Listener {

	@EventHandler
	public void onFight(final EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			Player player = (Player) event.getEntity();

			if (!KvadratData.players.contains(damager) && !KvadratData.players.contains(player)) {
				return;
			}

			if (KvadratData.inSafeZone(player)) {
				if (!JusticeListener.inSafePvp(player) && !JusticeListener.inChestPvp(player)) {
					event.setCancelled(true);
				} else {
					if (JusticeListener.inSafePvp(damager) || JusticeListener.inChestPvp(damager) || !KvadratData.inSafeZone(damager)) {
						event.setCancelled(false);
						return;
					} else {
						event.setCancelled(true);
					}
				}
				if (event.isCancelled()) {
					return;
				}
			}

			if (KvadratData.inSafeZone(damager)) {
				if (!JusticeListener.inSafePvp(damager) && !JusticeListener.inChestPvp(damager)) {
					event.setCancelled(true);
					return;
				}
			}
		}

		if (event.getDamager() instanceof ProjectileSource && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (!KvadratData.players.contains(player))
				return;

			if (KvadratData.inSafeZone(player))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(final EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (JusticeListener.inSafePvp(player) || JusticeListener.inChestPvp(player)) {
			event.setCancelled(false);
			return;
		}

		if (event.getCause().equals(EntityDamageEvent.DamageCause.POISON) || event.getCause().equals(EntityDamageEvent.DamageCause.MAGIC) ||
				event.getCause().equals(EntityDamageEvent.DamageCause.FALL) || event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) ||
				event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) ||
				event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			if (KvadratData.players.contains(player)) {
				if (KvadratData.inSafeZone(player))
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDeath(final PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (KvadratData.players.contains(player)) {
			int clicks = KvadratData.removeAndTake(player);

			Player killer = player.getKiller();
			if (killer != null)
				if (KvadratData.players.contains(killer))
					KvadratData.giveClicksForKill(killer, player.getName(), clicks);
		}
	}

	@EventHandler
	public void onBow(final EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();

		if (JusticeListener.inSafePvp(player) || JusticeListener.inChestPvp(player))
			return;

		if (KvadratData.players.contains(player))
			if (KvadratData.inSafeZone(player))
				event.setCancelled(true);
	}
}
