package org.lloyd.RedditScraper

class DownloadableItem {
	String link
	boolean downloaded = false

	static belongsTo = [topic: RedditTopic]

	static constraints = { link(unique: true) }

	String getType(){
		link.substring(link.lastIndexOf(".")+1)
	}

	String toString(){
		println "ID: $id: Link: $link; Downloaded: $downloaded"
	}

	def download = {
		println "Downloading: ${toString()}"
		try{
			new Download().download(this)
			new LoggedEvent(criticalLevel: 5, eventDateTime: new Date(), eventDesc: "Download Complete", associatedLink: this.link).save(flush: true)
		} catch (Exception e){
			new LoggedEvent(criticalLevel: 0, eventDateTime: new Date(), eventDesc: e.message, associatedLink: this.link).save(flush: true)
		}
	}
}
