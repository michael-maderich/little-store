<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
			<div id="side-nav">
<!-- 				Navbar -->
<!-- 				<nav class="navbar navbar-light light-blue lighten-4"> -->
				
<!-- 				  Collapse button -->
<!-- 				  <button class="navbar-toggler toggler-example" type="button" data-toggle="collapse" data-target="#navbarSupportedContent1" -->
<!-- 				    aria-controls="navbarSupportedContent1" aria-expanded="false" aria-label="Toggle navigation"><span class="dark-blue-text"><i -->
<!-- 				        class="fas fa-bars fa-1x"></i></span></button> -->
				
<!-- 				  Collapsible content -->
<!-- 				  <div class="collapse in navbar-collapse" id="navbarSupportedContent1"> -->
				
<!-- 				    Links -->
<!-- 				    <ul class="navbar-nav ml-auto"> -->
<!-- 				      <li class="nav-item active"> -->
<!-- 				        <a class="nav-link" href="#">Home <span class="sl-only">(current)</span></a> -->
<!-- 				      </li> -->
<!-- 				      <li class="nav-item"> -->
<!-- 				        <a class="nav-link" href="#">Features</a> -->
<!-- 				      </li> -->
<!-- 				      <li class="nav-item"> -->
<!-- 				        <a class="nav-link" href="#">Pricing</a> -->
<!-- 				      </li> -->
<!-- 				    </ul> -->
<!-- 				    Links -->
				
<!-- 				  </div> -->
<!-- 				  Collapsible content -->
				
<!-- 				  Navbar brand -->
<!-- 				  <a class="navbar-brand" href="#">Menu</a> -->
				
<!-- 				</nav> -->
<!-- 				/.Navbar -->
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