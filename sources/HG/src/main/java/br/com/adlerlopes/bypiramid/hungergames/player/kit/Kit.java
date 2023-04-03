package br.com.adlerlopes.bypiramid.hungergames.player.kit;

import br.com.adlerlopes.bypiramid.hungergames.HungerGames;
import br.com.adlerlopes.bypiramid.hungergames.game.stage.GameStage;
import br.com.adlerlopes.bypiramid.hungergames.manager.Manager;
import br.com.adlerlopes.bypiramid.hungergames.player.gamer.Gamer;
import br.com.adlerlopes.bypiramid.hungergames.player.item.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Kit implements Listener {

	private static final ItemBuilder itemBuilder = new ItemBuilder(Material.AIR);
	private ConcurrentHashMap<UUID, Long> cooldown = new ConcurrentHashMap<>();
	private ItemStack icon;
	private ItemStack[] items;
	private String name;
	private String description;
	private boolean free;
	private boolean active;
	private boolean recent;
	private double cooldownTime;
	private int id;
	private int price;
	private Manager manager;

	public Kit(Manager manager) {
		this.manager = manager;
		this.active = true;
		this.free = false;
		this.name = getClass().getSimpleName();
		this.items = null;
		this.recent = false;
	}

	public String getName() {
		return this.name;
	}

	public ItemStack getIcon() {
		return this.icon;
	}

	public double getCooldown(Player player) {
		return this.cooldown.contains(player.getUniqueId()) ? 0
				: (((Long) this.cooldown.get(player.getUniqueId())).longValue() - System.currentTimeMillis()) / 10;
	}

	public double getCooldownTime() {
		return this.cooldownTime;
	}

	public String getDescription() {
		return this.description;
	}

	public ItemStack[] getItems() {
		return this.items;
	}

	public Manager getManager() {
		return manager;
	}

	public Gamer getGamer(UUID uuid) {
		return getManager().getGamerManager().getGamer(uuid);
	}

	public Gamer getGamer(Player player) {
		return getManager().getGamerManager().getGamer(player);
	}

	public void setIcon(ItemStack icon) {
		this.icon = icon;
	}

	public void setItems(ItemStack... itens) {
		this.items = itens;
	}

	public void addCooldown(Player player) {
		addCooldown(player, this.cooldownTime);
	}

	public void addCooldown(Player player, double segundos) {
		this.cooldown.put(player.getUniqueId(), (long) (System.currentTimeMillis() + (segundos * 1000.0D)));
	}

	public void removeCooldown(Player player) {
		this.cooldown.remove(player.getUniqueId());
	}

	public void setRecent(Boolean bool) {
		this.recent = bool.booleanValue();
	}

	public void sendCooldown(Player player) {
		double cooldown = getCooldown(player) / 100.0D;

		player.sendMessage("§e§lCOOLDOWN §fAguarde mais §e§l" + cooldown + "§f segundos para usar novamente!");
	}

	public void give(Player player) {
		if (this.items == null) {
			return;
		}
		ItemStack[] arrayOfItemStack;
		int j = (arrayOfItemStack = this.items).length;
		for (int i = 0; i < j; i++) {
			ItemStack item = arrayOfItemStack[i];
			player.getInventory().addItem(new ItemStack[] { item });
		}
	}

	public void setDescription(String desc) {
		this.description = desc;
		ItemMeta meta = this.icon.getItemMeta();

		List<String> lore = new ArrayList<>();

		lore.add(ChatColor.RESET + desc);

		meta.setDisplayName(ChatColor.RESET + this.name);
		meta.setLore(lore);

		this.icon.setItemMeta(meta);
	}

	public void setFree(boolean bool) {
		this.free = bool;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setCooldownTime(double cooldownTime) {
		this.cooldownTime = cooldownTime;
	}

	public void setID(int id) {
		this.id = id;
	}

	public void setActive(Boolean active) {
		this.active = active.booleanValue();
	}

	public boolean inCooldown(Player player) {
		return cooldown.containsKey(player.getUniqueId()) ? getCooldown(player) / 100 >= 0 ? true : false : false;
	}

	public boolean isFree() {
		return this.free;
	}

	public boolean isRecent() {
		return this.recent;
	}

	public boolean isActive() {
		return this.active;
	}

	public int getPrice() {
		return this.price;
	}

	public int getID() {
		return this.id;
	}

	public boolean isPreGame() {
		return getManager().getGameManager().isPreGame();
	}

	public boolean isInvencibility() {
		return getManager().getGameManager().isInvencibility();
	}

	public boolean hasKit(Gamer gamer) {

		return (this.name.equals(gamer.getKit().getName())) || (this.name.equals(gamer.getKit2().getName()));
	}

	public boolean hasKit(Player player) {
		return (this.name.equals(this.manager.getGamerManager().getGamer(player).getKit().getName()))
				|| (this.name.equals(this.manager.getGamerManager().getGamer(player).getKit2().getName()));
	}

	public boolean hasKitSecondary(Player player) {
		return this.name.equals(this.manager.getGamerManager().getGamer(player).getKit2().getName());
	}

	public boolean isKitItem(ItemStack item, Material material, String name) {
		return (item != null) && (item.getType() == material) && (item.hasItemMeta())
				&& (item.getItemMeta().hasDisplayName()) && (item.getItemMeta().getDisplayName().equals(name));
	}

	public boolean isKitItem(ItemStack item, String name) {
		return (item != null) && (item.getType() != Material.AIR) && (item.hasItemMeta())
				&& (item.getItemMeta().hasDisplayName()) && (item.getItemMeta().getDisplayName().equals(name));
	}

	public ItemBuilder getItemBuilder() {
		return itemBuilder;
	}

	public ItemStack createItemStack(String name, Material material) {
		return getItemBuilder().setMaterial(material).setName(name).getStack();
	}

	public ItemStack createItemStack(String name, Material material, int amount) {
		return getItemBuilder().setMaterial(material).setName(name).setAmount(amount).getStack();
	}

	public ItemStack createItemStack(String name, String[] desc, Material material, int amount) {
		return getItemBuilder().setMaterial(material).setName(name).setAmount(amount).setDescription(desc).getStack();
	}

	public ItemStack createItemStack(String name, Material material, int amount, Enchantment enchant, int level) {
		return getItemBuilder().setMaterial(material).setName(name).setAmount(amount).setEnchant(enchant, level)
				.getStack();
	}
}
