package org.lloyd.RedditScraper

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*

class SubredditController {

	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def index() {
		redirect(action: "list", params: params)
	}

	def list(Integer max) {
		params.sort = "name"
		params.order = "asc"
		[subredditInstanceList: Subreddit.list(params), subredditInstanceTotal: Subreddit.count(), downloads: [count: 0, current: ""]]
	}

	def create() {
		[subredditInstance: new Subreddit(params)]
	}

	def save() {
		String nameString = params.name
		String[] names = nameString.split()
		names.each{
			def subredditInstance = new Subreddit(name: it)
			if (!subredditInstance.save(flush: true)) {
				render(view: "create", model: [subredditInstance: subredditInstance])
				return
			}
			flash.message = message(code: 'default.created.message', args: [message(code: 'subreddit.label', default: 'Subreddit'), subredditInstance.id])
		}
		redirect(action: "list")
	}

	def show(Long id) {
			def subredditInstance = Subreddit.get(id)
			if (!subredditInstance) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'subreddit.label', default: 'Subreddit'), id])
				redirect(action: "list")
				return
			}

			[subredditInstance: subredditInstance]
		}

		def edit(Long id) {
			def subredditInstance = Subreddit.get(id)
			if (!subredditInstance) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'subreddit.label', default: 'Subreddit'), id])
				redirect(action: "list")
				return
			}

			[subredditInstance: subredditInstance]
		}

		def update(Long id, Long version) {
			def subredditInstance = Subreddit.get(id)
			if (!subredditInstance) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'subreddit.label', default: 'Subreddit'), id])
				redirect(action: "list")
				return
			}

			if (version != null) {
				if (subredditInstance.version > version) {
					subredditInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
					[message(code: 'subreddit.label', default: 'Subreddit')] as Object[],
					"Another user has updated this Subreddit while you were editing")
					render(view: "edit", model: [subredditInstance: subredditInstance])
					return
				}
			}

			subredditInstance.properties = params

			if (!subredditInstance.save(flush: true)) {
				render(view: "edit", model: [subredditInstance: subredditInstance])
				return
			}

			flash.message = message(code: 'default.updated.message', args: [message(code: 'subreddit.label', default: 'Subreddit'), subredditInstance.id])
			redirect(action: "show", id: subredditInstance.id)
		}

		def delete(Long id) {
			def subredditInstance = Subreddit.get(id)
			if (!subredditInstance) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'subreddit.label', default: 'Subreddit'), id])
				redirect(action: "list")
				return
			}

			try {
				subredditInstance.delete(flush: true)
				flash.message = message(code: 'default.deleted.message', args: [message(code: 'subreddit.label', default: 'Subreddit'), id])
				redirect(action: "list")
			}
			catch (DataIntegrityViolationException e) {
				flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'subreddit.label', default: 'Subreddit'), id])
				redirect(action: "show", id: id)
			}
		}

		def collectFollowLinks = {
//			println "Hello"
			def id = params.id
			println "Subreddit Id: $id"
			Subreddit subreddit = Subreddit.findById(id)
//			println "For Subreddit: $subreddit"
			new PageParser().getTopicsFromSubreddit(subreddit.name)
//			println "Doing Render: [id: $subreddit.id, count: ${subreddit.topics.size()}]"
			render([id: subreddit.id, count: subreddit.topics.size()] as JSON)
		}
		
		def collectTopics = {
			println "Hello"
			println params
			render([message: "Hello", value: 1, id: params.itemName] as JSON)
		}

		def organizeDownloadsFromTopics = {			
			println "Hello"
			def id = params.id
			println "Id: $id"
			Subreddit subreddit = Subreddit.findById(id)
			println "For Subreddit: $subreddit"
			int totalCount = 0
			subreddit.topics.each{ RedditTopic topic ->
				if (!topic.followed){
					topic.handle()
					def topicList = topic.downloadableItems
					topicList.each{DownloadableItem di ->
						if (!di.downloaded){
							totalCount++
						}
					}
				}
			}
			render([id: subreddit.id, count: totalCount] as JSON)
		}

	def doDownloads = {
		DownloadableItem.list().each{ DownloadableItem downloadable ->
		if (!downloadable.downloaded){
			flash.message = "Downloading: $downloadable.link"
			downloadable.download()
			downloadable.downloaded = true
			if (!downloadable.save(flush: true)){
				downloadable.errors.each{ println it}
			}
		}
	}
		}
	class ElementCommand {
		List subreddits
	}
}