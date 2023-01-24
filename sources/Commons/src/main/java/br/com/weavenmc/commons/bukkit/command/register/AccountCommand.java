package br.com.weavenmc.commons.bukkit.command.register;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.AccountMenu;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.data.player.category.DataCategory;
import br.com.weavenmc.commons.core.data.player.type.DataType;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.core.profile.Profile;

public class AccountCommand implements CommandClass {

	@Command(name = "account", aliases = { "acc", "conta", "status", "stats" }, runAsync = true)
	public void account(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer opener = BukkitPlayer.getPlayer(p.getUniqueId());			
			if (opener.hasGroupPermission(Group.TRIAL)) {
			if (args.length == 1) {
				Profile profile = WeavenMC.getProfileCommon().tryPremium(args[0]);
				BukkitPlayer weavenPlayer = null;
				Player player = Bukkit.getPlayer(args[0]);

				if (profile == null) {
					profile = WeavenMC.getProfileCommon().tryCached(args[0]);
					if (profile == null) {
						profile = WeavenMC.getProfileCommon().tryCracked(args[0]);
					}
				}

				if (player != null)
					weavenPlayer = BukkitPlayer.getPlayer(player.getUniqueId());
				else {
					weavenPlayer = new BukkitPlayer(profile.getId(), profile.getName());

				}
				weavenPlayer.load(DataCategory.ACCOUNT);

				AccountMenu.open(p,  weavenPlayer);
				opener = null;
				p = null;
			} else {

				AccountMenu.open(p, opener);	
			}
			} else {

				AccountMenu.open(p, opener);
			}

		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}

	@Command(name = "mudarsenha", aliases = { "changepass", "changepassword" })
	public void changepass(BukkitCommandSender sender, String label, String[] args) {
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			BukkitPlayer bP = BukkitPlayer.getPlayer(p.getUniqueId());
			if (args.length <= 1) {
				p.sendMessage("§c§lCHANGEPASS§F Utilize: §c§l/mudarsenha§f <senha atual> <nova senha>");
			} else {
				String old = bP.getData(DataType.PASSWORD).asString();
				if (args[0].equals(old)) {
					String now = args[1];
					if (now.length() >= 8) {
						if (now.length() <= 20) {
							bP.getData(DataType.PASSWORD).setValue(now);
							bP.save(DataCategory.ACCOUNT);
							p.sendMessage(
									"§c§lCHANGEPASS§f A senha da conta foi alterada com sucesso! Para mais informações digite /account.");
						} else {
							p.sendMessage(
									"§c§lCHANGEPASS§f A senha está §c§lMUITO GRANDE§f! Precisa conter menos que 21 caráteres.");
						}
					} else {
						p.sendMessage(
								"§c§lCHANGEPASS§f A senha está §c§lMUITO PEQUENA§f! Precisa conter mais que 7 caráteres.");
					}
					now = null;
				} else {
					p.sendMessage("§c§lCHANGEPASS§f A senha atual digitada não conrresponde á senha registrada.");
				}
				old = null;
			}
			bP = null;
			p = null;
		} else {
			sender.sendMessage("§4§lERRO§f Comando disponivel apenas §c§lin-game");
		}
	}
}
