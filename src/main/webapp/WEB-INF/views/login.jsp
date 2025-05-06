<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
    <head>
        <jsp:include page="/WEB-INF/views/includes/headElement.jsp">
            <jsp:param name="title" value="The Little Store - Log In" />
            <jsp:param name="page" value="login" />
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
                <div id="login-panel">
                    <h2>Log In</h2>
                    <p>New User? Click <a href="/signup">here</a> to Sign Up</p>
                    <form method="POST" action="${contextPath}/login" class="form-signin">
						<div class="form-group ${error != null ? 'has-error' : ''}">
							<div${message!=null?' style="color:green; margin-bottom:.5em;"':''}>${message}</div>
	                        <br /><label for="email">
	                            <input id="email" name="username" type="email" placeholder=" Email Address" class = "text-field" value="${fn:escapeXml(lastUsername)}" required autofocus />
	                        </label>
	                        <br /><label for="password">
	                            <input id="password" name="password" type="password" placeholder=" Password" class="text-field" required />
	                            <i id="pass-status" class="fa fa-eye" aria-hidden="true" onClick="viewPassword()"></i>
	                        </label>
							<div${error!=null?' style="color:#cc0000; margin-bottom:.5em;"':''}>${error}</div>
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	                        <br /><label for="submit">
	                            <button id="submit" name="submit" type="submit">Log In</button>
	                        </label>
						</div>
                    </form>
                    <p>Forgot Password? Click <a href="/forgotPassword">here</a></p>
                </div>
                <!--div id="bottom-img-content">
                    <a href="${transparentImage.right}"><img src="${transparentImage.left}" alt="${transparentImage.middle }"/></a>
                </div-->
            </div>
        </div>
        <footer>
			<jsp:include page="/WEB-INF/views/includes/basicFooter.jsp"></jsp:include>
        </footer>
    </body>
</html>