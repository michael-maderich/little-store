<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
	<head>
<jsp:include page="/WEB-INF/views/includes/headElement.jsp">
	<jsp:param name="title" value="The Little Store - Checkout" />
	<jsp:param name="page" value="checkout" />
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
<!--             <div id="side-nav"> -->
<!--                 <ul class="nav flex-column"> -->
<!--                     <li class="nav-item"><a class="nav-link" href="/">Home</a></li> -->
<!--                     <li class="nav-item"><a class="nav-link" href="/cart">Back to Cart</a></li> -->
<!--                 </ul> -->
<!--             </div> -->
			<div id="center-content">
				<c:if test = "${not empty customerCart}">
				<div id="customer-panel">
                    <h2>Check Out</h2>
					<span style="color:blue;">
						<br />Please be sure to click the <b>Place Your Order</b> button to finalize your order.
						<br />You may make updates to the meet-up location and other options below.
						<br />Email confirmations are currently not working. Once submitted, please contact me about your order to set up meetup details.
					</span>
					${empty cartAdjustments ? '' : '<div class="cartChangeMsg"><br/><span style="color:red;">'.concat(cartAdjustments).concat('</span></div>')}
					<form:form id="orderForm" method="POST" modelAttribute="customerInfo" class="form-signin" action="/confirmation">
					<label for="submitBtn"><form:button id="submitBtn" name="submitBtn" type="submit" class="btn btn-sm btn-primary btn-block highlighted" onclick="submitForm()">
						PLACE YOUR ORDER
					</form:button></label>
                    <h4 class="checkoutHeader">Customer Details</h4>
					<span>(changes will be saved to profile)</span>
 					<table id="customer-table">
 						<tr>
 							<td></td>
 							<td class="customer_td_label">
 								<label for="name">Name:</label>
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
								placeholder=" (Or other contact)"
								value=" ${not empty customerInfo.phone ? customerInfo.phone.trim() : ''}" class="text-field"></form:input>
							</td>
 						</tr>
 						<tr>
 							<td colspan=2 class="customer_td_label">
 								<label for="address">Meet-Up Location:</label>
 							</td>
 							<td colspan=2 class="customer_td_input">
								<form:input path="address" id="address" type="text" class="text-field" placeholder="(Address, intersection, business, etc)"></form:input>
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
							<td colspan=1 class="customer_td_input">
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
								<form:input path="paymentHandle" type="text" id="paymentHandle" placeholder="$CashTag, @Venmo, etc (for confirmation)" class="text-field"></form:input>
							</td>
						</tr>
					</table>
					</form:form>
				</div>
				<div id="checkout-panel">
					<h4 class="checkoutHeader">Cart Details</h4>
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
								<td>${cartItem.price < cartItem.basePrice ? '<span style="color:green">':''}<fmt:formatNumber value = "${cartItem.price}" type = "currency" />${cartItem.price < cartItem.basePrice ? '</span>':''}
														 ${cartItem.price < cartItem.basePrice ? '<br /><span style="text-decoration:line-through">':'<span visible="false"'}<fmt:formatNumber value = "${cartItem.basePrice}" type = "currency" />${cartItem.price < cartItem.basePrice ? '</span>':'</span>'}</td>
								<td>${cartItem.price < cartItem.basePrice ? '<span style="color:green">':''}<fmt:formatNumber value = "${cartItem.qty * cartItem.price}" type = "currency" />${cartItem.price < cartItem.basePrice ? '</span>':''}
														 ${cartItem.price < cartItem.basePrice ? '<br /><span style="text-decoration:line-through">':'<span visible="false"'}<fmt:formatNumber value = "${cartItem.qty * cartItem.basePrice}" type = "currency" />${cartItem.price < cartItem.basePrice ? '</span>':'</span>'}</td>
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
				</c:if>
			</div>
		</div>
        <footer>
			<jsp:include page="/WEB-INF/views/includes/basicFooter.jsp"></jsp:include>
        </footer>
	</body>
</html>