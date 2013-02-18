package org.lloyd.RedditScraper

class LoggedEventController {
	def index() {
		redirect(action: "list", params: params)
	}

	def list(Integer max) {
		params.sort = "eventDateTime"
		params.order = "desc"
		[loggedEventInstanceList: LoggedEvent.list(params), loggedEventInstanceTotal: LoggedEvent.count()]
	}
	
}
