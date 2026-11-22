<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
/**
* @Class Name : egovShopItemDetail.jsp
* @Description : 쇼핑몰 상품 상세 화면
* @Modification Information
*
*   수정일         수정자                   수정내용
*  -------    --------    ---------------------------
*  2026.11.22				최초 생성
*/
%>
<c:set var="navActive" value="shop"/>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><c:out value="${item.itemName}"/> - <spring:message code="item.shop.title" /></title>

	<link type="text/css" rel="stylesheet" href="<c:url value='/css/component/output.css'/>" />
	<link type="text/css" rel="stylesheet" href="<c:url value='/css/egovframe.css'/>" />
	<link type="text/css" rel="stylesheet" href="<c:url value='/css/item.css'/>" />
</head>

<body>
<div id="container" class="inner">

	<%@ include file="itemNav.jspf" %>

	<c:url var="listUrl" value="/shop/itemList.do">
		<c:param name="searchCategory" value="${itemVO.searchCategory}"/>
		<c:param name="searchKeyword" value="${itemVO.searchKeyword}"/>
		<c:param name="sortOrder" value="${itemVO.sortOrder}"/>
		<c:param name="pageIndex" value="${itemVO.pageIndex}"/>
	</c:url>
	<c:url var="categoryUrl" value="/shop/itemList.do"><c:param name="searchCategory" value="${item.categoryCode}"/></c:url>
	<c:url var="imgSrc" value="${empty item.imageUrl ? '/img/item/category-'.concat(item.categoryCode).concat('.svg') : item.imageUrl}"/>

	<div class="item-detail">
		<div class="item-detail-image">
			<img src="<c:out value='${imgSrc}'/>" alt="<c:out value='${item.itemName}'/>" />
		</div>

		<div class="item-detail-info">
			<a class="item-card-category" href="<c:out value='${categoryUrl}'/>"><spring:message code="item.category.${item.categoryCode}" text="${item.categoryCode}" /></a>
			<h2 class="heading-large"><c:out value="${item.itemName}"/></h2>

			<div class="item-price item-detail-price">
				<c:if test="${item.onSale}">
					<span class="item-discount">${item.discountRate}%</span>
					<del class="item-price-origin"><fmt:formatNumber value="${item.price}" pattern="#,##0"/></del>
				</c:if>
				<fmt:formatNumber var="sellingPrice" value="${item.sellingPrice}" pattern="#,##0"/>
				<strong class="item-price-selling"><spring:message code="item.price.format" arguments="${sellingPrice}" argumentSeparator="|" /></strong>
			</div>

			<dl class="item-detail-meta">
				<dt><spring:message code="title.item.itemId" /></dt>
				<dd><c:out value="${item.itemId}"/></dd>
				<dt><spring:message code="title.item.statusCode" /></dt>
				<dd>
					<c:choose>
						<c:when test="${item.purchasable}">
							<span class="krds-badge bg-light-success"><spring:message code="item.status.SALE" /></span>
							<spring:message code="item.stock.count" arguments="${item.stockQty}" />
						</c:when>
						<c:otherwise>
							<span class="krds-badge bg-light-warning"><spring:message code="item.status.SOLDOUT" /></span>
							<spring:message code="item.stock.soldout" />
						</c:otherwise>
					</c:choose>
				</dd>
			</dl>

			<c:if test="${not empty item.description}">
				<div class="item-detail-desc"><c:out value="${item.description}"/></div>
			</c:if>

			<div class="page-btn-wrap">
				<a class="krds-btn medium secondary" href="<c:out value='${listUrl}'/>"><spring:message code="item.shop.back" /></a>
			</div>
		</div>
	</div>

</div>
</body>
</html>
