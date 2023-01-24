package br.com.weavenmc.commons.bukkit.command.register;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.WeavenMC;
import br.com.weavenmc.commons.bukkit.account.BukkitPlayer;
import br.com.weavenmc.commons.bukkit.api.player.PingAPI;
import br.com.weavenmc.commons.bukkit.command.BukkitCommandSender;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework.Command;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.commons.util.string.StringTimeUtils;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public class DebugCommand implements CommandClass {

	@Command(name = "debug", groupToUse = Group.ADMIN)
	public void memory(BukkitCommandSender sender, String label, String[] args) {
		Runtime runtime = Runtime.getRuntime();
		Long previous = (runtime.totalMemory() - runtime.freeMemory()) / 1024L / 1024L;
		System.gc();
		Long current = (runtime.totalMemory() - runtime.freeMemory()) / 1024L / 1024L;
		WeavenMC.debug("[Debug Command] [Executed by " + sender.getName() + "] Foram liberados " + (previous - current)
				+ " megabytes da memória em cache");
		for (Player o : Bukkit.getOnlinePlayers()) {
			BukkitPlayer online = BukkitPlayer.getPlayer(o.getUniqueId());
			if (!online.hasGroupPermission(Group.TRIAL))
				continue;
			o.sendMessage("§c[Debug Command] [Executed by " + sender.getName() + "] Foram liberados " + (previous - current)
					+ " megabytes da memória em cache");
			online = null;
		}
		current = null;
		previous = null;
		runtime = null;
	}

	@Command(name = "lag")
	public void lag(BukkitCommandSender sender, String label, String[] args) {
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		long used = total - free;
		double[] serverTicks = MinecraftServer.getServer().recentTps;
		double tps = Double.valueOf(StringTimeUtils.toMillis(serverTicks[0]));
		sender.sendMessage("§6Uso de memória: §a" + (used * 100) / total + "%");
		sender.sendMessage("§6Uso de cpu: §a" + getCpuUse() + "%");
		sender.sendMessage(
				"§6Tempo de atividade: §a" + getTimerFormat(ManagementFactory.getRuntimeMXBean().getUptime()));
		sender.sendMessage("§6Lag atual: §a" + StringTimeUtils.toMillis((20.0D - tps)) + "%");
		if (sender.isPlayer()) {
			Player p = sender.getPlayer();
			int ping = PingAPI.getPing(p);
			p.sendMessage("§6Seu ping: §a" + ping + "ms, §6Lag: " + (ping >= 110 ? "§cSim" : "§aNao"));
		}
	}

	protected double getCpuUse() {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
			AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });
			if (list.isEmpty())
				return Double.NaN;
			Attribute att = (Attribute) list.get(0);
			Double value = (Double) att.getValue();
			if (value == -1.0)
				return Double.NaN;
			return (int) (value * 1000) / 10.0;
		} catch (Exception e) {
			return 0.0;
		}
	}

	protected String getTimerFormat(long millis) {
		long days = TimeUnit.DAYS.convert(millis, TimeUnit.MILLISECONDS);
		millis -= TimeUnit.DAYS.toMillis(days);

		long hours = TimeUnit.HOURS.convert(millis, TimeUnit.MILLISECONDS);
		millis -= TimeUnit.HOURS.toMillis(hours);

		long minutes = TimeUnit.MINUTES.convert(millis, TimeUnit.MILLISECONDS);
		millis -= TimeUnit.MINUTES.toMillis(minutes);

		long seconds = TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS);

		StringBuilder sb = new StringBuilder();
		if (days != 0L)
			sb.append(days + (days == 1 ? " dia, " : " dias, "));
		if (hours != 0L && hours != 0)
			sb.append(hours + (hours == 1 ? " hora, " : " horas, "));
		if (minutes != 0L && minutes != 0)
			sb.append(minutes + (minutes == 1 ? " minuto, " : " minutos, "));
		if (seconds != 0L && seconds != 0)
			sb.append(seconds + (seconds == 1 ? " segundo" : " segundos"));

		return sb.toString().endsWith(", ") ? sb.toString().substring(0, sb.toString().length() - 2) : sb.toString();
	}
}
