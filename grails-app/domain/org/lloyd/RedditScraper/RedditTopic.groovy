package org.lloyd.RedditScraper

class RedditTopic {
	String name
	String link
	String author
	boolean followed = false
	Date timestamp

	static belongsTo = [subreddit: Subreddit]

	static hasMany = [downloadableItems: DownloadableItem, followableItems: FollowableItem]

	static constraints = {
		name(blank: false, nullable: false, unique: true)
		link(blank: true, nullable: true)
		followed(blank: false, nullable: false)
		timestamp(blank: true, nullable: true)
	}

	String toString(){
		return "$name; $link; $followed; $timestamp"
	}

	def store(){
		if (!RedditTopic.findByLink(this.link)){
			if (!this.save(flush:true)){
				this.errors.each{println "Error saving Topic: $it"}
			}
		}
	}

	def handle = {
		if (new PageLinks().isTargetDownloadUrl(link)){
			if (hasNoExtension(link)){
				link = "${link}.jpg"
			}
			def download = new DownloadableItem(link: link, topic: this)
			if (!download.save(flush: true)){
				download.errors.each{ println "Error Saving Download: $it" }
			}
		} else if (new PageLinks().isTargetFollowUrl(link)){
			def follow = new FollowableItem(link: link, topic: this)
			if (!follow.save(flush: true)){
				follow.errors.each{ println "Error Saving Follow: $it" }
			}
		}
		FollowableItem.list().each{ FollowableItem item ->
			if (!item.followed){
				new PageParser().getLinks(item)
			}
		}
		followed = true
		if (!save()){
			errors.each{ println "Errors Saving Topic: $it" }
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
	
	int getUndownloadedItems(){
		int count = 0
		downloadableItems.each{DownloadableItem item ->
			if (!item.downloaded){
				count ++
			}
		}
		return count
	}
}
