package org.lloyd.reddit.ripper

import java.io.File;
import java.io.Serializable;

class SubredditHistory implements Serializable{

	public Map<String, String> list = [:]
	
	public void addItem(String subreddit, String topItem){
		if (list.get(subreddit)){
			list.subreddit = topItem
		} else {
			list.put(subreddit, topItem)
		}
	}
	
	public String getTopItem(String subreddit){
		return list.subreddit
	}
	
	SubredditHistory(File rootFolder){
		loadHistory(new File(rootFolder, "history.cache"))
	}
	
	public void storeFile(File folder){
		File location = new File(folder, "history.cache")
		location.withObjectOutputStream { out ->
			out << list
		}
	}
	
	public void loadHistory(File location){
		if (location.exists()){
			location.withObjectInputStream { inStream ->
				list = inStream.readObject()
			}
		} else {
			list = [:]
		}
	}

}
