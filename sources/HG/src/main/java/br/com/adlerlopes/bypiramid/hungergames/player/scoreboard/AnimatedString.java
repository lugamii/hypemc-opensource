package br.com.adlerlopes.bypiramid.hungergames.player.scoreboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */
public class AnimatedString {

	private String text;
	private List<String> frames;
	private int position;
	private boolean finished;

	public AnimatedString(String text, String c1, String c2, String c3) {
		this(text, c1, c2, c3, 12);
	}

	public AnimatedString(String text, String c1, String c2, String c3, int p) {
		this.text = text;
		this.frames = new ArrayList<>();
		createFrames(c1, c2, c3, p);
	}

	private void createFrames(String c1, String c2, String c3, int p) {
		if (text != null && !text.isEmpty()) {
			for (int i = 0; i < text.length(); i++)
				if (text.charAt(i) != ' ')
					frames.add(c1 + text.substring(0, i) + c2 + text.charAt(i) + c3 + text.substring(i + 1));

			for (int i = 0; i < p; i++)
				frames.add(c1 + text);

			for (int i = 0; i < text.length(); i++)
				if (text.charAt(i) != ' ')
					frames.add(c3 + text.substring(0, i) + c2 + text.charAt(i) + c1 + text.substring(i + 1));

			for (int i = 0; i < p; i++)
				frames.add(c3 + text);
		}
	}

	public String next() {
		if (frames.isEmpty())
			return "";

		if (finished) {
			position--;
			if (position <= 0)
				finished = false;
		} else {
			position++;
			if (position >= frames.size()) {
				finished = true;
				return next();
			}
		}

		return frames.get(position);
	}
	
	public boolean isFinished() {
		return finished;
	}

}