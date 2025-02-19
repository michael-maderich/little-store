<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="headElement.jsp">
            <jsp:param name="title" value="The Little Store - Access Denied" />
            <jsp:param name="page" value="403" />
        </jsp:include>
    </head>
	<body>
        <header>
			<jsp:include page="basicHeader.jsp"></jsp:include>
        </header>
        <div id="main-content">
	        <jsp:include page="sideNav.jsp"></jsp:include>
            <div id="center-content">
				<div class="page-title"><b>Access Denied</b></div>
				<h3 style="color:red;">Sorry, you can not access this page!</h3>
            </div>
        </div>
        <footer>
			<jsp:include page="basicFooter.jsp"></jsp:include>
        </footer>
	</body>
</html>