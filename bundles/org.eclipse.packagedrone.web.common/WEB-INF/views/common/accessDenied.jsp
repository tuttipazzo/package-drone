<%@ page
    language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    trimDirectiveWhitespaces="true"
    %>

<%@ taglib tagdir="/WEB-INF/tags/main" prefix="h" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://eclipse.org/packagedrone/web" prefix="web" %>

<%
response.setStatus ( HttpServletResponse.SC_FORBIDDEN );
%>

<h:main title="Access denied" subtitle="${fn:escapeXml(id) }">

<div class="container"><div class="row">

<div class="col-md-offset-2 col-md-8">

<div class="alert alert-danger" role="alert">
    <strong>Access denied!</strong> You have no access to this part of Package Drone.
</div>

Try to <a href="#" onclick="history.back(-1);">go back</a>.

</div>

</div></div>

</h:main>