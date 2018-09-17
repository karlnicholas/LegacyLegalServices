<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="entries" required="true" type="java.util.List" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="myTags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${entries.size() == 1 }" >
<c:forEach items="${entries}" var="entry">
<li>
<a href="${urlBuilder.newPathUrl(viewModel, entry.fullFacet)}" data-toggle="tooltip" data-placement="bottom" title="${entry.statutesBaseClass.title}">
<c:choose>
<c:when test="${entry.entries.size()==1}"><c:out value="${entry.text}"/></c:when>
<c:when test="${entry.entries.size()>1}"><c:out value="${entry.text}"/> - <c:out value="${entry.statutesBaseClass.title}"/></c:when>
</c:choose>
</a>
</li>
<myTags:breadcrumb entries="${entry.entries}" />
</c:forEach>
</c:if>