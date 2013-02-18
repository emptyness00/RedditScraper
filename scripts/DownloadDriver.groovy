package org.lloyd.reddit.ripper

class DownloadDriver {
	public MasterDownloadList mdl
	public MasterFollowList mfl
	
	public void goFromList(String listDir, String downloadRoot){
		mdl = new MasterDownloadList(new File(downloadRoot))
		mdl.loadDownloadsFromList(new File(listDir))
		mfl = new MasterFollowList(new File(downloadRoot))
		mfl.loadFollowFromList(new File(listDir))
//		mdl.downloadList.each{
//			println "URL: $it.url, Author: $it.author, Subreddit: $it.subreddit"
//		}
//		mfl.followList.each{
//			println "URL: $it.url, Author: $it.author, Subreddit: $it.subreddit"
//		}
//		PageParser pp = new PageParser()
//		mfl.followList.each{ Downloadable dl ->
//			if (!mfl.followed.contains(dl.url)){
//				pp.getLinks(dl, mdl)
//				mdl.saveToList(new File(downloadRoot))
//				mdl.downloadList = []
//			}
//		}
//		mdl.saveToList(new File(downloadRoot))
		mdl.downloadItems()
	}
	
	public int go(String downloadRoot, String subreddit, String subredditList = null){
		mdl = new MasterDownloadList(new File(downloadRoot))
		JsonRedditParser jrp = new JsonRedditParser()
		if (subredditList){
			jrp.parse(subreddit, "", mdl, true, new File(subredditList))
		} else {
			jrp.parse(subreddit, "", mdl, true)
		}
		
		mdl.downloadItems()
		
		mdl.storeList()
		
		return 0
	}

	private PageLinks parsePage(String downloadStartUrl) {
		println "Currently Parsing: $downloadStartUrl"
		PageParser startPage = new PageParser()
		startPage.parentUrl = downloadStartUrl
		startPage.slurpPage(downloadStartUrl)
		if (startPage.was18Form){
			println "Was 18 form, doing it again after submitting"
			startPage.slurpPage(downloadStartUrl)
		}
		PageLinks results = startPage.getLinks()
		results.downloadLinks.each{
			println "Adding: $it"
			mdl.downloadList << it
		}
		results.followLinks.each{
			if (mfl.followList << it){
				println "Parsing: $it"
				sleep(1000)
				parsePage(it)
			}
		}
	}
}
