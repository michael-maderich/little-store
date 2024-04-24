<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
	<head>
<jsp:include page="headElement.jsp">
		<jsp:param name="title" value="The Little Store - Order History for ${customer.firstName.concat(' ').concat(customer.lastName)}" />
		<jsp:param name="page" value="order" />
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
                    <li class="nav-item"><a class="nav-link" href="/account">Back to Profile</a></li>
                    <li class="nav-item"><a class="nav-link" href="/index">Continue Shopping</a></li>
                </ul>
            </div>
			<div id="center-content">
				<div id="order-panel">
					<h2>Order History</h2>
					${empty orderList ? '<div class="orderDetailHeader"><h4>No Orders to Display</h4></div>' : ''}
				<c:if test = "${not empty orderList}">
					<c:forEach items="${orderList}" var="order">	<c:set var="orderTotal" value="${0}" />	<c:set var="orderFulfilledTotal" value="${0}" />
					<div class="orderDetailHeader"><h4>
						<span>Order #${order.orderNum}</span>
						<span>Order Date:
							<fmt:parseDate  value="${order.orderDateTime}"  type="date" pattern="yyyy-MM-dd" var="parsedDate" />
							<fmt:formatDate value="${parsedDate}" type="date" pattern="MM-dd-yyyy" />
						</span>
						<span>Status: ${order.status}</span>
					</h4></div>
					<table id="order-table">
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
						<tbody><c:forEach items="${order.orderItems}" var="orderItem">
							<tr>
								<td class="order_image_panel"><img src="${orderItem.product.image}" alt="${orderItem.product.description}" /></td>
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
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td class="order_subtotal_panel">Total:</td>
								<td class="order_subtotal_panel">
									<c:if test = "${orderFulfilledTotal < orderTotal}">
									<span style="margin-left:0.25rem; margin-right:0.25rem; text-decoration: line-through; color:red">
										<fmt:formatNumber value = "${orderTotal}" type = "currency" /><br />
									</span>
									</c:if>
									<fmt:formatNumber value = "${orderFulfilledTotal}" type = "currency" />
								</td>
						</tfoot>
					</table>
					</c:forEach>
				</c:if>
				</div>
			</div>
		</div>
		<footer>
			<jsp:include page="basicFooter.jsp"></jsp:include>
		</footer>
	</body>
</html>