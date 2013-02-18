package org.lloyd.RedditScraper

import java.util.List;

class PageLinks {
	public static List followDomains = ['imgur.com', 'www.imgur.com']
	public static List downloadDomains = ['i.imgur.com']
	public static List downloadExtensions = ["jpg", "jpeg", "gif", "png", "tif"]
	public static List movieDomains = ['www.xhamster.com', 'www.xvideos.com', 'xhamster.com', 'xvideos.com']
	
	List downloadLinks = []
	List followLinks = []
	
	public void addDownload(String url){
		downloadLinks << url
	}
	
	public void addFollow(String url){
		followLinks << url
	}
	
	public static isTargetDownloadUrl(String url){
		if (downloadDomains.contains(stripForDomain(url))){
			return true
		} else if (downloadExtensions.contains(url.substring(url.lastIndexOf(".")+1))){
			return true
		} else {
			return false
		}
	}
	
	public static isTargetFollowUrl(String url){
		return followDomains.contains(stripForDomain(url))
	}
	
	private static stripForDomain(String url) {
		if (!url.contains("http://")){
			return "Not a valid link"
		} else {
			int protocolEnd = url.indexOf("http://") + 7
			int endDomain = url.indexOf("/", protocolEnd)
			if (endDomain > 0 && protocolEnd > 0){
				return url.substring(protocolEnd, endDomain)
			} else {
				return url.substring(protocolEnd)
			}
		}
	}
	
	public static isMovieUrl(String url){
		return movieDomains.contains(stripForDomain(url))
	}
}
