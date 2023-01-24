package org.inventivetalent.hologram.touch;

public enum TouchAction {
	
	RIGHT_CLICK, LEFT_CLICK, UNKNOWN;

	private TouchAction() {
	}

	public static TouchAction fromUseAction(Object useAction) {
		if (useAction == null) {
			return UNKNOWN;
		}
		int i = ((Enum<?>) useAction).ordinal();
		switch (i) {
		case 0:
			return RIGHT_CLICK;
		case 1:
			return LEFT_CLICK;
		}
		return UNKNOWN;
	}
	
}
