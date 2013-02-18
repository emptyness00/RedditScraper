package org.lloyd.RedditScraper

class DownloadableItem {
	String link
	boolean downloaded = false
	
	static belongsTo = [topic: RedditTopic]
	
	static constraints = {
		link(unique: true)
	}
	
	String getType(){
		link.substring(link.lastIndexOf(".")+1)
	}
	
	String toString(){
		println "ID: $id: Link: $link; Downloaded: $downloaded"
	}
	
	def download = {
		println "Downloading: ${toString()}"
		new Download().download(this)
	}
}
