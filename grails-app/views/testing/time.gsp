<html>
<head>
<g:javascript library="jQuery" />
</head>
<body>
	<div id="message"></div>
	<div id="error"></div>
	<g:remoteLink update="[success: 'message', failure: 'error']"
		action="update">
		Delete Book
	</g:remoteLink>
</body>
</html>