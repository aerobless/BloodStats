package ch.theowinter.BloodStats;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BloodStats extends JavaPlugin{
	
	int onlinePlayers = 0;
	
	 @Override
	    public void onEnable(){
	        // TODO Insert logic to be performed when the plugin is enabled
		 this.saveDefaultConfig();
		 getLogger().info("BloodStats successfully started");
		 StatsTracker();
	    }
	 
	    @Override
	    public void onDisable() {
	        // TODO Insert logic to be performed when the plugin is disabled
		 	getLogger().info("BloodStats successfully exited");
	    }
	    
	    public void StatsTracker(){
	        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
	            @Override
	            public void run() {
	                // Your code goes here
	            	checkStats();
	            	StatsTracker();
	            }
	        }, 60*20L  );
	    }
	    

	    //Getting players from single bukkit server
	    public void checkStats(){
	    	Player[] player = Bukkit.getServer().getOnlinePlayers();
	    	
	    	int currentPlayersOnline = player.length;
	    	if (currentPlayersOnline != onlinePlayers) {
    		 	getLogger().info("Updating stats with new playerCount");
	    		String servername = BloodStats.this.getConfig().getString("server");
	    		updateDatabase(servername, currentPlayersOnline);
	    		onlinePlayers = (currentPlayersOnline+0);
	    	}
	    }
	 
	    public void updateDatabase(String servername, int i) {
			// SEND UPDATED VALUE
			Map<String, String> data = new HashMap<String, String>();
			data.put("timeStamp", getUnixTimeStamp());
			data.put("servername", ""+i);
			try {
				submitToWeb(BloodStats.this.getConfig().getString("uploadURL"), data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	    
	    public void submitToWeb(String url, Map<String, String> data) throws Exception {
			URL siteUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());

			Set<String> keys = data.keySet();
			Iterator<String> keyIter = keys.iterator();
			String content = "";
			for (int i = 0; keyIter.hasNext(); i++) {
				Object key = keyIter.next();
				if (i != 0) {
					content += "&";
				}
				content += key + "=" + URLEncoder.encode(data.get(key), "UTF-8");
			}
			System.out.println(content);
			out.writeBytes(content);
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			in.close();
		}
	    
	    public String getUnixTimeStamp(){
			long unixTime = System.currentTimeMillis() / 1000L;
			long makeUnixTimeImprecise = unixTime/100;
			makeUnixTimeImprecise = makeUnixTimeImprecise*100;
			System.out.println(makeUnixTimeImprecise);
			//Time made unprecise for 100s to allow for all servers to post stats.
			return Long.toString(makeUnixTimeImprecise);
	    }

}
