<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
		<div id="admin-side-nav" class="admin-sidebar">
			<ul class="nav flex-column admin-nav">
			    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
			    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/products">Products</a></li>
			    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/orders">Orders</a></li>
			    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/customers">Customers</a></li>
			    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/promotions">Promotions</a></li>
			    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/reports">Reports</a></li>
			    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/content">CMS Pages</a></li>
			    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/settings">Settings</a></li>
			    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/users">Admin Users</a></li>
			</ul>
		</div>