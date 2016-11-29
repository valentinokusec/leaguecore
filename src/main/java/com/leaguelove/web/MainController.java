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

import com.leaguelove.domain.SummonerProfile;
import com.leaguelove.services.LeadBoardService;
import com.leaguelove.services.LiveStatsService;
import com.leaguelove.services.MainService;
import com.leaguelove.services.MatchService;
import com.leaguelove.services.SubmitVotesService;
import com.leaguelove.services.UserService;
import com.leaguelove.services.VotesService;
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
	private MainService mainService;
    
    @Autowired
	private LeadBoardService leadBoardService;
    
    @Autowired
	private LiveStatsService livestatsService;
    
    @Autowired
	private MatchService matchService;
    
    @Autowired
	private SubmitVotesService submitvotesService;
    
    @Autowired
	private UserService userService;
    
    @Autowired
	private VotesService votesService;
       
    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/getgeneraldata")
    public String GeneralData(
          @RequestParam(value = "name", required = true) String name) throws Exception {


      	
        return  mainService.getGeneralData(name).toString();
    }
    
//    @CrossOrigin(origins = "http://leaguecore.com")
//    @RequestMapping("/checkname")
//    public String CheckSummonerName(
//          @RequestParam(value = "name", required = false) String name) throws Exception {
//
//
//      	
//        return  mainService.CheckSummonerName(name).toString();
//    }
    @RequestMapping("/index")
    public String Index() throws Exception {


      	
        return  "index";
    }
    @RequestMapping("/checkname/{name}")
    public @ResponseBody String CheckName( @PathVariable(value = "name") String name) throws Exception {


    	String check=mainService.CheckSummonerName(name).toString();
    	if (check.contains("ok")) {
    		 return  name;
		}
    	else
    	{
    		return "nope";
    	}
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


    	JSONArray data_get = livestatsService.getHistory(get_data);
//    	try {
//    		
//    		data.getString(0);
//    		
//    		data.put("no_game");
//    		return data.toString();
//    		
//    		
//		}
//    	catch(Exception e) {
//    		
    		return get_data.toString();
		
    	
    }
    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/getrandommessage")
    public String getRandomMessage() throws Exception {


      	
        return  mainService.getRandomMessage().toString();
    }
    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/getvotes")
    public String getVotes(
          @RequestParam(value = "pagination", required = true) int pagination) throws Exception {

      		        
		return  leadBoardService.getVotes(pagination).toString();
    }

    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/getvotesforsummoner")
    public String getVotesForSummoner(
          @RequestParam(value = "name", required = true) String name) throws Exception {

      		        
		return  leadBoardService.getVotesForSummoner(name).toString();
    }	
    
    @CrossOrigin(origins = "http://localhost:8001")
    @RequestMapping("/livestats/{summoner}")
    public String liveStats(
    		@PathVariable(value="summoner") String summoner, Model model) throws Exception {
      	

    	model.addAttribute("name",summoner);
    		
    	
        return  "main";
    }
    
    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/lastmatch")
    public String lastmatch(
          @RequestParam(value = "name", required = false) String name) throws Exception {

//        Greeting greeting = new Greeting(String.format(TEMPLATE, name));
//        greeting.add(linkTo(methodOn(GreetingController.class).greeting(name)).withSelfRel());
      	
        return  matchService.getLastMatch(name).toString();
    }
  
    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/votedata")
    public String voteData(
            @RequestParam(value = "data", required = false) String data) throws Exception {

//          Greeting greeting = new Greeting(String.format(TEMPLATE, name));
//          greeting.add(linkTo(methodOn(GreetingController.class).greeting(name)).withSelfRel());
        	
          return  matchService.voteData(data).toString();
      }
    
    	
    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/getvotesforsubmit")
    public String getVotes(
          @RequestParam(value = "name", required = true) String name) throws Exception {
    	
    	
      		        
		return  submitvotesService.getVotes(name).toString();
    }

    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/submitvotes")
    public String submitVotes(
          @RequestParam(value = "data", required = true) String data) throws Exception {

    	 String[] votes1=data.split(",");
       
		return  submitvotesService.submitVotes(votes1).toString();
    }	
    
    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/signup")
    public String signUp(
          @RequestParam(value = "data", required = true) String data) throws Exception {
    	
    	JSONObject json_data=new JSONObject(data);
      		        
		return  userService.signUp(json_data.getString("username"),json_data.getString("password"),json_data.getString("summonername")).toString();
    }

    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/signin")
    public String signIn(
          @RequestParam(value = "data", required = true) String data) throws Exception {

        JSONObject json_data=new JSONObject(data);	
        
		return  userService.signIn(json_data.getString("username"),json_data.getString("password")).toString();
    }	
    
    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/getvotesforvotes")
    public String getVotesForVotes(
          @RequestParam(value = "data", required = false) String data) throws Exception {

    	  JSONObject data_json=new JSONObject(data);
      	
        return  votesService.getVotes(data_json.getString("name"),data_json.getInt("pagination")).toString();
    }
    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/getlastvotes")
    public String getLastVotes(
          @RequestParam(value = "data", required = false) String data) throws Exception {

    	  JSONObject data_json=new JSONObject(data);
      	
        return  votesService.getLastVotes(data_json.getString("name"),data_json.getLong("match_id")).toString();
    }
    
    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/getvotesbyid")
    public String getVotesById(
          @RequestParam(value = "data", required = false) String data) throws Exception {

    	  JSONObject data_json=new JSONObject(data);
      	
        return  votesService.getVotesById(data_json.getString("name"),data_json.getLong("match_id")).toString();
    }
    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/getrecenthistory")
    public String RecentHistory(
          @RequestParam(value = "name", required = true) String name) throws Exception {


      	
        return  mainService.getRecentHistory(name).toString();
    }
   

	@CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/getmatch")
    public String GetMatch(
          @RequestParam(value = "matchid", required = false) long matchid) throws Exception {
    	

      	
        return  mainService.getMatch(matchid).toString();
    }
    

	@CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/getgeneralhistory")
    public String getgeneralhistory(
          @RequestParam(value = "data", required = true) String data) throws Exception {
          JSONObject json_data=new JSONObject(data);

      	
        return  mainService.getGeneralHistory(json_data.getString("name"),json_data.getInt("pagination"),json_data.getString("champion")).toString();
    }
    @CrossOrigin(origins = "http://leaguecore.com")
    @RequestMapping("/getchampionhistory")
    public String getchampionhistory(
          @RequestParam(value = "data", required = true) String data) throws Exception {
    	  JSONObject json_data=new JSONObject(data);

      	
        return  mainService.getChampionHistory(json_data.getString("name"),json_data.getString("champion")).toString();
    }
    

    
}
