<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
	<head>
<jsp:include page="/WEB-INF/views/includes/headElement.jsp">
	<jsp:param name="title" value="The Little Store - Home" />
	<jsp:param name="page" value="index" />
</jsp:include>
	</head>
	<body>
		<header>
			<jsp:include page="/WEB-INF/views/includes/basicHeader.jsp"></jsp:include>
		</header>
		<div id="main-content">
			<jsp:include page="/WEB-INF/views/includes/sideNav.jsp"></jsp:include>
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
				${(indexBgImageHyperlink eq null or indexBgImageHyperlink eq '') ? '' : ('<a href="').concat(indexBgImageHyperlink).concat('">')}
				<img src="${(indexBgImageSrc eq null or indexBgImageSrc eq '') ? indexBgImageSrcDefault : indexBgImageSrc}" alt="${indexBgImageAltText}" title="${indexBgImageTooltip}" />
				${(indexBgImageHyperlink eq null or indexBgImageHyperlink eq '') ? '' : '</a>'}
			</div>
		</div>
		<footer>
			<jsp:include page="/WEB-INF/views/includes/basicFooter.jsp"></jsp:include>
		</footer>
	</body>
</html>