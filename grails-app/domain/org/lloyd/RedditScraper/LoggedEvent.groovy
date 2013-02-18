package org.lloyd.RedditScraper

class LoggedEvent {
	String eventDesc
	String associatedLink
	Date eventDateTime
	int criticalLevel
	
	static def criticalLevels = [0: 'fatal', 1: 'critical', 2: 'error', 3: 'normal', 4: 'debug', 5: 'downloaded', 6: 'followed']
}
