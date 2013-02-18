<html>
<head>
<meta name="layout" content="main" />
<title>Welcome to Grails</title>
</head>
<body>
	<div id="page-body" role="main">
		<h1>
			${subredditInstance.name?.encodeAsHTML()}
		</h1>
		<table>
			<th>Topic
			<td>Followed?</td>
			<td>Link Address</td>
			<td>Date/Time</td>
			</th>
			<g:each in="${subredditInstance.topics}" status="i" var="topic">
				<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					<td>
						${topic.name.encodeAsHTML()}
					</td>
					<td>
						${topic.followed.encodeAsHTML()}
					</td>
					<td>
						${topic.link?.encodeAsHTML()}
					</td>
					<td>
						${topic.timestamp?.encodeAsHTML()}
					</td>
				</tr>
				<tr>
					<td><table>
							<th>Link
							<td>Downloaded?</td>
							</th>
							<g:each in="${topic.downloadableItems}" status="j"
								var="downloadable">
								<tr class="${(j % 2) == 0 ? 'even' : 'odd'}">
									<td>
										${downloadable.link?.encodeAsHTML()}
									</td>
									<td>
										${downloadable.downloaded?.encodeAsHTML()}
									</td>
								</tr>
							</g:each>
						</table></td>
				</tr>
				<tr>
					<td><table>
							<th>Link
							<td>Followed?</td>
							</th>
							<g:each in="${topic.followableItems}" status="k" var="followable">
								<tr class="${(k % 2) == 0 ? 'even' : 'odd'}">
									<td>
										${followable.link?.encodeAsHTML()}
									</td>
									<td>
										${followable.followed?.encodeAsHTML()}
									</td>
								</tr>
							</g:each>
						</table></td>
				</tr>
			</g:each>
		</table>

	</div>
</body>
</html>