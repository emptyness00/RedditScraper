package org.lloyd.RedditScraper

import groovyx.net.http.HTTPBuilder;

import net.sf.json.groovy.JsonGroovyBuilder
import net.sf.json.groovy.JsonSlurper;

class MockHTTPBuilder extends HTTPBuilder{
	public Object get(Map<String,?> args, groovy.lang.Closure responseClosure){
		if (args.path.endsWith(".json")){
//			JSONObject json = new JSONObject(new File("jsons", args.path.substring(2)).text)
			def json = new JsonSlurper().parse(new File("jsons", args.path.substring(2)))
			responseClosure.call([status: 200], json)
		} else {
			responseClosure.call(null, new File(args.path.substring(2)).text)
		}
	}

}
