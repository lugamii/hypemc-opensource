package br.com.adlerlopes.bypiramid.hungergames.schematic;

public final class EndTag extends Tag {

	public EndTag() {
		super("");
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public String toString() {
		return "TAG_End";
	}
}
