<%@ include file="/WEB-INF/views/admin/fragments/adminHeader.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script>
async function confirmDelete(event, upc) {
  event.preventDefault();
  const url = event.currentTarget.href;
  try {
    const res = await fetch('${pageContext.request.contextPath}/admin/products/' + upc + '/in-cart');
    const inCart = await res.json();
    const msg = inCart
      ? 'This product is currently in one or more customer carts. Deleting it will also remove it from those carts. Delete anyway?'
      : 'Delete this product?';
    if (confirm(msg)) window.location.href = url;
  } catch (e) {
    if (confirm('Delete this product?')) window.location.href = url;
  }
}
</script>
<h2>Products</h2>

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
                   loading="lazy"
                   class="product-thumb" />
            </c:when>
            <c:otherwise>
              <!-- Placeholder if no image set -->
              <img src="${contextPath}/images/placeholder.jpg"
                   alt="No image"
                   class="product-thumb" />
            </c:otherwise>
          </c:choose>
        </td>
        <td>${p.upc}</td>
        <td>${p.description}</td>
		<td>
		  $<fmt:formatNumber 
		       value="${p.currentPrice}" 
		       pattern="0.00##" />
		</td>
        <td>${p.stockQty}</td>
        <td>
          <a href="${pageContext.request.contextPath}/admin/products/edit/${p.upc}">Edit</a> |
          <a href="${pageContext.request.contextPath}/admin/products/delete/${p.upc}"
             onclick="confirmDelete(event, '${p.upc}'); return false;">Delete</a>
        </td>
      </tr>
    </c:forEach>
  </tbody>
</table>

<%@ include file="/WEB-INF/views/admin/fragments/adminFooter.jsp" %>
