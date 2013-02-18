<html>
<head>
<meta name="layout" content="main" />
<title>Welcome to Grails</title>
</head>
<body>
	<div id="page-body" role="main">
		<g:form controller="configuration">
			<g:textField name="saveToDir" value="${configurationInstance.saveToDir}"/>
			<g:hiddenField name="id" value="${configurationInstance.id}"/>
			<g:actionSubmit name="submit" value="Save" action="update"/>
		</g:form>

	</div>
</body>
</html>