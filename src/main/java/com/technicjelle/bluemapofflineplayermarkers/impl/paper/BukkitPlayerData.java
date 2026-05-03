package com.technicjelle.bluemapofflineplayermarkers.impl.paper;

import com.flowpowered.math.vector.Vector3d;
import com.technicjelle.bluemapofflineplayermarkers.common.PlayerData;
import com.technicjelle.bluemapofflineplayermarkers.core.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class BukkitPlayerData implements PlayerData {
	final Player bukkitPlayer;

	public BukkitPlayerData(Player bukkitPlayer) {
		this.bukkitPlayer = bukkitPlayer;
	}

	@Override
	public GameMode getGameMode() {
		org.bukkit.GameMode bukkitGameMode = bukkitPlayer.getGameMode();
		@SuppressWarnings("deprecation") GameMode gameMode = GameMode.getByValue(bukkitGameMode.getValue());
		return gameMode;

	}

	@Override
	public Vector3d getPosition() {
		Location location = bukkitPlayer.getLocation();
		return new Vector3d(location.getX(), location.getY(), location.getZ());
	}

	@Override
	public Optional<UUID> getWorldUUID() {
		return Optional.of(bukkitPlayer.getWorld().getUID());
	}

	@Override
	public Object getDimension() {
		return bukkitPlayer.getWorld();
	}
}
