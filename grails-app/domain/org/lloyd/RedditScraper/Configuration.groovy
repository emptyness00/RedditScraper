package org.lloyd.RedditScraper

class Configuration {
	String saveToDir = "E:/temp"
	
	static constraints = {
		saveToDir(nullable: false, blank: false)
	}
	
	String toString(){
		return "$id, $saveToDir"
	}
}
