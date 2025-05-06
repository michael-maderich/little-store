<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
	<head>
<jsp:include page="/WEB-INF/views/includes/headElement.jsp">
		<jsp:param name="title" value="The Little Store - Order History for ${customer.firstName.concat(' ').concat(customer.lastName)}" />
		<jsp:param name="page" value="order" />
</jsp:include>
	</head>
	<body>
		<header>
<jsp:include page="/WEB-INF/views/includes/basicHeader.jsp"></jsp:include>
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
					<c:forEach items="${orderList}" var="order">
					<c:set var="orderTotal" value="${0}" />
					<c:set var="orderFulfilledTotal" value="${0}" />
					<c:set var="orderRetailTotal" value="${0}" />
					<c:set var="orderSavingsTotal" value="${0}" />
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
								<th>Unit Price</th>
								<th>Quantity</th>
								<th>Sub-total</th>
							</tr>
						</thead>
						<tbody><c:forEach items="${order.orderItems}" var="orderItem">
							<tr>
								<td class="order_image_panel"><a href="/category/${orderItem.product.categoryMain}/${orderItem.product.categorySpecific}/#${orderItem.product.upc}"><img src="${orderItem.product.image}" alt="${orderItem.product.description}" /></a></td>
								<td><a href="/category/${orderItem.product.categoryMain}/${orderItem.product.categorySpecific}/#${orderItem.product.upc}">${orderItem.product.name}</a></td>
								<td><a href="/category/${orderItem.product.categoryMain}/${orderItem.product.categorySpecific}/#${orderItem.product.upc}">${orderItem.product.options}</a></td>
								<td><a href="/category/${orderItem.product.categoryMain}/${orderItem.product.categorySpecific}/#${orderItem.product.upc}">${orderItem.product.size}</a></td>
								<td>${orderItem.price < orderItem.basePrice ? '<span style="color:green; white-space: nowrap">Sale Price: ':''}<fmt:formatNumber value = "${orderItem.price}" type = "currency" />${orderItem.price < orderItem.basePrice ? '</span>':''}
								    ${orderItem.price < orderItem.basePrice ? '<br /><span style="text-decoration:line-through; white-space: nowrap">':'<span visible="false"'}<fmt:formatNumber value = "${orderItem.basePrice}" type = "currency" />${orderItem.price < orderItem.basePrice ? '</span>':'</span>'}
							 	    ${(showRetailPrice eq 1) and (orderItem.retailPrice > 0) ? '<br /><span title="(Lowest retail price)" style="text-decoration:line-through; white-space: nowrap; font-size: 75%;">':'<span visible="false"'}Lowest Retail: <fmt:formatNumber value = "${orderItem.retailPrice}" type = "currency" />${(showRetailPrice eq 1) and (orderItem.retailPrice > 0) ? '</span>':'</span>'}
						 	    </td>
								<td>${(orderItem.qtyFulfilled != orderItem.qty) ? '<span style=\"margin-left:0.25rem; margin-right:0.25rem; text-decoration: line-through; color:red\" title=\"Qty adjusted\"> '.concat(orderItem.qty).concat(' </span> ').concat(orderItem.qtyFulfilled) : orderItem.qty}</td>
								<td>
									<span style="white-space: nowrap">
										<c:if test = "${(orderItem.qtyFulfilled != orderItem.qty) && (orderItem.qty != 0)}">
										<span style="margin-left:0.25rem; margin-right:0.25rem; text-decoration: line-through; color:red">
											<fmt:formatNumber value = "${orderItem.qty * orderItem.price}" type = "currency" />
										</span></c:if>
										${orderItem.price < orderItem.basePrice ? '<span style="color:green; white-space: nowrap">':''}<fmt:formatNumber value = "${orderItem.qtyFulfilled * orderItem.price}" type = "currency" />${orderItem.price < orderItem.basePrice ? '</span>':''}
									</span>
							 	    ${(showRetailPrice eq 1) and (orderItem.retailPrice > 0) ? '<br /><span title="(Savings off Lowest Retail Total)" style="color:green; white-space: nowrap; font-size: 75%;">':'<span visible="false"'}Savings off Retail: <fmt:formatNumber value = "${orderItem.qtyFulfilled * (orderItem.retailPrice - orderItem.price)}" type = "currency" />${(showRetailPrice eq 1) and (orderItem.retailPrice > 0) ? '</span>':'</span>'}
								</td>
							</tr>
							<c:set var="orderTotal" value="${orderTotal + orderItem.qty * orderItem.price}" />
							<c:set var="orderFulfilledTotal" value="${orderFulfilledTotal + orderItem.qtyFulfilled * orderItem.price}" />
							<c:set var="orderRetailTotal" value="${orderRetailTotal + orderItem.qtyFulfilled * orderItem.retailPrice}" />
							<c:if test = "${orderItem.retailPrice > 0}">
								<c:set var="orderSavingsTotal" value="${orderSavingsTotal + orderItem.qtyFulfilled * (orderItem.retailPrice - orderItem.price)}" />
							</c:if>
						</c:forEach></tbody>
						<tfoot>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td class="order_subtotal_panel">
									<span style="white-space: nowrap">Total:</span>${(orderFulfilledTotal != orderTotal) ? '<br />' : ''}
 	    							${(showRetailPrice eq 1) and (orderSavingsTotal > 0) ? '<br /><span title="(Savings off lowest retail total)" style="color:green; white-space: nowrap; font-size: 75%;">':'<span visible="false"'}Your Savings:${(showRetailPrice eq 1) and (orderSavingsTotal > 0) ? '</span>':'</span>'}
    							</td>
								<td class="order_subtotal_panel">
									<c:if test = "${orderFulfilledTotal != orderTotal}">
									<span style="margin-left:0.25rem; margin-right:0.25rem; text-decoration: line-through; color:red">
										<fmt:formatNumber value = "${orderTotal}" type = "currency" /><br />
									</span>
									</c:if>
									<span style="white-space: nowrap"><fmt:formatNumber value = "${orderFulfilledTotal}" type = "currency" /></span>
 	    							${(showRetailPrice eq 1) and (orderSavingsTotal > 0) ? '<br /><span title="(Savings off lowest retail total)" style="color:green; white-space: nowrap; font-size: 75%;">':'<span visible="false"'}<fmt:formatNumber value = "${orderSavingsTotal}" type = "currency" />${(showRetailPrice eq 1) and (orderSavingsTotal > 0) ? '</span>':'</span>'}
								</td>
						</tfoot>
					</table>
					</c:forEach>
				</c:if>
				</div>
			</div>
		</div>
		<footer>
			<jsp:include page="/WEB-INF/views/includes/basicFooter.jsp"></jsp:include>
		</footer>
	</body>
</html>