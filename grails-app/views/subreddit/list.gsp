<html>
<head>
<meta name="layout" content="main" />
<g:javascript library="jquery" />
<script>
var count = 0
var total = 0
function showData(data){
	console.log("showData")
	
	var elementId = "topicsCount_" + data.id
	
    var element = document.getElementById(elementId)
    console.log("Would update " + elementId + " with values: " + data.count)
    element.innerHTML = data.count

	console.log("end showData")
	count++
	if (count >= total){
		document.getElementById("message").innerHTML = ""
	}
	
	
}

function updateTopicCount() {
	var all = document.getElementsByTagName("div");
	document.getElementById("message").innerHTML = "Processing..."
    console.log("Logging")
	for (var i=0, max=all.length; i < max; i++) {
	     var full_id = all[i].id
	     if (full_id.startsWith("subredditId_")){
		     var id = full_id.substring(full_id.indexOf("_") + 1)
		     console.log("Running")
		     console.log(id)
		     console.log(all[i])
		     console.log(full_id)
		     total++
		     ${remoteFunction(action:'collectFollowLinks', controller:'subreddit', params:'\'id=\' + id', onSuccess: 'showData(data)')}
		     console.log("end Running")
		 }
	}
	console.log("FINISHED")
}
</script>
<title>Welcome to Grails</title>
</head>
<body>
	<div id="page-body" role="main">
		Hello World!
		<div id="message"></div>
		<table>
			<th>
			<td>Name</td>
			<td>Last Downloaded Topic</td>
			<td>Total Downloads Available</td>
			<td>Total Topics</td>
			<td>Last Downloaded From</td>
			</th>
			<g:form name="getTopics" controller="subreddit">
				<g:each in="${subredditInstanceList}" status="i" var="subreddit">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show"
								id="${subreddit.id}">
								${subreddit.id?.encodeAsHTML()}</g:link>
						</td>
						<td><div id="subredditId_${subreddit.id}"></div>
							${subreddit.name.encodeAsHTML()}
						</td>
						<td><div id="lastDownload_${subreddit.id}">
								${subreddit.latestDownloadedTopic?.encodeAsHTML()}
							</div>
						</td>
						<td><div id="availableDownloadCount_${subreddit.id}">
								${subreddit.getAvailableDownloadCount()?.encodeAsHTML()}
							</div>
						</td>
						<td>
							<div id="topicsCount_${subreddit.id}">
								${subreddit.getNewTopicCount()?.encodeAsHTML()}	
							</div>
						</td>
						<td><div id="lastDownloadFrom_${subreddit.id}">
								${subreddit.lastDownloadedFrom?.encodeAsHTML()}
							</div>
						</td>
					</tr>
				</g:each>
				<input class="button" value="Get Topics" onClick="updateTopicCount()"/>
				<g:actionSubmit value="Find Downloads" action="organizeDownloadsFromTopics" />
				<g:actionSubmit value="Add Subreddit" action="create"/>
			</g:form>
			<g:form controller="subreddit">
				<g:submitToRemote value="Download Items" action="doDownloads" update="downloadStatus"/>
			</g:form>
			<g:link class="input" action="show" controller="configuration">Configure</g:link>
		</table>
	</div>
	<g:render template="downloadStatus"/>
</body>
</html>