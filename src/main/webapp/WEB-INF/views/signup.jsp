<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
	<head>
<jsp:include page="/WEB-INF/views/includes/headElement.jsp">
	<jsp:param name="title" value="The Little Store - Sign Up" />
	<jsp:param name="page" value="signup" />
</jsp:include>
	</head>
	<body>
		<header>
			<jsp:include page="/WEB-INF/views/includes/basicHeader.jsp"></jsp:include>
		</header>
		<div id="main-content">
	        <jsp:include page="/WEB-INF/views/includes/sideNav.jsp"></jsp:include>
			<div id="left-img-content">
				<div class="image-buffer"></div>
				<a href="${transparentImageLeft.right}"><img src="${transparentImageLeft.left}" alt="${transparentImageLeft.middle }"/></a>
			</div>
			<div id="right-img-content">
				<div class="image-buffer"></div>
				<a href="${transparentImageRight.right}"><img src="${transparentImageRight.left}" alt="${transparentImageRight.middle }"/></a>
			</div>
			<div id="center-content">
				<div id="registration-panel">
					<h2>New User Sign-Up</h2>
					<p>Already Registered? Click <a href="/login">here</a> to Log In</p>
					<form:form method="POST" modelAttribute="customerForm" class="form-signin">
						<spring:bind path="firstName">
								<form:errors path="firstName"></form:errors>${status.error ? '<br />' : ''}
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<label for="firstName"><form:input path="firstName" id="firstName" type="text"
								placeholder=" First Name" class="text-field" required="true" autofocus="true"></form:input></label>
							</div>
						</spring:bind>
						<spring:bind path="lastName">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="lastName"></form:errors>${status.error ? '<br />' : ''}
								<label for="lastName"><form:input path="lastName" id="lastName" type="text"
								placeholder=" Last Name" class="text-field" required="true"></form:input></label>
							</div>
						</spring:bind>
						<spring:bind path="email">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="email"></form:errors>${status.error ? '<br />' : ''}
								<label for="email"><form:input path="email" id="email" type="email"
								placeholder=" Email Address" class="text-field" required="true" autocomplete="username"></form:input></label>
							</div>
						</spring:bind>
						<spring:bind path="password">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="password"></form:errors>${status.error ? '<br />' : ''}
								<label for="password"><form:input path="password" id="password" type="password"
								placeholder=" Password" class="text-field" required="true" autocomplete="new-password"></form:input></label>
							</div>
						</spring:bind>
						<spring:bind path="passwordConfirm">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="passwordConfirm"></form:errors>${status.error ? '<br />' : ''}
								<label for="passwordConfirm"><form:input path="passwordConfirm" id="passwordConfirm"
								type="password" placeholder=" Confirm Password" class="text-field" required="true" autocomplete="new-password"></form:input></label>
							</div>
						</spring:bind>
						<p>Meet-Up Address (Optional):</p>
						<spring:bind path="address">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="address"></form:errors>${status.error ? '<br />' : ''}
								<label for="address"><form:input path="address" id="address" name="address" type="text" placeholder=" Street Address (optional)" class="text-field"></form:input></label>
							</div>
						</spring:bind>
						<spring:bind path="city">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="city"></form:errors>${status.error ? '<br />' : ''}
								<label for="city"><form:input path="city" id="city" name="city" type="text" placeholder=" City (optional)" class="text-field"></form:input></label>
								<label for="statedd">
									<form:select path="state" name="state" id="state">
									<c:forEach items="${listStates}" var="st">
										<option value="${st}" ${st=="PA" ? 'selected' : ''}>${st}</option>
									</c:forEach>
									</form:select>
								</label>
							</div>
						</spring:bind>
						<spring:bind path="phone">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="phone"></form:errors>${status.error ? '<br />' : ''}
								<label for="phone"><form:input path="phone" id="phone" name="phone" type="text" placeholder=" Phone Number (optional)" class="text-field"></form:input></label>
							</div>
						</spring:bind>
						<br /><label for="submit"><form:button id="submit" name="submit" type="submit">Submit</form:button></label>
					</form:form>
				</div>
			</div>
		</div>
        <footer>
			<jsp:include page="/WEB-INF/views/includes/basicFooter.jsp"></jsp:include>
        </footer>
	</body>
</html>