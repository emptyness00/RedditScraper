<html>
<head>
<meta name="layout" content="main" />
<g:javascript library="jquery" />
<script>
var count = 0
var total = 0
function showData(data){
	console.log("showData")
	
	var elementId = "subreddit_" + data.id
	
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
	     if (full_id.startsWith("subreddit_")){
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
</head>
<body>
	<div id="message"></div>
	<table>
		<tr>
			<td>
				<div id="subreddit_1">0</div>
			</td>
			<td>
				<div id="subreddit_2">0</div>
			</td>
		</tr>
	</table>
	<div id="error"></div>
	<button onclick="updateTopicCount()">Try it</button>
</body>
</html>