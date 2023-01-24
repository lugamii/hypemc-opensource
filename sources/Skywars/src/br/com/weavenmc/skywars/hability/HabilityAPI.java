package br.com.weavenmc.skywars.hability;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import br.com.weavenmc.commons.core.permission.Group;
import br.com.weavenmc.skywars.scoreboard.Scoreboarding;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class HabilityAPI {

	@AllArgsConstructor
	@Getter
	public enum Hability {
		COUNTER("Counter",
				new String[] { "§6Habilidade:", "§f Tenha 25% de chance de revidar",
						"§f o dano que o adversário causou!" },
				Group.VIP, Material.IRON_SWORD, 20000),
		FOGUETE("Foguete",
				new String[] { "§6Habilidade:", "§f Suba aos ares com seu foguete", " ", "§6Item:", " §fFirework", " ",
						"§6Cooldown:", "§f 15 segundos" },
				Group.ULTRA, Material.FIREWORK, 70000),
		FENIX("Fenix", new String[] { "§6Habilidade:", "§f Tenha uma segunda chance na vida!" }, Group.BLADE,
				Material.BLAZE_POWDER, 70000),
		KING_HELL("King Hell", new String[] { "§6Habilidade:", "§f Não sofra dano do fogo!" }, Group.ULTRA,
				Material.NETHER_FENCE, 50000),
		SPARTAN("Spartan",
				new String[] { "§6Habilidade:", " §fGanhe velocidade, resistência ao",
						" §ffogo e regeneração por 10 segundos,", " §fmas sofrerá lentidão e cegueira com o tempo",
						" §figual à cinco multiplicado ao número de kills", " §fefetuado com o kit.", " ", "§6Item:",
						" §fLã vermelha", " ", "§6Cooldown:", " §f35 segundos" },
				Group.BLADE, Material.IRON_CHESTPLATE, 50000),
		JOHN_WICK("John Wick",
				new String[] { "§6Habilidade:", "§f O adversário sofrerá", "§f o dobro de dano se for headshot!" },
				Group.PRO, Material.IRON_SWORD, 50000),
		VAMPIRE("Vampire",
				new String[] { "§6Habilidade:", "§f Tenha 25% de chance de roubar", " §fo sangue do adversário!" },
				Group.PRO, Material.REDSTONE, 50000),
		FLASH("Flash", new String[] { "§6Habilidade:", "§f Ganhe velocidade I a", "§f cada kill que faz!" }, Group.VIP,
				Material.REDSTONE_TORCH_ON, 50000),
		YATI("Yati", new String[] { "§6Habilidade:", "§f Congele o advérsario acertando", "§f bolinhas de neve!" },
				Group.VIP, Material.SNOW_BALL, 50000),
		ARROW("Arrow", new String[] { "§6Habilidade:", "§f Dê efeitos negativos nos adversários!" }, Group.ULTRA,
				Material.ARROW, 50000),
		ENDER_LORD("Lord Ender",
				new String[] { "§6Habilidade:", "§f Não sofra dano quando se", "§f teleporta com ender pearl" },
				Group.VIP, Material.ENDER_PEARL, 50000),
		VANESSA("Vanessa",
				new String[] { "§6Habilidade:", "§f Ganhe fire aspect na espada", "§f a cada kill que faz!" },
				Group.ULTRA, Material.DIAMOND_SWORD, 50000);

		String name;
		String[] description;
		Group group;
		Material display;
		int price;
	}

	@Getter
	private HashMap<UUID, Hability> habilidade = new HashMap<>();

	public Hability getHabilidade(Player player) {
		return habilidade.get(player.getUniqueId());
	}

	public boolean isHabilidade(Player player, Hability habilidade) {
		if (this.habilidade.containsKey(player.getUniqueId())) {
			return getHabilidade(player) == habilidade;
		} else {
			return false;
		}
	}

	public void setHabilidade(Player player, Hability habilidade) {
		this.habilidade.put(player.getUniqueId(), habilidade);
	}

	public String name(Player player) {
		if (habilidade.containsKey(player.getUniqueId())) {
			if (getHabilidade(player).equals(Hability.COUNTER)) {
				return "§bCounter";
			} else if (getHabilidade(player).equals(Hability.FOGUETE)) {
				return "§bFoguete";
			} else if (getHabilidade(player).equals(Hability.FENIX)) {
				return "§bFênix";
			} else if (getHabilidade(player).equals(Hability.KING_HELL)) {
				return "§bKing Hell";
			} else if (getHabilidade(player).equals(Hability.SPARTAN)) {
				return "§bSpartan";
			} else if (getHabilidade(player).equals(Hability.JOHN_WICK)) {
				return "§bJohn Wick";
			} else if (getHabilidade(player).equals(Hability.VAMPIRE)) {
				return "§bVampire";
			} else if (getHabilidade(player).equals(Hability.FLASH)) {
				return "§bFlash";
			} else if (getHabilidade(player).equals(Hability.YATI)) {
				return "§bYati";
			} else if (getHabilidade(player).equals(Hability.ARROW)) {
				return "§bArrow";
			} else if (getHabilidade(player).equals(Hability.ENDER_LORD)) {
				return "§bEnder Lord";
			} else if (getHabilidade(player).equals(Hability.VANESSA)) {
				return "§bVanessa";
			} else {
				return "§bNenhum";
			}
		} else {
			return "§bNenhum";
		}
	}

	public void setItens(Player player) {
		if (habilidade.containsKey(player.getUniqueId())) {
			if (getHabilidade(player).equals(Hability.FOGUETE)) {
				player.getInventory().addItem(new ItemBuilder().type(Material.FIREWORK).name("§aFoguete").build());
				player.sendMessage("§eVocê recebeu os itens da habilidade §cFoguete§e.");
			}
			if (getHabilidade(player).equals(Hability.SPARTAN)) {
				ItemStack stack = new ItemStack(Material.WOOL, 1, (short) 14);
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName("§c§lSpartan");
				stack.setItemMeta(meta);
				player.getInventory().addItem(stack);
				player.sendMessage("§eVocê recebeu os itens da habilidade §cSpartan§e.");
			}
			player.updateInventory();
		}
	}

}
