package org.lloyd.RedditScraper

import org.springframework.dao.DataIntegrityViolationException

class DownloadableItemController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	
		def index() {
			redirect(action: "list", params: params)
		}
	
		def list(Integer max) {
			params.sort = "link"
			params.order = "asc"
			[downloadableItemInstanceList: DownloadableItem.list(params), downloadableItemInstanceTotal: DownloadableItem.count()]
		}
	
		def create() {
			[downloadableItemInstance: new DownloadableItem(params)]
		}
	
		def save() {
			def downloadableItemInstance = new DownloadableItem(params)
			if (!downloadableItemInstance.save(flush: true)) {
				render(view: "create", model: [downloadableItemInstance: downloadableItemInstance])
				return
			}
	
			flash.message = message(code: 'default.created.message', args: [message(code: 'DownloadableItem.label', default: 'DownloadableItem'), downloadableItemInstance.id])
			redirect(action: "show", id: downloadableItemInstance.id)
		}
	
		def show(Long id) {
			def downloadableItemInstance = DownloadableItem.get(id)
			if (!downloadableItemInstance) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'DownloadableItem.label', default: 'DownloadableItem'), id])
				redirect(action: "list")
				return
			}
			println "Instance: ${downloadableItemInstance.toString()}"
	
			[downloadableItemInstance: downloadableItemInstance]
		}
	
		def edit(Long id) {
			def downloadableItemInstance = DownloadableItem.get(id)
			if (!downloadableItemInstance) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'DownloadableItem.label', default: 'DownloadableItem'), id])
				redirect(action: "list")
				return
			}
	
			[downloadableItemInstance: downloadableItemInstance]
		}
	
		def update(Long id, Long version) {
			def downloadableItemInstance = DownloadableItem.get(id)
			if (!downloadableItemInstance) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'DownloadableItem.label', default: 'DownloadableItem'), id])
				redirect(action: "list")
				return
			}
	
			if (version != null) {
				if (downloadableItemInstance.version > version) {
					downloadableItemInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
							  [message(code: 'DownloadableItem.label', default: 'DownloadableItem')] as Object[],
							  "Another user has updated this DownloadableItem while you were editing")
					render(view: "edit", model: [downloadableItemInstance: downloadableItemInstance])
					return
				}
			}
	
			downloadableItemInstance.properties = params
	
			if (!downloadableItemInstance.save(flush: true)) {
				render(view: "edit", model: [downloadableItemInstance: downloadableItemInstance])
				return
			}
	
			flash.message = message(code: 'default.updated.message', args: [message(code: 'DownloadableItem.label', default: 'DownloadableItem'), downloadableItemInstance.id])
			redirect(action: "show", id: downloadableItemInstance.id)
		}
	
		def delete(Long id) {
			def downloadableItemInstance = DownloadableItem.get(id)
			if (!downloadableItemInstance) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'DownloadableItem.label', default: 'DownloadableItem'), id])
				redirect(action: "list")
				return
			}
	
			try {
				downloadableItemInstance.delete(flush: true)
				flash.message = message(code: 'default.deleted.message', args: [message(code: 'DownloadableItem.label', default: 'DownloadableItem'), id])
				redirect(action: "list")
			}
			catch (DataIntegrityViolationException e) {
				flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'DownloadableItem.label', default: 'DownloadableItem'), id])
				redirect(action: "show", id: id)
			}
		}
}
