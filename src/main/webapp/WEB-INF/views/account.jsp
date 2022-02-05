<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html>
	<head>
        <jsp:include page="headElement.jsp">
            <jsp:param name="title" value="The Little Store - Account Profile" />
            <jsp:param name="page" value="account" />
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
                    <li class="nav-item"><a class="nav-link" href="/account/edit">Edit Profile</a></li>
                    <li class="nav-item"><a class="nav-link" href="/account/orders">Order History</a></li>
                </ul>
            </div>
			<div id="left-img-content">
				<img src="https://www.dove.com/content/dam/unilever/dove/canada/pack_shot/011111121140-1773965-png.png" alt="Dove Women Body Wash Purifying Detox with Green Clay" />
			</div>
			<div id="center-content">
				<div id="profile-panel">
					<h2>Account Profile</h2>
					<h5>${customerForm != null ? 'Hello, '.concat(customerForm.firstName).concat('!') : 'Not Logged In'}</h5>
					<br /><h6>Profile Information:</h6>
					<!--form id="logoutForm" method="POST" action="${contextPath}/logout">
					<c:if test="${customerForm != null}">
			            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
						<input type="submit" id="logoutbtn" name="logoutbtn" value="Sign Out" class="btn btn-sm btn-primary btn-block" />
					</c:if>
					</form-->
					<form:form method="POST" modelAttribute="customerForm" class="form-signin">
						<spring:bind path="firstName">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<label for="firstName"><form:input path="firstName" id="firstName" name="firstName" type="text"
								placeholder=" First Name" class="text-field" required="true" autofocus="true" disabled="true"></form:input></label>
								<form:errors path="firstName"></form:errors>
							</div>
						</spring:bind>
						<spring:bind path="lastName">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<label for="lastName"><form:input path="lastName" id="lastName" name="lastName" type="text"
								placeholder=" Last Name" class="text-field" required="true" disabled="true"></form:input></label>
								<form:errors path="lastName"></form:errors>
							</div>
						</spring:bind>
						<spring:bind path="email">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<label for="email"><form:input path="email" id="email" name="email" type="email"
								placeholder=" Email Address" class="text-field" required="true" autocomplete="username" disabled="true"></form:input></label>
								<form:errors path="email"></form:errors>
							</div>
						</spring:bind>
						<spring:bind path="password">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<label for="password"><form:input path="password" id="password" name="password" type="hidden"
								placeholder=" Password" class="text-field" required="true" autocomplete="new-password" disabled="true"></form:input></label>
								<form:errors path="password"></form:errors>
							</div>
						</spring:bind>
						<p>Address:</p>
						<label for="address"><form:input path="address" id="address" name="address" type="text" placeholder=" Street Address" class="text-field" disabled="true"></form:input></label>
						<br /><label for="city"><form:input path="city" id="city" name="city" type="text" placeholder=" City" class="text-field" disabled="true"></form:input></label>
						<label for="statedd">
							<form:select path="state" name="state" id="state" disabled="true">
							<c:forEach items="${listStates}" var="st">
								<option value="${st}" ${st eq customerForm.state ? 'selected' : ''}>${st}</option>
							</c:forEach>
							</form:select>
						</label>
						<br /><label for="phone"><form:input path="phone" id="phone" name="phone" type="text" placeholder=" Phone Number" class="text-field" disabled="true"></form:input></label>
<%-- 						<br /><label for="submit"><form:button id="edit" name="edit" type="submit">Edit Profile</form:button></label> --%>
					</form:form>
				</div>
			</div>
			<div id="right-img-content">
				<img src="https://www.dove.com/content/dam/unilever/dove/united_states_of_america/pack_shot/011111023659-2252303-png.png" alt="Dove Women Body Wash Irritation Care Fragrance-Free 22 fl oz" />
			</div>
        </div>
        <footer>
			<jsp:include page="basicFooter.jsp"></jsp:include>
        </footer>
	</body>
</html>