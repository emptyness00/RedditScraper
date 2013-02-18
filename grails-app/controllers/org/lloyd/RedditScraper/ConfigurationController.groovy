package org.lloyd.RedditScraper

class ConfigurationController {
	def show = {
		def configurationInstance = Configuration.get(0)
		if (!configurationInstance){
			configurationInstance = new Configuration()
			configurationInstance.save(flush:true)
		}
		[configurationInstance: configurationInstance]
	}
	
	def update = {
		Configuration configuration = Configuration.findById(params.id)
		println configuration
		configuration.saveToDir = params.saveToDir
		configuration.save(flush: true)
		redirect(action: "show")
	}
}
