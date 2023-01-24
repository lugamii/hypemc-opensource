package br.com.weavenmc.lobby.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.weavenmc.commons.bukkit.api.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EnumParticle;

@AllArgsConstructor
@Getter
public enum Wings {

	SMOKE("Smoke", EnumParticle.SMOKE_NORMAL,
			new ItemBuilder().type(Material.FLINT_AND_STEEL).name("§eSmoke").build()),
	FOGO("Fogo", EnumParticle.FLAME, new ItemBuilder().type(Material.LAVA_BUCKET).name("§eFogo").build());

	String name;
	EnumParticle particle;
	ItemStack item;

	public static Wings getWingsByName(String nameOfParticle) {
		for (Wings p : Wings.values())
			if (p.getName().equalsIgnoreCase(nameOfParticle))
				return p;
		return null;
	}

}
