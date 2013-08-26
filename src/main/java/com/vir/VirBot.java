package com.vir;
import static com.es.rax.RaxLocator.TENANT_ID;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.jibble.pircbot.PircBot;
import com.es.api.ClientImpl;
import com.es.client.ClientManager;


public class VirBot extends PircBot{
	private class VirIsBack implements Runnable {

		@Override
		public void run() {
			sendMessage(Vir, "welcome back, you were last seen " + lastSeen.get());
			if (lastSeen.get() == 0) {
				return;
			}
			ClientManager.getClient().admin().indices().prepareRefresh(index).execute().actionGet();
			SearchResponse searchRes = ClientManager.getClient().prepareSearch(index).setFilter(
					FilterBuilders.rangeFilter("time-numeric").from(lastSeen).to(getTime()))
					//.setQuery(QueryBuilders.fieldQuery("sender", Vir).analyzeWildcard(true))
					.setQuery(QueryBuilders.multiMatchQuery(Vir, "sender", "message"))
					.setFrom(0).setSize(100)
					.execute().actionGet();
			SearchHit[] hits = searchRes.getHits().hits();
			for (SearchHit hit: hits) {
				Map<String, Object> source = hit.getSource();
				String evtTypeString = (String)source.get("eventtype");
				if (evtTypeString.equals("message")) {
					String notification = "time=" + (String)source.get("time") 
							+ " sender=" + (String)source.get("sender")
							+ " message=" + (String)source.get("message");
					sendMessage(Vir, notification);
				}
			}
			lastSeen.set(getTime());
		}
		
	}
	private static ExecutorService exec = Executors.newCachedThreadPool();
	private String botName = "ratanasv-bot";
	private ClientImpl impl = new ClientImpl();
	private String index = "test-index-55";
	private AtomicLong lastSeen = new AtomicLong(0);
	private AtomicLong lastNotified = new AtomicLong(0);
	private String tenantId = TENANT_ID.getPrefix() + "Asdfqwer";

	private String Vir = "vir_ratana";

	public VirBot() {
		this.setName(botName);
	}

	@Override
	public void onJoin(String channel, String sender, String login, String hostname) {
		
		insertDataPoint(channel, sender, "", "join");
		if (sender.equals(Vir)) {
			// won't report anything if join within 4 seconds.
			if (getTime() - lastNotified.get() > 4) {
				// fork a new thread since it might take awhile for the search operation.
				exec.execute(new VirIsBack());
				lastNotified.set(getTime());
			}
		}
	}

	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		insertDataPoint(channel, sender, message, "message");
	}

	@Override
	public void onPart(String channel, String sender, String login, String hostname) {
		insertDataPoint(channel, sender, "", "part");
		if (sender.equals(Vir)) {
			lastSeen.set(getTime());
		}
	}

	@Override
	protected void onChannelInfo(String channel,
			int userCount,
			String topic) {
		if (userCount > 700) {
			System.out.println("virsays " + channel + " " + userCount);
		}
		//this.joinChannel(channel);
	}
	
	@Override
	protected void onQuit(String sourceNick,
			String sourceLogin,
			String sourceHostname,
			String reason) {
		insertDataPoint("unknown", sourceNick, "", "quit");
		if (sourceNick.equals(Vir)) {
			lastSeen.set(getTime());
		}
	}

	private long getTime() {
		return System.currentTimeMillis() / 1000L;
	}

	private void insertDataPoint(String channel, String sender, String message, String evtType) {
		String time = String.valueOf(getTime());

		Map<String, String> map = new HashMap<String, String>();
		map.put("channel", channel);
		map.put("sender", sender);
		map.put("message", message);
		map.put("time", time);
		map.put("time-numeric", time);
		map.put("eventtype", evtType);
		impl.insert(tenantId, map);
	}
}
