<%@ include file="/WEB-INF/views/admin/fragments/adminHeader.jsp" %>

<h2>Products</h2>

<a href="${pageContext.request.contextPath}/admin/products/create" class="btn">New Product</a>

<table class="admin-table">
  <thead>
    <tr>
      <th>Image<th>UPC</th><th>Name</th><th>Price</th><th>Stock</th><th>Actions</th>
    </tr>
  </thead>
  <tbody>
    <c:forEach var="p" items="${products}">
      <tr>
        <td>
          <c:choose>
            <c:when test="${not empty p.image}">
              <img src="${p.image}"
                   alt="${p.name}"
                   width="60"
                   height="60"
                   loading="lazy" />
            </c:when>
            <c:otherwise>
              <!-- Placeholder if no image set -->
              <img src="${contextPath}/images/placeholder.jpg"
                   alt="No image"
                   width="60" height="60" />
            </c:otherwise>
          </c:choose>
        </td>
        <td>${p.upc}</td>
        <td>${p.description}</td>
        <td>${p.currentPrice}</td>
        <td>${p.stockQty}</td>
        <td>
          <a href="${pageContext.request.contextPath}/admin/products/edit/${p.upc}">Edit</a> |
          <a href="${pageContext.request.contextPath}/admin/products/delete/${p.upc}"
             onclick="return confirm('Delete this product?');">Delete</a>
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>

<%@ include file="/WEB-INF/views/admin/fragments/adminFooter.jsp" %>
