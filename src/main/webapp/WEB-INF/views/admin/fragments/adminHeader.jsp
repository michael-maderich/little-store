<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="userName" value="${pageContext.request.userPrincipal.name}" />
<!DOCTYPE html>
<html lang="en">
	<head>
<jsp:include page="/WEB-INF/views/includes/headElement.jsp">
	<jsp:param name="title" value="The Little Store - Admin Console" />
	<jsp:param name="page" value="admin" />
</jsp:include>
	</head>
	<body>
		<header>
			<div id="action-icons">
				<form id="logoutForm" method="POST" action="${contextPath}/logout">
<%-- 					<span>${userName != null ? 'Logged in as: '.concat(userName) : 'Login/Sign Up ->'}</span> --%>
					<c:if test="${userName != null}">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
					<input type="submit" id="logoutbtn" name="logoutbtn" value="Sign Out" class="btn btn-sm btn-primary btn-block" />
					</c:if>
					<a href="${pageContext.request.userPrincipal.name != null ? '/account' : '/login'}">
						<i class="fas fa-user-alt" title="${pageContext.request.userPrincipal.name != null ? 'Account' : 'Sign Up/Login'}"></i>
					</a>
				</form>
			</div>
		</header>
		<div id="main-content">
<jsp:include page="/WEB-INF/views/admin/fragments/adminSideNav.jsp"/>
			<div id="center-content">
