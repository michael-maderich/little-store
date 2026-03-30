<%@ include file="/WEB-INF/views/admin/fragments/adminHeader.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<h2>Orders</h2>

<c:if test="${not empty message}">
  <div class="alert alert-success" style="margin-bottom:1rem;">${message}</div>
</c:if>
<c:if test="${not empty error}">
  <div class="alert alert-danger" style="margin-bottom:1rem;">${error}</div>
</c:if>

<table class="admin-table">
  <thead>
    <tr>
      <th>Order #</th>
      <th>Customer</th>
      <th>Order Date</th>
      <th>Status</th>
      <th>Status Date</th>
      <th>Total</th>
      <th>Actions</th>
    </tr>
  </thead>
  <tbody>
    <c:forEach var="o" items="${orders}">
      <tr>
        <td>${o.orderNum}</td>
        <td>${o.customer.email}</td>
        <td>
          <fmt:parseDate value="${o.orderDateTime}" type="date" pattern="yyyy-MM-dd" var="parsedOrderDate" />
          <fmt:formatDate value="${parsedOrderDate}" type="date" pattern="MM-dd-yyyy" />
        </td>
        <td>
          <form method="post" action="/admin/orders/${o.orderNum}/status" style="display:inline-flex;gap:.4rem;align-items:center;">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <select name="status" class="admin-select">
              <option value="PROCESSING"  <c:if test="${o.status == 'PROCESSING'}">selected</c:if>>Processing</option>
              <option value="SHIPPED"     <c:if test="${o.status == 'SHIPPED'}">selected</c:if>>Shipped</option>
              <option value="DELIVERED"   <c:if test="${o.status == 'DELIVERED'}">selected</c:if>>Delivered</option>
              <option value="PICKED_UP"   <c:if test="${o.status == 'PICKED_UP'}">selected</c:if>>Picked Up</option>
              <option value="CANCELLED"   <c:if test="${o.status == 'CANCELLED'}">selected</c:if>>Cancelled</option>
              <option value="RETURNED"    <c:if test="${o.status == 'RETURNED'}">selected</c:if>>Returned</option>
            </select>
            <button type="submit" class="admin-btn">Update</button>
          </form>
        </td>
        <td>
          <fmt:parseDate value="${o.statusDateTime}" type="date" pattern="yyyy-MM-dd" var="parsedStatusDate" />
          <fmt:formatDate value="${parsedStatusDate}" type="date" pattern="MM-dd-yyyy" />
        </td>
        <td>
          <c:set var="total" value="0" />
          <c:forEach var="item" items="${o.orderItems}">
            <c:set var="total" value="${total + item.qty * item.price}" />
          </c:forEach>
          $<fmt:formatNumber value="${total}" pattern="0.00" />
        </td>
        <td style="white-space: nowrap">
          <a href="/admin/orders/${o.orderNum}/print" target="_blank"
             class="admin-btn">Print Order</a>
          <a href="/admin/orders/${o.orderNum}/resend"
             class="admin-btn"
             onclick="return confirm('Resend confirmation for order #${o.orderNum} to ${o.customer.email}?')">Resend Confirmation</a>
          <c:if test="${o.status != 'DELIVERED'}">
            <a href="/admin/orders/${o.orderNum}/cancel"
               class="admin-btn admin-btn-danger"
               onclick="return confirm('Cancel order #${o.orderNum}?')">Cancel</a>
          </c:if>
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>

<%@ include file="/WEB-INF/views/admin/fragments/adminFooter.jsp" %>

