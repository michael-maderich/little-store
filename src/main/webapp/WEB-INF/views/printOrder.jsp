<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix = "func" uri = "http://java.sun.com/jsp/jstl/functions" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
	<head>
<jsp:include page="headElement.jsp">
	<jsp:param name="title" value="The Little Store - Confirmation" />
	<jsp:param name="page" value="confirmation" />
</jsp:include>
	</head>
	<body>
		<div id="main-content">
			<div id="center-content">
				<c:if test = "${not empty customerOrder}">
				<div id="customer-panel">
                    <h2>Thank You For Your Order!</h2>
                    <h4 class="checkoutHeader">Customer Details</h4>
 					<table id="customer-table">
 						<tr>
 							<td></td>
 							<td class="customer_td_label">
 								<label for="name">Name:</label>
 							</td>
 							<td colspan=2 class="customer_td_input">
								<input id="customerName" name="customerName" type="text"
								placeholder=" ${customerInfo.firstName} ${customerInfo.lastName}" class="info-field" disabled></input>
 							</td>
 							<td class="customer_td_label">
 								<label for="email">Email:</label>
 							</td>
 							<td colspan=2 class="customer_td_input">
								<input name="email" id="email" type="email"
								placeholder=" ${customerInfo.email}" class="info-field" disabled></input>
							</td>
							<td class="customer_td_label">
 								<label for="phone">Phone:</label>
 							</td>
 							<td colspan=2 class="customer_td_input">
								<input name="phone" id="phone" type="text"
								placeholder=" ${not empty customerInfo.phone ? customerInfo.phone : '(None Supplied)'}" class="info-field" disabled></input>
							</td>
 						</tr>
 						<tr>
 							<td colspan=2 class="customer_td_label">
 								<label for="address">Meet-Up Address:</label>
 							</td>
 							<td colspan=2 class="customer_td_input">
								<input name="address" id="address" type="text" placeholder=" ${customerInfo.address}" class="text-field" disabled></input>
							</td>
 							<td class="customer_td_label">
 								<label for="city">City:</label>
 							</td>
 							<td colspan=2 class="customer_td_input">
								<input name="city" id="city" type="text" placeholder=" ${customerInfo.city}" class="text-field" disabled></input>
							</td>
 							<td class="customer_td_label">
								<label for="state">State:</label>
 							</td>
 							<td class="customer_td_input">
 							<c:forEach items="${listStates}" var="st">
								<c:if test="${st==customerInfo.state}">
									<input name="state" id="state" type="text" placeholder=" ${st}" class="text-field" disabled></input>
								</c:if>
							</c:forEach>
							</td>
							<td></td>
						</tr>
						<tr>
							<td colspan=2 class="customer_td_label">
								<label for="paymentType">Payment Type:</label>
							</td>
							<td colspan=2 class="customer_td_input">
 							<c:forEach items="${listPayTypes}" var="payType">
								<c:if test="${payType==customerInfo.preferredPayment}">
									<input name="paymentType" id="paymentType" type="text" placeholder=" ${payType}" class="text-field" disabled>
								</c:if>
							</c:forEach>
							</td>
							<td colspan=2 class="customer_td_label">
								<label for="paymentHandle">Payment Handle:</label>
							</td>
							<td colspan=2 class="customer_td_input">
								<input type="text" id="paymentHandle" name="paymentHandle" placeholder=" ${customerInfo.paymentHandle}" class="text-field" disabled/>
							</td>
						</tr>
					</table>
				</div>
				<div id="checkout-panel">
					<h4 class="checkoutHeader">Order Details</h4>
					<div class="orderDetailHeader"><h4>
						<span>Order #${customerOrder.orderNum}</span>
						<span>Order Date:
							<fmt:parseDate  value="${customerOrder.orderDateTime}"  type="date" pattern="yyyy-MM-dd" var="parsedDate" />
							<fmt:formatDate value="${parsedDate}" type="date" pattern="MM-dd-yyyy" />
						</span>
						<span>Status: ${customerOrder.status}</span>
					</h4></div>
					<c:set var="orderTotal" value="${0}" />
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
						<tbody><c:forEach items="${customerOrder.orderItems}" var="orderItem">
							<tr>
								<td class="checkout_image_panel"><img src="${orderItem.product.image}" alt="${orderItem.product.description}" /></td>
								<td>${orderItem.product.name}</td>
								<td>${orderItem.product.options}</td>
								<td>${orderItem.product.size}</td>
								<td>${(orderItem.qtyFulfilled < orderItem.qty) ? '<span style=\"margin-left:0.25rem; margin-right:0.25rem; text-decoration: line-through; color:red\"> '.concat(orderItem.qty).concat(' </span> ').concat(orderItem.qtyFulfilled) : orderItem.qty}</td>
								<td><fmt:formatNumber value = "${orderItem.price}" type = "currency" /></td>
								<td><c:if test = "${orderItem.qtyFulfilled < orderItem.qty}">
									<span style="margin-left:0.25rem; margin-right:0.25rem; text-decoration: line-through; color:red">
										<fmt:formatNumber value = "${orderItem.qty * orderItem.price}" type = "currency" /><br />
									</span></c:if>
									<fmt:formatNumber value = "${orderItem.qtyFulfilled * orderItem.price}" type = "currency" />
								</td>
							</tr><c:set var="orderTotal" value="${orderTotal + orderItem.qty * orderItem.price}" /><c:set var="orderFulfilledTotal" value="${orderFulfilledTotal + orderItem.qtyFulfilled * orderItem.price}" />
						</c:forEach></tbody>
						<tfoot>
							<tr>
								<td  colspan=6 style="text-align:right;" class="checkout_subtotal_panel">Total:</td>
								<td class="checkout_subtotal_panel">
									<c:if test = "${orderFulfilledTotal < orderTotal}">
									<span style="margin-left:0.25rem; margin-right:0.25rem; text-decoration: line-through; color:red">
										<fmt:formatNumber value = "${orderTotal}" type = "currency" /><br />
									</span>
									</c:if>
									<fmt:formatNumber value = "${orderFulfilledTotal}" type = "currency" />
								</td>
							</tr>
						</tfoot>
					</table>
				</div>
				</c:if>
			</div>
		</div>
	</body>
</html>