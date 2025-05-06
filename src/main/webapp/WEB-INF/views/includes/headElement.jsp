		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<title>${param.title}</title>
		<link href="${contextPath}/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
		<link href='https://fonts.googleapis.com/css?family=Inknut Antiqua|Gravitas One' rel='stylesheet'>
		<link href="${contextPath}/css/main.css" rel="stylesheet" type="text/css" />
		<link href="${contextPath}/css/${param.page}.css" rel="stylesheet" type="text/css" />
		<script src="https://kit.fontawesome.com/5910fe5993.js"></script>
		<script src="${contextPath}/js/bootstrap.min.js"></script>
		<script src="${contextPath}/js/${param.page}.js" async="true"></script>
		<link rel="shortcut icon" type="image/jpg" href="${contextPath}/images/favicon.jpg"/>
		<style>${param.page eq 'index' ? indexStyle : mainStyle}
		</style>