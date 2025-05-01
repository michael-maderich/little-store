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
    <jsp:param name="page" value="login" />
</jsp:include>
    </head>
    <body>
        <header>
            <jsp:include page="basicHeader.jsp"></jsp:include>
        </header>
        <div id="main-content">
            <jsp:include page="sideNav.jsp"></jsp:include>
            <div id="left-img-content">
                <div class="image-buffer"></div>
                <a href="${transparentImageLeft.right}"><img src="${transparentImageLeft.left}" alt="${transparentImageLeft.middle}" /></a>
            </div>
            <div id="right-img-content">
                <div class="image-buffer"></div>
                <a href="${transparentImageRight.right}"><img src="${transparentImageRight.left}" alt="${transparentImageRight.middle}" /></a>
            </div>
            <div id="center-content">
                <div id="login-panel">
                    <h2>Reset Your Password</h2>
                    <p>Please enter your new password below:</p>
                    <form method="POST" action="${contextPath}/resetPassword" class="form-signin">
                        <input type="hidden" name="token" value="${token}" />
                        <div class="form-group ${error != null ? 'has-error' : ''}">
                            <div${message!=null?' style="color:green; margin-bottom:.5em;"':''}>${message}</div>
                            <br /><label for="password">
                                <input id="password" name="password" type="password" placeholder=" New Password" class="text-field" required />
	                            <i id="pass-status" class="fa fa-eye" aria-hidden="true" onClick="viewPassword()"></i>
                            </label>
                            <br /><label for="confirmPassword">
                                <input id="confirmPassword" name="confirmPassword" type="password" placeholder=" Confirm Password" class="text-field" required />
                            </label>
                            <div${error!=null?' style="color:#cc0000; margin-bottom:.5em;"':''}>${error}</div>
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <br /><label for="submit">
                                <button id="submit" name="submit" type="submit">Reset Password</button>
                            </label>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <footer>
            <jsp:include page="basicFooter.jsp"></jsp:include>
        </footer>
    </body>
</html>
