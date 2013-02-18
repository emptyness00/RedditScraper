
package org.lloyd.RedditScraper

import org.junit.Test
import static org.junit.Assert.*

@TestFor(RedditTopic)
class CanGetTopicsFromSubredditTests {
	@Test
	public void canGetTopicsFromSubreddit(){
		PageParser pageParser = new PageParser()
//		pageParser.http = new MockHTTPBuilder()
//		pageParser.getTopicsFromSubreddit("someRandomSubreddit")
		pageParser.getTopicsFromSubreddit("cottonpanties")
		assertEquals 1, RedditTopic.list().size()
	}
}
