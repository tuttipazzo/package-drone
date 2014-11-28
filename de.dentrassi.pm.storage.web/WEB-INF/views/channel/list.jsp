<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib tagdir="/WEB-INF/tags" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<h:main title="Channels">

<ul class="button-bar">
<li><a class="pure-button" href="channel/create">Create Channel</a></li>
</ul>

<table style="width: 100%">

<thead>
	<tr>
    	<th>Name</th>
    	<th>ID</th>
	</tr>
</thead>

<tbody>
<c:forEach items="${channels}" var="channel">
<tr>
    <td><a href="<c:url value="/channel/${channel.id }/view"/>">${channel.name }</a></td>
	<td><a href="<c:url value="/channel/${channel.id }/view"/>">${channel.id }</a></td>
</tr>
</c:forEach>
</tbody>

</table>

</h:main>