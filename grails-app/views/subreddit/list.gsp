<html>
<head>
<meta name="layout" content="main" />
<META HTTP-EQUIV="REFRESH" CONTENT="5">
<g:javascript library="jquery" />
<script>
var count = 0
var total = 0
function showData(data){
	var elementId = "topicsCount_" + data.id
	
    var element = document.getElementById(elementId)
    element.innerHTML = data.count

	count++
	if (count >= total){
		document.getElementById("message").innerHTML = ""
	}
}

function showDownloadsData(data){
	var elementId = "availableDownloadCount_" + data.id
	
    var element = document.getElementById(elementId)
    element.innerHTML = data.count

	count++
	if (count >= total){
		document.getElementById("message").innerHTML = ""
	}
}

function updateTopicCount() {
	var all = document.getElementsByTagName("div");
	document.getElementById("message").innerHTML = "Processing..."
	count = 0
	total = 0
	for (var i=0, max=all.length; i < max; i++) {
	     var full_id = all[i].id
	     if (full_id.startsWith("subredditId_")){
		     var id = full_id.substring(full_id.indexOf("_") + 1)
		     total++
		     console.log("Calling collectFollowLinks with parameter: " + id)
		     ${remoteFunction(action:'collectFollowLinks', controller:'subreddit', params:'\'id=\' + id', onSuccess: 'showData(data)')}
		 }
	}
}

function updateDownloadCount() {
	console.log("updateDownloadCount")
	var all = document.getElementsByTagName("div");
	document.getElementById("message").innerHTML = "Processing..."
	count = 0
	total = 0
	for (var i=0, max=all.length; i < max; i++) {
	     var full_id = all[i].id
	     if (full_id.startsWith("subredditId_")){
		     var id = full_id.substring(full_id.indexOf("_") + 1)
		     total++
		     console.log("Total: " + total)
		     ${remoteFunction(action:'organizeDownloadsFromTopics', controller:'subreddit', params:'\'id=\' + id', onSuccess: 'showDownloadsData(data)')}
		     console.log("Called...")
		 }
	}
}
</script>
<title>Welcome to Grails</title>
</head>
<body>
	<div id="page-body" role="main">
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
						<td><g:link action="show" id="${subreddit.id}">
								${subreddit.id?.encodeAsHTML()}
							</g:link></td>
						<td><div id="subredditId_${subreddit.id}"></div> ${subreddit.name.encodeAsHTML()}
						</td>
						<td><div id="lastDownload_${subreddit.id}">
								${subreddit.latestDownloadedTopic?.encodeAsHTML()}
							</div></td>
						<td><div id="availableDownloadCount_${subreddit.id}">
								${subreddit.getAvailableDownloadCount()?.encodeAsHTML()}
							</div></td>
						<td>
							<div id="topicsCount_${subreddit.id}">
								${subreddit.getNewTopics()?.size().encodeAsHTML()}
							</div>
						</td>
						<td><div id="lastDownloadFrom_${subreddit.id}">
								${subreddit.lastDownloadedFrom?.encodeAsHTML()}
							</div></td>
					</tr>
				</g:each>
				<input class="button" value="Find Topics"
					onClick="updateTopicCount()"/>
				<input class="button" value="Find Downloads"
					onClick="updateDownloadCount()"/>
				<g:actionSubmit value="Add Subreddit" action="create" />
			</g:form>
			<g:form controller="subreddit">
				<g:submitToRemote value="Download Items" action="doDownloads"
					update="downloadStatus" />
			</g:form>
			<g:link class="input" action="show" controller="configuration">Configure</g:link>
		</table>
	</div>
	<g:render template="downloadStatus" />
</body>
</html>