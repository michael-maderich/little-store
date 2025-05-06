<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="/WEB-INF/views/includes/headElement.jsp">
			<jsp:param name="title" value="The Little Store - ADMIN ACCESS" />
			<jsp:param name="page" value="admin" />
		</jsp:include>
	</head>
	<body>
		<header>
			<jsp:include page="/WEB-INF/views/includes/basicHeader.jsp"></jsp:include>
		</header>
		<div id="main-content">
<%-- 			<jsp:include page="sideNav.jsp"></jsp:include> --%>
			<ul>
				<li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
				<li><a href="${path}/admin/orders">Orders</a></li>
				<li><a href="${path}/admin/products">Inventory</a></li>
				<li><a href="${path}/admin/customers">Customers</a></li>
				<sec:authorize access="hasRole('ADMIN')">
				<li><a href="${path}/admin/users">User Management</a></li>
			   	</sec:authorize>
			</ul>
			<div id="center-content" style="height:100%; background-image:url('images/Main_BG.jpg');opacity:50%;">
				<h1>The Little Store ADMIN</h1>
				<img src="images/Main_BG.jpg" alt="Stockpile Photo" />
			</div>
		</div>
		<footer>
			<jsp:include page="/WEB-INF/views/includes/basicFooter.jsp"></jsp:include>
		</footer>
	</body>
</html>