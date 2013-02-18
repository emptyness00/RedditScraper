package org.lloyd.RedditScraper

class FollowableItem {
	URL link
	boolean followed

	static belongsTo = [topic: RedditTopic]
	
	static constraints = {
		link(unique: true)
	}

	String getDomain(){
		String linkString = link.toString()
		if (!linkString.contains("http://")){
			return "Not a valid link"
		} else {
			int protocolEnd = linkString.indexOf("http://") + 7
			int endDomain = linkString.indexOf("/", protocolEnd)
			if (endDomain > 0 && protocolEnd > 0){
				return linkString.substring(protocolEnd, endDomain)
			} else {
				return linkString.substring(protocolEnd)
			}
		}
	}
}
