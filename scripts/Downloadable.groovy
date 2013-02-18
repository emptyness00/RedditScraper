package org.lloyd.reddit.ripper

import java.io.File;
import java.io.Serializable;

class Downloadable implements Serializable{

	public String url
	public String author
	public String subreddit
	public boolean done
	
	Downloadable(String inUrl, String inAuthor, String inSubreddit){
		url = inUrl
		author = inAuthor
		subreddit = inSubreddit
		done = false
	}
	
	Downloadable(String inUrl, String inAuthor, String inSubreddit, boolean inDone){
		url = inUrl
		author = inAuthor
		subreddit = inSubreddit
		done = inDone
	}
	
	public String getPath(File rootDir){
		return "$rootDir.canonicalPath/$subreddit/$author"
	}
}
