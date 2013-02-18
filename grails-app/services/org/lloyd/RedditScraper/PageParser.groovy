package org.lloyd.RedditScraper

import groovyx.net.http.HttpResponseException
import groovyx.net.http.HTTPBuilder;

import java.util.List;

import javax.activation.MimetypesFileTypeMap;

class PageParser {
	public def html
	public String parentUrl
	private List redditLinks = []
	public downloadAllImages = false
	public was18Form = false
	
	public HTTPBuilder http

	public def getLinks(FollowableItem followable){
		if (followable && followable.domain == "imgur.com"){
			downloadAllImages = true
		}
		String domain = followable.link.toString().substring(0, followable.link.toString().indexOf(".com/") + ".com/".length())
		String urlPath = followable.link.toString().substring(followable.link.toString().indexOf(".com/") + ".com/".length())
		if (urlPath.contains("#")){
			urlPath = urlPath.substring(0, urlPath.indexOf("#"))
		}
		if (!http){
			http = new HTTPBuilder(domain)
		}
		try{
			http.get(path: urlPath){resp, html ->
				def listOfImages = getImageNames(html)
				if (listOfImages.size() > 0){
					listOfImages.each { String imageName, String ext ->
						if (domain.contains("imgur.com")){
							domain = "http://i.imgur.com/"
						}
						String imgurl = "$domain$imageName$ext"
						def download = new DownloadableItem(link: imgurl, topic: followable.topic)
						if (!download.save(flush: true)){
							download.errors.each{
								println "Error Saving Download: $it"
							}
						}
					}
				} else {
					if (domain.contains("imgur.com")){
						domain = "http://i.imgur.com/"
						String imgurl = "$domain$urlPath"
						if (hasNoExtension(imgurl)){
							imgurl = "${imgurl}.jpg"
						}
						def download = new DownloadableItem(link: imgurl, topic: followable.topic)
						if (!download.save(flush: true)){
							download.errors.each{
								println "Error Saving Download: $it"
							}
						}
					}
				}
				
			}
		} catch(HttpResponseException hrE){
		    if (!hrE.message.contains("Not Found")){
				throw hrE
			}
		} catch (Exception ex){
			ex.printStackTrace()
			println "Unknown exception, continuing if possible"
		}
		followable.followed = true
		if (!followable.save(flush:true)){
			followable.errors.each{ println "Error saving Followable: $it" }
		}
		
	}
	
	public boolean hasNoExtension(String link){
		String fileName = "${link.tokenize("/")[-1]}"
		if (!fileName.contains(".")){
			return true
		} else {
			return false
		}
	}

	public def getImageNames(def html){
		def list = [:]
		String hashKeyString = "\"hash\":\""
		String extKeyString = "\"ext\":\""
		boolean end = false
		int endOfHash = 0
		while (!end){
			String htmlString = html.toString()
			String img = ""
			String ext = ""
			int hash = htmlString.indexOf(hashKeyString, endOfHash)
			if (hash > 0){
				endOfHash = htmlString.indexOf("\"", hash + hashKeyString.length())
				img = htmlString.substring(hash + hashKeyString.length(), endOfHash)
				int extLoc = htmlString.indexOf(extKeyString, endOfHash)
				int endOfExt = htmlString.indexOf("\"", extLoc + extKeyString.length())
				ext = htmlString.substring(extLoc + extKeyString.length(), endOfExt)
				list.put(img, ext)
			} else {
				end = true
			}
		}
		return list
	}

	public String getNextUrl(){
		if (stripForDomain(parentUrl) == "www.reddit.com"){
			html.depthFirst().collect { it }.findAll { it.name() == "a" }.each {
				String domain = stripForDomain(it.@href.text())
				if (domain == "www.reddit.com" && it.@href.text().contains("comments")){
					redditLinks << it.@href.text()
				}
			}
			String lastItemCommentsLink = redditLinks.last()
			int commentsLoc = lastItemCommentsLink.indexOf("comments/") + "comments/".length()
			int nextSlashLoc = lastItemCommentsLink.indexOf("/", commentsLoc + 1)
			String lastItemName = lastItemCommentsLink.substring(commentsLoc, nextSlashLoc)
			return "http://www.reddit.com/?count=100&after=t3_$lastItemName"
		} else {
			return ""
		}
	}
	
	public void getTopicsFromSubreddit(String subreddit){
		if (!http){
			http = new HTTPBuilder("http://www.reddit.com/r/$subreddit")
		}
		doParse(http, [limit: 100], subreddit, "", false)
	}
	
	public String doParse(HTTPBuilder http, def params, String subreddit, String lastLastId, boolean finished){
		String lastId
		if (lastLastId){
			params = [limit: 100, after: "t3_$lastLastId"]
		}
		http.get( path: "/r/$subreddit/.json", query: params ) { resp, json ->
			json.each {
				String key = it.key
				if (key == "data"){
					def children = it.value.get("children")
					if (children){
						lastId = children.last().get("data").get("id")
						children.each{ child ->
							String name = child.get("data").get("name")
							String url = child.get("data").get("url")
							String author = child.get("data").get("author")
							String title = child.get("data").get("title")
							Long created = child.get("data").get("created")
							Date date = new Date(created)
							RedditTopic topic = new RedditTopic(name: name, link: url, author: author, subreddit: Subreddit.findByName(subreddit), timestamp: new Date(created))
							topic.store()
							lastId = child.get("data").get("id")
						}
						if (lastId != lastLastId){
							doParse(http, params, subreddit, lastId, finished)
						}						
					}
				}
			}
		}
		return lastId
	}
}
