<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="myTags" %>
<jsp:useBean id="highlighter" class="gsearch.util.Highlighter" scope="request"/>
<jsp:useBean id="urlBuilder" class="guidedsearchweb.controller.UrlBuilder" scope="request"/>
<jsp:include page="/WEB-INF/template/template.jsp" />
<ol class="breadcrumb">
<li><a href="${urlBuilder.homeUrl(viewModel)}" data-toggle="tooltip" data-placement="bottom" title="Home">Home</a></li><myTags:breadcrumb entries="${viewModel.entries}" />
<c:if test="${not empty viewModel.term}">
<li><span class="badge pull-right">${viewModel.totalCount}</span></li>
</c:if>
</ol>
<div class="container-fluid">
<div class="panel-group" id="accordion">
<div class="panel">
<myTags:recurse entries="${viewModel.entries}"/>
</div>
</div>
</div>