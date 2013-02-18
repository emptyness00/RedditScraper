<html>
<head>
<meta name="layout" content="main" />
<title>Welcome to Grails</title>
</head>
<body>
	<div id="page-body" role="main">
		<h1>
			${redditTopicInstance.name?.encodeAsHTML()}
		</h1>
		<table>
			<th>Link
			<td>Followed?</td>
			</th><g:each in="${redditTopicInstance.followableItems}" status="i" var="followableItem">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td>${followableItem.link?.encodeAsHTML()}</td>
						<td>${followableItem.followed.encodeAsHTML()}</td>
					</tr>
				</g:each>
		</table>

	</div>
</body>
</html>