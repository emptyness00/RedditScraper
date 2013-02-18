package org.lloyd.reddit.ripper

import java.io.File;
import java.io.Serializable;
import java.security.MessageDigest

class MasterDownloadList implements Serializable{
	public def downloadList = []
	private File rootDir
	public FolderHashes hasher
	public def downloaded = []
	public boolean done = false
	
	private static final int SAME = 0
	private static final int DIFFERENT = 1

	public MasterDownloadList(File rootDirectory){
		rootDir = rootDirectory
		loadList(new File("$rootDir.canonicalPath/downloaded.cache"))
	}
	
	public void addItem(String url, String author, String subreddit){
		if (!downloadList.find{Downloadable dl -> dl.url == url}){
			downloadList << new Downloadable(url, author, subreddit)
		}
	}

	public void downloadItems(){
		hasher = new FolderHashes(rootDir)

		println "In Download"
		int i = 1
		downloadList.each{ Downloadable downloadable ->
			if (!downloadable.url.contains(".php")){
				if (downloaded.contains(downloadable.url)){
					println "We already downloaded this url..."
				} else {
					println "Downloading $i of ${downloadList.size()}"
					println "Address: $downloadable.url"
					if (hasher.hasAddress(downloadable.url)){
						println "We already downloaded this exact address, it probably doesn't need to be downloaded again..."
					} else {
						println "Starting download."
						performDownload(downloadable)
					}
				}
			} else {
				println "We can't download from a PHP location, not going to bother... Skipping: $downloadable.url"
			}
			i++
		}
		hasher.storeFile(rootDir)
	}

	private performDownload(Downloadable downloadable) {
		String path = downloadable.getPath(rootDir)
		if (!new File(path).exists()){
			new File(path).mkdirs()
		}
		
		
		String addressFileName = stripIllegalCharacters("${downloadable.url.tokenize("/")[-1]}")
		println "AddressFileName: $addressFileName"
		
		String fileName = "$path/$addressFileName"
		String tmpFileName = "$path/${addressFileName}.tmp"

		//			if (PageLinks.isMovieUrl(downloadable.url)){
		//				downloadMovie(downloadable.url)
		//			} else {
		boolean done = false
		URL url = new URL(downloadable.url)
		println "Opening Connection"
		URLConnection conn = url.openConnection()
		println "Connection Opened"
		int totalSize
		done = false
		File currentFile = new File(fileName)
		try{
			totalSize = conn.getContentLength()
			println "Total Size: $totalSize"
			if (currentFile.exists() && currentFile.size() == totalSize){
				println "Already Done."
				done = true
			} else {
				println "Size: $totalSize"
				println "Actually gonna download"
				def th = Thread.start{
					try{
						downloadImage(tmpFileName, downloadable.url, conn.inputStream, totalSize)
					} catch (Exception e){
						e.printStackTrace()
						println "Exception was thrown, moving on: $downloadable.url"
					}
					done = true
				}
			}
		} catch (IOException ioE){
			ioE.printStackTrace()
			if (ioE.message.contains("504")){
				try{
					totalSize = conn.getContentLength()
					if (currentFile.exists() && currentFile.size() == totalSize){
						done = true
					} else {
						println "Size: $totalSize"
						def th = Thread.start{
							downloadImage(tmpFileName, downloadable.url, conn.inputStream, totalSize)
							done = true
						}
					}
				} catch (IOException ioE2){
					println "Could not get access to resource: $downloadable.url, skipping."
					ioE2.printStackTrace()
					totalSize = 0
				}
			}
		} catch (FileNotFoundException fnfE){
			println "Couldn't find this file, moving on... $donwloadable.url"
		} catch (Exception e){
			println "Exception found while accessing address: $downloadable.url, skipping"
			e.printStackTrace()
			totalSize = 0
		}


		if (!done){
			println "Downloading..."
			sleep 100
		}
		if (totalSize > 0){
			while (!done){
				int sleepTime = totalSize / 1024
				sleep sleepTime
				File downloadingFile = new File(tmpFileName)
				if (downloadingFile.exists()){
					int doneSize = downloadingFile.size()
					int percentage = (doneSize / totalSize) * 100
					println "Download ${percentage}% complete"
				}
			
			}
		} else {
			println "Total Size was not calculated correctly, we'll just tell you we're still working on it..."
			int i = 0
			while (!done){
				i ++
				println "Still downloading something..."
			}
		}
		//			}

		File oldFile = new File(fileName)
		File tmpFile = new File(tmpFileName)
		String hash
		if (tmpFile.exists() && oldFile.exists()){
			tmpFile.renameTo("$path/${getHash(tmpFile)}_$downloadable.url")
		} else if (tmpFile.exists() && !oldFile.exists()){
			tmpFile.renameTo("$oldFile.canonicalPath")
		} else if (!tmpFile.exists()){
			println "File was already downloaded, so we didn't download it again.  $downloadable.url; $fileName"
		}
	}

	private boolean doDownloadInThread(int totalSize, String tmpFileName, String address, URLConnection conn, File oldFile) {
		boolean done = false
		
		return done
	}

	private downloadImage(String tmpFileName, String address, InputStream inStream, int totalSize) {
		def file = new FileOutputStream(tmpFileName)
		def out = new BufferedOutputStream(file)
		try{
			out << inStream
			out.flush()
		} catch (FileNotFoundException e){
			println "This address returned a 404 error: $address"
		} catch (IOException ioE){
			if (ioE.message.contains("400")){
				println "Invalid image location at: $address.  Skipping"
			}
		} catch (Exception ex){
			println "Threw Exception, but trying to continue..."
			ex.printStackTrace()
			throw ex
		}
		println "Done Downloading..."
		out.close()
	}

	public int compareHashes(File fileA, File fileB){
		if (getHash(fileA) != getHash(fileB)){
			return DIFFERENT
		} else {
			return SAME
		}
	}

	def getHash(File file){
		generateMD5(file.bytes)
	}

	def generateMD5(byte[] bytes) {
		MessageDigest digest = MessageDigest.getInstance("MD5")
		digest.update(bytes);
		new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
	}

	def stripIllegalCharacters(String inString){
		String string = inString.replaceAll("\\?", "")
		string = string.replaceAll("\\*", "")
		string = string.replaceAll("\\\\", "")
		string = string.replaceAll("\\[", "")
		string = string.replaceAll("\\]", "")
		string = string.replaceAll("\\{", "")
		string = string.replaceAll("\\}", "")
		string = string.replaceAll("~", "")
		string = string.replaceAll("=", "")
		string = string.replaceAll("\\+", "")
		string = string.replaceAll(":", "")
		if (string.length() > 100){
			string = string.substring(string.length() - 55)
		}
		return string
	}
	
	public void saveToList(File rootDir){
		File listFile = new File("$rootDir.canonicalPath/downloadList.txt")
		if (!listFile.exists()){
			listFile.withWriter("UTF-8"){ BufferedWriter writer ->
				downloadList.each{ Downloadable dl ->
					println "Here"
					writer.writeLine("${dl.url}__mysplit__${dl.author}__mysplit__${dl.subreddit}")
				}
			}
		} else {
			listFile.withWriterAppend("UTF-8"){ BufferedWriter writer ->
				downloadList.each{ Downloadable dl ->
					println "There"
					writer.writeLine("${dl.url}__mysplit__${dl.author}__mysplit__${dl.subreddit}")
				}
			}
		}
	}
	
	public void loadDownloadsFromList(File rootDir){
		File listFile = new File("$rootDir.canonicalPath/downloadList.txt")
		listFile.eachLine{String line ->
			String[] parts = line.split("__mysplit__")
			Downloadable dl = new Downloadable(parts[0], parts[1], parts[2])
			downloadList << dl
		}
	}
	
	public void storeList(){
		downloadList.each{ Downloadable dl ->
			downloaded << dl.url
		}
		File location = new File(rootDir, "downloaded.cache")
		location.withObjectOutputStream { out ->
			out << downloaded
		}
	}
	
	public void loadList(File location){
		if (location.exists()){
			location.withObjectInputStream { inStream ->
				downloaded = inStream.readObject()
			}
		} else {
			downloaded = []
		}
	}
}
