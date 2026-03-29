<%@ include file="/WEB-INF/views/admin/fragments/adminHeader.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<h2>Orders</h2>

<c:if test="${not empty message}">
  <div class="alert alert-success" style="margin-bottom:1rem;">
    ${message}
  </div>
</c:if>
<c:if test="${not empty error}">
  <div class="alert alert-danger" style="margin-bottom:1rem;">
    ${error}
  </div>
</c:if>

<table class="admin-table">
  <thead>
    <tr>
      <th>Order #</th>
      <th>Customer</th>
      <th>Date</th>
      <th>Status</th>
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
          <fmt:parseDate value="${o.orderDateTime}" type="date" pattern="yyyy-MM-dd" var="parsedDate" />
          <fmt:formatDate value="${parsedDate}" type="date" pattern="MM-dd-yyyy" />
        </td>
        <td>${o.status}</td>
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
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>

<%@ include file="/WEB-INF/views/admin/fragments/adminFooter.jsp" %>
