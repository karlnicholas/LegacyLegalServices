<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="entry" required="true" type="gsearch.viewmodel.EntryReference"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="myTags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${entry.sectionText}">
<div class="row"><c:out value="${highlighter.highlightText(entry.text, viewModel.term)}" escapeXml="false" /></div><br>
</c:if>