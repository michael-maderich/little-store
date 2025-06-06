<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
	<head>
<jsp:include page="/WEB-INF/views/includes/headElement.jsp">
	<jsp:param name="title" value="The Little Store - New Items" />
	<jsp:param name="page" value="newitems" />
</jsp:include>
	</head>
	<body>
		<header>
<jsp:include page="/WEB-INF/views/includes/basicHeader.jsp"></jsp:include>
		</header>
		<div id="main-content">
<jsp:include page="/WEB-INF/views/includes/sideNav.jsp">
	<jsp:param name="categoryName" value="${categoryName}" />
	<jsp:param name="subCatName" value="${subCategoryName}" />
</jsp:include>
 			<div id="center-content">
				<div id="product-panel">
					<h2>New Items Since Your Last Order</h2>
					${empty itemList ? '<div class="newItemsHeader"><br><h4>No New Items :(</h4></div>' : ''}
					${empty cartAdjustments ? '' : '<div class="cartChangeMsg"><br/><span style="color:red;">'.concat(cartAdjustments).concat('</span></div>')}
				<c:if test = "${not empty itemList}">
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
							<tr id="${item.upc}" ${item.stockQty==0 ? 'class="inactive"' : ''}>
								<c:set var="qtyInCart" value="${0}" />
								<c:forEach items="${cartItems}" var="cartItem">
								<c:if test="${(cartItem.product.upc == item.upc) && (cartItem.qty != 0)}">
									<c:set var="qtyInCart" value="${cartItem.qty}" />
								</c:if>
								</c:forEach>
								<td class="product_image_panel product_info"><a href="${item.image}" target="_new"><img src="${item.image}" alt="${item.description}" title="${item.description}" /></a></td>
								<td class="product_info">${item.name}</td>
								<td class="product_info">${item.options}</td>
								<td class="product_info">${item.size}</td>
								<td class="product_info">${item.currentPrice < item.basePrice ? '<span style="color:green; white-space: nowrap">Sale Price: ':''}<fmt:formatNumber value = "${item.currentPrice}" type = "currency" />${item.currentPrice < item.basePrice ? '</span>':''}
														 ${item.currentPrice < item.basePrice ? '<br /><span style="text-decoration:line-through; white-space: nowrap">':'<span visible="false"'}<fmt:formatNumber value = "${item.basePrice}" type = "currency" />${item.currentPrice < item.basePrice ? '</span>':'</span>'}
													 	 ${(showRetailPrice eq 1) and (item.retailPrice > 0) ? '<br /><span title="(Lowest retail price)" style="text-decoration:line-through; white-space: nowrap; font-size: 75%;">':'<span visible="false"'}Lowest Retail: <fmt:formatNumber value = "${item.retailPrice}" type = "currency" />${(showRetailPrice eq 1) and (item.retailPrice > 0) ? '</span>':'</span>'}</td>
								<td class="product_info">${(item.purchaseLimit != 0 and item.purchaseLimit < item.stockQty) ? item.purchaseLimit : item.stockQty}</td>
								<td class="customerQty product_info">
									<input type="hidden" id="upc${item.upc}" name="upc" value="${item.upc}" />
									<label for="itemQty">
										<input id="itemQty${item.upc}" type="number" class="qtyInput" name="itemQty" min="0" max="${(item.purchaseLimit != 0 and item.purchaseLimit < item.stockQty) ? (item.purchaseLimit - qtyInCart) : (item.stockQty - qtyInCart)}" step="1" value="0" ${item.stockQty==0 ? 'disabled style="visibility:hidden"' : ''} onInput="updateRows('${item.upc}')" />
									</label>
								</td>
 								<td class="button_panel product_info">
									<button id="addBtn${item.upc}" type="submit" class="addBtns btn btn-sm btn-primary btn-block" ${item.stockQty==0 ? 'disabled' : 'style="visibility:hidden"'}>${item.stockQty==0 ? 'Sold Out' : 'Add to Cart'}</button>
								</td>
								<c:if test = "${not empty cartItems}">
								<td class="transparent-td">
								<c:if test="${(qtyInCart > 0)}">(${qtyInCart} in cart)</c:if>
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
				</c:if>
				</div>
			</div>
		</div>
        <footer>
			<jsp:include page="/WEB-INF/views/includes/basicFooter.jsp"></jsp:include>
        </footer>
	</body>
</html>