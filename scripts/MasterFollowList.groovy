package org.lloyd.reddit.ripper

import java.io.File;
import java.io.Serializable;
import java.util.List;

class MasterFollowList implements Serializable {
	public List<Downloadable> followList = []
	public def followed = []
	
	MasterFollowList(File rootDir){
		loadList(new File("$rootDir.canonicalPath/followed.cache"))
	}
		
	public boolean addItem(String url, String author, String subreddit){
		if (!followList.find{Downloadable dl -> dl.url == url}){
			followList << new Downloadable(url, author, subreddit)
			return true
		} else {
			return false
		}
	}
	
	public void storeList(File folder){
		followList.each{ Downloadable dl ->
			followed << dl.url
		}
		File location = new File(folder, "followed.cache")
		location.withObjectOutputStream { out ->
			out << followed
		}
	}
	
	public void loadList(File location){
		if (location.exists()){
			location.withObjectInputStream { inStream ->
				followed = inStream.readObject()
			}
		} else {
			followed = []
		}
	}
	
	public void saveToList(File rootDir){
		File listFile = new File("$rootDir.canonicalPath/followList.txt")
		if (!listFile.exists()){
			listFile.withWriter("UTF-8"){ BufferedWriter writer ->
				followList.each{ Downloadable dl ->
					writer.writeLine("${dl.url}__mysplit__${dl.author}__mysplit__${dl.subreddit}")
				}
			}
		} else {
			listFile.withWriterAppend("UTF-8"){ BufferedWriter writer ->
				followList.each{ Downloadable dl ->
					writer.writeLine("${dl.url}__mysplit__${dl.author}__mysplit__${dl.subreddit}")
				}
			}
		}
	}
	
	public void loadFollowFromList(File rootDir){
		File listFile = new File("$rootDir.canonicalPath/followList.txt")
		listFile.eachLine{String line ->
			String[] parts = line.split("__mysplit__")
			Downloadable dl = new Downloadable(parts[0], parts[1], parts[2])
			followList << dl
		}
	}
}
