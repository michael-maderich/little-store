<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="headElement.jsp">
			<jsp:param name="title" value="The Little Store - Home" />
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
				<a href="/category/Christmas%20Shop" title="Christmas Shop"><img src="https://scontent-lga3-1.xx.fbcdn.net/v/t39.30808-6/317681968_10229362818512720_266396512975389182_n.jpg?stp=cp6_dst-jpg&_nc_cat=103&ccb=1-7&_nc_sid=8bfeb9&_nc_ohc=SrFaZsm_RmoAX_uaofZ&_nc_ht=scontent-lga3-1.xx&oh=00_AfAGReC9La25BxdXMdbBM24_k81TO0CLFv7HAg9aXWgjng&oe=638E3A6E" alt="Christmas Shop" /></a>
				<!--img src="images/Main_BG.jpg" alt="Stockpile Photo" /-->
			</div>
		</div>
		<footer>
			<jsp:include page="basicFooter.jsp"></jsp:include>
		</footer>
	</body>
</html>