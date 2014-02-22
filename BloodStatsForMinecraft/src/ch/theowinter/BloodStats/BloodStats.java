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
	
	String servername = "";
	String uploadURL = "";
	HashMap<Player, Integer> onlinePlayers = new HashMap<Player, Integer>();
	
	 @Override
	    public void onEnable(){
		 this.saveDefaultConfig();
		 servername = BloodStats.this.getConfig().getString("server");
		 uploadURL = BloodStats.this.getConfig().getString("uploadURL");
		 getLogger().info("BloodStats successfully started");
		 StatsTracker();
	    }
	 
	    @Override
	    public void onDisable() {
		 	getLogger().info("BloodStats successfully exited");
	    }
	    
	    public void StatsTracker(){
	        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
	            @Override
	            public void run() {
	            	checkStats();
	            	StatsTracker();
	            }
	        }, 240*20L  );
	    }
	    
	    //Getting players from single bukkit server
	    public void checkStats(){
	    	Player[] currentlyOnlinePlayerArray = Bukkit.getServer().getOnlinePlayers();
	    	
	    	//Give Rewards
	    	updateAndRewardOnlinePlayers(currentlyOnlinePlayerArray);
	    	
	    	int currentPlayersOnline = currentlyOnlinePlayerArray.length;
    		 //	getLogger().info("Updating stats with new playerCount");
	    		updateDatabase(servername, currentPlayersOnline);
	    }
	 
	    public void updateDatabase(String servername, int i) {
			// SEND UPDATED VALUE
			Map<String, String> data = new HashMap<String, String>();
			data.put("timeStamp", getUnixTimeStamp());
			data.put(servername, ""+i);
			try {
				submitToWeb(uploadURL, data);
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
			out.writeBytes(content);
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
			}
			in.close();
		}
	    
	    public String getUnixTimeStamp(){
			long unixTime = System.currentTimeMillis() / 1000L;
			long makeUnixTimeImprecise = unixTime/100;
			makeUnixTimeImprecise = makeUnixTimeImprecise*100;
			return Long.toString(makeUnixTimeImprecise);
	    }
	    
	    public void updateAndRewardOnlinePlayers(Player[] currentlyOnlinePlayerArray){
	    	for (int i=0; i<currentlyOnlinePlayerArray.length; i++){
	    		Integer alreadyLoggedTime = onlinePlayers.get(currentlyOnlinePlayerArray[i]);
	    		if (alreadyLoggedTime != null){
	    			if (alreadyLoggedTime.intValue()>59){
	    				currentlyOnlinePlayerArray[i].sendMessage("Thank you for playing on bCloud! We just sent you 50 Crystals :) Do \"/warp shop\" to spend them.");
	    				//todo: DEPENDENCY iConomy
	    				Bukkit.getServer().dispatchCommand(getServer().getConsoleSender(), "money give "+currentlyOnlinePlayerArray[i].getName()+" 50");
	    				alreadyLoggedTime = 1;
	    				getLogger().info("BloodStats rewarded: "+currentlyOnlinePlayerArray[i].getName());
	    			}
			    	onlinePlayers.put(currentlyOnlinePlayerArray[i], (new Integer(alreadyLoggedTime.intValue()+4)));
	    		}
	    		else {
			    	onlinePlayers.put(currentlyOnlinePlayerArray[i], Integer.valueOf(1));
	    		}
	    	}
	    }
}
