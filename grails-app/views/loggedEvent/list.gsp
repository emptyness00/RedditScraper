<html>
<head>
<title>Welcome to Grails</title>
</head>
<body>
	<div id="page-body" role="main">
		<table>
			<th>
			<td>Critical Level</td><td>DateTime</td><td>Event Description</td><td>Link</td>
			</th>
				<g:each in="${loggedEventInstanceList}" status="i" var="logEvent">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td>${logEvent.criticalLevel}</td>
						<td>${logEvent.eventDateTime.encodeAsHTML()}</td>
						<td>${logEvent.eventDesc.encodeAsHTML()}</td>
						<td>${logEvent.associatedLink.encodeAsHTML()}</td>
					</tr>
				</g:each>
		</table>
	</div>
</body>
</html>