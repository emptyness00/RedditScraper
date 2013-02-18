package org.lloyd.RedditScraper

import org.junit.Test
import static org.junit.Assert.*

@TestFor(DownloadableItem)
class CanParsePageForLinkTests {

//	@Test
	public void canParsePageForLink(){
		File page = new File("test/resources/org/lloyd/RedditScraper/PageParser/pageDirectImage.html")
		FollowableItem fi = new FollowableItem(link: page.toURL())
		PageParser pageParser = new PageParser()
		pageParser.http = new MockHTTPBuilder()
		pageParser.getLinks(fi)
		assertEquals 1, DownloadableItem.list().size()
	}
	
	@Test
	public void canParsePageForLink_PageHasNoImages(){
		File page = new File("test/resources/org/lloyd/RedditScraper/PageParser/pageWithOutImageLinks.html")
		FollowableItem fi = new FollowableItem(link: page.toURL())
		PageParser pageParser = new PageParser()
		pageParser.http = new MockHTTPBuilder()
		pageParser.getLinks(fi)
		assertEquals 0, DownloadableItem.list().size()
	}
	
//	@Test
	public void canParsePageForLink_RealURL(){
		File page = new File("test/resources/org/lloyd/RedditScraper/PageParser/pageWithSingleImageInAlbum.html")
		FollowableItem fi = new FollowableItem(link: page.toURL())
		PageParser pageParser = new PageParser()
		pageParser.http = new MockHTTPBuilder()
		pageParser.getLinks(fi)
		assertEquals 1, DownloadableItem.list().size()
	}
	
	@Test
	public void canParsePageForLink_RealURL_NotDirectImage(){
//		FollowableItem fi = new FollowableItem(link: new URL("http://imgur.com/a/Sybks"))
		File page = new File("test/resources/org/lloyd/RedditScraper/PageParser/pageWithNoDirectImagesButAList.html")
		FollowableItem fi = new FollowableItem(link: page.toURL())
		PageParser pageParser = new PageParser()
		pageParser.http = new MockHTTPBuilder()
		pageParser.getLinks(fi)
		assertEquals 2, DownloadableItem.list().size()
	}
	
	
	@Test
	public void canParsePageForLink_RealURL_FileNotFoundException(){
//		FollowableItem fi = new FollowableItem(link: new URL("http://imgur.com/wefioj"))
		File page = new File("test/resources/org/lloyd/RedditScraper/PageParser/thisPageShouldNotExist.html")
		FollowableItem fi = new FollowableItem(link: page.toURL())
		PageParser pageParser = new PageParser()
		pageParser.http = new MockHTTPBuilder()
		pageParser.getLinks(fi)
		assertEquals 0, DownloadableItem.list().size()
	}
}
