package org.lloyd.reddit.ripper

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException;
import static groovyx.net.http.ContentType.URLENC

import org.ccil.cowan.tagsoup.Parser

class PageParser {
	public def html
	public String parentUrl
	private List redditLinks = []
	public downloadAllImages = false
	public was18Form = false


	public void slurpPage(String urlString){
		if (!urlString){
			throw new IllegalArgumentException("URL was not provided.  Please correct this.  URL provided was: $urlString")
		}
		def slurper = new XmlSlurper(new Parser())
		URL url = new URL(urlString)
		url.withReader("UTF-8") { reader ->
			html = slurper.parse(reader)
		}
	}

	public def getLinks(Downloadable dl, MasterDownloadList mdf){
		if (!mdf){
			throw new IllegalArgumentException("Master Download List was null.  This shouldn't be...")
		}
		if (dl.url && stripForDomain(dl.url) == "imgur.com"){
			downloadAllImages = true
		}
		def links = new PageLinks()
		println "URL: $dl.url"
		String domain = dl.url.substring(0, dl.url.indexOf(".com/") + ".com/".length())
		String urlPath = dl.url.substring(dl.url.indexOf(".com/") + ".com/".length())
		if (urlPath.contains("#")){
			urlPath = urlPath.substring(0, urlPath.indexOf("#"))
			println urlPath
		}
		def http = new HTTPBuilder(domain)
		println "Getting Images from: $dl.url"
		try{
			http.get(path: urlPath){resp, html ->
				println "Returned?"
				def listOfImages = getImageNames(html)
				println "Image Names: $listOfImages"
				if (listOfImages.size() > 0){
					listOfImages.each { String imageName, String ext ->
						if (domain.contains("imgur.com")){
							domain = "http://i.imgur.com/"
						}
						String imgurl = "$domain$imageName$ext"
						mdf.addItem(imgurl, dl.author, dl.subreddit)
					}
				} else {
					if (domain.contains("imgur.com")){
						domain = "http://i.imgur.com/"
						urlPath = "${urlPath}.jpg"
					}
					mdf.addItem("$domain$urlPath", dl.author, dl.subreddit)
				}
			}
		} catch (HttpResponseException e){
			if (e.getMessage().contains("Not Found")){
				println "Address: $dl.url was not found.  Skipping."
			} else {
				throw e
			}
		} catch (SocketTimeoutException stE){
			println "Timed out: $address, will not try again."
		} catch (Exception ex){
			ex.printStackTrace()
			println "Unknown exception, continuing if possible"
		}

		//		println html
		//		html.depthFirst().collect { it }.findAll { it.name() == "img" }.each {
		//			String imgUrl = it.@src.text()
		//			String domain = stripForDomain(imgUrl)
		//			if (PageLinks.downloadDomains.contains(domain)){
		//				mdf.addItem(imgUrl)
		//			}
		//		}
		//		links.addFollow(getNextUrl())
		return mdf
	}

	public stripForDomain(String url) {
		if (!url.contains("http://")){
			return "Not a valid link"
		} else {
			int protocolEnd = url.indexOf("http://") + 7
			int endDomain = url.indexOf("/", protocolEnd)
			if (endDomain > 0 && protocolEnd > 0){
				return url.substring(protocolEnd, endDomain)
			} else {
				return url.substring(protocolEnd)
			}
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
			println "RedditLinks: $redditLinks"
			String lastItemCommentsLink = redditLinks.last()
			int commentsLoc = lastItemCommentsLink.indexOf("comments/") + "comments/".length()
			int nextSlashLoc = lastItemCommentsLink.indexOf("/", commentsLoc + 1)
			String lastItemName = lastItemCommentsLink.substring(commentsLoc, nextSlashLoc)
			return "http://www.reddit.com/?count=100&after=t3_$lastItemName"
		} else {
			return ""
		}
	}

	public void submit18Form(){

		def http = new HTTPBuilder( parentUrl )
		// auth omitted...
		def postBody = [over18:'yes'] // will be url-encoded

		http.post(body: postBody,
				requestContentType: URLENC ) { resp ->

					println "Response status: ${resp.statusLine}"
					//		  assert resp.statusLine.statusCode == 200
				}
	}
}
