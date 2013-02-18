package org.lloyd.reddit.ripper;

import groovyx.net.http.HTTPBuilder;
import groovyx.net.http.HttpResponseException;

public class JsonRedditParser {
	public MasterDownloadList masterDownloadList
	public MasterFollowList masterFollowList
	public String parentUrl = "http://www.reddit.com"
	public int depth = 1
	private int currentDepth = 0
	
	private SubredditHistory history


	private String imgurApiId = "a15f626ad861da34c823899a70a72d6f"

	public void  parse(String subreddit, String afterId, MasterDownloadList inMasterDownloadList, boolean noDepth = false, File subredditListFile = null){
		
		if (!inMasterDownloadList){
			throw IllegalArgumentException("No Master Download List was set.  This happened in JsonRedditParser.parse()")
		}
		masterDownloadList = inMasterDownloadList
		history = new SubredditHistory(masterDownloadList.rootDir)
		masterFollowList = new MasterFollowList(masterDownloadList.rootDir)
		def subredditList = []
		if (subredditListFile){
			subredditList = readList(subredditListFile)
		} else {
			subredditList << subreddit
		}
		subredditList.each{ String thisSubreddit ->
			parseUrl(thisSubreddit, "", noDepth)
			masterDownloadList.saveToList(masterDownloadList.rootDir)
			masterDownloadList.downloadList = []
			masterFollowList.saveToList(masterDownloadList.rootDir)
			masterFollowList.followList = []
		}
		
//		followLinks()
//		masterFollowList.storeList(masterDownloadList.rootDir)
//		masterFollowList.followList = []
	}

	public void parseUrl(String subreddit, String afterId, boolean noDepth){
		def http = new HTTPBuilder( parentUrl )

		String lastId
		currentDepth++

		def params = [limit: 100]
		if (afterId && afterId.length() > 0){
			params.put("after", afterId)
		}
		String topItem
		String lastTopItem = history.getTopItem(subreddit)
		println "Scanning $subreddit with params: $params"
		boolean finished = false
		try{
			lastId = doParse(http, params, subreddit, lastTopItem, finished)
		} catch (HttpResponseException e){
			sleep 100
			lastId = doParse(http, params, subreddit, lastTopItem, finished)
		} catch (Exception e) {
			println "Couldn't parse this: $subreddit with params: $params"
			e.printStackTrace()
		}
		
		if (lastId && !finished && (noDepth || currentDepth < depth)){
			sleep (2000)
			parseUrl(subreddit, "t3_$lastId", noDepth)
		}
		history.addItem(subreddit, topItem)
	}

	public void followLinks(){
		masterFollowList.followList.each{Downloadable dl ->
			if (!masterFollowList.followed.contains(dl.url)){
				PageParser pp = new PageParser()
				//	pp.slurpPage(url)
				masterDownloadList = pp.getLinks(dl, masterDownloadList)
				masterDownloadList.saveToList(masterDownloadList.rootDir)
			}
		}
	}
	
	public String doParse(HTTPBuilder http, def params, String subreddit, String lastTopItem, boolean finished){
		String lastId
		http.get( path: "/r/$subreddit/.json", query: params ) { resp, json ->

			println resp.status

			json.each {
				String key = it.key
				if (key == "data"){
					def children = it.value.get("children")
					if (children){
						children.each{ child ->
							String name = child.get("data").get("name")
							if (name == lastTopItem){
//									finished = true
							}
							if (!finished){
								String url = child.get("data").get("url")
								String author = child.get("data").get("author")
						
								if (PageLinks.isTargetDownloadUrl(url)){
									masterDownloadList.addItem(url, author, subreddit)
								} else if (PageLinks.isTargetFollowUrl(url)){
									masterFollowList.addItem(url, author, subreddit)
								} else if (PageLinks.isMovieUrl(url)){
//										masterDownloadList.addItem(url)
								}
							}
						}
						if (!finished){
							lastId = children.last().get("data").get("id")
						}
					}
				}
			}
		}
		return lastId
	}
	
	public def readList(File list){
		def returnList = []
		println "List File: $list"
		list.eachLine{
			println "It: $it"
			returnList << it
		}
		println "List: $returnList"
		return returnList
	}
}
