package org.lloyd.reddit.ripper

class FolderHashes implements Serializable {
	
	public List<Map<String, String>> hashes = [[:]]
	
	FolderHashes(File rootFolder){
		loadHashes(new File(rootFolder, "hashes.cache"))
	}
	
	public def addHash(String inHash, String inAddress, String inSavedToPath){
		hashes << [hash: inHash, address: inAddress, saveToPath: inSavedToPath]
	}
	
	public void storeFile(File folder){
		File location = new File(folder, "hashes.cache")
		location.withObjectOutputStream { out ->
			out << hashes
		}
	}
	
	public void loadHashes(File location){
		if (location.exists()){
			location.withObjectInputStream { inStream ->
				hashes = inStream.readObject()
			}
		} else {
			hashes = [[:]]
		}
	}
	
	public boolean hasHash(String hash){
		return hashes.find{ it.get("hash") == hash }
	}
	
	public boolean hasAddress(String address){
		return hashes.find{ it.get("address") == address }
	}

}
