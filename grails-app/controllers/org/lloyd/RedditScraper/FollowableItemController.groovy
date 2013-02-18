package org.lloyd.RedditScraper

import org.springframework.dao.DataIntegrityViolationException

class FollowableItemController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	
		def index() {
			redirect(action: "list", params: params)
		}
	
		def list(Integer max) {
			params.max = Math.min(max ?: 10, 100)
			params.sort = "link"
			params.order = "asc"
			println FollowableItem.list(params)
			[followableItemInstanceList: FollowableItem.list(params), followableItemInstanceTotal: FollowableItem.count()]
		}
	
		def create() {
			[followableItemInstance: new FollowableItem(params)]
		}
	
		def save() {
			def followableItemInstance = new FollowableItem(params)
			if (!followableItemInstance.save(flush: true)) {
				render(view: "create", model: [followableItemInstance: followableItemInstance])
				return
			}
	
			flash.message = message(code: 'default.created.message', args: [message(code: 'FollowableItem.label', default: 'FollowableItem'), followableItemInstance.id])
			redirect(action: "show", id: followableItemInstance.id)
		}
	
		def show(Long id) {
			def followableItemInstance = FollowableItem.get(id)
			if (!followableItemInstance) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'FollowableItem.label', default: 'FollowableItem'), id])
				redirect(action: "list")
				return
			}
			println "Instance: ${followableItemInstance.toString()}"
	
			[followableItemInstance: followableItemInstance]
		}
	
		def edit(Long id) {
			def followableItemInstance = FollowableItem.get(id)
			if (!followableItemInstance) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'FollowableItem.label', default: 'FollowableItem'), id])
				redirect(action: "list")
				return
			}
	
			[followableItemInstance: followableItemInstance]
		}
	
		def update(Long id, Long version) {
			def followableItemInstance = FollowableItem.get(id)
			if (!followableItemInstance) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'FollowableItem.label', default: 'FollowableItem'), id])
				redirect(action: "list")
				return
			}
	
			if (version != null) {
				if (followableItemInstance.version > version) {
					followableItemInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
							  [message(code: 'FollowableItem.label', default: 'FollowableItem')] as Object[],
							  "Another user has updated this FollowableItem while you were editing")
					render(view: "edit", model: [followableItemInstance: followableItemInstance])
					return
				}
			}
	
			followableItemInstance.properties = params
	
			if (!followableItemInstance.save(flush: true)) {
				render(view: "edit", model: [followableItemInstance: followableItemInstance])
				return
			}
	
			flash.message = message(code: 'default.updated.message', args: [message(code: 'FollowableItem.label', default: 'FollowableItem'), followableItemInstance.id])
			redirect(action: "show", id: followableItemInstance.id)
		}
	
		def delete(Long id) {
			def followableItemInstance = FollowableItem.get(id)
			if (!followableItemInstance) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'FollowableItem.label', default: 'FollowableItem'), id])
				redirect(action: "list")
				return
			}
	
			try {
				followableItemInstance.delete(flush: true)
				flash.message = message(code: 'default.deleted.message', args: [message(code: 'FollowableItem.label', default: 'FollowableItem'), id])
				redirect(action: "list")
			}
			catch (DataIntegrityViolationException e) {
				flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'FollowableItem.label', default: 'FollowableItem'), id])
				redirect(action: "show", id: id)
			}
		}
}
