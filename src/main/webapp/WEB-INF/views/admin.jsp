<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="headElement.jsp">
			<jsp:param name="title" value="The Little Store - ADMIN ACCESS" />
			<jsp:param name="page" value="index" />
		</jsp:include>
	</head>
	<body>
		<header>
			<jsp:include page="basicHeader.jsp"></jsp:include>
		</header>
		<div id="main-content">
			<jsp:include page="sideNav.jsp"></jsp:include>
			<div id="center-content"><!--  style="height:100%; background-image:url('images/Main_BG.jpg');opacity:50%;"> -->
				<p>The Little Store</p>
 				<div id="sitemap">
					<table>
						<tr><c:set var="numCats" value="${0}" /><c:forEach items="${navMenuItems}" var="mainCategory">
							<td>
								<a class="nav-link" href="/category/${mainCategory}">${mainCategory}</a>
							</td><c:set var="numCats" value="${numCats+1}" /><c:if test = "${numCats eq 4}"><c:set var="numCats" value="${0}" />
						</tr>
						<tr></c:if></c:forEach>
						</tr>
					</table>
				</div>
				<img src="images/Main_BG.jpg" alt="Stockpile Photo" />
			</div>
		</div>
		<footer>
			<jsp:include page="basicFooter.jsp"></jsp:include>
		</footer>
	</body>
</html>