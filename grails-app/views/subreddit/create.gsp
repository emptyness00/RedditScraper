<html>
<head>
<meta name="layout" content="main" />
<title>Welcome to Grails</title>
</head>
<body>
	<div id="page-body" role="main">
		<g:form name="createReddit" controller="subreddit">
			Enter Subreddits to add.  Delimit by whitespace.
			<g:textArea name="name" rows="10000" cols="25"/>
			<g:actionSubmit value="Save!" action="save"/>
		</g:form>
	</div>
</body>
</html>