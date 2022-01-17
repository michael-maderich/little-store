<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
			<div id="side-nav">
				<ul class="nav flex-column">
					<li class="nav-item"><a class="nav-link" href="/">Home</a></li>
					<c:forEach items="${navMenuItems}" var="mainCategory">
						<c:set var="subCatList" value="" />
					<li class="nav-item ${categoryName == mainCategory ? 'highlighted' : ''}">
					<c:forEach items="${navSubMenuItems}" var="subCat">
						<c:set var="subCatList" value="${subCatList}${empty subCatList ? '' : ', '}${subCat}" />
					</c:forEach>
						<a class="nav-link" href="/category/${mainCategory}" title="${subCatList}">${mainCategory}</a>
					</li>
					<c:if test = "${mainCategory == param.categoryName}">
					<c:forEach items="${navSubMenuItems}" var="subCategory">
						<li class="nav-item subNavItem ${subCategoryName == subCategory ? 'highlighted' : ''}">
							<a class="nav-link subNavLink" href="/category/${mainCategory}/${subCategory}">${subCategory}</a>
						</li>
					</c:forEach>
					</c:if>
					</c:forEach>
				</ul>
			</div>