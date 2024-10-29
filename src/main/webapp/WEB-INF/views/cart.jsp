<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
	<head>
<jsp:include page="headElement.jsp">
	<jsp:param name="title" value="The Little Store - Shopping Cart" />
	<jsp:param name="page" value="cart" />
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
				<div id="cart-panel">
                    <h2>Shopping Cart</h2>
					${empty customerCart.cartItems ? '<div class="cartDetailHeader"><h4>No Items in Cart</h4></div>' : '<span style="color:blue;">'
									.concat('When you are ready, please click the <b>Proceed to Check Out</b> button, then Submit your order on the next page.')}
					${empty cartAdjustments ? '' : '<div class="cartChangeMsg"><br/><span style="color:red;">'.concat(cartAdjustments).concat('</span></div>')}
				<c:if test = "${not empty customerCart.cartItems}">
					<c:set var="cartTotal" value="${0}" />
					<c:set var="cartRetailTotal" value="${0}" />
					<c:set var="cartSavingsTotal" value="${0}" />
					<table id="cart-table">
						<thead>
							<tr>
								<th></th>
								<th>Item</th>
								<th>Scent/Style</th>
								<th>Size</th>
								<th>Quantity</th>
								<th>Unit Price</th>
								<th>Subtotal</th>
								<th>
									<form action="${customerCart.cartItems!=null ? '/clearCart' : '/'}">
										<button type="submit" ${customerCart.cartItems==null ? 'hidden ' : ''}class="btn btn-sm btn-primary btn-block highlighted">
											Remove All
										</button>
									</form>
								</th>
							</tr>
						</thead>
						<tbody><c:forEach items="${customerCart.cartItems}" var="cartItem">
						<form action="/removeFromCart" method="GET">
							<tr>
								<td class="cart_image_panel"><a href="/category/${cartItem.product.categoryMain}/${cartItem.product.categorySpecific}/#${cartItem.product.upc}"><img src="${cartItem.product.image}" alt="${cartItem.product.description}" /></a></td>
								<td><a href="/category/${cartItem.product.categoryMain}/${cartItem.product.categorySpecific}/#${cartItem.product.upc}">${cartItem.product.name}</a></td>
								<td><a href="/category/${cartItem.product.categoryMain}/${cartItem.product.categorySpecific}/#${cartItem.product.upc}">${cartItem.product.options}</a></td>
								<td><a href="/category/${cartItem.product.categoryMain}/${cartItem.product.categorySpecific}/#${cartItem.product.upc}">${cartItem.product.size}</a></td>
								<td>${cartItem.qty}</td>
								<td>${cartItem.price < cartItem.basePrice ? '<span style="color:green; white-space: nowrap">Sale Price: ':''}<fmt:formatNumber value = "${cartItem.price}" type = "currency" />${cartItem.price < cartItem.basePrice ? '</span>':''}
								    ${cartItem.price < cartItem.basePrice ? '<br /><span style="text-decoration:line-through; white-space: nowrap">':'<span visible="false"'}<fmt:formatNumber value = "${cartItem.basePrice}" type = "currency" />${cartItem.price < cartItem.basePrice ? '</span>':'</span>'}
							 	    ${(showRetailPrice eq 1) and (cartItem.retailPrice > 0) ? '<br /><span title="(Lowest retail price)" style="text-decoration:line-through; white-space: nowrap; font-size: 75%;">':'<span visible="false"'}Lowest Retail: <fmt:formatNumber value = "${cartItem.retailPrice}" type = "currency" />${(showRetailPrice eq 1) and (cartItem.retailPrice > 0) ? '</span>':'</span>'}</td>
								<td>${cartItem.price < cartItem.basePrice ? '<span style="color:green; white-space: nowrap">Sale Price: ':''}<fmt:formatNumber value = "${cartItem.qty * cartItem.price}" type = "currency" />${cartItem.price < cartItem.basePrice ? '</span>':''}
								    ${cartItem.price < cartItem.basePrice ? '<br /><span style="text-decoration:line-through; white-space: nowrap">':'<span visible="false"'}<fmt:formatNumber value = "${cartItem.qty * cartItem.basePrice}" type = "currency" />${cartItem.price < cartItem.basePrice ? '</span>':'</span>'}
							 	    ${(showRetailPrice eq 1) and (cartItem.retailPrice > 0) ? '<br /><span title="(Savings off Retail)" style="color:green; white-space: nowrap; font-size: 75%;">':'<span visible="false"'}Savings off Retail: <fmt:formatNumber value = "${cartItem.qty * (cartItem.retailPrice - cartItem.price)}" type = "currency" />${(showRetailPrice eq 1) and (cartItem.retailPrice > 0) ? '</span>':'</span>'}</td>
								<td class="button_panel">
									<input type="hidden" id="upc" name="upc" value="${cartItem.product.upc}" />
									<button type="submit" class="btn btn-sm btn-primary btn-block">Remove</button>
								</td>
							</tr>
							<c:set var="cartTotal" value="${cartTotal + cartItem.qty * cartItem.price}" />
							<c:set var="cartRetailTotal" value="${cartRetailTotal + cartItem.qty * cartItem.retailPrice}" />
							<c:if test = "${cartItem.retailPrice > 0}">
								<c:set var="cartSavingsTotal" value="${cartSavingsTotal + cartItem.qty * (cartItem.retailPrice - cartItem.price)}" />
							</c:if>
						</form>
						</c:forEach></tbody>
						<tfoot>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td class="cart_subtotal_panel">
									<span style="white-space: nowrap">Total:</span>
 	    							${(showRetailPrice eq 1) and (cartSavingsTotal > 0) ? '<br /><span title="(Savings off lowest retail total)" style="color:green; white-space: nowrap; font-size: 75%;">':'<span visible="false"'}Your Savings:${(showRetailPrice eq 1) and (cartSavingsTotal > 0) ? '</span>':'</span>'}
    							</td>
								<td class="cart_subtotal_panel">
									<span style="white-space: nowrap"><fmt:formatNumber value = "${cartTotal}" type = "currency" /></span>
 	    							${(showRetailPrice eq 1) and (cartSavingsTotal > 0) ? '<br /><span title="(Savings off lowest retail total)" style="color:green; white-space: nowrap; font-size: 75%;">':'<span visible="false"'}<fmt:formatNumber value = "${cartSavingsTotal}" type = "currency" />${(showRetailPrice eq 1) and (cartSavingsTotal > 0) ? '</span>':'</span>'}
    							</td>
    							<td>
								<form action="${cartTotal!=0 ? '/checkout' : '/'}"><!-- method="POST" modelAttribute="customerCart"-->
									<button type="submit" class="btn btn-sm btn-primary btn-block highlighted" ${cartTotal < orderMinimum ? 'disabled' : ''}>
										<c:if test = "${cartTotal ==0}">
										Continue Shopping
										</c:if>
										<c:if test = "${cartTotal < orderMinimum}">
										<fmt:formatNumber value = "${orderMinimum}" type = "currency" /> Order<br />Minimum
										</c:if>
										<c:if test = "${cartTotal >= orderMinimum}">
										Proceed to<br />Check Out
										</c:if>
									</button>
								</form>
								</td>
							</tr>
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