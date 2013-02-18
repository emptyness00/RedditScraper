<html>
<head>
<meta name="layout" content="main" />
<title>Welcome to Grails</title>
</head>
<body>
	<div id="page-body" role="main">
		Hello World!
		<table>
			<th>
			<td>Name</td>
			<td>Last Downloaded Topic</td>
			<td>Total Downloads</td>
			<td>Last Downloaded From</td>
			</th>
			<g:form name="getTopics" action="collectFollowLinks">
				<g:each in="${subredditInstanceList}" status="i" var="subreddit">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><input type="checkbox" name="subredditId"
							value="subreddit.id">
						<g:link action="show" id="${subreddit.id}">
								${subreddit.id?.encodeAsHTML()}
							</g:link></td>
						</input>
						</td>
						<td>
							${subreddit.name.encodeAsHTML()}
						</td>
						<td>
							${subreddit.latestDownloadedTopic?.encodeAsHTML()}
						</td>
						<td>
							${subreddit.totalDownloadedTopics?.encodeAsHTML()}
						</td>
						<td>
							${subreddit.lastDownloadedFrom?.encodeAsHTML()}
						</td>
					</tr>
				</g:each>
				<g:submitButton name="getTopicsButton" class="save"
					value="Get Topics" />
			</g:form>
		</table>
	</div>
</body>
</html>