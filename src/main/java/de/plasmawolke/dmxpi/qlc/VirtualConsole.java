package de.plasmawolke.dmxpi.qlc;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualConsole {

	private final static Logger logger = LoggerFactory.getLogger(VirtualConsole.class);

	private Set<VirtualConsoleButton> buttons = new HashSet<VirtualConsoleButton>();

	private static final String url = "http://192.168.23.121:9999/";

	public void clickButton(Integer id) {

		for (VirtualConsoleButton virtualConsoleButton : buttons) {

			if (id == virtualConsoleButton.getId()) {
				virtualConsoleButton.click();
				return;
			}

		}

	}

	public void populate() throws Exception {

		// Instantiate HttpClient
		HttpClient httpClient = new HttpClient();

		// Configure HttpClient, for example:
		httpClient.setFollowRedirects(false);

		// Start HttpClient
		httpClient.start();
		ContentResponse response = httpClient.newRequest(url).method(HttpMethod.GET)
				.agent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:17.0) Gecko/20100101 Firefox/17.0").send();

		String html = response.getContentAsString();

		httpClient.stop();

		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		Elements links = doc.select("a.vcbutton");

		logger.debug("Found " + links.size() + " buttons.");

		for (Element element : links) {

			String id = element.attr("id");
			String name = element.text().trim();

			VirtualConsoleButton vcb = new VirtualConsoleButton();

			vcb.setId(Integer.parseInt(id));
			vcb.setName(name);

			buttons.add(vcb);

			logger.debug("Collected " + vcb);

		}

	}

}
