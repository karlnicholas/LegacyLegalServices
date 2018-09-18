<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="myTags" %>
<!DOCTYPE html>
<jsp:useBean id="highlighter" class="gsearch.util.Highlighter" scope="request"/>
<jsp:useBean id="urlBuilder" class="guidedsearchweb.controller.UrlBuilder" scope="request"/>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Guided Code Search</title>
    <!-- Bootstrap -->
	<link rel='stylesheet' type='text/css' href='webjars/bootstrap/3.3.7/css/bootstrap.min.css'/>
	<link rel='stylesheet' type='text/css' href='css/non-responsive.css'/>
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
<body>
<div class="container">
<jsp:include page="/WEB-INF/template/navigation.jsp" />
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
</div>
<script src="webjars/jquery/1.12.4/jquery.min.js" type="text/javascript"></script>
<script src="webjars/bootstrap/3.3.7/js/bootstrap.min.js" type="text/javascript"></script>
</body>
</html>
