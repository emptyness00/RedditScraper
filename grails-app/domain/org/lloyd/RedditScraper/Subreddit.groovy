package org.lloyd.RedditScraper

class Subreddit {
	String name
	String latestDownloadedTopic
	int totalDownloadedTopics
	Date lastDownloadedFrom
	
	static hasMany = [topics: RedditTopic]
	
	static constraints = {
		name(blank: false, nullable: false, unique: true)
		latestDownloadedTopic(blank: true, nullable: true)
		totalDownloadedTopics(blank: true, nullable: true)
		lastDownloadedFrom(blank: true, nullable: true)
	}
	
	String toString(){
		return "$name; $latestDownloadedTopic; $totalDownloadedTopics; $lastDownloadedFrom; ${topics.collect{it.toString()}}"
	}
	
	int getAvailableDownloadCount(){
		int count = 0
		topics.each{ RedditTopic topic ->
			count += topic.downloadableItems.size()
		}
		return count
	}
}
