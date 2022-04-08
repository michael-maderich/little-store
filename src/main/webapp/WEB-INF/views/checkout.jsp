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
	<jsp:param name="title" value="The Little Store - Checkout" />
	<jsp:param name="page" value="checkout" />
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
                    <li class="nav-item"><a class="nav-link" href="/cart">Back to Cart</a></li>
                </ul>
            </div>
			<div id="center-content">
				<c:if test = "${not empty customerCart}">
				<div id="checkout-panel">
                    <h2>Check Out</h2>
					<span style="color:blue;">
						<br />Please be sure to click the <b>Submit Order</b> button to finalize your order.
						<br />You will receive an email confirmation. Once submitted, please contact me to set up meetup details.
					</span>
					<h4 class="checkoutHeader">Order Details</h4>
					${empty cartAdjustments ? '' : '<div class="cartChangeMsg"><br/><span style="color:red;">'.concat(cartAdjustments).concat('</span></div>')}
					<c:set var="cartTotal" value="${0}" />
					<table id="checkout-table">
						<thead>
							<tr>
								<th></th>
								<th>Item</th>
								<th>Scent/Style</th>
								<th>Size</th>
								<th>Quantity</th>
								<th>Unit Price</th>
								<th>Subtotal</th>
							</tr>
						</thead>
						<tbody>
						<c:forEach items="${customerCart.cartItems}" var="cartItem">
							<tr>
								<td class="checkout_image_panel"><img src="${cartItem.product.image}" alt="${cartItem.product.description}" /></td>
								<td>${cartItem.product.name}</td>
								<td>${cartItem.product.options}</td>
								<td>${cartItem.product.size}</td>
								<td>${cartItem.qty}</td>
								<td><fmt:formatNumber value = "${cartItem.price}" type = "currency" /></td>
								<td><fmt:formatNumber value = "${cartItem.qty * cartItem.price}" type = "currency" /></td>
							</tr>
							<c:set var="cartTotal" value="${cartTotal + cartItem.qty * cartItem.price}" />
						</c:forEach></tbody>
						<tfoot>
							<tr>
								<td  colspan=6 style="text-align:right;" class="checkout_subtotal_panel">Total:</td>
								<td class="checkout_subtotal_panel"><fmt:formatNumber value = "${cartTotal}" type = "currency" /></td>
							</tr>
						</tfoot>
					</table>
				</div>
				<div id="customer-panel">
					<form:form method="POST" modelAttribute="customerInfo" class="form-signin" action="/confirmation">
					<label for="submit"><form:button id="submit" name="submit" type="submit" class="btn btn-sm btn-primary btn-block highlighted">
						Submit Order
					</form:button></label>
                    <h4 class="checkoutHeader">Customer Details</h4>
					<span>(changes will be saved to profile)</span>
 					<table id="customer-table">
 						<tr>
 							<td></td>
 							<td class="customer_td_label">
 								<label for="email">Name:</label>
 							</td>
 							<td colspan=2 class="customer_td_input">
								<input id="customerName" name="customerName" type="text"
								placeholder=" ${customerInfo.firstName} ${customerInfo.lastName}" class="info-field" disabled></input>
								<form:input path="lastName" id="lastName" type="hidden" class="text-field"></form:input>
								<form:input path="lastName" id="lastName" type="hidden" class="text-field"></form:input>
 							</td>
 							<td class="customer_td_label">
 								<label for="email">Email:</label>
 							</td>
 							<td colspan=2 class="customer_td_input">
								<input id="email" type="email" placeholder=" ${customerInfo.email}" class="text-field" disabled></input>
							</td>
							<td class="customer_td_label">
 								<label for="phone">Phone:</label>
 							</td>
 							<td colspan=2 class="customer_td_input">
								<form:input path="phone" id="phone" type="text"
								placeholder=" (Or Other Contact)"
								value=" ${not empty customerInfo.phone ? customerInfo.phone.trim() : ''}" class="text-field"></form:input>
							</td>
 						</tr>
 						<tr>
 							<td colspan=2 class="customer_td_label">
 								<label for="address">Meet-Up Address:</label>
 							</td>
 							<td colspan=2 class="customer_td_input">
								<form:input path="address" id="address" type="text" class="text-field"></form:input>
							</td>
 							<td class="customer_td_label">
 								<label for="city">City:</label>
 							</td>
 							<td colspan=2 class="customer_td_input">
								<form:input path="city" id="city" type="text" class="text-field"></form:input>
							</td>
 							<td class="customer_td_label">
								<label for="state">State:</label>
 							</td>
 							<td class="customer_td_input">
							<form:select path="state" name="state" id="state">
							<c:forEach items="${listStates}" var="st">
								<option value="${st}" ${st==customerInfo.state ? 'selected' : ''}>${st}</option>
							</c:forEach>
							</form:select>
							</td>
						</tr>
						<tr>
							<td colspan=2 class="customer_td_label">
								<label for="paymentType">Payment Type:</label>
							</td>
							<td colspan=2 class="customer_td_input">
								<form:select path="preferredPayment" name="preferredPayment">
									<c:forEach items="${listPayTypes}" var="paymentType">
									<option value="${paymentType}" ${paymentType==customerInfo.preferredPayment ? 'selected' : ''}>
										${paymentType}
									</option>
									</c:forEach>
								</form:select>
							</td>
							<td colspan=2 class="customer_td_label">
								<label for="paymentHandle">Payment Handle:</label>
							</td>
							<td colspan=2 class="customer_td_input">
								<form:input path="paymentHandle" type="text" id="paymentHandle" placeholder="$CashTag, @Venmo, etc" class="text-field"></form:input>
							</td>
						</tr>
					</table>
					</form:form>
				</div>
				</c:if>
			</div>
		</div>
        <footer>
			<jsp:include page="basicFooter.jsp"></jsp:include>
        </footer>
	</body>
</html>