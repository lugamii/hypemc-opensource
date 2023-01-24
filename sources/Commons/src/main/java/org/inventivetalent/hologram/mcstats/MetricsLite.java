package org.inventivetalent.hologram.mcstats;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitTask;

public class MetricsLite {

    private static int REVISION = 5;
    private static String BASE_URL = "http://mcstats.org";
    private static String REPORT_URL = "/report/%s";
    private static String CUSTOM_DATA_SEPARATOR = "~~";
    private static int PING_INTERVAL = 10;
    private Plugin plugin;
    private Set<Graph> graphs = Collections.synchronizedSet(new HashSet<Graph>());
    private Graph defaultGraph = new Graph("Default");
    private YamlConfiguration configuration;
    private File configurationFile;
    private String guid;
    private boolean debug;
    private Object optOutLock = new Object();
    private volatile BukkitTask task = null;

    public MetricsLite(Plugin plugin) throws IOException {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }

        this.plugin = plugin;

        configurationFile = getConfigFile();
        configuration = YamlConfiguration.loadConfiguration(configurationFile);

        configuration.addDefault("opt-out", false);
        configuration.addDefault("guid", UUID.randomUUID().toString());

        if (configuration.get("guid", null) == null) {
            configuration.options().header("http://mcstats.org").copyDefaults(true);
            configuration.save(configurationFile);
        }

        guid = configuration.getString("guid");
        this.debug = configuration.getBoolean("debug", false);
    }

    public Graph createGraph(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Graph name cannot be null");
        }

        Graph graph = new Graph(name);

        graphs.add(graph);

        return graph;
    }

    public void addCustomData(Plotter plotter) {
        if (plotter == null) {
            throw new IllegalArgumentException("Plotter cannot be null");
        }

        defaultGraph.addPlotter(plotter);

        graphs.add(defaultGraph);
    }

    public boolean start() {
        synchronized (optOutLock) {
            if (isOptOut()) {
                return false;
            }

            if (task != null) {
                return true;
            }

            task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

                private boolean firstPost = true;

                public void run() {
                    try {
                        synchronized (optOutLock) {
                            if (isOptOut() && task != null) {
                                MetricsLite.this.task.cancel();
                                MetricsLite.this.task = null;
                            }
                        }
                        postPlugin(!firstPost);
                        firstPost = false;
                    } catch (IOException e) {
                    	if(debug)
                    		Bukkit.getLogger().log(Level.INFO, "[Metrics] {0}", e.getMessage());
                    }
                }
            }, 0, PING_INTERVAL * 1200);

            return true;
        }
    }

    public boolean isOptOut() {
        synchronized(optOutLock) {
            try {
                configuration.load(getConfigFile());
            } catch (IOException ex) {
            	if(debug)
            		Bukkit.getLogger().log(Level.INFO, "[Metrics] {0}", ex.getMessage());
                return true;
            } catch (InvalidConfigurationException ex) {
                if(debug)	
                	Bukkit.getLogger().log(Level.INFO, "[Metrics] {0}", ex.getMessage());
                return true;
            }
            return configuration.getBoolean("opt-out", false);
        }
    }

    public void enable() throws IOException {
        synchronized (optOutLock) {
        	if (isOptOut()) {
        		configuration.set("opt-out", false);
        		configuration.save(configurationFile);
        	}
        	if (task == null) {
        		start();
        	}
        }
    }

	public void disable() throws IOException {
		synchronized (optOutLock) {
			if (!isOptOut()) {
				configuration.set("opt-out", true);
				configuration.save(configurationFile);
			}

			if (task != null) {
				task.cancel();
				task = null;
			}
		}
	}

	public File getConfigFile() {
		File pluginsFolder = this.plugin.getDataFolder().getParentFile();
		return new File(new File(pluginsFolder, "PluginMetrics"), "config.yml");
	}

	private int getOnlinePlayers() {
		try {
			Method onlinePlayerMethod = Server.class.getMethod("getOnlinePlayers", new Class[0]);
			if (onlinePlayerMethod.getReturnType().equals(Collection.class)) {
				return ((Collection<?>) onlinePlayerMethod.invoke(Bukkit.getServer(), new Object[0])).size();
			}
			return ((Player[]) onlinePlayerMethod.invoke(Bukkit.getServer(), new Object[0])).length;
		} catch (Exception ex) {
			if (debug)
				Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
		}
		return 0;
	}

    private void postPlugin(boolean isPing) throws IOException {
        PluginDescriptionFile description = plugin.getDescription();

        StringBuilder data = new StringBuilder();
        data.append(encode("guid")).append('=').append(encode(guid));
        encodeDataPair(data, "version", description.getVersion());
        encodeDataPair(data, "server", Bukkit.getVersion());
        encodeDataPair(data, "players", getOnlinePlayers() + "");
        encodeDataPair(data, "revision", String.valueOf(REVISION));

        if (isPing) {
            encodeDataPair(data, "ping", "true");
        }

        synchronized (graphs) {
            Iterator<Graph> iter = graphs.iterator();

            while (iter.hasNext()) {
                Graph graph = iter.next();

                for (Plotter plotter : graph.getPlotters()) {
                    String key = String.format("C%s%s%s%s", CUSTOM_DATA_SEPARATOR, graph.getName(), CUSTOM_DATA_SEPARATOR, plotter.getColumnName());

                    String value = Integer.toString(plotter.getValue());

                    encodeDataPair(data, key, value);
                }
            }
        }

        URL url = new URL(BASE_URL + String.format(REPORT_URL, encode(plugin.getDescription().getName())));

        URLConnection connection;

        if (isMineshafterPresent()) {
            connection = url.openConnection(Proxy.NO_PROXY);
        } else {
            connection = url.openConnection();
        }

        connection.setDoOutput(true);

        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data.toString());
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = reader.readLine();

        writer.close();
        reader.close();

        if (response == null || response.startsWith("ERR")) {
            throw new IOException(response);
        } else {
            if (response.contains("OK This is your first update this hour")) {
                synchronized (graphs) {
                    Iterator<Graph> iter = graphs.iterator();

                    while (iter.hasNext()) {
                        Graph graph = iter.next();

                        for (Plotter plotter : graph.getPlotters()) {
                            plotter.reset();
                        }
                    }
                }
            }
        }
    }

    private boolean isMineshafterPresent() {
        try {
            Class.forName("mineshafter.MineServer");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void encodeDataPair(StringBuilder buffer, String key, String value) throws UnsupportedEncodingException {
        buffer.append('&').append(encode(key)).append('=').append(encode(value));
    }

    private static String encode(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, "UTF-8");
    }

    public static class Graph {

        private String name;

        private Set<Plotter> plotters = new LinkedHashSet<Plotter>();

        private Graph(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void addPlotter(Plotter plotter) {
            plotters.add(plotter);
        }

        public void removePlotter(Plotter plotter) {
            plotters.remove(plotter);
        }

        public Set<Plotter> getPlotters() {
            return Collections.unmodifiableSet(plotters);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Graph)) {
                return false;
            }

            Graph graph = (Graph) object;
            return graph.name.equals(name);
        }

    }

    public static abstract class Plotter {

        private String name;

        public Plotter() {
            this("Default");
        }

        public Plotter(String name) {
            this.name = name;
        }

        public abstract int getValue();

        public String getColumnName() {
            return name;
        }

        public void reset() {
        }

        @Override
        public int hashCode() {
            return getColumnName().hashCode() + getValue();
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Plotter)) {
                return false;
            }

            Plotter plotter = (Plotter) object;
            return plotter.name.equals(name) && plotter.getValue() == getValue();
        }

    }

}
