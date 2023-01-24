package br.com.weavenmc.skywars.nbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import br.com.weavenmc.skywars.WeavenSkywars;

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

	@SuppressWarnings("deprecation")
	public void generateSchematic(World world, Location loc, Schematic schematic) {
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
					block.setTypeIdAndData(blocks[index], blockData[index], true);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void generateSchematic(World world, Location loc, Schematic schematic, ArrayList<Block> array) {
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
					block.setTypeIdAndData(blocks[index], blockData[index], true);
					array.add(block);
				}
			}
		}
	}
	
	public Schematic carregarSchematics(File file) throws Exception {
		FileInputStream stream = new FileInputStream(file);
		NBTInputStream nbtStream = new NBTInputStream((stream));
		CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
		nbtStream.close();
		if (!schematicTag.getName().equalsIgnoreCase("Schematic")) {
			throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
		}
		Map<String, Tag> schematic;
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
	
	public static void setupShematics() {
		File file = new File(WeavenSkywars.getInstance().getDataFolder(), "waiting.schematic");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			copiarConfig(WeavenSkywars.getInstance().getResource("waiting.schematic"), file);
		}
		file = new File(WeavenSkywars.getInstance().getDataFolder(), "skywars.schematic");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			copiarConfig(WeavenSkywars.getInstance().getResource("skywars.schematic"), file);
		}
	}

	protected static void copiarConfig(InputStream i, File config) {
		try {
			OutputStream out = new FileOutputStream(config);
			byte[] buf = new byte[710];
			int len;
			while ((len = i.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			i.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setFastBlock(Location loc, int id, byte data) {
		net.minecraft.server.v1_8_R3.World w = ((org.bukkit.craftbukkit.v1_8_R3.CraftWorld) loc.getWorld()).getHandle();
		net.minecraft.server.v1_8_R3.Chunk chunk = w.getChunkAt(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
		net.minecraft.server.v1_8_R3.BlockPosition bp = new net.minecraft.server.v1_8_R3.BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		int combined = id + (data << 12);
		net.minecraft.server.v1_8_R3.IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(combined);
		w.setTypeAndData(bp, ibd, 2);
		chunk.a(bp, ibd);
	}
}
