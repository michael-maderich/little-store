<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
    <head>
<jsp:include page="headElement.jsp">
    <jsp:param name="title" value="The Little Store - Forgot Password" />
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
				<a href="${transparentImageLeft.right}"><img src="${transparentImageLeft.left}" alt="${transparentImageLeft.middle }"/></a>
			</div>
			<div id="right-img-content">
				<div class="image-buffer"></div>
				<a href="${transparentImageRight.right}"><img src="${transparentImageRight.left}" alt="${transparentImageRight.middle }"/></a>
			</div>
            <div id="center-content">
                <div id="login-panel">
                    <h2>Reset Password</h2>
                    <p>Enter your e-mail address to receive a password reset link:</p>
                    <form method="POST" action="/passwordReset" method="GET" class="form-signin">
						<div class="form-group ${error != null ? 'has-error' : ''}">
							<div${message!=null?' style="color:green; margin-bottom:.5em;"':''}>${message}</div>
	                        <br /><label for="email">
	                            <input id="email" name="username" type="email" placeholder="Email Address" class = "text-field" required autofocus />
	                        </label>
							<div${error!=null?' style="color:#cc0000; margin-bottom:.5em;"':''}>${error}</div>
	                        <br /><label for="submit">
	                            <button id="submit" name="submit" type="submit">Send Reset Link</button>
	                        </label>
						</div>
                    </form>
                </div>
                <!--div id="bottom-img-content">
					<a href="${transparentImageBottom.right}"><img src="${transparentImageBottom.left}" alt="${transparentImageBottom.middle }"/></a>
                </div-->
            </div>
        </div>
        <footer>
			<jsp:include page="basicFooter.jsp"></jsp:include>
        </footer>
    </body>
</html>