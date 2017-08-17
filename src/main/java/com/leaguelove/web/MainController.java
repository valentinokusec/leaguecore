package com.leaguelove.web;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leaguelove.services.LiveStatsService;
import com.leaguelove.services.StatsService;

@Controller
public class MainController {
	
    
   
   
    
    @Autowired
	private LiveStatsService livestatsService;
    
    @Autowired
	private StatsService statsService;
    
    
       
   
    

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
    @RequestMapping("/getstatsdata/{name}")
    public @ResponseBody String GetStatsData( @PathVariable(value = "name") String name,Model model) throws Exception {


    	JSONObject data = statsService.getStats(name);
    	try {
    		
    	
    		return data.toString();
    		
    		
		}
    	catch(Exception e) {
    		
    		return null;
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
    @CrossOrigin(origins = "http://localhost:8001")
    @RequestMapping("/stats/{summoner}")
    public String getStats(
    		@PathVariable(value="summoner") String summoner, Model model) throws Exception {
      	

    	model.addAttribute("name",summoner);
    		
    	
        return  "stats";
    }
    

  
    
}
