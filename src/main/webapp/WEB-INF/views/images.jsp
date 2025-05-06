<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
	<head>
<jsp:include page="/WEB-INF/views/includes/headElement.jsp">
	<jsp:param name="title" value="The Little Store - Item Grid" />
	<jsp:param name="page" value="images" />
</jsp:include>
	</head>
	<body>
		<header>
<jsp:include page="/WEB-INF/views/includes/basicHeader.jsp"></jsp:include>
		</header>
		<div id="main-content">
<jsp:include page="/WEB-INF/views/includes/sideNav.jsp"></jsp:include>
			<div id="center-content">
				<div id="product-panel">
					<h2>Product List</h2>
<c:forEach items="${allItems}" var="mainCatGroup">
					<div>
	<c:forEach items="${mainCatGroup}" var="subCatGroup">
						<div>
						<h4><br><br>${subCatGroup[0][0].categorySpecific}</h4>
		<c:forEach items="${subCatGroup}" var="nameSizeGroup"><c:set var="rowMax" value="${Math.ceil(Math.sqrt(nameSizeGroup.size()))}"/>
							<table id="product-table">
								<tr><th colspan=4>${nameSizeGroup[0].name} ${nameSizeGroup[0].size} - <fmt:formatNumber value = "${nameSizeGroup[0].currentPrice}" type = "currency" /></th></tr>
								<tr><c:set var="numItems" value="${0}" /><c:forEach items="${nameSizeGroup}" var="item">
									<td class="product_image_panel product_info">
										<a href="${item.image}" target="_blank"><img src="${item.image}" alt="${item.description}" title="${item.description}" /></a>
									</td>
									<c:set var="numItems" value="${numItems+1}" /><c:if test = "${numItems eq rowMax}"><c:set var="numItems" value="${0}" />
								</tr>
								<tr></c:if></c:forEach>
								</tr>
							</table>
		</c:forEach>
						</div>
	</c:forEach>
					</div>
</c:forEach>
				</div>
			</div>
		</div>
        <footer>
			<jsp:include page="/WEB-INF/views/includes/basicFooter.jsp"></jsp:include>
        </footer>
	</body>
</html>