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
            <div id="side-nav">
                <ul class="nav flex-column">
                    <li class="nav-item"><a class="nav-link" href="/">Home</a></li>
                    <li class="nav-item"><a class="nav-link" href="/account">My Account</a></li>
                    <li class="nav-item"><a class="nav-link" href="/index">Continue Shopping</a></li>
                </ul>
            </div>
			<div id="center-content">
				<div id="cart-panel">
                    <h2>Shopping Cart</h2>
					${empty customerCart.cartItems ? '<div class="cartDetailHeader"><h4>No Items in Cart</h4></div>' : '<span style="color:blue;">'
									.concat('When you are ready, please click the <b>Check Out</b> button, then Submit your order on the next page.')}
					${empty cartAdjustments ? '' : '<div class="cartChangeMsg"><br/><span style="color:red;">'.concat(cartAdjustments).concat('</span></div>')}
				<c:if test = "${not empty customerCart.cartItems}">
					<c:set var="cartTotal" value="${0}" />
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
								<td class="cart_image_panel"><img src="${cartItem.product.image}" alt="${cartItem.product.description}" /></td>
								<td>${cartItem.product.name}</td>
								<td>${cartItem.product.options}</td>
								<td>${cartItem.product.size}</td>
								<td>${cartItem.qty}</td>
								<td><fmt:formatNumber value = "${cartItem.price}" type = "currency" /></td>
								<td><fmt:formatNumber value = "${cartItem.qty * cartItem.price}" type = "currency" /></td>
								<td class="button_panel">
									<input type="hidden" id="upc" name="upc" value="${cartItem.product.upc}" />
									<button type="submit" class="btn btn-sm btn-primary btn-block">Remove</button>
								</td>
							</tr>
							<c:set var="cartTotal" value="${cartTotal + cartItem.qty * cartItem.price}" />
						</form>
						</c:forEach></tbody>
						<tfoot>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td class="cart_subtotal_panel">Total:</td>
								<td class="cart_subtotal_panel"><fmt:formatNumber value = "${cartTotal}" type = "currency" /></td>
								<td>
								<form action="${cartTotal!=0 ? '/checkout' : '/'}"><!-- method="POST" modelAttribute="customerCart"-->
									<button type="submit" class="btn btn-sm btn-primary btn-block highlighted">
										${cartTotal!=0 ? 'Proceed to<br />Check Out' : 'Continue Shopping'}
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