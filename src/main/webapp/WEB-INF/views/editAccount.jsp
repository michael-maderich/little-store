<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="headElement.jsp">
            <jsp:param name="title" value="The Little Store - Reset Password" />
            <jsp:param name="page" value="signup" />
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
                </ul>
            </div>
            <div id="center-content">
				<div id="registration-panel">
					<h2>Reset Your Password</h2>
					<form class="form-signin">
						<div class="form-group">
							<label for="email"><input id="emailDisp" type="email" disabled
							value="${customerForm.email}" class="text-field"></input></label>
						</div>
					</form>
					<form:form method="POST" modelAttribute="customerForm" class="form-signin">
						<spring:bind path="firstName">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="firstName"></form:errors>${status.error ? '<br />' : ''}
								<form:input type="hidden" path="firstName" id="firstName"
								value="${customerForm.firstName}" class="text-field" required="true"></form:input>
							</div>
						</spring:bind>
						<spring:bind path="lastName">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="lastName"></form:errors>${status.error ? '<br />' : ''}
								<form:input type="hidden" path="lastName" id="lastName"
								value="${customerForm.lastName}" class="text-field" required="true"></form:input>
							</div>
						</spring:bind>
						<spring:bind path="email">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="email"></form:errors>${status.error ? '<br />' : ''}
								<form:input path="email" id="email" type="hidden"
								value="${customerForm.email}" class="text-field" required="true"></form:input>
							</div>
						</spring:bind>
						<spring:bind path="password">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="password"></form:errors>${status.error ? '<br />' : ''}
								<label for="password"><form:input path="password" id="password" type="password"
								placeholder=" New Password" value="" class="text-field" required="true"></form:input></label>
							</div>
						</spring:bind>
						<spring:bind path="passwordConfirm">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="passwordConfirm"></form:errors>${status.error ? '<br />' : ''}
								<label for="passwordConfirm"><form:input path="passwordConfirm" id="passwordConfirm"
								type="password" placeholder=" Confirm Password" class="text-field" required="true"></form:input></label>
							</div>
						</spring:bind>
						<spring:bind path="address">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="address"></form:errors>${status.error ? '<br />' : ''}
								<form:input type="hidden" path="address" id="address" name="address"
								value="${customerForm.address}" class="text-field"></form:input>
							</div>
						</spring:bind>
						<spring:bind path="city">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="city"></form:errors>${status.error ? '<br />' : ''}
								<form:input path="city" id="city" name="city" type="hidden"
								value="${customerForm.city}" class="text-field"></form:input>
							</div>
						</spring:bind>
						<spring:bind path="phone">
							<div class="form-group ${status.error ? 'has-error' : ''}">
								<form:errors path="phone"></form:errors>${status.error ? '<br />' : ''}
								<form:input type="hidden" path="phone" id="phone" name="phone"
								value="${customerForm.phone}" class="text-field"></form:input>
							</div>
						</spring:bind>
						<br /><label for="submit"><form:button id="submit" name="submit" type="submit">Submit</form:button></label>
					</form:form>
				</div>
                <div id="bottom-img-content">
                    <img src="https://www.suave.com/sk-eu/content/dam/brands/suave/united_states_ofamerica/1319751-079400459534.png.rendition.767.767.png" alt="Suave Kids Silly Apple 3-in-1 Shampoo + Conditioner + Body Wash 18 fl oz" width=400em; />
                </div>
            </div>
        </div>
        <footer>
			<jsp:include page="basicFooter.jsp"></jsp:include>
        </footer>
    </body>
</html>