package org.lloyd.RedditScraper

class TestingController {
	def time = {
		println "Hello"	
	}
	def update(){
		println "${new Date().toString()}"
		render "Book was deleted"
	}
	
	def recurringUpdates = {
		
	}
}
