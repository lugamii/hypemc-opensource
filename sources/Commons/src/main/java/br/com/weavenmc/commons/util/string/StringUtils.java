package br.com.weavenmc.commons.util.string;

public class StringUtils {

	public static String createArgs(int index, String[] args, String defaultArgs, boolean color) {
		StringBuilder sb = new StringBuilder();
		for (int i = index; i < args.length; i++) 
			sb.append(args[i]).append((i + 1 >= args.length ? "" : " "));
		if (sb.length() == 0)
			sb.append(defaultArgs);
		return color ? sb.toString().replace("&", "§") : sb.toString();
	}
	
	public static String replace(String message, String[] old, String[] now) {
		String replaced = message;
		for (int i = 0; i < old.length; i++) 
			replaced = replaced.replace(old[i], now[i]);		
		return replaced;
	}
}
