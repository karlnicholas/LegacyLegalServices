<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="entry" required="true" type="gsearch.viewmodel.EntryReference"%>
<%@ attribute name="index" required="true" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="myTags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${!entry.pathPart && !entry.sectionText}">
<div class="row panel-title">
   <span class="col-xs-1">
     <c:if test="${entry.count > 0 && viewModel.fragments}">
       <a data-toggle="collapse" data-parent="#accordion" href="#collapse${index}">
         <span class="glyphicon glyphicon-asterisk"></span></a>
     </c:if>
   </span>
   <a href="${urlBuilder.newPathUrl(viewModel, entry.fullFacet)}">
     <span class="col-xs-3">
       ${entry.displayTitle}
       <c:choose>
       <c:when test="${entry.count > 0}">
         <span class="badge pull-right"><c:out value="${entry.count}" /></span>
       </c:when>
       <c:when test="${entry.count == 0}">&nbsp;</c:when>
       </c:choose>
     </span>
     <span class="col-xs-5">
     ${entry.statutesBaseClass.title}
     </span>
     <span class="col-xs-3">§§&nbsp;${entry.statutesBaseClass.statuteRange}
     </span>
   </a>
</div>
<c:if test="${!empty entry.entries }">
  <div class="panel-collapse collapse" id="collapse${index}">
    <c:forEach items="${entry.entries}" var="entryText"><div class="row" style="white-space: pre;"><c:out value="${highlighter.highlightText(entryText.text, viewModel.term)}" escapeXml="false" /></div><br></c:forEach>
  </div>
</c:if>
</c:if>