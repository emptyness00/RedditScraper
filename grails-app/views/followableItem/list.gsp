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
			<td>Followed?</td>
			</th>
				<g:each in="${followableItemInstanceList}" status="i" var="followableItem">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link action="show"
								id="${followableItem.id}">
								${followableItem.id?.encodeAsHTML()}
							</g:link></td>
						</td>
						<td>
							${followableItem.link.encodeAsHTML()}
						</td>
						<td>
							${followableItem.followed.encodeAsHTML()}
						</td>
					</tr>
				</g:each>
		</table>
	</div>
</body>
</html>