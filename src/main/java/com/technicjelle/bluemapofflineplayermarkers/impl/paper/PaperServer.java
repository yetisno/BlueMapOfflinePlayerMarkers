package com.technicjelle.bluemapofflineplayermarkers.impl.paper;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.technicjelle.bluemapofflineplayermarkers.common.PlayerData;
import com.technicjelle.bluemapofflineplayermarkers.common.Server;
import com.technicjelle.bluemapofflineplayermarkers.core.Player;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapWorld;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class PaperServer implements Server {
	final JavaPlugin plugin;
	final org.bukkit.Server server;

	public PaperServer(JavaPlugin plugin) {
		this.plugin = plugin;
		this.server = plugin.getServer();
	}

	@Override
	public boolean isPlayerOnline(UUID playerUUID) {
		OfflinePlayer op = server.getOfflinePlayer(playerUUID);
		return op.isOnline();
	}

	@Override
	public Path getConfigFolder() {
		return plugin.getDataFolder().toPath();
	}

	@Override
	public Path getPlayerDataFolder() {
		//I really don't like this way to get the main world, but as far as I can tell, there is no other way
		World bukkitWorld = Bukkit.getWorlds().getFirst();
		Path dimensionFolder = bukkitWorld.getWorldFolder().toPath();

		//This is how BlueMap does it too... https://github.com/BlueMap-Minecraft/BlueMap/blob/c115f26d7b2330b83c368396b4f84c9ce53945ae/implementations/paper/src/main/java/de/bluecolored/bluemap/bukkit/BukkitWorld.java#L55
		Path worldFolder = dimensionFolder.getParent().getParent().getParent();

		Path newPlayerDataFolder = worldFolder.resolve("players").resolve("data");
		if (Files.exists(newPlayerDataFolder)) return newPlayerDataFolder;

		//Pre 26.1 format:
		Path oldPlayerDataFolder = dimensionFolder.resolve("playerdata");
		if (Files.exists(oldPlayerDataFolder)) return oldPlayerDataFolder;

		return Path.of("");
	}

	@Override
	public Optional<Instant> getPlayerLastPlayed(UUID playerUUID) {
		OfflinePlayer op = server.getOfflinePlayer(playerUUID);
		long millisSinceEpoch = op.getLastSeen();
		if (millisSinceEpoch == 0) return Optional.empty();
		return Optional.of(Instant.ofEpochMilli(millisSinceEpoch));
	}

	@Override
	public String getPlayerName(UUID playerUUID) {
		OfflinePlayer op = server.getOfflinePlayer(playerUUID);
		@Nullable String name = op.getName();
		if (name != null) return name;

		PlayerProfile playerProfile = server.createProfile(playerUUID);
		if (playerProfile.complete(false)) {
			name = playerProfile.getName();
			if (name != null && !name.isBlank()) return name;
		}

		try {
			return Server.nameFromMojangAPI(playerUUID);
		} catch (IOException e) {
			//If the player is not found, return the UUID as a string
			return playerUUID.toString();
		}
	}

	@Override
	public BlueMapWorld getBlueMapWorldForPlayer(BlueMapAPI api, Player player) {
		PlayerData playerData = player.getPlayerData();

		//If this is a BukkitPlayer, then we can just call Bukkit's player.getWorld() on it and feed that right into BlueMap.
		if (playerData instanceof BukkitPlayerData) {
			Object bukkitWorld = playerData.getDimension();
			Optional<BlueMapWorld> oBmWorld = api.getWorld(bukkitWorld);
			if (oBmWorld.isPresent()) return oBmWorld.get();
		}

		//It's (likely) an NBT Player
		//So then first, we try to use the WorldUUID from the player data NBT
		// (it seems like this is an exclusive property to Bukkit)
		Optional<UUID> worldUUID = playerData.getWorldUUID();
		if (worldUUID.isPresent()) {
			Optional<BlueMapWorld> oBmWorld = api.getWorld(worldUUID.get());
			if (oBmWorld.isPresent()) return oBmWorld.get();
		}

		//The player data NBT did not have a WorldUUID, so we try to use the dimension
		Object dimension = playerData.getDimension();
		if (dimension instanceof String dimensionString) {
			//Try to get world by name
			{
				@Nullable World world = server.getWorld(dimensionString);
				if (world != null) {
					Optional<BlueMapWorld> oBmWorld = api.getWorld(world);
					if (oBmWorld.isPresent()) return oBmWorld.get();
				}
			}

			//Try to get world by dimension
			for (World world : server.getWorlds()) {
				switch (world.getEnvironment()) {
					case NORMAL:
						if (dimensionString.contains("overworld")) {
							Optional<BlueMapWorld> oBmWorld = api.getWorld(world);
							if (oBmWorld.isPresent()) return oBmWorld.get();
						}
					case NETHER:
						if (dimensionString.contains("the_nether")) {
							Optional<BlueMapWorld> oBmWorld = api.getWorld(world);
							if (oBmWorld.isPresent()) return oBmWorld.get();
						}
					case THE_END:
						if (dimensionString.contains("the_end")) {
							Optional<BlueMapWorld> oBmWorld = api.getWorld(world);
							if (oBmWorld.isPresent()) return oBmWorld.get();
						}
				}
			}
		}

		if (dimension instanceof Integer dimensionInt) {
			for (World world : server.getWorlds()) {
				@SuppressWarnings("deprecation") int worldID = world.getEnvironment().getId();
				if (worldID == dimensionInt) {
					Optional<BlueMapWorld> oBmWorld = api.getWorld(world);
					if (oBmWorld.isPresent()) return oBmWorld.get();
				}
			}
		}

		throw new IllegalArgumentException("Could not find BlueMap world for dimension: " + dimension);
	}

	@Override
	public boolean isPlayerBanned(UUID playerUUID) {
		OfflinePlayer op = server.getOfflinePlayer(playerUUID);
		return op.isBanned();
	}
}
