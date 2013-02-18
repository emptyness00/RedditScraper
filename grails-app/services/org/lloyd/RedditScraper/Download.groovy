package org.lloyd.RedditScraper

import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

class Download {
	
	def fileMapping = ["image/jpeg": "jpg",
		"image/gif": "gif",
		"image/png": "png",
		"image/tiff": "tiff"]
	
	public def download(DownloadableItem downloadable){
		if (!downloadable.link.contains(".php")){
			println "Address: $downloadable.link"
			performDownload(downloadable)
		} else {
			println "We can't download from a PHP location, not going to bother... Skipping: $downloadable.link"
		}
	}

	public def performDownload(DownloadableItem downloadable){
		String path = "E:/TempTwo"
		if (!new File(path).exists()){
			new File(path).mkdirs()
		}
		
		File subredditdir = new File(path, downloadable.topic.subreddit.name)
		if (!subredditdir.exists()){
			subredditdir.mkdirs()
		}
		
		path = subredditdir.path

		String addressFileName = stripIllegalCharacters("${downloadable.link.tokenize("/")[-1]}")
		println "AddressFileName: $addressFileName"
		addressFileName = stripExtension(addressFileName)

		addressFileName = "${downloadable.topic.author}_${downloadable.topic.name}_${addressFileName}"
		
		

		String fileName = "$path/$addressFileName"
		String tmpFileName = "$path/${addressFileName}.tmp"

		//			if (PageLinks.isMovieUrl(downloadable.url)){
		//				downloadMovie(downloadable.url)
		//			} else {
		boolean done = false
		URL url = new URL(downloadable.link)
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
						downloadImage(tmpFileName, downloadable.link, conn.inputStream, totalSize)
					} catch (Exception e){
						e.printStackTrace()
						println "Exception was thrown, moving on: $downloadable.link"
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
							downloadImage(tmpFileName, downloadable.link, conn.inputStream, totalSize)
							done = true
						}
					}
				} catch (IOException ioE2){
					println "Could not get access to resource: $downloadable.link, skipping."
					ioE2.printStackTrace()
					totalSize = 0
				}
			}
		} catch (FileNotFoundException fnfE){
			println "Couldn't find this file, moving on... $downloadable.link"
		} catch (Exception e){
			println "Exception found while accessing address: $downloadable.link, skipping"
			println "E: ${e.message}"
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
//				println "Still downloading something..."
			}
		}
		//			}

		File oldFile = new File(fileName)
		File tmpFile = new File(tmpFileName)
		String hash
		if (tmpFile.exists() && oldFile.exists()){
			tmpFile.renameTo("$path/${getHash(tmpFile)}_$downloadable.link")
		} else if (tmpFile.exists() && !oldFile.exists()){
			tmpFile.renameTo("$oldFile.canonicalPath")
		} else if (!tmpFile.exists()){
			println "File was already downloaded, so we didn't download it again.  $downloadable.link; $fileName"
		}
		String extension = getProperExtension(oldFile)
		println "Extension: $extension"
		oldFile.renameTo("${oldFile.canonicalPath}.$extension")
		
	}
	
	public String getProperExtension(File file){
		String type = getMimeType(file)
		println "Type: $type"
		String extension = fileMapping.get(type)
		println "Ext: $extension"
		if (!extension){
			extension = "jpg"
		}
		return extension	
	}
	
	public String getMimeType(File file){
		Magic parser = new Magic() ;
		MagicMatch match = parser.getMagicMatch(file, true);
		
		String type = match.mimeType
		println "Type: $type"
		return type
	}
	
	public String stripExtension(String string){
		if (string.contains(".")){
			return string.substring(0, string.lastIndexOf(".") - 1)
		} else {
			return string
		}
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
}
