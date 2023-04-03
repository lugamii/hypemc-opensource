package br.com.adlerlopes.bypiramid.hungergames.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import br.com.adlerlopes.bypiramid.hungergames.HungerGames;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Schematic {

	private short[] blocks;
	private byte[] data;
	private short width;
	private short lenght;
	private short height;

	private static Schematic instance = new Schematic();

	public static Schematic getInstance() {
		return instance;
	}

	private Schematic() {
	}

	private Schematic(short[] blocks2, byte[] data, short width, short lenght, short height) {
		this.blocks = blocks2;
		this.data = data;
		this.width = width;
		this.lenght = lenght;
		this.height = height;
	}

	private short[] getBlocks() {
		return this.blocks;
	}

	private byte[] getData() {
		return this.data;
	}

	private short getWidth() {
		return this.width;
	}

	private short getLenght() {
		return this.lenght;
	}

	private short getHeight() {
		return this.height;
	}
	
	private static final Set<Block> doors = new HashSet<>();
	
	@SuppressWarnings("deprecation")
	public void newSchematic(World world, Location loc, Schematic schematic, ArrayList<Block> array) {

		short[] blocks = schematic.getBlocks();
		byte[] blockData = schematic.getData();

		short length = schematic.getLenght();
		short width = schematic.getWidth();
		short height = schematic.getHeight();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					int index = y * width * length + z * width + x;
					Block block = new Location(world, x + loc.getX(), y + loc.getY(), z + loc.getZ()).getBlock();
					array.add(block);
					HungerGames.getManager().getBO2().setBlockFast(world, block.getLocation().getBlockX(),
							block.getLocation().getBlockY(), block.getLocation().getBlockZ(), blocks[index],
							blockData[index]);

					if (block.getType() == Material.getMaterial(33)) 
						doors.add(block);
					// block.setTypeIdAndData(blocks[index], blockData[index], true);
				}
			}
		}
	}
	
	public static void openDoors() {
		for (Block door : doors) {
			HungerGames.getManager().getBO2().setBlockFast(door.getLocation(), Material.AIR, (byte) 0);
		}
	}

	public void deletSchematic(World world, Location loc, Schematic schematic) {

		short length = schematic.getLenght();
		short width = schematic.getWidth();
		short height = schematic.getHeight();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					Block block = new Location(world, x + loc.getX(), y + loc.getY(), z + loc.getZ()).getBlock();
					HungerGames.getManager().getBO2().setBlockFast(block.getLocation(), Material.AIR, (byte) 0);
					// block.setType(Material.AIR);
				}
			}
		}
	}

	public Schematic loadSchematic(File file) throws IOException, DataException {

		FileInputStream stream = new FileInputStream(file);
		NBTInputStream nbtStream = new NBTInputStream((stream));
		CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
		nbtStream.close();
		if (!schematicTag.getName().equalsIgnoreCase("Schematic")) {
			throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
		}
		Map schematic;
		if (!(schematic = schematicTag.getValue()).containsKey("Blocks")) {
			throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
		}

		short width = ((ShortTag) getChildTag(schematic, "Width", ShortTag.class)).getValue().shortValue();
		short length = ((ShortTag) getChildTag(schematic, "Length", ShortTag.class)).getValue().shortValue();
		short height = ((ShortTag) getChildTag(schematic, "Height", ShortTag.class)).getValue().shortValue();

		byte[] blockId = ((ByteArrayTag) getChildTag(schematic, "Blocks", ByteArrayTag.class)).getValue();
		byte[] blockData = ((ByteArrayTag) getChildTag(schematic, "Data", ByteArrayTag.class)).getValue();
		byte[] addId = new byte[0];
		short[] blocks = new short[blockId.length];

		if (schematic.containsKey("AddBlocks")) {
			addId = ((ByteArrayTag) getChildTag(schematic, "AddBlocks", ByteArrayTag.class)).getValue();
		}

		for (int index = 0; index < blockId.length; index++) {
			if (index >> 1 >= addId.length) {
				blocks[index] = ((short) (blockId[index] & 0xFF));
			} else if ((index & 0x1) == 0)
				blocks[index] = ((short) (((addId[(index >> 1)] & 0xF) << 8) + (blockId[index] & 0xFF)));
			else {
				blocks[index] = ((short) (((addId[(index >> 1)] & 0xF0) << 4) + (blockId[index] & 0xFF)));
			}

		}

		return new Schematic(blocks, blockData, width, length, height);
	}

	private <T extends Tag> Tag getChildTag(Map<String, Tag> items, String key, Class<T> expected)
			throws DataException {

		if (!items.containsKey(key)) {
			throw new DataException("Schematic file is missing a \"" + key + "\" tag");
		}
		Tag tag = (Tag) items.get(key);
		if (!expected.isInstance(tag)) {
			throw new DataException(key + " tag is not of tag type " + expected.getName());
		}

		return (Tag) expected.cast(tag);
	}
}
