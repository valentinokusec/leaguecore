package com.leaguelove.web;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;



import com.leaguelove.services.LiveStatsService;

import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.core.common.Season;
import com.robrua.orianna.type.core.match.Participant;
import com.robrua.orianna.type.core.matchlist.MatchReference;
import com.robrua.orianna.type.core.staticdata.Champion;
import com.robrua.orianna.type.core.stats.ChampionStats;
import com.robrua.orianna.type.core.summoner.Summoner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
	
    
   
   
    
    @Autowired
	private LiveStatsService livestatsService;
    
    
       
   
    

    @RequestMapping("/index")
    public String Index() throws Exception {


      	
        return  "index";
    }


    @RequestMapping("/getdata/{name}")
    public @ResponseBody String GetData( @PathVariable(value = "name") String name,Model model) throws Exception {


    	JSONArray data = livestatsService.getLiveStats(name);
    	try {
    		
    		data.getString(0);
    		
    		data.put("no_game");
    		return data.toString();
    		
    		
		}
    	catch(Exception e) {
    		
    		return data.toString();
		}
    	
    }
    @RequestMapping("/gethistory/{name}")
    public @ResponseBody String GetHistory( @PathVariable(value = "name") String name,Model model) throws Exception {


    	JSONArray data = livestatsService.getHistory(name);
    	try {
    		
    		data.getString(0);
    		
    		data.put("no_game");
    		return data.toString();
    		
    		
		}
    	catch(Exception e) {
    		
    		return data.toString();
		}
    	
    }
    @RequestMapping(value="/gethistoryroles",method=RequestMethod.POST)
    public @ResponseBody String GetHistoryRoles( @RequestBody String get_data ,Model model) throws Exception {

    	String data=get_data.replaceAll("%7B", "{");
    	data=data.replaceAll("%22", "\"");
    	data=data.replaceAll("%22", ":");
    	data=data.replaceAll("%5B", "[");
    	data=data.replaceAll("%5D", "]");
    	data=data.replaceAll("%3A", ":");
    	data=data.replaceAll("%7D", "}");
    	data=data.replaceAll("%2C", ",");
    	
    	JSONObject data_get = new JSONObject(data);
    	JSONArray data_a = livestatsService.getHistoryRoles(data_get);
	
    		return data_a.toString();
		
    	
    }
    @RequestMapping(value="/getsort",method=RequestMethod.POST)
    public @ResponseBody String getSort( @RequestBody String get_data ,Model model) throws Exception {

    	String data=get_data;
    	data=data.replaceAll("%22", "\"");
    
    	data=data.replaceAll("%5B", "[");
    	data=data.replaceAll("%5D", "]");
    

    	data=data.replaceAll("%2C", ",");
    	data=data.substring(1, data.length()-2);
    	
    	JSONArray data_a = livestatsService.getSort(new JSONArray(data));
	
    		return data_a.toString();
		
    	
    }
//    @CrossOrigin(origins = "http://leaguecore.com")
//    @RequestMapping("/getrandommessage")
//    public String getRandomMessage() throws Exception {
//
//
//      	
//        return  mainService.getRandomMessage().toString();
//    }

    
    @CrossOrigin(origins = "http://localhost:8001")
    @RequestMapping("/livestats/{summoner}")
    public String liveStats(
    		@PathVariable(value="summoner") String summoner, Model model) throws Exception {
      	

    	model.addAttribute("name",summoner);
    		
    	
        return  "main";
    }
    

  
    
}
