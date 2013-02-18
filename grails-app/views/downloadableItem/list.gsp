<html>
<head>
<meta name="layout" content="main" />
<title>Welcome to Grails</title>
</head>
<body>
	<div id="page-body" role="main">
		<table>
			<th>
			<td>Link URL</td>
			<td>Downloaded?</td>
			</th>
				<g:each in="${downloadableItemInstanceList}" status="i" var="downloadableItem">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show"
								id="${downloadableItem.id}">
								${downloadableItem.id?.encodeAsHTML()}
							</g:link></td>
						</td>
						<td>
							${downloadableItem.link.encodeAsHTML()}
						</td>
						<td>
							${downloadableItem.downloaded.encodeAsHTML()}
						</td>
					</tr>
				</g:each>
		</table>
	</div>
</body>
</html>