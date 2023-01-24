package br.com.weavenmc.commons.bungee.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.weavenmc.commons.bungee.account.ProxyPlayer;
import br.com.weavenmc.commons.core.command.CommandClass;
import br.com.weavenmc.commons.core.command.CommandFramework;
import br.com.weavenmc.commons.core.server.ServerType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class BungeeCommandFramework implements CommandFramework {

	private final Map<String, Entry<Method, Object>> commandMap = new HashMap<String, Entry<Method, Object>>();
	private final Map<String, Entry<Method, Object>> completers = new HashMap<String, Entry<Method, Object>>();
	private final Plugin plugin;

	public BungeeCommandFramework(Plugin plugin) {
		this.plugin = plugin;
		this.plugin.getProxy().getPluginManager().registerListener(plugin, new BungeeCompleter());
	}

	public boolean handleCommand(CommandSender sender, String label, String[] args) {
		for (int i = args.length; i >= 0; i--) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(label.toLowerCase());
			for (int x = 0; x < i; x++) {
				buffer.append(".").append(args[x].toLowerCase());
			}
			String cmdLabel = buffer.toString();
			if (commandMap.containsKey(cmdLabel)) {
				Entry<Method, Object> entry = commandMap.get(cmdLabel);
				Command command = entry.getKey().getAnnotation(Command.class);
				if (sender instanceof ProxiedPlayer) {
					ProxiedPlayer p = (ProxiedPlayer) sender;
					ProxyPlayer player = ProxyPlayer.getPlayer(p.getUniqueId());
					if (!player.hasGroupPermission(command.groupToUse())) {
						p.sendMessage(TextComponent.fromLegacyText(
								"§c§lPERMISSAO§f Você não tem §c§lPERMISSAO§f para executar este comando!"));
						return true;
					} else if (player.getServerConnectedType() == ServerType.LOGIN) {
						return true;
					}
					player = null;
					p = null;
				}

				if (command.runAsync()) {
					plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {

						@Override
						public void run() {
							try {
								entry.getKey().invoke(entry.getValue(), new BungeeCommandSender(sender), label, args);
							} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
				} else {
					try {
						entry.getKey().invoke(entry.getValue(), new BungeeCommandSender(sender), label, args);
					} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				return true;
			}
		}
		return true;
	}

	@Override
	public void registerCommands(CommandClass cls) {
		for (Method m : cls.getClass().getMethods()) {
			if (m.getAnnotation(Command.class) != null) {
				Command command = m.getAnnotation(Command.class);
				if (m.getParameterTypes().length > 3 || m.getParameterTypes().length <= 2
						|| !BungeeCommandSender.class.isAssignableFrom(m.getParameterTypes()[0])
								&& !String.class.isAssignableFrom(m.getParameterTypes()[1])
								&& !String[].class.isAssignableFrom(m.getParameterTypes()[2])) {
					System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
					continue;
				}
				registerCommand(command, command.name(), m, cls);
				for (String alias : command.aliases()) {
					registerCommand(command, alias, m, cls);
				}
			} else if (m.getAnnotation(Completer.class) != null) {
				Completer comp = m.getAnnotation(Completer.class);
				if (m.getParameterTypes().length > 3 || m.getParameterTypes().length <= 2
						|| m.getParameterTypes()[0] != ProxiedPlayer.class
								&& m.getParameterTypes()[1] != String.class
								&& m.getParameterTypes()[2] != String[].class) {
					System.out.println(
							"Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
					continue;
				}
				if (m.getReturnType() != List.class) {
					System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
					continue;
				}
				registerCompleter(comp.name(), m, cls);
				for (String alias : comp.aliases()) {
					registerCompleter(alias, m, cls);
				}
			}
		}
	}

	/**
	 * Registers all the commands under the plugin's help
	 */

	private void registerCommand(Command command, String label, Method m, Object obj) {
		Entry<Method, Object> entry = new AbstractMap.SimpleEntry<Method, Object>(m, obj);
		commandMap.put(label.toLowerCase(), entry);
		String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();

		net.md_5.bungee.api.plugin.Command cmd;
		if (command.permission().isEmpty())
			cmd = new BungeeCommand(cmdLabel);
		else
			cmd = new BungeeCommand(cmdLabel, command.permission());
		plugin.getProxy().getPluginManager().registerCommand(plugin, cmd);
	}

	private void registerCompleter(String label, Method m, Object obj) {
		completers.put(label, new AbstractMap.SimpleEntry<Method, Object>(m, obj));
	}

	class BungeeCommand extends net.md_5.bungee.api.plugin.Command {

		protected BungeeCommand(String label) {
			super(label);
		}

		protected BungeeCommand(String label, String permission) {
			super(label, permission);
		}

		@Override
		public void execute(net.md_5.bungee.api.CommandSender sender, String[] args) {
			handleCommand(sender, getName(), args);
		}

	}

	public class BungeeCompleter implements Listener {

		@SuppressWarnings("unchecked")
		@EventHandler
		public void onTabComplete(TabCompleteEvent event) {
			if (!(event.getSender() instanceof ProxiedPlayer))
				return;
			ProxiedPlayer player = (ProxiedPlayer) event.getSender();
			String[] split = event.getCursor().replaceAll("\\s+", " ").split(" ");
			String[] args = new String[split.length - 1];
			for (int i = 1; i < split.length; i++) {
				args[i - 1] = split[i];
			}
			String label = split[0].substring(1);
			for (int i = args.length; i >= 0; i--) {
				StringBuilder buffer = new StringBuilder();
				buffer.append(label.toLowerCase());
				for (int x = 0; x < i; x++) {
					buffer.append(".").append(args[x].toLowerCase());
				}
				String cmdLabel = buffer.toString();
				if (completers.containsKey(cmdLabel)) {
					Entry<Method, Object> entry = completers.get(cmdLabel);
					try {
						event.getSuggestions().clear();
						event.getSuggestions()
								.addAll((List<String>) entry.getKey().invoke(entry.getValue(), player, label, args));
					} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public Plugin getPlugin() {
		return plugin;
	}
}
