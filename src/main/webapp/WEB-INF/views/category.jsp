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
	<jsp:param name="title" value="The Little Store - ${categoryName}${subCategoryName != null? ' - '.concat(subCategoryName).concat(' ') : ' ' }Category" />
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
					<h2>${categoryName}</h2>
					${subCategoryName != null? '<h4>'.concat(subCategoryName).concat('</h4>') : ''}
					${empty cartAdjustments ? '' : '<div class="cartChangeMsg"><br/><span style="color:red;">'.concat(cartAdjustments).concat('</span></div>')}
					<table id="product-table">
						<thead>
							<tr>
								<th>(Click to<br /> Enlarge)</th>
								<th>Item</th>
								<th>Scent/Style</th>
								<th>Size</th>
								<th>Price</th>
								<th>Quantity<br />Available</th>
								<th>Quantity<br />to Add</th>
								<th></th>
							</tr>
						</thead>
						<tbody>
						<c:forEach items="${itemList}" var="item">
						<form action="/addToCart" method="GET">
							<tr ${item.stockQty==0 ? 'class="inactive"' : ''}>
								<td class="product_image_panel product_info"><a href="${item.image}" target="_blank"><img src="${item.image}" alt="${item.description}" title="${item.description}" /></a></td>
								<td class="product_info">${item.name}</td>
								<td class="product_info">${item.options}</td>
								<td class="product_info">${item.size}</td>
								<td class="product_info">${item.onSale eq true ? '<span style="color:green">':''}<fmt:formatNumber value = "${item.currentPrice}" type = "currency" />${item.onSale eq true ? '</span>':''}
														${item.onSale eq true ? '<br /><span style="text-decoration:line-through">':'<span visible="false"'}<fmt:formatNumber value = "${item.basePrice}" type = "currency" />${item.onSale eq true ? '</span>':'</span>'}</td>
								<td class="product_info">${(item.purchaseLimit != 0 and item.purchaseLimit < item.stockQty) ? item.purchaseLimit : item.stockQty}</td>
								<td class="customerQty product_info">
									<input type="hidden" id="upc${item.upc}" name="upc" value="${item.upc}" />
									<label for="itemQty">
										<input type="number" id="itemQty${item.upc}" name="itemQty" min="0" max="${(item.purchaseLimit != 0 and item.purchaseLimit < item.stockQty) ? item.purchaseLimit : item.stockQty}" step="1" value="0" ${item.stockQty==0 ? 'disabled' : ''} />
									</label>
								</td>
								<td class="button_panel product_info">
									<button type="submit" class="btn btn-sm btn-primary btn-block" ${item.stockQty==0 ? 'disabled' : ''}>${item.stockQty==0 ? 'Out of Stock' : 'Add to Cart'}</button>
								</td>
								<c:if test = "${not empty cartItems}">
								<td class="transparent-td">
								<c:forEach items="${cartItems}" var="cartItem">
								<c:if test="${(cartItem.product.upc == item.upc) && (cartItem.qty != 0)}">(${cartItem.qty} in cart)</c:if>
								</c:forEach>
								</td>
								</c:if>
								<span>
									${ addedUpc eq item.upc ? '<span style="color:blue;">'
									.concat(addedItemQty).concat(' ').concat(item.name).concat(' ').concat(item.options)
									.concat(' ').concat(item.size).concat(' added to <a href="/cart" alt="View Cart" title="View Cart">cart</a>')
									.concat('<br/>If you are ready to check out, please click the <a href="/cart" alt="View Cart" title="View Cart">cart</a> icon in the upper right.</span> ') : ''}
								</span>
							</tr>
						</form>
						</c:forEach>
						</tbody>
						<tfoot>
						</tfoot>
					</table>
				</div>
			</div>
		</div>
        <footer>
			<jsp:include page="basicFooter.jsp"></jsp:include>
        </footer>
	</body>
</html>