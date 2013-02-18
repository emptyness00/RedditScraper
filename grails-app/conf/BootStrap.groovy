import grails.util.GrailsUtil;

import org.lloyd.RedditScraper.RedditTopic;
import org.lloyd.RedditScraper.Subreddit

class BootStrap {

	def init = { servletContext ->
		switch (GrailsUtil.environment) {
			case "development":
				Subreddit subreddit1 = new Subreddit(name: "cottonpanties")
				subreddit1.save()
				Subreddit subreddit2 = new Subreddit(name: "celebritiesgonewild")
				subreddit2.save()
				break;
		}
	}
	def destroy = {
	}
}
