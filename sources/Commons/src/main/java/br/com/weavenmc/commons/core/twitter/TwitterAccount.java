package br.com.weavenmc.commons.core.twitter;

public enum TwitterAccount {

	WEAVENBANS("x7EwwXKi899RPxHnQt0Dshttv", "VgaIwMZ4Y3DHezsfq1EIhWptaVU2XQVTdW3y0cHLmkbngqaTzb",
			"924694578480992258-JXE1bTaPM2XlveawwsyXBGc2iTadVqs", "e5oJzXSA3joU9uAQAzWqMVwpvetPIoCJfJudFmftYDjmo");

	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	private String accessSecret;

	TwitterAccount(String consumerKey, String consumerSecret, String accessToken, String accessSecret) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.accessSecret = accessSecret;
		this.accessToken = accessToken;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

}
