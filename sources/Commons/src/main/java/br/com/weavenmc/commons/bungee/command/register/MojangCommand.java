package br.com.weavenmc.commons.bungee.command.register;

import br.com.weavenmc.commons.bungee.BungeeMain;
import br.com.weavenmc.commons.bungee.command.BungeeCommandSender;
import br.com.weavenmc.commons.bungee.hostname.Hostname;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.permission.Group;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MojangCommand implements CommandClass {

	@Command(name = "mojang", groupToUse = Group.DONO)
	public void onMojang(BungeeCommandSender sender, String label, String[] args) {
		sender.sendMessage("§9Estatisticas de jogadores §6originais§9 e §epiratas§9:");
		
		for (ServerInfo server : BungeeCord.getInstance().getServers().values()) 
		{
			int crackedCount = 0, premiumCount = 0;
			
			for (ProxiedPlayer o : server.getPlayers()) 
			{
				
				if (o.getPendingConnection().isOnlineMode()) 
				{
					premiumCount++;
				} else {
					crackedCount++;
				}
			}
			
			sender.sendMessage("§9" + server.getName().toUpperCase() + ": §6" + premiumCount + "§9/§e" + crackedCount);
		}
		
		int crackedCount = 0, premiumCount = 0, onlineCount = 0;
		
		for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) 
		{
			
			if (o.getPendingConnection().isOnlineMode()) 
			{
				premiumCount++;
			} else {
				crackedCount++;
			}
			
			onlineCount++;
		}
		
		sender.sendMessage("§9No momento há em toda a Rede:");
		
		sender.sendMessage("§6" + premiumCount + " jogadores originais");
		sender.sendMessage("§e" + crackedCount + " jogadores piratas");
		sender.sendMessage("§a" + onlineCount + " jogadores totais online");
		sender.sendMessage("§9Endereços utilizados para conexão:");
		int totalConnections = 0;
		for (Hostname hostnames :  BungeeMain.getInstance().getHostnameManager().getHostnames())  {
			sender.sendMessage("§e" + hostnames.getHostname() + " - " + hostnames.getConnections() + " conexões!"); 
			totalConnections += hostnames.getConnections();
		}
		sender.sendMessage("§b§l" + totalConnections + " §bconexões efetuadas no total!");
		
		
	}
}
