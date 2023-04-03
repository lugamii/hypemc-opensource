package br.com.adlerlopes.bypiramid.hungergames.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.structures.types.Feast;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.GamerMode;
import br.com.weavenmc.commons.bukkit.api.admin.AdminMode;
import br.com.weavenmc.commons.bukkit.api.vanish.VanishAPI;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.bukkit.util.EntityUtils;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.permission.Group;

public class ModeratorCommand implements CommandClass {

	@Command(name = "revive", aliases = { "reviver" }, groupToUse = Group.YOUTUBERPLUS)
	public void revive(BukkitCommandSender commandSender, String label, String[] args) {
		if (args.length != 1) {
			commandSender.sendMessage("§c§lREVIVE §fUse: /revive <player>");
			return;
		}

		Player player = Bukkit.getPlayer(args[0]);
		if (player == null) {
			commandSender.sendMessage("§c§lREVIVE §fO player está §4§lOFFLINE");
			return;
		}
		Gamer g = getManager().getGamerManager().getGamer(player);

		if (g.isAlive()) {
			commandSender.sendMessage("§c§lREVIVE §fO player §4§L" + player.getName() + "§f já está vivo!");
			return;
		}

		if (AdminMode.getInstance().isAdmin(player)) {
			AdminMode.getInstance().setPlayer(player);
		}

		g.setMode(GamerMode.ALIVE);

		VanishAPI.getInstance().updateVanishToPlayer(player);
		for (Player o : Bukkit.getOnlinePlayers()) {
			o.showPlayer(player);
		}

		player.setGameMode(GameMode.SURVIVAL);
		player.setAllowFlight(false);
		player.closeInventory();
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.getInventory().addItem(new ItemStack(Material.COMPASS));

		getManager().getGamerManager().respawnPlayer(g);

		commandSender.sendMessage("§c§lREVIVE §fVocê reviveu o player §4§L" + player.getName());
	}

	@Command(name = "dano", aliases = { "damage" }, groupToUse = Group.YOUTUBERPLUS)
	public void damage(BukkitCommandSender sender, String label, String[] args) {
		getManager().getGameManager().getDamage().setActive(!getManager().getGameManager().getDamage().isActive());

		Bukkit.getServer()
				.broadcastMessage("§7Dano agora: " + getColor(getManager().getGameManager().getDamage().isActive()));
	}

	@Command(name = "pvp", aliases = { "pvp" }, groupToUse = Group.YOUTUBERPLUS)
	public void pvp(BukkitCommandSender sender, String label, String[] args) {
		getManager().getGameManager().getPvP().setActive(!getManager().getGameManager().getPvP().isActive());

		Bukkit.getServer()
				.broadcastMessage("§7PvP agora: " + getColor(getManager().getGameManager().getPvP().isActive()));
	}

	@Command(name = "build", aliases = { "togglebuild" }, groupToUse = Group.YOUTUBERPLUS)
	public void build(BukkitCommandSender commandSender, String label, String[] args) {
		getManager().getGameManager().getBuild().setActive(!getManager().getGameManager().getBuild().isActive());

		Bukkit.getServer()
				.broadcastMessage("§7Build agora: " + getColor(getManager().getGameManager().getBuild().isActive()));
	}

	public String getColor(boolean bool) {
		return bool ? "§aativado" : "§cdesativado";
	}

	@Command(name = "time", aliases = { "tempo" }, groupToUse = Group.YOUTUBERPLUS)
	public void time(BukkitCommandSender commandSender, String label, String[] args) {
		if (args.length != 1) {
			commandSender.sendMessage("§3§lTEMPO §fUse: /tempo <segundos/minutos>");
			return;
		}

		long time;
		try {
			time = getManager().getUtils().parseDateDiff(args[0], true);
		} catch (Exception e) {
			commandSender.sendMessage("§3§lTEMPO §fA sua sintaxe está incorreta!");
			return;
		}

		time = (time - System.currentTimeMillis()) / 1000;

		getManager().getGameManager().setGameTime((int) time);
		commandSender.sendMessage("§3§lTEMPO §fVocê alterou o tempo do §e§lTORNEIO§f para "
				+ getManager().getUtils().formatTime((int) time));
	}

	@Command(name = "start", aliases = { "iniciar" }, groupToUse = Group.YOUTUBERPLUS)
	public void start(BukkitCommandSender commandSender, String label, String[] args) {
		if (!getManager().getGameManager().isPreGame()) {
			commandSender.sendMessage("§a§lSTART §fVocê não pode §a§lINICIAR§f a partida depois que a mesma iniciou.");
		} else {
			getManager().getGameManager().getTimer().startGame();
			commandSender.sendMessage("§a§lSTART §fVocê §a§lINICIOU§f a §2§lPARTIDA!");
		}
	}

	@Command(name = "forcefeast", groupToUse = Group.YOUTUBERPLUS)
	public void forcefeast(BukkitCommandSender commandSender, String label, String[] args) {
		if (getManager().getGameManager().isPreGame()) {
			commandSender
					.sendMessage("§e§lFORCEFEAST §fÉ necessário que a partida esteja em jogo para spawnar o feast!");
		} else {
			Feast feast = new Feast(getManager(), 50);
			feast.forceFeast();
			commandSender.sendMessage("§e§lFORCEFEAST §fVocê criou um novo §6§lFEAST");
		}
	}

	@Command(name = "cleardrops", groupToUse = Group.YOUTUBERPLUS)
	public void cleardrops(BukkitCommandSender commandSender, String label, String[] args) {
		commandSender.sendMessage("§3§lCLEARDROPS§f Você limpou §b§l" + EntityUtils.clearDrops() + " DROPS§f do chão!");
	}

	@SuppressWarnings("deprecation")
	@Command(name = "arena", aliases = { "createarena" }, groupToUse = Group.YOUTUBERPLUS)
	public void arena(BukkitCommandSender commandSender, String label, String[] args) {
		if (commandSender.isPlayer()) {
			Player p = commandSender.getPlayer();
			if (args.length != 3) {
				commandSender.sendMessage("§d§lARENA §fUse: /createarena <raio> <bloco> <altura>");
				return;

			} else {
				if (!isInteger(args[0])) {
					p.sendMessage("§d§lARENA §fO raio precisa ser um número!");
					return;
				}

				if (!isInteger(args[1])) {
					p.sendMessage("§d§lARENA §fO bloco precisa ser um número (id)!");
					return;
				}

				if (!isInteger(args[2])) {
					p.sendMessage("§d§lARENA §fA altura precisa ser um número!");
					return;
				}

				if (Integer.valueOf(args[0]) > 100) {
					p.sendMessage("§d§lARENA §fO raio não pode ser maior que 100 blocos!");
					return;
				}

				if (Integer.valueOf(args[2]) > 100) {
					p.sendMessage("§d§lARENA §fA altura não pode ser maior que 100 blocos!");
					return;
				}

				Integer size = Integer.valueOf(args[0]);
				Integer block = Integer.valueOf(args[1]);
				Integer y = Integer.valueOf(args[2]);
				generateArena(p.getLocation(), size, y, Material.getMaterial(block).getId());
				p.sendMessage("§d§lARENA §fVocê gerou uma arena com o tamanho de §d§l" + size
						+ "§f blocos e com uma altura de §d§l" + y + "§f com o bloco §d§l"
						+ Material.getMaterial(block).name().toUpperCase() + "§f!");
			}
		} else {
			commandSender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game!");
		}
	}

	@SuppressWarnings("deprecation")
	public static void generateArena(Location location, int size, int height, int blockId) {
		Material type = Material.getMaterial(blockId);
		for (int y = 0; y <= height; y++) {
			if (y == 0) {
				for (int x = -size; x <= size; x++) {
					for (int z = -size; z <= size; z++) {
						Location loc = location.clone().add(x, y, z);
						getManager().getBO2().setBlockFast(location.getWorld(), loc.getBlockX(), loc.getBlockY(),
								loc.getBlockZ(), type.getId(), (byte) 0);
					}
				}
			} else {
				for (int x = -size; x <= size; x++) {
					Location loc = location.clone().add(x, y, size);
					Location loc2 = location.clone().add(x, y, -size);
					getManager().getBO2().setBlockFast(location.getWorld(), loc.getBlockX(), loc.getBlockY(),
							loc.getBlockZ(), type.getId(), (byte) 0);
					getManager().getBO2().setBlockFast(location.getWorld(), loc2.getBlockX(), loc2.getBlockY(),
							loc2.getBlockZ(), type.getId(), (byte) 0);
				}
				for (int z = -size; z <= size; z++) {
					Location loc = location.clone().add(size, y, z);
					Location loc2 = location.clone().add(-size, y, z);
					getManager().getBO2().setBlockFast(location.getWorld(), loc.getBlockX(), loc.getBlockY(),
							loc.getBlockZ(), type.getId(), (byte) 0);
					getManager().getBO2().setBlockFast(location.getWorld(), loc2.getBlockX(), loc2.getBlockY(),
							loc2.getBlockZ(), type.getId(), (byte) 0);
				}
			}
		}
	}

	public boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static Manager getManager() {
		return HungerGames.getManager();
	}
}
