<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
	<head>
<jsp:include page="headElement.jsp">
	<jsp:param name="title" value="The Little Store - ${categoryName}${subCategoryName != null? ' - '.concat(subCategoryName).concat(' ') : '' }Category" />
	<jsp:param name="page" value="category" />
</jsp:include>
	</head>
	<body>
		<header>
<jsp:include page="basicHeader.jsp"></jsp:include>
		</header>
		<div id="main-content">
<jsp:include page="sideNav.jsp">
	<jsp:param name="categoryName" value="${categoryName}" />
	<jsp:param name="subCatName" value="${subCategoryName}" />
</jsp:include>
 			<div id="center-content">
				<div id="product-panel">
					<h2>New Items Since Your Last Visit</h2>
					${empty itemList ? '<div class="newItemsHeader"><br><h4>No New Items :(</h4></div>' : ''}
				<c:if test = "${not empty itemList}">
					<table id="product-table">
						<thead>
							<tr>
								<th>(Click to<br /> Enlarge)</th>
								<th>Item</th>
								<th>Scent/Style</th>
								<th>Size</th>
								<th>Price</th>
<%--								<th>Quantity<br />Available</th>
								<th>Quantity<br />to Add</th>
								<th></th>--%>
							</tr>
						</thead>
						<tbody>
						<c:forEach items="${itemList}" var="item">
						<form action="/addToCart" method="GET">
							<tr ${item.stockQty==0 ? 'class="inactive"' : ''}>
								<td class="product_image_panel"><a href="${item.image}" target="_blank"><img src="${item.image}" alt="${item.description}" title="${item.description}" /></a></td>
								<td>${item.name}</td>
								<td>${item.options}</td>
								<td>${item.size}</td>
								<td><fmt:formatNumber value = "${item.currentPrice}" type = "currency" /></td>
<%--								<td>${item.stockQty}</td>
								<td class="customerQty">
									<input type="hidden" id="upc${item.upc}" name="upc" value="${item.upc}" />
									<label for="itemQty">
										<input type="number" id="itemQty${item.upc}" name="itemQty" min="0" max="${item.stockQty}" step="1" value="0" ${item.stockQty==0 ? 'disabled' : ''} />
									</label>
								</td>
 								<td class="button_panel">
									<button type="submit" class="btn btn-sm btn-primary btn-block" ${item.stockQty==0 ? 'disabled' : ''}>${item.stockQty==0 ? 'Out of Stock' : 'Add to Cart'}</button>
								</td> --%>
								<span>
									${ addedUpc eq item.upc ? '<span style="color:blue;">'
									.concat(addedItemQty).concat(' ').concat(item.name).concat(' ').concat(item.options)
									.concat(' ').concat(item.size).concat(' added to <a href="/cart" alt="View Cart" title="View Cart">cart</a></span> ') : ''}
								</span>
							</tr>
						</form>
						</c:forEach>
						</tbody>
						<tfoot>
						</tfoot>
					</table>
				</c:if>
				</div>
			</div>
		</div>
        <footer>
			<jsp:include page="basicFooter.jsp"></jsp:include>
        </footer>
	</body>
</html>