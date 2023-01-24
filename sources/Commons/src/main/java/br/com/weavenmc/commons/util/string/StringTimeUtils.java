package br.com.weavenmc.commons.util.string;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTimeUtils {

	public static String toMillis(double d) {
		String string = String.valueOf(d);
		StringBuilder sb = new StringBuilder();
		boolean stop = false;
		for (char c : string.toCharArray()) {
			if (stop)
				return sb.append(c).toString();
			if (c == '.')
				stop = true;
			sb.append(c);
		}
		return sb.toString();
	}

	private static String fromLong(long lenth) {
		int days = (int) TimeUnit.SECONDS.toDays(lenth);
		long hours = TimeUnit.SECONDS.toHours(lenth) - days * 24;
		long minutes = TimeUnit.SECONDS.toMinutes(lenth) - TimeUnit.SECONDS.toHours(lenth) * 60L;
		long seconds = TimeUnit.SECONDS.toSeconds(lenth) - TimeUnit.SECONDS.toMinutes(lenth) * 60L;
		String totalDay = days + (days == 1 ? " dia " : " dias ");
		String totalHours = hours + (hours == 1 ? " hora " : " horas ");
		String totalMinutes = minutes + (minutes == 1 ? " minuto " : " minutos ");
		String totalSeconds = seconds + (seconds == 1 ? " segundo" : " segundos");
		if (days == 0)
			totalDay = "";
		if (hours == 0L)
			totalHours = "";
		if (minutes == 0L)
			totalMinutes = "";
		if (seconds == 0L)
			totalSeconds = "";
		String restingTime = totalDay + totalHours + totalMinutes + totalSeconds;
		restingTime = restingTime.trim();
		if (restingTime.equals(""))
			restingTime = "0 segundos";
		return restingTime;
	}

	public static String getDifferenceFormat(long time) {
		if (time <= 0) {
			return "";
		}

		long day = TimeUnit.SECONDS.toDays(time);
		long hours = TimeUnit.SECONDS.toHours(time) - (day * 24);
		long minutes = TimeUnit.SECONDS.toMinutes(time) - (TimeUnit.SECONDS.toHours(time) * 60);
		long seconds = TimeUnit.SECONDS.toSeconds(time) - (TimeUnit.SECONDS.toMinutes(time) * 60);

		StringBuilder sb = new StringBuilder();

		if (day > 0) {
			sb.append(day).append(" ").append("dia" + (day > 1 ? "s" : "")).append(" ");
		}

		if (hours > 0) {
			sb.append(hours).append(" ").append("hora" + (hours > 1 ? "s" : "")).append(" ");
		}

		if (minutes > 0) {
			sb.append(minutes).append(" ").append("minuto" + (minutes > 1 ? "s" : "")).append(" ");
		}

		if (seconds > 0) {
			sb.append(seconds).append(" ").append("segundo" + (seconds > 1 ? "s" : ""));
		}

		return sb.toString();
	}

	public static String formatDifference(long time) {
		long timeLefting = time - System.currentTimeMillis();
		long seconds = timeLefting / 1000L;
		return fromLong(seconds);
	}

	public static long parseDateDiff(String time, boolean future) throws Exception {
		Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
		Matcher m = timePattern.matcher(time);
		int years = 0;
		int months = 0;
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		boolean found = false;
		while (m.find()) {
			if (m.group() == null || m.group().isEmpty()) {
				continue;
			}
			for (int i = 0; i < m.groupCount(); i++) {
				if (m.group(i) != null && !m.group(i).isEmpty()) {
					found = true;
					break;
				}
			}
			if (found) {
				if (m.group(1) != null && !m.group(1).isEmpty()) {
					years = Integer.parseInt(m.group(1));
				}
				if (m.group(2) != null && !m.group(2).isEmpty()) {
					months = Integer.parseInt(m.group(2));
				}
				if (m.group(3) != null && !m.group(3).isEmpty()) {
					weeks = Integer.parseInt(m.group(3));
				}
				if (m.group(4) != null && !m.group(4).isEmpty()) {
					days = Integer.parseInt(m.group(4));
				}
				if (m.group(5) != null && !m.group(5).isEmpty()) {
					hours = Integer.parseInt(m.group(5));
				}
				if (m.group(6) != null && !m.group(6).isEmpty()) {
					minutes = Integer.parseInt(m.group(6));
				}
				if (m.group(7) != null && !m.group(7).isEmpty()) {
					seconds = Integer.parseInt(m.group(7));
				}
				break;
			}
		}
		if (!found) {
			throw new Exception("Illegal Time");
		}

		if (years > 20) {
			throw new Exception("Illegal Time");
		}

		Calendar c = new GregorianCalendar();
		if (years > 0) {
			c.add(Calendar.YEAR, years * (future ? 1 : -1));
		}
		if (months > 0) {
			c.add(Calendar.MONTH, months * (future ? 1 : -1));
		}
		if (weeks > 0) {
			c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
		}
		if (days > 0) {
			c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
		}
		if (hours > 0) {
			c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
		}
		if (minutes > 0) {
			c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
		}
		if (seconds > 0) {
			c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
		}
		return c.getTimeInMillis();
	}
}
